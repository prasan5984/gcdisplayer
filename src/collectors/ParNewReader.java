package collectors;

import java.util.regex.Pattern;

import data_structure.Field;

public class ParNewReader extends SerialGCLogReader
{
	public ParNewReader()
	{
		this.matchString = "ParNew";
		this.collectorName = "Parallel New";
	}

	protected void initializePattern()
	{
		String pattern1 = "([0-9]*\\.?[0-9]*)";
		String pattern2 = pattern1 + "[KkMmGgBb]{1}";
		String pattern3 = pattern2 + "->" + pattern2 + "\\(" + pattern2 + "\\)";
		String pattern4 = pattern1 + " *" + "secs";
		String pattern5 = "(\\[" + "ParNew *(?:\\(.*\\))?" + " *: *" + pattern3 + ", *" + pattern4 + "\\])";
		String pattern6 = "(\\[CMS(?:.*) *(?:\\(.*\\))? *: *" + pattern3 + ", *" + pattern4 + "\\])";
		String pattern9 = "(\\[CMS Perm *: *" + pattern3 + "\\])";
		String pattern7 = pattern1 + ": *";
		String pattern10 =
				pattern7 + " *\\[(Full GC|GC) *" + pattern7 + pattern5 + "? *" + "(?:" + pattern7 + ")?(?:" + pattern6 + ")? *" + pattern3 + ", *(?:"
						+ pattern9 + ",)? *" + pattern4 + "\\].*";

		pattern = Pattern.compile( pattern10 );
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
					{
						String majorCollection = matcher.group( matchIndex );
						String minorCollection = matcher.group( 4 );
						if ( majorCollection == null )
							f.setValue( "Minor" );
						else if ( majorCollection.contains( ( "concurrent mode failure" ) ) )
							f.setValue( "Major (Concurrent mode failure)" );
						else if ( minorCollection != null && minorCollection.contains( "promotion failed" ) )
							f.setValue( "Major (Promotion failed)" );
						else
							f.setValue( "Major" );
					}
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

}
