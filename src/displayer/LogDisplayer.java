package src.displayer;

import java.util.ArrayList;

import src.data_structure.Field;

public interface LogDisplayer
{
	public void initialize();

	public void writeFile( String filename );

	public void writeLine( ArrayList< Field > records );

}
