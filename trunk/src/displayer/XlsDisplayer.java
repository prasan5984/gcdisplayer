package displayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import data_structure.DataStructureException;
import data_structure.Field;
import data_structure.GCLogDataStructure;


public class XlsDisplayer implements LogDisplayer
{
	private FileOutputStream			fOpStream;
	private HSSFWorkbook				workbook;
	private HSSFSheet					sheet;
	private HSSFCellStyle				style1, style2, style3;
	private GCLogDataStructure			dataStructure;
	private int							rowIndex		= 7;
	private HashMap< Integer, HSSFRow >	createdRowIndex	= new HashMap< Integer, HSSFRow >();

	public XlsDisplayer( GCLogDataStructure dataStructure2 )
	{
		this.dataStructure = dataStructure2;
	}

	private HSSFCellStyle getBasicStyle()
	{
		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setFontName( "Times New Roman" );
		font.setColor( HSSFFont.COLOR_NORMAL );
		style.setFont( font );

		style.setBorderBottom( CellStyle.BORDER_THIN );
		style.setBorderLeft( CellStyle.BORDER_THIN );
		style.setBorderRight( CellStyle.BORDER_THIN );
		style.setBorderTop( CellStyle.BORDER_THIN );
		style.setAlignment( CellStyle.ALIGN_CENTER );

		return style;

	}

	private void setStyles()
	{
		style1 = getBasicStyle();

		style2 = getBasicStyle();
		( style2.getFont( workbook ) ).setBoldweight( HSSFFont.BOLDWEIGHT_BOLD );
		( style2.getFont( workbook ) ).setFontHeightInPoints( (short)10 );

		style3 = getBasicStyle();
		( style3.getFont( workbook ) ).setBoldweight( HSSFFont.BOLDWEIGHT_BOLD );
		( style3.getFont( workbook ) ).setFontHeightInPoints( (short)12 );

	}

	private void setSheet()
	{
		sheet = workbook.createSheet( "Log Details" );
	}

	private void createSubCell( Field field, int rowStart, int colStart, HSSFCellStyle style )
	{
		HSSFRow row;
		if ( createdRowIndex.containsKey( rowStart ) )
			row = createdRowIndex.get( rowStart );
		else
		{
			row = sheet.createRow( rowStart );
			row.setRowStyle( style2 );
			createdRowIndex.put( rowStart, row );
		}

		for ( Field subField : field.getSubFields() )
		{
			if ( subField.getSubFieldsCount() > 0 )
				createSubCell( subField, rowStart + 1, colStart, style );

			int maxDepth = field.getMaxSubFieldDepth();
			int width = subField.getMaxSubFieldWidth();

			// Since do not require to merge if it has only 1 child
			if ( width > 0 )
				width = width - 1;

			int fieldDepth = subField.getMaxSubFieldDepth();

			int depth = 0;

			if ( subField.getSubFieldsCount() == 0 && maxDepth > ( fieldDepth + 1 ) )
				depth = maxDepth - 1;

			if ( width != 0 || depth != 0 )
			{
				CellRangeAddress rangeAddress = new CellRangeAddress( rowStart, rowStart + depth, colStart, colStart + width );
				sheet.addMergedRegion( rangeAddress );

			}

			HSSFCell cell = row.createCell( colStart );
			cell.setCellValue( subField.getValue() );
			cell.setCellStyle( style );

			colStart = colStart + width + 1;
		}

	}

	private void writeHeader() throws DataStructureException
	{
		// Main Header

		Field header = dataStructure.getField( GCLogDataStructure.HEADER_FIELD );

		int width = header.getMaxSubFieldWidth();

		CellRangeAddress mainHeaderRange = new CellRangeAddress( 2, 2, 2, 2 + width - 1 );
		sheet.addMergedRegion( mainHeaderRange );
		HSSFRow mainHeaderRow = sheet.createRow( 2 );
		mainHeaderRow.setRowStyle( style3 );
		HSSFCell mainHeader = mainHeaderRow.createCell( 2 );
		mainHeader.setCellValue( "Garbage Collection - Details" );
		mainHeader.setCellStyle( style3 );

		// Record Headers

		createSubCell( header, 5, 2, style2 );

		// Collector Type

		Field collectorType = dataStructure.getField( GCLogDataStructure.FILE_LEVEL_FIELDS, 0 );

		CellRangeAddress collectorTypeRange = new CellRangeAddress( 3, 3, 2, 5 );
		sheet.addMergedRegion( collectorTypeRange );
		HSSFRow collectorTypeRow = sheet.createRow( 3 );
		collectorTypeRow.setRowStyle( style2 );
		HSSFCell collectorTypeCell = collectorTypeRow.createCell( 2 );
		collectorTypeCell.setCellValue( "Collector Type: " + collectorType.getValue() );
		collectorTypeCell.setCellStyle( style2 );

	}

	@Override
	public void initialize()
	{
		workbook = new HSSFWorkbook();
		setStyles();
		setSheet();
		try
		{
			writeHeader();
		}
		catch ( DataStructureException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int writeFields( Field record, HSSFRow row, int colIndex )
	{
		for ( Field f : record.getSubFields() )
		{
			if ( f.getSubFieldsCount() != 0 )
				colIndex = writeFields( f, row, colIndex );
			else
			{
				HSSFCell cell = row.createCell( colIndex++ );
				cell.setCellValue( f.getValue() );
				cell.setCellStyle( style1 );
			}

		}
		return colIndex;

	}

	@Override
	public void writeLine( ArrayList< Field > records )
	{
		for ( Field record : records )
		{
			HSSFRow row = sheet.createRow( rowIndex++ );
			writeFields( record, row, 2 );
		}
	}

	@Override
	public void writeFile( String filename )
	{
		File xlsFile = new File( filename );
		try
		{
			for ( int i = 1; i < 20; i++ )
				sheet.autoSizeColumn( i, true );

			fOpStream = new FileOutputStream( xlsFile );
			workbook.write( fOpStream );
			fOpStream.close();

			System.out.println( "Output file is written to the path " + xlsFile.getAbsolutePath() );
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

	}
}
