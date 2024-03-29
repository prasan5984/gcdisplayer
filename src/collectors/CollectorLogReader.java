package collectors;

import java.util.List;

import data_structure.GCLogDataStructure;

public interface CollectorLogReader

{
	public void initialize();

	public String getCollectorNameDetails();
	
	public String getCollectorName();

	public boolean checkIfMatches( List< String > sampleLines );

	public void setValues( String line );

	public GCLogDataStructure getDataStructure();

	public boolean checkIfMatches( String line );

	public void setMatchString( String matchString );

}
