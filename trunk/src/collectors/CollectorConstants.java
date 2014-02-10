package src.collectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class CollectorConstants
{

	public static final ArrayList< String >										FIELDS					= new ArrayList< String >( Arrays.asList(
																												"GC Time", "Collection Type",
																												"Is total heap", "Young Generation",
																												"Tenured Generation", "Total heap" ) );

	public static final LinkedHashMap< String, String >							FIELD_MATCHORDER_MAP	= new LinkedHashMap< String, String >()
																										{
																											/**
																											 * 
																											 */
																											private static final long	serialVersionUID	=
																																									1L;

																											{
																												put( "GC Time(secs)", "1" );
																												put( "Collection Type", "10" );
																												put( "Is total heap", "2" );
																												put( "Young Generation", null );
																												put( "Tenured Generation", null );
																												put( "Total heap", null );

																											}
																										};

	private static LinkedHashMap< String, String >								YGMap					= new LinkedHashMap< String, String >()
																										{
																											/**
																											 * 
																											 */
																											private static final long	serialVersionUID	=
																																									1L;

																											{
																												put( "Size Before(K)", "5" );
																												put( "Size After(K)", "6" );
																												put( "Total Size(K)", "7" );
																												put( "Time Taken(secs)", "8" );
																											}
																										};

	private static LinkedHashMap< String, String >								OGMap					= new LinkedHashMap< String, String >()
																										{
																											/**
																											 * 
																											 */
																											private static final long	serialVersionUID	=
																																									1L;

																											{
																												put( "Size Before(K)", "11" );
																												put( "Size After(K)", "12" );
																												put( "Total Size(K)", "13" );
																												put( "Time Taken(secs)", "14" );
																											}
																										};

	private static LinkedHashMap< String, String >								TGMap					= new LinkedHashMap< String, String >()
																										{
																											/**
																											 * 
																											 */
																											private static final long	serialVersionUID	=
																																									1L;

																											{
																												put( "Size Before(K)", "15" );
																												put( "Size After(K)", "16" );
																												put( "Total Size(K)", "17" );
																												put( "Time Taken(secs)", "22" );
																											}
																										};

	public static final LinkedHashMap< String, LinkedHashMap< String, String >>	SUB_FIELDS				=
																												new LinkedHashMap< String, LinkedHashMap< String, String >>()
																												{
																													/**
																													 * 
																													 */
																													private static final long	serialVersionUID	=
																																											1L;

																													{
																														put( "Young Generation",
																																YGMap );
																														put( "Tenured Generation",
																																OGMap );
																														put( "Total heap", TGMap );
																													}
																												};

}
