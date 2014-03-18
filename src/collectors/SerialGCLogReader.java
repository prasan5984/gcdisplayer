package collectors;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data_structure.DataStructureException;
import data_structure.Field;
import data_structure.GCLogDataStructure;

public class SerialGCLogReader implements CollectorLogReader
{
	protected Pattern							pattern;
	protected GCLogDataStructure				dataStructure		= new GCLogDataStructure();
	protected String							matchString			= "DefNew";
	protected String							collectorName		= "Serial";
	protected boolean							isTotalHeapPrinted	= true;
	protected LinkedHashMap< String, String >	matchOrderMap		= CollectorConstants.FIELD_MATCHORDER_MAP;
	protected Matcher							matcher;

	private Field createField( String fieldName )
	{
		Field f = new Field();
		f.setFieldName( fieldName );
		return f;
	}

	protected void createStructure() throws DataStructureException
	{
		for ( Map.Entry< String, String > e : matchOrderMap.entrySet() )
		{
			String fieldName = e.getKey();
			String matchOrderNo = e.getValue();
			Field f = createField( fieldName );
			f.addParameter( matchOrderNo );
			f.setValue( fieldName );

			if ( CollectorConstants.SUB_FIELDS.containsKey( fieldName ) )
			{
				HashMap< String, String > subFieldMap = CollectorConstants.SUB_FIELDS.get( fieldName );

				for ( Map.Entry< String, String > e1 : subFieldMap.entrySet() )
				{
					Field subField = createField( e1.getKey() );
					subField.addParameter( e1.getValue() );
					subField.setValue( e1.getKey() );
					f.addSubField( subField );

				}
			}
			dataStructure.addField( GCLogDataStructure.HEADER_FIELD, f );
		}
		Field f = createField( "Collector Name" );
		f.setValue( getCollectorNameDetails() );
		dataStructure.addField( GCLogDataStructure.FILE_LEVEL_FIELDS, f );
	}

	protected void initializePattern()
	{
		String pattern1 = "([0-9]*\\.?[0-9]*)";
		String pattern2 = pattern1 + "[KkMmGgBb]{1}";
		String pattern3 = pattern2 + "->" + pattern2 + "\\(" + pattern2 + "\\)";
		String pattern4 = pattern1 + " *" + "secs";
		String pattern5 = "(\\[" + matchString + " *: *" + pattern3 + ", *" + pattern4 + "\\])";
		String pattern6 = "(\\[Tenured *: *" + pattern3 + ", *" + pattern4 + "\\])";
		String pattern9 = "(\\[Perm *: *" + pattern3 + "\\])";
		String pattern7 = pattern1 + ": *";
		String pattern10 =
				pattern7 + " *\\[(Full GC|GC) *" + pattern7 + pattern5 + "? *" + "(?:" + pattern7 + ")?(?:" + pattern6 + ")? *" + pattern3 + ", *(?:"
						+ pattern9 + ",)? *" + pattern4 + "\\].*";

		pattern = Pattern.compile( pattern10 );
	}

	@Override
	public boolean checkIfMatches( List< String > lines )
	{
		for ( String str : lines )
			if ( str.matches( ".*(" + matchString + ").*" ) )
				return true;
		return false;
	}

	public void setValues( String line )
	{
		if ( checkIfMatches( line ) )
		{
			Field record = dataStructure.createNewRecord();

			for ( Field f : record.getSubFields() )
			{
				if ( f.getSubFieldsCount() != 0 )
					for ( Field subField : f.getSubFields() )
						subField.setValue( matcher.group( Integer.parseInt( subField.getParameter( 0 ) ) ) );
				else
				{
					int matchIndex = Integer.parseInt( f.getParameter( 0 ) );
					if ( matchIndex == 10 )
						if ( matcher.group( matchIndex ) == null )
							f.setValue( "Minor" );
						else
							f.setValue( "Major" );
					else if ( matchIndex == 2 && isTotalHeapPrinted )
						if ( matcher.group( matchIndex ).equals( "GC" ) )
							f.setValue( "No" );
						else
							f.setValue( "Yes" );
					else
						f.setValue( matcher.group( matchIndex ) );
				}
			}
		}
	}

	public GCLogDataStructure getDataStructure()
	{
		return this.dataStructure;
	}

	@Override
	public String getCollectorNameDetails()
	{
		return getCollectorName();
	}

	@Override
	public void initialize()
	{
		initializePattern();
		try
		{
			createStructure();
		}
		catch ( DataStructureException e )
		{
			e.printStackTrace();
		}

	}

	@Override
	public boolean checkIfMatches( String line )
	{
		this.matcher = pattern.matcher( line );
		return matcher.matches();
	}

	@Override
	public void setMatchString( String matchString )
	{
		//this.matchString = matchString;

	}

	@Override
	public String getCollectorName()
	{
		return collectorName;
	}

}
