package collectors;

import java.util.regex.Pattern;

public class ParallelGCReader extends SerialGCLogReader
{
	public ParallelGCReader()
	{
		matchString = "PSYoungGen";
		collectorName = "Parallel";
		isTotalHeapPrinted = false;
		matchOrderMap = CollectorConstants.PAR_FIELD_MATCHORDER_MAP;
	}

	protected void initializePattern()
	{
		String pattern1 = "([0-9]*\\.?[0-9]*)";
		String pattern2 = pattern1 + "[KkMmGgBb]{1}";
		String pattern3 = pattern2 + "->" + pattern2 + "\\(" + pattern2 + "\\)";
		String pattern4 = pattern1 + " *" + "secs";
		String pattern5 = "(\\[PSYoungGen *: *" + pattern3 + ",? *(?:" + pattern4 + ")?\\])";
		String pattern6 = "(\\[(?:PSOldGen|ParOldGen) *: *" + pattern3 + ",? *(?:" + pattern4 + ")?\\])";
		String pattern9 = "(\\[PSPermGen *: *" + pattern3 + "\\])";
		String pattern7 = pattern1 + ": *";
		String pattern10 =
				pattern7 + " *\\[(Full GC|GC) *" + pattern1 + "?:? *" + pattern5 + "? *" + "(?:" + pattern7 + ")?(?:" + pattern6 + ")? *" + pattern3
						+ ",? *(?:" + pattern9 + ",)? *" + pattern4 + "\\].*";

		pattern = Pattern.compile( pattern10 );
	}

}
