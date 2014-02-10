package src.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import src.collectors.CollectorLogReader;
import src.collectors.SerialGCLogReader;
import src.displayer.LogDisplayer;
import src.displayer.XlsDisplayer;

public class GCLogViewer
{
	private static ArrayList< CollectorLogReader >	collectorReaders	= new ArrayList< CollectorLogReader >();
	private final static Charset					CHARACTER_SET		= Charset.forName( "UTF-8" );

	private static void initializeCollectors()
	{
		collectorReaders.add( new SerialGCLogReader() );
	}

	private String readLine()
	{
		Scanner scanner = new Scanner( System.in );
		String input = scanner.nextLine();
		return input;
	}

	private String showOptions()
	{
		System.out.println( "Enter full path of the GC Log:" );
		return readLine();

	}

	private CollectorLogReader chooseReader()
	{
		System.out.println( "Choose one of the collector below" );
		CollectorLogReader reader = null;
		int i = 1;

		HashMap< Integer, CollectorLogReader > collectorNoMap = new HashMap< Integer, CollectorLogReader >();

		for ( CollectorLogReader reader1 : collectorReaders )
		{
			System.out.println( i + " " + reader1.getCollectorName() );
			collectorNoMap.put( i, reader1 );
		}

		String input = readLine();

		if ( collectorNoMap.containsKey( input ) )
			reader = collectorNoMap.get( input );
		else
		{
			System.out.println( "Not a valid option." );
			reader = chooseReader();
		}

		return reader;
	}

	private void start()
	{
		String filePath = showOptions();
		File logFile = new File( filePath );
		readFile( logFile );

	}

	private void readFile( File file )
	{

		String fileContent = "";

		try
		{
			FileInputStream fileInputStream = new FileInputStream( file );
			FileChannel channel = fileInputStream.getChannel();

			ByteBuffer byteBuffer = ByteBuffer.allocate( 1024 );

			while ( channel.read( byteBuffer ) > 0 )
			{
				byteBuffer.flip();
				fileContent = fileContent + CHARACTER_SET.decode( byteBuffer ).toString();
				byteBuffer.clear();
			}

		}
		catch ( FileNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList< String > fileLines = new ArrayList<String> (Arrays.asList( fileContent.split( "(\r\n|\n\r|\r|\n)" ) ));
		ArrayList< String > sampleLines;

		if ( fileLines.size() > 10 )
			sampleLines = new ArrayList<String> (fileLines.subList( 0, 10 ));
		else
			sampleLines = fileLines;

		CollectorLogReader reader = null;

		for ( CollectorLogReader curReader : collectorReaders )
		{
			if ( curReader.checkIfMatches( sampleLines ) )
			{
				reader = curReader;
				break;
			}
		}

		if ( reader == null )
		{
			System.out.println( "Unable to detect the collector." );
			reader = chooseReader();
		}
		
		
		for ( String line : fileLines )
		{
			reader.setValues( line );
		}

		LogDisplayer displayer = new XlsDisplayer( reader.getDataStructure() );
		displayer.initialize();
		displayer.writeLine( reader.getDataStructure().getRecordsList() );
		displayer.writeFile( "GC_Format.xls" );

	}

	public static void main( String[] args )
	{
		initializeCollectors();
		GCLogViewer logDisplayer = new GCLogViewer();
		logDisplayer.start();
	}

}
