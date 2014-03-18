package Legends;

import javax.swing.JCheckBox;

import org.jfree.chart.renderer.AbstractRenderer;

public class SeriesLegendItem extends LegendItem
{
	private AbstractRenderer	renderer;
	private int					index;

	public SeriesLegendItem( String name, JCheckBox chkBox, AbstractRenderer renderer, int seriesIndex )
	{
		super( name, chkBox );
		this.renderer = renderer;
		this.index = seriesIndex;
	}

	public void setVisibility( boolean flag )
	{
		renderer.setSeriesVisible( index, flag );
		this.checkBox.setSelected( flag );
	}

}
