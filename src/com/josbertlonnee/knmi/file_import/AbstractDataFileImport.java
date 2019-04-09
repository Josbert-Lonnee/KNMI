package com.josbertlonnee.knmi.file_import;

import java.io.*;

import com.josbertlonnee.VERIFY;

abstract class AbstractDataFileImport<P extends AbstractParameter>
{
	protected KNMI_DatabaseManager databaseManager;
	
	/** Station ID to Station. */
	protected StationMap stations = new StationMap();
	
	/** Ordered list of parameters. */
	protected ParameterList<P> parameters = new ParameterList<P>();
	
	private int nextParameterId = 0;
	
	protected AbstractDataFileImport(KNMI_DatabaseManager databaseManager, String dataFileUrl) throws Exception
	{
		this.databaseManager = databaseManager;

		// Open the data file:
		File f = new File(dataFileUrl);
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isReader = new InputStreamReader(fis);
		BufferedReader reader = new BufferedReader(isReader);
		
		System.out.println("File \"" + dataFileUrl + "\" found and opened...");
		
		try {
			processDataFileHeader(reader);
			initializeDatabase();
			processDataFileData(reader);
		} finally {
			reader.close();
		}
		
		System.out.println("File \"" + dataFileUrl + "\" processed.");
	}
	
	protected final void processDataFileHeader(BufferedReader reader) throws Exception
	{
		// Read to the first line starting with "# STN":
		readUntilLine(reader, line -> (line.length() > 5 && line.substring(0, 5).equalsIgnoreCase("# STN")));
		
    	// For each non-empty line, create and add a new station on basis of the data in the line:
		processUntilEmptyLine(reader, (line) -> stations.add( new Station(line) ), /*orEOF=*/false);
		
		// Read to the first line starting with something:
		String bfplPrefix = getBeforeFirstParameterLinePrefix();
		readUntilLine(reader, line -> (line.length() > 10 && line.substring(0, bfplPrefix.length()).equalsIgnoreCase(bfplPrefix)));
		
    	// For each non-empty line, create and add a new parameter on basis of the data in the line:
		processUntilEmptyLine(reader, (line) -> { P p = createParameter(); p.id = ++nextParameterId; p.fromDataLine(line); parameters.add( p ); }, /*orEOF=*/false);
		
		// Read to the first line starting being only "# ":
		readUntilLine(reader, line -> line.equals("# "));
	}
	
	protected abstract String getBeforeFirstParameterLinePrefix();

	protected abstract P createParameter();
	
	private void processDataFileData(BufferedReader reader) throws Exception
	{
		DataLinesProcessor dataLinesProcessor = createDataLinesProcessor();
		
    	// For each non-empty measurements line, add data to the database:
		processUntilEmptyLine(reader, dataLinesProcessor, /*orEOF=*/true);
		databaseManager.flushInsertValues(); // And flush the data not yet inserted / buffered.
		System.out.println("All data inserted.");
	}
	
	/** Clear all data from the database and rewrite all meta-data (stations and parameters) to it. */
	protected void initializeDatabase() throws Exception
	{
    	for(AbstractParameter parameter : parameters)
    		parameter.writeTo(databaseManager);
		
    	for(Station station : stations.values())
    		station.writeTo(databaseManager);
    	
		System.out.println("Database initialized...");
	}

	public abstract class DataLinesProcessor implements LineAction
	{
		private static final int LOG_PER_NUMBER_OF_DATA_LINES = 1000;
		
		private int nLinesProcessed = 0;
		private int nLinesProcessedTotal = 0;
		
		@Override
		public final void processLine(String line) throws Exception
		{
	    	String[] lineParts = line.split(",");
	    	
	    	VERIFY.equals("data line number of parts", lineParts.length, getLeadingPartsNumber() + parameters.size());
	    	
	    	int index = 0;
	    	
	    	int stationId = Integer.parseInt( lineParts[index++].trim() );
	    	
	    	Station station = stations.get(stationId);
	    	
	    	int day = Integer.parseInt( lineParts[index++].trim() );
	    	int year = day / 10000;
	    	day -= year*10000;
	    	int month = day / 100;
	    	day -= month*100;
	    	
	    	if (year  < 1900) throw new RuntimeException("Unexpected low year: "      + year );
	    	if (month <    1) throw new RuntimeException("Unexpected low month: "     + month);
	    	if (month >   12) throw new RuntimeException("Unexpected high month: "    + month);
	    	if (day   <    1) throw new RuntimeException("Unexpected low date day: "  + day  );
	    	if (day   >   31) throw new RuntimeException("Unexpected high date day: " + day  );
	    	if (day == 31 && (month == 2 || month == 4 || month == 6 || month == 9 || month == 11))
	    		throw new RuntimeException("Unexpected date day of 31 for month: " + month);
	    	// TODO: Check February months in combination with leap years?
	    	
	    	int extra = readExtra(lineParts, index);
	    	if (extra != Integer.MIN_VALUE)
	    		++index;
	    	
	    	for(AbstractParameter parameter : parameters)
	    	{
	    		String valueString = lineParts[index++].trim();
	    		
	    		// No value for this parameter on this date for this station?
	    		if (valueString.isEmpty())
	    			continue;
	    		
				int value = Integer.parseInt( valueString  );
	    		
	    		if (value < -99999 || value > 99999)
	    			throw new RuntimeException("Value " + value + " out of bounds [-99999 .. 99999].");
	    		
	    		insertValue(station.id, year, month, day, parameter.id, extra, value);
	    	}
	    	
	    	countAndLog(stationId);
		}
		
		protected abstract int getLeadingPartsNumber();

		protected int readExtra(String[] lineParts, int index)
		{
			return Integer.MIN_VALUE; // No extra value
		}

		protected abstract void insertValue(int stationId, int year, int month, int day, int parameterId, int extra, int value) throws Exception;
		
	    /** Count the number of lines processed and do logging. */
		private void countAndLog(int stationId)
		{
	    	++nLinesProcessedTotal;
	    	if (++nLinesProcessed >= LOG_PER_NUMBER_OF_DATA_LINES) {
	    		System.out.println("Processed " + nLinesProcessedTotal + " lines. (Now station " + stationId + ".)");
	    		
	    		nLinesProcessed = 0;
	    	}
		}
	}
	
	protected abstract DataLinesProcessor createDataLinesProcessor();
	
	protected final String readUntilLine(BufferedReader reader, LineCondition condition) throws Exception
	{
	    for(String line = reader.readLine();; line = reader.readLine()) {
	    	if (line == null)
	    		throw new RuntimeException("Unexpected EOF.");
	    	
	    	if (condition.checkLine(line))
	    		return line;
	    }
	}
	
	protected final String processUntilEmptyLine(BufferedReader reader, LineAction action, boolean orEOF) throws Exception
	{
	    for(String line = reader.readLine();; line = reader.readLine()) {
	    	if (line == null) {
	    		if (orEOF)
	    			return null;
	    		
	    		throw new RuntimeException("Unexpected EOF.");
	    	}
	    	
	    	// Found an empty line?
	    	if (line.length() < 3)
	    		return line; // End of list of stations assumed.
	    	
	    	action.processLine(line);
	    }
	}
}
