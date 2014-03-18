package Legends;

import java.util.ArrayList;

import javax.swing.JCheckBox;

import displayer.CMSChartDisplayerConstants;

public class LegendItem
{
	protected String				categoryName;
	protected JCheckBox				checkBox;
	private ArrayList< LegendItem >	subItems	= new ArrayList< LegendItem >();

	public String getCategoryName()
	{
		return this.categoryName;
	}

	public JCheckBox getCheckBox()
	{
		return this.checkBox;
	}

	public LegendItem( String name, JCheckBox chkBox, LegendItem[] items )
	{
		this.categoryName = name;
		this.checkBox = chkBox;

		for ( LegendItem item : items )
			subItems.add( item );
	}

	protected LegendItem( String name, JCheckBox chkBox )
	{
		this.categoryName = name;
		this.checkBox = chkBox;
	}

	public LegendItem find( String category )
	{
		if ( this.categoryName.equals( category ) )
			return this;
		else
			for ( LegendItem item : subItems )
			{
				LegendItem itemFound = item.find( category );
				if ( itemFound != null )
					return itemFound;
			}
		return null;
	}

	public LegendItem find( JCheckBox chkBox )
	{
		if ( this.checkBox.equals( chkBox ) )
			return this;
		else
			for ( LegendItem item : subItems )
			{
				LegendItem itemFound = item.find( chkBox );
				if ( itemFound != null )
					return itemFound;
			}
		return null;
	}

	public void setVisibility( boolean flag )
	{
		for ( LegendItem item : subItems )
			item.setVisibility( flag );

		this.checkBox.setSelected( flag );
	}

	public int getHierarchy( JCheckBox chkBox )
	{
		if ( chkBox.equals( this.checkBox ) )
			return CMSChartDisplayerConstants.HIERARCHY_LEVEL_ONE;

		for ( LegendItem item : subItems )
			if ( item.checkBox.equals( chkBox ) )
				return CMSChartDisplayerConstants.HIERARCHY_LEVEL_TWO;

		return CMSChartDisplayerConstants.HIERARCHY_LEVEL_THREE;
	}

	public ArrayList< JCheckBox > getCheckBoxList()
	{
		ArrayList< JCheckBox > chkBoxList = new ArrayList< JCheckBox >();
		chkBoxList.add( this.checkBox );

		for ( LegendItem item : subItems )
			chkBoxList.addAll( item.getCheckBoxList() );

		return chkBoxList;

	}

	public ArrayList< LegendItem > getLegendItemList()
	{
		ArrayList< LegendItem > itemList = new ArrayList< LegendItem >();
		itemList.add( this );

		for ( LegendItem item : subItems )
			itemList.addAll( item.getLegendItemList() );

		return itemList;

	}

}
