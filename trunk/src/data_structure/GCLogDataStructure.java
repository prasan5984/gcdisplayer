package data_structure;

import java.util.ArrayList;

public class GCLogDataStructure
{
	public static int			HEADER_FIELD		= 1;
	public static int			FILE_LEVEL_FIELDS	= 2;
	public static int			RECORD_FIELD		= 3;

	private Field				headerField			= new Field();
	private String				fileName;
	private Field				fileLevelField		= new Field();
	private ArrayList< Field >	recordsList			= new ArrayList< Field >();
	private Field				currentRecord		= new Field();

	public Field getField( int fieldType ) throws DataStructureException
	{
		Field curField;
		if ( fieldType == 1 )
			curField = headerField;
		else if ( fieldType == 2 )
			curField = fileLevelField;
		else if ( fieldType == 3 )
			curField = currentRecord;
		else
			throw new DataStructureException( "Not a valid field Type: " + fieldType );

		return curField;

	}

	public void addField( int fieldType, Field field ) throws DataStructureException
	{
		getField( fieldType ).addSubField( field );
	}

	public int getFieldCount( int fieldType ) throws DataStructureException
	{
		return getField( fieldType ).getSubFieldsCount();
	}

	public Field getField( int fieldType, int index ) throws DataStructureException
	{
		Field f = getField( fieldType ).getSubField( index );
		return f;
	}

	public void setFileName( String fileName )
	{
		this.fileName = fileName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public Field createNewRecord()
	{

		Field record = new Field();

		for ( Field f : headerField.getSubFields() )
		{
			Field newField = f.getCopy();
			record.addSubField( newField );
		}

		this.currentRecord = record;
		this.recordsList.add( currentRecord );

		return record;

	}

	public Field createNewRecord( Field f, boolean includeFl )
	{
		Field record = new Field();

		for ( Field subField : f.getSubFields() )
		{
			Field newField = subField.getCopy();
			record.addSubField( newField );
		}

		if ( includeFl )
		{
			this.recordsList.add( record );
			this.currentRecord = record;
		}

		return record;

	}

	public void setValue( String fieldName, String value ) throws DataStructureException
	{
		for ( Field f : currentRecord.getSubFields() )
			if ( f.getFieldName().equals( fieldName ) )
			{
				f.setValue( value );
				return;
			}

		throw new DataStructureException( "Unable to identify the field Name " + fieldName );

	}

	public int getRecordCount()
	{
		return this.recordsList.size();
	}

	public void setRecordIndex( int index ) throws DataStructureException
	{
		if ( recordsList.size() > index | index < 0 )
			throw new DataStructureException( index + " not a valid index" );
		currentRecord = recordsList.get( index );

	}

	public ArrayList< Field > getRecordsList()
	{
		return recordsList;
	}

}
