package collectors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

public class CMSInitializer
{
	private List< FieldDetail >	fieldDetailList	= new ArrayList< FieldDetail >();

	public List< FieldDetail > getFieldDetailList()
	{
		String pattern1 = "([0-9]*\\.?[0-9]*)";
		String pattern2 = pattern1 + "[KkMmGgBb]{1}";
		String pattern3 = pattern1 + " *" + "secs";
		String pattern4 = pattern1 + ": *";
		String pattern5 = pattern2 + "\\(" + pattern2 + "\\)";
		String pattern6 = "\\[1 (CMS-initial-mark) *: *" + pattern5 + "?\\]";
		String pattern7 = pattern4 + " *\\[(Full GC|GC) *" + pattern6 + " *" + pattern5 + " *, *" + pattern3 + " *\\].*";

		// Remark
		String pattern8 = "\\[1 (CMS-remark) *: *" + pattern5 + "?\\]";
		String pattern9 = pattern4 + ".*" + pattern8 + " *" + pattern5 + " *, *" + pattern3 + " *\\].*";

		//Concurrent Mark Start
		String pattern10 = "\\[(CMS-concurrent-mark)-start\\]";
		String pattern11 = pattern4 + " *" + pattern10 + ".*";

		// Concurrent Mark End
		String pattern13 = pattern1 + " */ *" + pattern3;
		String pattern14 = "\\[CMS-concurrent-mark *: *" + pattern13 + " *\\]";
		String pattern15 = ".*?" + pattern4 + " *" + pattern14 + ".*";

		//Preclean Start
		String pattern16 = "\\[(CMS-concurrent-preclean)-start\\]";
		String pattern17 = pattern4 + " *" + pattern16 + ".*";

		// Preclean End
		String pattern19 = "\\[CMS-concurrent-preclean *: *" + pattern13 + " *\\]";
		String pattern20 = ".*?" + pattern4 + " *" + pattern19 + ".*";

		//Sweep Start
		String pattern21 = "\\[(CMS-concurrent-sweep)-start\\]";
		String pattern22 = pattern4 + " *" + pattern21 + ".*";

		// Sweep End
		String pattern23 = "\\[CMS-concurrent-sweep *: *" + pattern13 + " *\\]";
		String pattern24 = ".*?" + pattern4 + " *" + pattern23 + ".*";

		//Reset Start
		String pattern25 = "\\[(CMS-concurrent-reset)-start\\]";
		String pattern26 = pattern4 + " *" + pattern25 + ".*";

		//Reset End
		String pattern27 = "\\[CMS-concurrent-reset *: *" + pattern13 + " *\\]";
		String pattern28 = ".*?" + pattern4 + " *" + pattern27 + ".*";

		// Initial Mark
		FieldDetail fDetail1 = new FieldDetail();
		fDetail1.fieldName = "CMS-initial-mark";

		fDetail1.fieldMap = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "GC Time(secs)", "1" );
				put( "Collection Type", "3" );
				put( "Tenured Generation", null );
				put( "Total heap", null );
			}
		};

		final LinkedHashMap< String, String > tenuredMap = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Size Before(K)", "4" );
				put( "Total Size(K)", "5" );
			}
		};

		final LinkedHashMap< String, String > totalHeapMap = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Size Before(K)", "6" );
				put( "Total Size(K)", "7" );
				put( "Time Taken(secs)", "8" );
			}
		};

		fDetail1.subFieldMap = new LinkedHashMap< String, LinkedHashMap< String, String >>()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Tenured Generation", tenuredMap );
				put( "Total heap", totalHeapMap );
			}
		};

		fDetail1.startPattern = Pattern.compile( pattern7 );

		fieldDetailList.add( fDetail1 );

		// Concurrent Mark
		FieldDetail fDetail2 = new FieldDetail();
		fDetail2.fieldName = "CMS-concurrent-mark";

		fDetail2.fieldMap = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "GC Time(secs)", "1" );
				put( "Collection Type", "2" );
			}
		};

		final LinkedHashMap< String, String > tenuredMap2 = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Time Taken(secs)", "2,3" );
			}
		};
		fDetail2.subFieldMap = new LinkedHashMap< String, LinkedHashMap< String, String >>()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Tenured Generation", tenuredMap2 );
			}
		};

		fDetail2.startPattern = Pattern.compile( pattern11 );
		fDetail2.endPattern = Pattern.compile( pattern15 );

		fieldDetailList.add( fDetail2 );

		// Preclean

		FieldDetail fDetail3 = new FieldDetail();
		fDetail3.fieldName = "CMS-concurrent-preclean";

		fDetail3.fieldMap = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "GC Time(secs)", "1" );
				put( "Collection Type", "2" );
			}
		};

		final LinkedHashMap< String, String > tenuredMap3 = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Time Taken(secs)", "2,3" );
			}
		};
		fDetail3.subFieldMap = new LinkedHashMap< String, LinkedHashMap< String, String >>()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Tenured Generation", tenuredMap3 );
			}
		};
		fDetail3.startPattern = Pattern.compile( pattern17 );
		fDetail3.endPattern = Pattern.compile( pattern20 );

		fieldDetailList.add( fDetail3 );

		// Remark
		FieldDetail fDetail4 = new FieldDetail();
		fDetail4.fieldName = "CMS-remark";

		fDetail4.fieldMap = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "GC Time(secs)", "1" );
				put( "Collection Type", "2" );
				put( "Tenured Generation", null );
				put( "Total heap", null );
			}
		};

		final LinkedHashMap< String, String > tenuredMap4 = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Size Before(K)", "3" );
				put( "Total Size(K)", "4" );
			}
		};

		final LinkedHashMap< String, String > totalHeapMap4 = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Size Before(K)", "5" );
				put( "Total Size(K)", "6" );
				put( "Time Taken(secs)", "7" );
			}
		};

		fDetail4.subFieldMap = new LinkedHashMap< String, LinkedHashMap< String, String >>()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Tenured Generation", tenuredMap4 );
				put( "Total heap", totalHeapMap4 );
			}
		};
		fDetail4.startPattern = Pattern.compile( pattern9 );

		fieldDetailList.add( fDetail4 );

		// Sweep

		FieldDetail fDetail5 = new FieldDetail();
		fDetail5.fieldName = "CMS-concurrent-sweep";

		fDetail5.fieldMap = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "GC Time(secs)", "1" );
				put( "Collection Type", "2" );
			}
		};

		final LinkedHashMap< String, String > tenuredMap5 = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Time Taken(secs)", "2,3" );
			}
		};
		fDetail5.subFieldMap = new LinkedHashMap< String, LinkedHashMap< String, String >>()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Tenured Generation", tenuredMap5 );
			}
		};
		fDetail5.startPattern = Pattern.compile( pattern22 );
		fDetail5.endPattern = Pattern.compile( pattern24 );

		fieldDetailList.add( fDetail5 );

		// Reset

		FieldDetail fDetail6 = new FieldDetail();
		fDetail6.fieldName = "CMS-concurrent-reset";

		fDetail6.fieldMap = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "GC Time(secs)", "1" );
				put( "Collection Type", "2" );
			}
		};

		final LinkedHashMap< String, String > tenuredMap6 = new LinkedHashMap< String, String >()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Time Taken(secs)", "2,3" );
			}
		};
		fDetail6.subFieldMap = new LinkedHashMap< String, LinkedHashMap< String, String >>()
		{
			private static final long	serialVersionUID	= 1L;
			{
				put( "Tenured Generation", tenuredMap6 );
			}
		};

		fDetail6.startPattern = Pattern.compile( pattern26 );
		fDetail6.endPattern = Pattern.compile( pattern28 );

		fieldDetailList.add( fDetail6 );

		return fieldDetailList;

	}
}
