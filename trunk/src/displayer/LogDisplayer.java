package displayer;

import java.util.ArrayList;

import data_structure.Field;


public interface LogDisplayer
{
	public void initialize();

	public void writeFile( String filename );

	public void writeLine( ArrayList< Field > records );

}
