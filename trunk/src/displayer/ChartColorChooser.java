package displayer;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

public class ChartColorChooser
{
	private Color						majorColor			= new Color( 255, 0, 0 );
	private Color						minorColor			= new Color( 128, 0, 128 );
	private Color						initialColor		= new Color( 0, 0, 255 );
	private Color						remarkColor			= new Color( 0, 128, 0 );

	private Color						concurrentMark		= new Color( 200, 180, 0 );
	private Color						precleanColor		= new Color( 112, 128, 144 );
	private Color						sweepColor			= new Color( 233, 150, 122 );
	private Color						resetColor			= new Color( 123, 104, 160 );

	private Color						ygColor				= new Color( 0, 128, 128 );
	private Color						ogColor				= new Color( 153, 50, 204 );

	private HashMap< String, Color >	colorCategoryMap	= new HashMap< String, Color >();

	public ChartColorChooser()
	{
		colorCategoryMap.put( CMSChartDisplayerConstants.MINOR, minorColor );
		colorCategoryMap.put( CMSChartDisplayerConstants.MAJOR, majorColor );
		colorCategoryMap.put( CMSChartDisplayerConstants.INITIAL_MARK, initialColor );
		colorCategoryMap.put( CMSChartDisplayerConstants.REMARK, remarkColor );
		colorCategoryMap.put( CMSChartDisplayerConstants.CONCURRENT_MARK, concurrentMark );
		colorCategoryMap.put( CMSChartDisplayerConstants.PRECLEAN, precleanColor );
		colorCategoryMap.put( CMSChartDisplayerConstants.SWEEP, sweepColor );
		colorCategoryMap.put( CMSChartDisplayerConstants.RESET, resetColor );
		colorCategoryMap.put( CMSChartDisplayerConstants.YG, ygColor );
		colorCategoryMap.put( CMSChartDisplayerConstants.OG, ogColor );
	}

	public Color getColor( String category )
	{
		return colorCategoryMap.get( category );
	}
	
	public Font getFont () 
	{
		Font f = new Font( "TIMES", Font.BOLD, 12 );
		return f;
	}

}
