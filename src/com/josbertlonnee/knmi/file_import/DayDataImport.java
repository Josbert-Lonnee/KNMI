package com.josbertlonnee.knmi.file_import;

public class DayDataImport extends AbstractDataFileImport<DayParameter>
{
	private static final String DATA_FILE_PATH = "c:\\Users\\Josbert\\KNMI.txt";
	
	public static void main(String[] args) {
		try {
			KNMI_DatabaseManager databaseManager = new KNMI_DatabaseManager();
			databaseManager.regenerateDerivedMonthTemperaturesData();
			new DayDataImport( databaseManager, DATA_FILE_PATH );
			
			// Delete all generated data and create it all new:
			System.out.println("Deriving data...");
			databaseManager.regenerateDerivedDayData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Done");
	}

	private DayDataImport(KNMI_DatabaseManager databaseManager, String dataFileUrl) throws Exception
	{
		super(databaseManager, dataFileUrl);
	}
	
	@Override
	protected String getBeforeFirstParameterLinePrefix()
	{
		return "# YYYYMMDD";
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
	protected DayParameter createParameter()
	{
		return new DayParameter();
	}

	class DataLinesProcessor extends AbstractDataFileImport<DayParameter>.DataLinesProcessor
	{
		@Override
		protected int getLeadingPartsNumber()
		{
			return 2;
		}
		
		@Override
		protected void insertValue(int stationId, int year, int month, int day, int parameterId, int extra, int value) throws Exception
		{
			if (extra != Integer.MIN_VALUE)
				throw new IllegalArgumentException();
			
    		databaseManager.insertDayValueBuffered(stationId, year, month, day, parameterId, value);
		}
	}
	
	@Override
	protected AbstractDataFileImport<DayParameter>.DataLinesProcessor createDataLinesProcessor()
	{
		return new DataLinesProcessor();
	}
}
