package com.josbertlonnee.knmi.file_import;

public class HourDataImport extends AbstractDataFileImport<HourParameter>
{
	private static final String DATA_FILE_PATH1 = "c:\\Users\\Josbert\\KNMI_hourly\\KNMI_20181128_hourly_1951-1975.txt";
	private static final String DATA_FILE_PATH2 = "c:\\Users\\Josbert\\KNMI_hourly\\KNMI_20181128_hourly_1976-1999.txt";
	private static final String DATA_FILE_PATH3 = "c:\\Users\\Josbert\\KNMI_hourly\\KNMI_20181128_hourly_2000-2018.txt";
	
	public static void main(String[] args) {
		try {
			KNMI_DatabaseManager databaseManager = new KNMI_DatabaseManager();
			//databaseManager.restoreDays(2200);
			databaseManager.regenerateDerivedPrecipitationDataOnly();
			
			new HourDataImport( databaseManager, DATA_FILE_PATH1 );
			new HourDataImport( databaseManager, DATA_FILE_PATH2 );
			new HourDataImport( databaseManager, DATA_FILE_PATH3 );
			
			// Delete all generated data and create it all new:
			System.out.println("Deriving data...");
			databaseManager.regenerateDerivedHourData();
			
			System.out.println("Done");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HourDataImport(KNMI_DatabaseManager databaseManager, String dataFileUrl) throws Exception
	{
		super(databaseManager, dataFileUrl);
	}
	
	@Override
	protected String getBeforeFirstParameterLinePrefix()
	{
		return "# HH";
	}

	/** Clear all data from the database and rewrite all meta-data (stations and parameters) to it. */
	@Override
	protected final void initializeDatabase() throws Exception
	{
		// TODO Only uncomment when necessary!!
		//databaseManager.clearAll();
		
		super.initializeDatabase();
	}
	
	@Override
	protected HourParameter createParameter()
	{
		return new HourParameter();
	}

	class DataLinesProcessor extends AbstractDataFileImport<HourParameter>.DataLinesProcessor
	{
		@Override
		protected int getLeadingPartsNumber()
		{
			return 3;
		}
		
		@Override
		protected int readExtra(String[] lineParts, int index)
		{
			return Integer.parseInt( lineParts[index].trim() );
		}
		
		@Override
		protected void insertValue(int stationId, int year, int month, int day, int parameterId, int hour, int value) throws Exception
		{
    		databaseManager.insertHourValueBuffered(stationId, year, month, day, parameterId, hour, value);
		}
	}
	
	@Override
	protected AbstractDataFileImport<HourParameter>.DataLinesProcessor createDataLinesProcessor()
	{
		return new DataLinesProcessor();
	}
}
