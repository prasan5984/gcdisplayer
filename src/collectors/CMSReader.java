package collectors;

import java.util.ArrayList;
import java.util.List;
import data_structure.DataStructureException;
import data_structure.Field;
import data_structure.GCLogDataStructure;

public class CMSReader implements CollectorLogReader
{
	private String						matchString		= "CMS-initial-mark";
	private List< CollectorLogReader >	newCollectors	= new ArrayList< CollectorLogReader >();
	private CollectorLogReader			newCollector;
	private List< FieldDetail >			fieldDtlList;
	private GCLogDataStructure			dataStructure;
	private String						previousLine	= "";

	@Override
	public void initialize()
	{
		newCollector.initialize();
		dataStructure = newCollector.getDataStructure();

		try
		{
			Field f = dataStructure.getField( GCLogDataStructure.FILE_LEVEL_FIELDS );
			f.getSubField( 0 ).setValue( getCollectorNameDetails() );
		}
		catch ( DataStructureException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		CMSInitializer initializer = new CMSInitializer();
		fieldDtlList = initializer.getFieldDetailList();

		for ( FieldDetail fDtl : fieldDtlList )
			try
			{
				fDtl.setFieldFormat( dataStructure );
			}
			catch ( DataStructureException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public String getCollectorNameDetails()
	{
		if ( newCollector == null )
			return "CMS";
		else
			return newCollector.getCollectorNameDetails() + "(Young);CMS(Old)";
	}
	
	

	@Override
	public boolean checkIfMatches( List< String > sampleLines )
	{
		setNewCollectors();

		for ( String str : sampleLines )
			if ( str.matches( ".*(" + matchString + ").*" ) )
			{
				for ( CollectorLogReader reader : newCollectors )
				{
					if ( reader.checkIfMatches( sampleLines ) )
					{
						newCollector = reader;
						return true;
					}
				}
				return false;
			}

		return false;
	}

	@Override
	public void setValues( String line )
	{
		if ( !line.matches( "^[0-9].*" ) )
		{
			line = this.previousLine + line;
			setCMSValues( line );
		}
		this.previousLine = line;

		if ( newCollector.checkIfMatches( line ) )
			newCollector.setValues( line );
		else
			setCMSValues( line );
	}

	private void setCMSValues( String line )
	{
		for ( FieldDetail fDetail : fieldDtlList )
			if ( line.contains( fDetail.fieldName ) )
				try
				{
					fDetail.matchLine( line );
				}
				catch ( DataStructureException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	@Override
	public GCLogDataStructure getDataStructure()
	{
		// TODO Auto-generated method stub
		return dataStructure;
	}

	@Override
	public boolean checkIfMatches( String line )
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setNewCollectors()
	{
		newCollectors.add( new SerialGCLogReader() );
		newCollectors.add( new ParallelGCReader() );
		newCollectors.add( new ParNewReader() );
	}

	@Override
	public void setMatchString( String matchString )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getCollectorName()
	{
		return "CMS";
	}

}
