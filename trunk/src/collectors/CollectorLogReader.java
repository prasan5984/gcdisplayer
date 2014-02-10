package src.collectors;

import java.util.List;

import src.data_structure.GCLogDataStructure;

public interface CollectorLogReader
{
	public String getCollectorName();

	public boolean checkIfMatches( List< String > sampleLines );

	public void setValues( String line );
	
	public GCLogDataStructure getDataStructure();

}
