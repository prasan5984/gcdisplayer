package displayer;

import java.util.ArrayList;
import javax.swing.JCheckBox;
import org.jfree.data.xy.XYSeries;
import Legends.LegendItem;
import Legends.SeriesLegendItem;
import data_structure.Field;

public class BasicGCChartDisplayer extends ChartDisplayer
{
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
		/*imSeries = new XYSeries( "Initial Mark" );
		rmSeries = new XYSeries( "Remark" );*/

		durationDataset.addSeries( minorSeries );
		durationDataset.addSeries( majorSeries );
		/*durationDataset.addSeries( imSeries );
		durationDataset.addSeries( rmSeries );*/

		dotRenderer.setSeriesPaint( 0, colorChooser.getColor( CMSChartDisplayerConstants.MINOR ) );
		dotRenderer.setSeriesPaint( 1, colorChooser.getColor( CMSChartDisplayerConstants.MAJOR ) );
		/*dotRenderer.setSeriesPaint( 2, colorChooser.getColor( CMSChartDisplayerConstants.INITIAL_MARK ) );
		dotRenderer.setSeriesPaint( 3, colorChooser.getColor( CMSChartDisplayerConstants.REMARK ) );*/
		dotRenderer.setDotWidth( 5 );
		dotRenderer.setDotHeight( 5 );

		SeriesLegendItem minorItem = new SeriesLegendItem( CMSChartDisplayerConstants.MINOR, new JCheckBox( "Minor" ), dotRenderer, 0 );
		SeriesLegendItem majorItem = new SeriesLegendItem( CMSChartDisplayerConstants.MAJOR, new JCheckBox( "Major" ), dotRenderer, 1 );
		/*SeriesLegendItem imItem = new SeriesLegendItem( CMSChartDisplayerConstants.INITIAL_MARK, new JCheckBox( "Initial Mark" ), dotRenderer, 2 );
		SeriesLegendItem rmItem = new SeriesLegendItem( CMSChartDisplayerConstants.REMARK, new JCheckBox( "Remark" ), dotRenderer, 3 );*/

		ncItem =
				new LegendItem( CMSChartDisplayerConstants.BASIC_COLLECTIONS, new JCheckBox( "Non-concurrent Collections" ), new LegendItem[] {
						minorItem, majorItem } );

		xyPlot.setRangeAxis( 1, gcDurationAxis );
		xyPlot.setDataset( 1, durationDataset );
		xyPlot.setRenderer( 1, dotRenderer );
		xyPlot.mapDatasetToRangeAxis( 1, 1 );

		// Concurrent Collections

		/*	MarkerLegendItem markItem = new MarkerLegendItem( CMSChartDisplayerConstants.CONCURRENT_MARK, new JCheckBox( "Mark" ), xyPlot );
			MarkerLegendItem precleanItem = new MarkerLegendItem( CMSChartDisplayerConstants.PRECLEAN, new JCheckBox( "Preclean" ), xyPlot );
			MarkerLegendItem sweepItem = new MarkerLegendItem( CMSChartDisplayerConstants.SWEEP, new JCheckBox( "Sweep" ), xyPlot );
			MarkerLegendItem resetItem = new MarkerLegendItem( CMSChartDisplayerConstants.RESET, new JCheckBox( "Reset" ), xyPlot );

			ccItem =
					new LegendItem( CMSChartDisplayerConstants.CC, new JCheckBox( "Concurrent Collections" ), new LegendItem[] { markItem, precleanItem,
							sweepItem, resetItem } );*/

		// All Object

		allItem = new LegendItem( CMSChartDisplayerConstants.ALL, new JCheckBox( "All" ), new LegendItem[] { ygItem, ogItem, ncItem } );

		setCheckBoxAttributes();

		// Update Map
		gcSeriesMap.put( CMSChartDisplayerConstants.MINOR, minorSeries );
		gcSeriesMap.put( CMSChartDisplayerConstants.MAJOR, majorSeries );
		/*	gcSeriesMap.put( CMSChartDisplayerConstants.INITIAL_MARK, imSeries );
			gcSeriesMap.put( CMSChartDisplayerConstants.REMARK, rmSeries );*/
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

			/*if ( collectionType.contains( "concurrent" ) )
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
			{*/
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
		//}

	}
	
	public ChartDisplayer getCopy() {
		return new BasicGCChartDisplayer();
	}

}
