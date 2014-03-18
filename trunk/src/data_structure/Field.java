package data_structure;

import java.util.ArrayList;

public class Field
{
	private String				fieldName;
	private ArrayList< Field >	subFields	= new ArrayList< Field >();
	private String				value;
	private ArrayList< String >	parameters	= new ArrayList< String >();

	public void addParameter( String value )
	{
		parameters.add( value );
	}

	public String getParameter( int position )
	{
		return parameters.get( position );
	}

	public void setFieldName( String name )
	{
		fieldName = name;
	}

	public void addSubField( Field subField )
	{
		subFields.add( subField );
	}

	public String getFieldName()
	{
		return fieldName;
	}

	public int getSubFieldsCount()
	{
		return subFields.size();
	}

	public Field getSubField( int index )
	{
		return subFields.get( index );
	}

	public ArrayList< Field > getSubFields()
	{
		return subFields;
	}

	public void setValue( String value )
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public Field getCopy()
	{
		Field f = new Field();
		f.setFieldName( getFieldName() );
		f.parameters = this.parameters;
		for ( Field subField : this.subFields )
		{
			Field newSubField = subField.getCopy();
			f.addSubField( newSubField );
		}

		return f;
	}

	public Field getCopy( boolean parFlag )
	{
		if ( !parFlag )
		{
			Field f = new Field();
			f.setFieldName( getFieldName() );

			for ( Field subField : this.subFields )
			{
				Field newSubField = subField.getCopy( false );
				f.addSubField( newSubField );
			}

			return f;
		}
		else
			return getCopy();

	}

	public int getMaxSubFieldDepth()
	{
		return getMaxSubFieldDepth( this );
	}

	private int getMaxSubFieldDepth( Field f )
	{
		int count = 0;
		for ( Field subField : f.getSubFields() )
		{
			int curCount = 1;
			int subFieldCount = getMaxSubFieldDepth( subField );
			curCount = curCount + ( ( subFieldCount > 0 ) ? ( subFieldCount ) : 0 );
			if ( curCount > count )
				count = curCount;
		}
		return count;
	}

	public int getMaxSubFieldWidth()
	{
		return getMaxSubFieldWidth( this );
	}

	private int getMaxSubFieldWidth( Field f )
	{
		int width = 0;

		for ( Field subField : f.getSubFields() )
		{
			width++;
			int subFieldWidth;

			if ( ( subFieldWidth = getMaxSubFieldWidth( subField ) ) != 0 )
				width = width + subFieldWidth - 1;
		}

		return width;

	}

	public void resetParameter()
	{
		parameters = new ArrayList< String >();
	}

	public int getParameterSize()
	{
		return parameters.size();
	}
}
