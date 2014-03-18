package displayer;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

import Legends.LegendItem;
import Legends.MarkerLegendItem;
import Legends.SeriesLegendItem;
import data_structure.Field;

public class ChartDisplayer implements LogDisplayer, ItemListener
{

	protected String						patternFormat	= ".*/([0-9]*\\.[0-9]*)\\(Clock\\)";
	protected Pattern						pattern			= Pattern.compile( patternFormat );

	protected NumberAxis					gcTimeAxis		= new NumberAxis( "GC Time (ms)" );
	protected NumberAxis					sizeAxis		= new NumberAxis( "Memory (K)" );
	protected NumberAxis					gcDurationAxis	= new NumberAxis( "GC Duration (ms)" );

	protected XYSeries						ygSeries, ogSeries, minorSeries, majorSeries, imSeries, rmSeries;
	protected XYSeriesCollection			sizeDataset		= new XYSeriesCollection();
	protected XYSeriesCollection			durationDataset	= new XYSeriesCollection();

	protected XYLineAndShapeRenderer		lineRenderer	= new XYLineAndShapeRenderer();
	protected XYDotRenderer					dotRenderer		= new XYDotRenderer();

	protected XYPlot						xyPlot			= new XYPlot();

	protected ChartPanel					chartPanel;

	protected HashMap< String, XYSeries >	gcSeriesMap		= new HashMap< String, XYSeries >();

	protected ChartColorChooser				colorChooser	= new ChartColorChooser();

	//protected ArrayList< JCheckBox >		parentLegendItems	= new ArrayList< JCheckBox >();

	protected LegendItem					allItem, ccItem, ncItem;

	protected double						gcTimeLimit		= 0;

	public ChartPanel getChartPanel()
	{
		return chartPanel;
	}

	public int getLevel( JCheckBox checkBox )
	{
		return allItem.getHierarchy( checkBox );
	}

	public ArrayList< JCheckBox > getLegendCheckBoxes()
	{
		return allItem.getCheckBoxList();
	}

	@Override
	public void initialize()
	{
		gcTimeAxis.setLabelFont( colorChooser.getFont() );
		sizeAxis.setLabelFont( colorChooser.getFont() );
		gcDurationAxis.setLabelFont( colorChooser.getFont() );

		xyPlot.setDomainAxis( gcTimeAxis );

		// Size Series
		ygSeries = new XYSeries( "Young Generation" );
		ogSeries = new XYSeries( "Tenure Generation" );

		ygSeries.add( 0, 0 );
		ogSeries.add( 0, 0 );

		sizeDataset.addSeries( ygSeries );
		sizeDataset.addSeries( ogSeries );

		lineRenderer.setSeriesPaint( 0, colorChooser.getColor( CMSChartDisplayerConstants.YG ) );
		lineRenderer.setSeriesPaint( 1, colorChooser.getColor( CMSChartDisplayerConstants.OG ) );

		lineRenderer.setSeriesShapesVisible( 0, false );
		lineRenderer.setSeriesShapesVisible( 1, false );

		xyPlot.setRangeAxis( 0, sizeAxis );
		xyPlot.setDataset( 0, sizeDataset );
		xyPlot.setRenderer( lineRenderer );
		xyPlot.mapDatasetToRangeAxis( 0, 0 );

		SeriesLegendItem ygItem = new SeriesLegendItem( CMSChartDisplayerConstants.YG, new JCheckBox( "Young Generation" ), lineRenderer, 0 );
		SeriesLegendItem ogItem = new SeriesLegendItem( CMSChartDisplayerConstants.OG, new JCheckBox( "Tenure Generation" ), lineRenderer, 1 );

		// Duration Series
		minorSeries = new XYSeries( "Minor" );
		majorSeries = new XYSeries( "Major" );
		imSeries = new XYSeries( "Initial Mark" );
		rmSeries = new XYSeries( "Remark" );

		durationDataset.addSeries( minorSeries );
		durationDataset.addSeries( majorSeries );
		durationDataset.addSeries( imSeries );
		durationDataset.addSeries( rmSeries );

		dotRenderer.setSeriesPaint( 0, colorChooser.getColor( CMSChartDisplayerConstants.MINOR ) );
		dotRenderer.setSeriesPaint( 1, colorChooser.getColor( CMSChartDisplayerConstants.MAJOR ) );
		dotRenderer.setSeriesPaint( 2, colorChooser.getColor( CMSChartDisplayerConstants.INITIAL_MARK ) );
		dotRenderer.setSeriesPaint( 3, colorChooser.getColor( CMSChartDisplayerConstants.REMARK ) );
		dotRenderer.setDotWidth( 5 );
		dotRenderer.setDotHeight( 5 );

		SeriesLegendItem minorItem = new SeriesLegendItem( CMSChartDisplayerConstants.MINOR, new JCheckBox( "Minor" ), dotRenderer, 0 );
		SeriesLegendItem majorItem = new SeriesLegendItem( CMSChartDisplayerConstants.MAJOR, new JCheckBox( "Major" ), dotRenderer, 1 );
		SeriesLegendItem imItem = new SeriesLegendItem( CMSChartDisplayerConstants.INITIAL_MARK, new JCheckBox( "Initial Mark" ), dotRenderer, 2 );
		SeriesLegendItem rmItem = new SeriesLegendItem( CMSChartDisplayerConstants.REMARK, new JCheckBox( "Remark" ), dotRenderer, 3 );

		ncItem =
				new LegendItem( CMSChartDisplayerConstants.NC, new JCheckBox( "Non-concurrent Collections" ), new LegendItem[] { minorItem,
						majorItem, imItem, rmItem } );

		xyPlot.setRangeAxis( 1, gcDurationAxis );
		xyPlot.setDataset( 1, durationDataset );
		xyPlot.setRenderer( 1, dotRenderer );
		xyPlot.mapDatasetToRangeAxis( 1, 1 );

		// Concurrent Collections

		MarkerLegendItem markItem = new MarkerLegendItem( CMSChartDisplayerConstants.CONCURRENT_MARK, new JCheckBox( "Mark" ), xyPlot );
		MarkerLegendItem precleanItem = new MarkerLegendItem( CMSChartDisplayerConstants.PRECLEAN, new JCheckBox( "Preclean" ), xyPlot );
		MarkerLegendItem sweepItem = new MarkerLegendItem( CMSChartDisplayerConstants.SWEEP, new JCheckBox( "Sweep" ), xyPlot );
		MarkerLegendItem resetItem = new MarkerLegendItem( CMSChartDisplayerConstants.RESET, new JCheckBox( "Reset" ), xyPlot );

		ccItem =
				new LegendItem( CMSChartDisplayerConstants.CC, new JCheckBox( "Concurrent Collections" ), new LegendItem[] { markItem, precleanItem,
						sweepItem, resetItem } );

		// All Object

		allItem = new LegendItem( CMSChartDisplayerConstants.ALL, new JCheckBox( "All" ), new LegendItem[] { ygItem, ogItem, ncItem, ccItem } );

		setCheckBoxAttributes();

		// Update Map
		gcSeriesMap.put( CMSChartDisplayerConstants.MINOR, minorSeries );
		gcSeriesMap.put( CMSChartDisplayerConstants.MAJOR, majorSeries );
		gcSeriesMap.put( CMSChartDisplayerConstants.INITIAL_MARK, imSeries );
		gcSeriesMap.put( CMSChartDisplayerConstants.REMARK, rmSeries );

	}

	@Override
	public void writeFile( String filename )
	{
		//ccItem.setVisibility( true );

		JFreeChart chart = new JFreeChart( xyPlot );
		chart.removeLegend();

		gcTimeAxis.setUpperBound( gcTimeLimit );

		chartPanel = new ChartPanel( chart );

	}

	protected void setCheckBoxAttributes()
	{
		for ( LegendItem item : allItem.getLegendItemList() )
		{
			String categoryName = item.getCategoryName();
			JCheckBox checkBox = item.getCheckBox();
			checkBox.setForeground( colorChooser.getColor( categoryName ) );
			checkBox.addItemListener( this );
			checkBox.setSelected( true );
			checkBox.setFont( new Font( "Times", Font.BOLD, 12 ) );

		}
	}

	@Override
	public void writeLine( ArrayList< Field > records )
	{
		for ( Field record : records )
		{
			Field gcTimeField = record.getSubField( 0 );
			double gcTimeInSecs = Double.parseDouble( gcTimeField.getValue() );
			double gcTime = gcTimeInSecs * 1000;
			gcTimeLimit = gcTime;

			Field collectionTypeField = record.getSubField( 1 );
			String collectionType = collectionTypeField.getValue();

			if ( collectionType.contains( "concurrent" ) )
			{
				String markerDurationDetails = record.getSubField( 4 ).getSubField( 3 ).getValue();

				double duration = 0;
				if ( markerDurationDetails != null )
				{

					Matcher m = pattern.matcher( markerDurationDetails );

					if ( m.matches() )
						duration = Double.parseDouble( m.group( 1 ) );
				}

				addConcurrentMarkers( gcTime, collectionType, duration );
			}
			else
			{
				double duration = getNormalizedChartValues( record.getSubField( 5 ).getSubField( 3 ) );
				addNonConcurrentSeries( gcTime, duration, collectionType );

				if ( collectionType.contains( ( "Minor" ) ) || collectionType.contains( ".*Major.*" ) )
				{
					double value1 = getNormalizedChartValues( record.getSubField( 3 ).getSubField( 0 ) );
					double value2 = getNormalizedChartValues( record.getSubField( 3 ).getSubField( 1 ) );

					if ( value1 != 0 )
						ygSeries.add( gcTime, value1 );
					if ( value2 != 0 )
						ygSeries.add( gcTime, value2 );

					if ( collectionType.contains( ".*Major.*" ) )
					{
						double value3 = getNormalizedChartValues( record.getSubField( 4 ).getSubField( 0 ) );
						double value4 = getNormalizedChartValues( record.getSubField( 4 ).getSubField( 1 ) );

						if ( value3 != 0 )
							ogSeries.add( gcTime, value3 );

						if ( value4 != 0 )
							ogSeries.add( gcTime, value4 );
					}
				}
				else
				{
					double value = getNormalizedChartValues( record.getSubField( 4 ).getSubField( 0 ) );
					if ( value != 0 )
						ogSeries.add( gcTime, value );
				}
			}
		}

	}

	protected void addNonConcurrentSeries( double gcTime, double duration, String collectionType )
	{
		double durationInMillis = duration * 1000;

		XYSeries series = gcSeriesMap.get( collectionType );

		if ( series != null )
			series.add( gcTime, durationInMillis );
	}

	protected double getNormalizedChartValues( Field f )
	{
		String strValue = f.getValue();
		if ( strValue != null && strValue != "" )
			return Double.parseDouble( strValue );
		return 0;
	}

	protected void addConcurrentMarkers( double gcTime, String collectionType, double duration )
	{
		double upperLimirInMilliSeconds = gcTime + duration * 1000;

		IntervalMarker m = new IntervalMarker( gcTime, upperLimirInMilliSeconds );
		m.setLabel( null );
		m.setPaint( colorChooser.getColor( collectionType ) );
		m.setAlpha( 0.25f );
		xyPlot.addDomainMarker( m, Layer.BACKGROUND );

		( (MarkerLegendItem)ccItem.find( collectionType ) ).addMarker( m );
	}

	@Override
	public void itemStateChanged( ItemEvent e )
	{
		JCheckBox checkBox = (JCheckBox)e.getSource();

		int state = e.getStateChange();

		LegendItem item = allItem.find( checkBox );

		if ( state == ItemEvent.SELECTED )
			item.setVisibility( true );
		else
			item.setVisibility( false );

	}
	
	public ChartDisplayer getCopy() {
		return new ChartDisplayer();
	}

}
