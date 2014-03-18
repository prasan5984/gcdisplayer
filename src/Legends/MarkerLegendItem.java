package Legends;

import java.util.ArrayList;

import javax.swing.JCheckBox;

import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;

public class MarkerLegendItem extends LegendItem
{

	private ArrayList< Marker >	markerList	= new ArrayList< Marker >();

	public MarkerLegendItem( String name, JCheckBox chkBox, XYPlot plot )
	{
		super( name, chkBox );
	}

	public void addMarker( Marker m )
	{
		markerList.add( m );
	}

	public void setVisibility( boolean flag )
	{
		if ( flag == false )
			for ( Marker m : markerList )
				m.setAlpha( 0 );
		else
			for ( Marker m : markerList )
				m.setAlpha( 0.25f );

		this.checkBox.setSelected( flag );

	}

}
