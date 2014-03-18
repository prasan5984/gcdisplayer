package main;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import collectors.CMSReader;
import collectors.CollectorLogReader;
import collectors.ParNewReader;
import collectors.ParallelGCReader;
import collectors.SerialGCLogReader;
import displayer.BasicGCChartDisplayer;
import displayer.CMSChartDisplayerConstants;
import displayer.ChartDisplayer;

public class GCLogViewer implements ActionListener
{
	private static ArrayList< CollectorLogReader >					collectorReaders	= new ArrayList< CollectorLogReader >();

	private final static Charset									CHARACTER_SET		= Charset.forName( "UTF-8" );
	private static final int										CHART_TYPE			= 4;
	private static final int										BUTTON				= 5;
	private static final int										TEXT				= 6;
	private static final int										FINAL				= 7;
	private static JFileChooser										fileChooser			= new JFileChooser();
	private JPanel													startPanel;
	private JFrame													startFrame;
	private JTextPane												text;
	private JLabel													label;
	private ArrayList< String >										fileLines;

	private JMenuItem												chooseMenuItem;
	private JMenuItem												openMenuItem;

	private JMenuBar												menuBar;

	private JMenu													typeMenu;

	private static HashMap< CollectorLogReader, ChartDisplayer >	readerDisplayerMap	= new HashMap< CollectorLogReader, ChartDisplayer >();

	private static void initializeCollectors()
	{
		CollectorLogReader cmsReader = new CMSReader();
		CollectorLogReader serialReader = new SerialGCLogReader();
		CollectorLogReader parallelReader = new ParallelGCReader();
		CollectorLogReader parallelNewReader = new ParNewReader();

		collectorReaders.add( cmsReader );
		collectorReaders.add( serialReader );
		collectorReaders.add( parallelReader );
		collectorReaders.add( parallelNewReader );

		ChartDisplayer displayer = new BasicGCChartDisplayer();
		readerDisplayerMap.put( cmsReader, new ChartDisplayer() );
		readerDisplayerMap.put( serialReader, displayer );
		readerDisplayerMap.put( parallelReader, displayer );
		readerDisplayerMap.put( parallelNewReader, displayer );
	}

	private void chooseReader( String message )
	{
		String[] readerNameList = new String[4];

		for ( int i = 0; i < collectorReaders.size(); i++ )
			readerNameList[ i ] = collectorReaders.get( i ).getCollectorName();

		Object chosenOption =
				JOptionPane.showInputDialog( startFrame, message, "Reader Selection", JOptionPane.PLAIN_MESSAGE, null, readerNameList,
						readerNameList[ 0 ] );

		for ( int i = 0; i < readerNameList.length; i++ )
			if ( readerNameList[ i ].equals( chosenOption ) )
			{
				CollectorLogReader reader = collectorReaders.get( i );
				processLogFile( reader );
				break;
			}

	}

	private void showErrorDialog( String message )
	{
		JOptionPane.showMessageDialog( startFrame, message, "Error", JOptionPane.ERROR_MESSAGE );

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
			showErrorDialog( "Unable to find the file: " + file.getAbsolutePath() );
			System.exit( 0 );

		}
		catch ( IOException e )
		{
			showErrorDialog( "Error occured while reading the file: " + file.getAbsolutePath() );
			System.exit( 0 );
		}

		fileLines = new ArrayList< String >( Arrays.asList( fileContent.split( "(\r\n|\n\r|\r|\n)" ) ) );
		ArrayList< String > sampleLines;

		if ( fileLines.size() > 10 )
			sampleLines = new ArrayList< String >( fileLines.subList( 0, 10 ) );
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
			chooseReader( "Unable to detect the GC Type. Please select one of the below listed type" );
		else
			processLogFile( reader );
	}

	private void processLogFile( CollectorLogReader reader )
	{
		reader.initialize();

		for ( String line : fileLines )
			reader.setValues( line );

		buildChartPage( reader );

	}

	public void displayStartPage()
	{
		startFrame = new JFrame( "GC Log Viewer" );

		GridBagLayout layout = new GridBagLayout();
		startPanel = new JPanel( layout );

		startFrame.add( startPanel );

		new JButton( "Choose Log File" );

		label = new JLabel();

		text = new JTextPane();
		text.setEditable( false );
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 0;

		startFrame.setLocationRelativeTo( null );

		startFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		startFrame.setSize( 500, 500 );

		menuBar = new JMenuBar();
		c.anchor = GridBagConstraints.FIRST_LINE_START;

		JMenu fileMenu = new JMenu( "File" );
		menuBar.add( fileMenu );

		openMenuItem = new JMenuItem( "Open" );
		fileMenu.add( openMenuItem );

		typeMenu = new JMenu( "GC Type" );

		chooseMenuItem = new JMenuItem( "Choose..." );
		typeMenu.add( chooseMenuItem );
		chooseMenuItem.addActionListener( this );

		startFrame.setJMenuBar( menuBar );

		openMenuItem.addActionListener( this );

		JTextPane textPane = new JTextPane();
		textPane.setEditable( false );
		StyledDocument doc = textPane.getStyledDocument();

		try
		{
			Style s = StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE );
			StyleConstants.setFontFamily( s, "Times" );
			StyleConstants.setFontSize( s, 13 );
			StyleConstants.setBold( s, true );
			StyleConstants.setItalic( s, true );
			StyleConstants.setAlignment( s, StyleConstants.ALIGN_CENTER );

			doc.insertString( doc.getLength(), " Welcome to Garbage Collection Log Viewer", s );

			StyleConstants.setBold( s, false );
			StyleConstants.setAlignment( s, StyleConstants.ALIGN_LEFT );

			doc.insertString(
					doc.getLength(),
					"\nThis application is for anlaysing HotSpot's GC Logs.\n The Collector Type currently supported are \n 1. Concurrent Mark and Sweep \n 2. Serial \n 3. Parallel \n 4. Parallel Old \n Open a log file to proceed (using File -> Open)",
					s );

		}
		catch ( BadLocationException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startPanel.add( textPane );

		startFrame.setExtendedState( JFrame.MAXIMIZED_BOTH );
		//startFrame.setResizable( false );
		startFrame.setVisible( true );
	}

	public static void main( String[] args )
	{
		initializeCollectors();
		GCLogViewer logDisplayer = new GCLogViewer();
		logDisplayer.displayStartPage();
	}

	public void buildChartPage( CollectorLogReader reader )
	{
		ChartDisplayer displayer = readerDisplayerMap.get( reader ).getCopy();
		displayer.initialize();
		displayer.writeLine( reader.getDataStructure().getRecordsList() );
		displayer.writeFile( null );

		JFrame frame = new JFrame( "GC Log Viewer" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.pack();
		frame.setLocationRelativeTo( null );

		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel( layout );
		frame.add( panel );

		label.setText( "Collector Type: " + reader.getCollectorNameDetails() );

		panel.add( label, getConstraints( TEXT ) );
		//panel.add( button, getConstraints( BUTTON ) );

		panel.add( displayer.getChartPanel(), getConstraints( CHART_TYPE ) );

		ArrayList< JCheckBox > checkBoxList = displayer.getLegendCheckBoxes();

		for ( int i = 0; i < checkBoxList.size(); i++ )
		{
			JCheckBox checkBox = checkBoxList.get( i );
			if ( i + 1 == checkBoxList.size() )
				panel.add( checkBox, getConstraints( FINAL ) );
			else
				panel.add( checkBox, getConstraints( displayer.getLevel( checkBox ) ) );
		}

		//startFrame.dispose();
		startFrame.remove( startPanel );
		startPanel = panel;
		startFrame.add( panel );
		startFrame.setCursor( null );
		startFrame.setVisible( true );

	}

	private GridBagConstraints getConstraints( int type )
	{
		GridBagConstraints c = new GridBagConstraints();

		if ( type == CHART_TYPE )
		{
			c.fill = GridBagConstraints.BOTH;
			c.gridy = 1;
			c.gridwidth = 2;
			c.weighty = 0.3;
			c.weightx = 0.3;
			c.insets = new Insets( 50, 10, 50, 10 );
			c.anchor = GridBagConstraints.CENTER;
			c.gridheight = GridBagConstraints.REMAINDER;
		}
		else if ( type == TEXT )
		{
			//c.weightx = 0.05;
			//c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets( 20, 30, 30, 10 );
			c.anchor = GridBagConstraints.LINE_START;
		}

		else if ( type == BUTTON )
		{
			c.gridx = 1;
			//c.weightx = 0.01;
			//c.weightx = 0.05;
			//c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets( 20, 10, 20, 30 );
			c.anchor = GridBagConstraints.LINE_START;
		}

		else
		{
			c.gridx = 2;
			c.anchor = GridBagConstraints.LINE_START;

			if ( type == CMSChartDisplayerConstants.HIERARCHY_LEVEL_ONE )
			{
				c.weighty = 0.1;
				c.gridy = 1;
				c.anchor = GridBagConstraints.LAST_LINE_START;
			}

			if ( type == CMSChartDisplayerConstants.HIERARCHY_LEVEL_TWO )
				c.insets = new Insets( 0, 5, 0, 0 );

			if ( type == CMSChartDisplayerConstants.HIERARCHY_LEVEL_THREE )
				c.insets = new Insets( 0, 10, 0, 0 );

			if ( type == FINAL )
				c.insets = new Insets( 0, 10, 50, 0 );
		}
		return c;
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		Object actionComponent = e.getSource();

		if ( actionComponent.equals( openMenuItem ) )
		{
			int state = fileChooser.showOpenDialog( (Component)e.getSource() );
			if ( state == JFileChooser.APPROVE_OPTION )
			{
				File logFile = fileChooser.getSelectedFile();
				if ( !logFile.exists() )
				{
					showErrorDialog( "File not found: " + logFile.getAbsolutePath() );
					return;
				}
				Cursor waitCursor = new Cursor( Cursor.WAIT_CURSOR );
				startFrame.setCursor( waitCursor );

				readFile( logFile );
				menuBar.add( typeMenu );
			}

		}

		else if ( actionComponent.equals( chooseMenuItem ) )
			chooseReader( "Choose one of the below collector type" );
	}
}
