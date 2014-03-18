package collectors;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data_structure.DataStructureException;
import data_structure.Field;
import data_structure.GCLogDataStructure;

class FieldDetail
{
	public String											fieldName;
	public Map< String, LinkedHashMap< String, String >>	subFieldMap;
	public LinkedHashMap< String, String >					fieldMap;
	public Pattern											startPattern, endPattern;
	public Field											patternField, lastRecord;
	GCLogDataStructure										dataStructure;

	public void setFieldFormat( GCLogDataStructure dataStructure ) throws DataStructureException
	{
		this.dataStructure = dataStructure;

		Field header = dataStructure.getField( GCLogDataStructure.HEADER_FIELD ).getCopy( false );

		for ( Field f : header.getSubFields() )
		{
			String fieldName = f.getFieldName();

			String curOrder;
			if ( fieldMap.containsKey( fieldName ) )
				if ( ( curOrder = fieldMap.get( fieldName ) ) != null )
					for ( String str : curOrder.split( "," ) )
						f.addParameter( str );

			if ( subFieldMap.containsKey( fieldName ) )
			{
				for ( Field subField : f.getSubFields() )
				{
					String subFieldName = subField.getFieldName();
					if ( subFieldMap.get( fieldName ).containsKey( subFieldName ) )
						for ( String str : subFieldMap.get( fieldName ).get( subFieldName ).split( "," ) )
							subField.addParameter( str );

				}
			}
		}

		patternField = header;

	}

	public void matchLine( String line ) throws DataStructureException
	{
		Matcher m;
		Field record;
		int matchFl = 0;

		if ( endPattern == null || line.contains( "-start" ) )
		{
			m = startPattern.matcher( line );
			record = dataStructure.createNewRecord( patternField, true );

			if ( endPattern != null )
				matchFl = -1;
		}
		else
		{
			m = endPattern.matcher( line );
			record = lastRecord;
			matchFl = 1;
		}

		if ( m.matches() )
			lastRecord = updatePattern( record, m, matchFl );

	}

	public Field updatePattern( Field record, Matcher matcher, int matchFl )
	{
		for ( Field field : record.getSubFields() )
		{
			if ( field.getParameterSize() > 0 && matchFl <= 0 )
				field.setValue( matcher.group( Integer.parseInt( field.getParameter( 0 ) ) ) );

			if ( matchFl >= 0 )
				for ( Field subField : field.getSubFields() )
				{
					if ( subField.getParameterSize() > 1 )
						subField.setValue( matcher.group( Integer.parseInt( subField.getParameter( 0 ) ) ) + "(CPU)/"
								+ matcher.group( Integer.parseInt( subField.getParameter( 1 ) ) ) + "(Clock)" );
					else if ( subField.getParameterSize() > 0 )
						subField.setValue( matcher.group( Integer.parseInt( subField.getParameter( 0 ) ) ) );
				}
		}

		return record;
	}
}
