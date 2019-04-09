package com.josbertlonnee.knmi.file_import;

import java.sql.*;
import java.util.*;

import com.josbertlonnee.DatabaseManager;

class KNMI_DatabaseManager extends DatabaseManager
{
	private static int PSTMT_GET_LAST_INSERT_ID = 0;
	
	private static int PSTMT_DELETE_DAY = 1;
	private static int PSTMT_INSERT_DAY = 2;
	
	private static int PSTMT_DELETE_STATION   = 3;
	private static int PSTMT_INSERT_STATION   = 4;
	
	private static int PSTMT_DELETE_DAY_VALUE     = 5;
	private static int PSTMT_INSERT_DAY_VALUE     = 6;
	private static int PSTMT_INSERT_DAY_VALUE_MUL = 7;
	
	private static int PSTMT_INSERT_DAY_PARAMETER = 8;
	private static int PSTMT_DELETE_DAY_PARAMETER = 9;
	
	private static int PSTMT_DELETE_HOUR_PARAMETER = 10;
	private static int PSTMT_INSERT_HOUR_PARAMETER = 11;
	
	private static int PSTMT_DELETE_HOUR_VALUE     = 12;
	private static int PSTMT_INSERT_HOUR_VALUE     = 13;
	private static int PSTMT_INSERT_HOUR_VALUE_MUL = 14;
	
	private static int PSTMT_DELETE_MONTH_AVERAGE_TEMPS               = 15;
	private static int PSTMT_GENERATE_MONTH_AVERAGE_TEMPS_DAY_AVERAGE = 16;
	private static int PSTMT_GENERATE_MONTH_AVERAGE_TEMPS_DAY_MINIMUM = 17;
	private static int PSTMT_GENERATE_MONTH_AVERAGE_TEMPS_DAY_MAXIMUM = 18;
	
	private static int PSTMT_DELETE_MONTH_WIND_DIRS   = 19;
	private static int PSTMT_GENERATE_MONTH_WIND_DIRS = 20;
	
	private static int PSTMT_GENERATE_STATION_DAY_TEMPS = 21;
	
	private static int PSTMT_DELETE_MONTH_PRECIPITATION                 = 22;
	private static int PSTMT_GENERATE_MONTH_PRECIPITATION               = 23;
	private static int PSTMT_GENERATE_MONTH_PRECIPITATION_EXTREME_DAYS  = 24;
	private static int PSTMT_GENERATE_MONTH_PRECIPITATION_EXTREME_HOURS = 25;
	
	private static int INSERT_VALUE_MUL_NUM = 100;
	
	public KNMI_DatabaseManager() throws SQLException
	{
		super("knmi", "knmi", "knmi123");
		
		createPreparedStatement(PSTMT_GET_LAST_INSERT_ID, "SELECT LAST_INSERT_ID()");
		
		createPreparedStatement(PSTMT_DELETE_HOUR_VALUE    , "DELETE FROM hour_value"    );
		createPreparedStatement(PSTMT_DELETE_DAY_VALUE     , "DELETE FROM day_value"     );
		createPreparedStatement(PSTMT_DELETE_HOUR_PARAMETER, "DELETE FROM hour_parameter");
		createPreparedStatement(PSTMT_DELETE_DAY_PARAMETER , "DELETE FROM day_parameter" );
		createPreparedStatement(PSTMT_DELETE_STATION       , "DELETE FROM station"       );
		createPreparedStatement(PSTMT_DELETE_DAY           , "DELETE FROM day"           );
		
		createPreparedStatement(PSTMT_INSERT_DAY, "INSERT IGNORE INTO day(day_number,year,month,day) values(?,?,?,?)");
		
		createPreparedStatement(PSTMT_INSERT_STATION, "INSERT IGNORE INTO station(stationid,longitude,latitude,altitude,name) VALUES(?,?,?,?,?)");
		
		createPreparedStatement(PSTMT_INSERT_DAY_PARAMETER, "INSERT IGNORE INTO day_parameter(day_parameterid,code,description) VALUES(?,?,?)");
		
		// Generate the insert statements for single and multiple inserts of values:
		String insertValueSql = "INSERT IGNORE INTO day_value(stationid,year,month,day,day_parameterid,value) VALUES(?,?,?,?,?,?)";
		createPreparedStatement(PSTMT_INSERT_DAY_VALUE, insertValueSql);
		for(int i=1; i<INSERT_VALUE_MUL_NUM; ++i)
			insertValueSql += ",(?,?,?,?,?,?)";
		createPreparedStatement(PSTMT_INSERT_DAY_VALUE_MUL, insertValueSql);
		
		createPreparedStatement(PSTMT_INSERT_HOUR_PARAMETER, "INSERT IGNORE INTO hour_parameter(hour_parameterid,code,description) VALUES(?,?,?)");
		
		// Generate the insert statements for single and multiple inserts of values:
		insertValueSql = "INSERT IGNORE INTO hour_value(stationid,year,month,day,hour_parameterid,hour,value) VALUES(?,?,?,?,?,?,?)";
		createPreparedStatement(PSTMT_INSERT_HOUR_VALUE, insertValueSql);
		for(int i=1; i<INSERT_VALUE_MUL_NUM; ++i)
			insertValueSql += ",(?,?,?,?,?,?,?)";
		createPreparedStatement(PSTMT_INSERT_HOUR_VALUE_MUL, insertValueSql);
		
		// Month average temperatures:
		createPreparedStatement(PSTMT_DELETE_MONTH_AVERAGE_TEMPS, "DELETE FROM month_average_temp");
		createPreparedStatement(PSTMT_GENERATE_MONTH_AVERAGE_TEMPS_DAY_AVERAGE,
				"INSERT IGNORE INTO month_average_temp(year,month,day_average) " +
				"SELECT year,month,SUM(value)/COUNT(value)/10 " +
				"FROM day_value " +
				"WHERE day_parameterid=10 AND (" +
					"year<(SELECT max(year) FROM day_value WHERE day_parameterid=10) OR " +
					"month<(SELECT MAX(month) FROM day_value WHERE day_parameterid=10 AND year=(SELECT MAX(year) FROM day_value WHERE day_parameterid=10)) ) " +
				"GROUP BY year, month " +
				"ON DUPLICATE KEY UPDATE day_average=VALUES(day_average)");
		createPreparedStatement(PSTMT_GENERATE_MONTH_AVERAGE_TEMPS_DAY_MINIMUM,
				"INSERT IGNORE INTO month_average_temp(year,month,day_minimum) " +
				"SELECT year,month,SUM(value)/COUNT(value)/10 " +
				"FROM day_value " +
				"WHERE day_parameterid=11 AND (" +
					"year<(SELECT max(year) FROM day_value WHERE day_parameterid=11) OR " +
					"month<(SELECT MAX(month) FROM day_value WHERE day_parameterid=11 AND year=(SELECT MAX(year) FROM day_value WHERE day_parameterid=11)) ) " +
				"GROUP BY year, month " +
				"ON DUPLICATE KEY UPDATE day_minimum=VALUES(day_minimum)");
		createPreparedStatement(PSTMT_GENERATE_MONTH_AVERAGE_TEMPS_DAY_MAXIMUM,
				"INSERT IGNORE INTO month_average_temp(year,month,day_maximum) " +
				"SELECT year,month,SUM(value)/COUNT(value)/10 " +
				"FROM day_value " +
				"WHERE day_parameterid=13 AND (" +
					"year<(SELECT max(year) FROM day_value WHERE day_parameterid=13) OR " +
					"month<(SELECT MAX(month) FROM day_value WHERE day_parameterid=13 AND year=(SELECT MAX(year) FROM day_value WHERE day_parameterid=13)) ) " +
				"GROUP BY year, month " +
				"ON DUPLICATE KEY UPDATE day_maximum=VALUES(day_maximum)");
		
		// Month wind directions:
		createPreparedStatement(PSTMT_DELETE_MONTH_WIND_DIRS, "DELETE FROM month_wind_dir_share");
		createPreparedStatement(PSTMT_GENERATE_MONTH_WIND_DIRS,
				"INSERT INTO month_wind_dir_share" + 
				"SELECT year, month," + 
					"SUM(value=0) / COUNT(value) AS V," + 
					"SUM(value<>0 AND (value<=22 OR value>349)) / COUNT(value) AS N," + 
					"SUM(value> 22 AND value<= 67) / COUNT(value) AS NE," + 
					"SUM(value> 67 AND value<=112) / COUNT(value) AS E," + 
					"SUM(value>112 AND value<=157) / COUNT(value) AS SE," + 
					"SUM(value>157 AND value<=202) / COUNT(value) AS S," + 
					"SUM(value>202 AND value<=247) / COUNT(value) AS SW," + 
					"SUM(value>247 AND value<=292) / COUNT(value) AS W," + 
					"SUM(value>292 AND value<=349) / COUNT(value) AS NW" + 
				"FROM day_value " +
				"WHERE day_parameterid=1 " +
				"GROUP BY year, month " +
				"ORDER BY year, month");
		
		createPreparedStatement(PSTMT_GENERATE_STATION_DAY_TEMPS,
				"INSERT IGNORE INTO station_day_temps(day_number,stationid,t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16,t17,t18,t19,t20,t21,t22,t23,t24) " + 
				"SELECT d.day_number AS day_number,s.stationid AS stationid" + 
					",t1 .value AS t1" + 
					",t2 .value AS t2" + 
					",t3 .value AS t3" + 
					",t4 .value AS t4" + 
					",t5 .value AS t5" + 
					",t6 .value AS t6" + 
					",t7 .value AS t7" + 
					",t8 .value AS t8" + 
					",t9 .value AS t9" + 
					",t10.value AS t10" + 
					",t11.value AS t11" + 
					",t12.value AS t12" + 
					",t13.value AS t13" + 
					",t14.value AS t14" + 
					",t15.value AS t15" + 
					",t16.value AS t16" + 
					",t17.value AS t17" + 
					",t18.value AS t18" + 
					",t19.value AS t19" + 
					",t20.value AS t20" + 
					",t21.value AS t21" + 
					",t22.value AS t22" + 
					",t23.value AS t23" + 
					",t24.value AS t24" + 
				"FROM station AS s " + 
				"INNER JOIN day AS d " + 
				"INNER JOIN hour_value AS t1  ON t1 .hour_parameterid=5 AND t1 .hour= 1 AND t1 .stationid=s.stationid AND t1 .year=d.year AND t1 .month=d.month AND t1 .day=d.day " + 
				"LEFT  JOIN hour_value AS t2  ON t2 .hour_parameterid=5 AND t2 .hour= 2 AND t2 .stationid=s.stationid AND t2 .year=d.year AND t2 .month=d.month AND t2 .day=d.day " + 
				"LEFT  JOIN hour_value AS t3  ON t3 .hour_parameterid=5 AND t3 .hour= 3 AND t3 .stationid=s.stationid AND t3 .year=d.year AND t3 .month=d.month AND t3 .day=d.day " + 
				"LEFT  JOIN hour_value AS t4  ON t4 .hour_parameterid=5 AND t4 .hour= 4 AND t4 .stationid=s.stationid AND t4 .year=d.year AND t4 .month=d.month AND t4 .day=d.day " + 
				"LEFT  JOIN hour_value AS t5  ON t5 .hour_parameterid=5 AND t5 .hour= 5 AND t5 .stationid=s.stationid AND t5 .year=d.year AND t5 .month=d.month AND t5 .day=d.day " + 
				"LEFT  JOIN hour_value AS t6  ON t6 .hour_parameterid=5 AND t6 .hour= 6 AND t6 .stationid=s.stationid AND t6 .year=d.year AND t6 .month=d.month AND t6 .day=d.day " + 
				"LEFT  JOIN hour_value AS t7  ON t7 .hour_parameterid=5 AND t7 .hour= 7 AND t7 .stationid=s.stationid AND t7 .year=d.year AND t7 .month=d.month AND t7 .day=d.day " + 
				"LEFT  JOIN hour_value AS t8  ON t8 .hour_parameterid=5 AND t8 .hour= 8 AND t8 .stationid=s.stationid AND t8 .year=d.year AND t8 .month=d.month AND t8 .day=d.day " + 
				"LEFT  JOIN hour_value AS t9  ON t9 .hour_parameterid=5 AND t9 .hour= 9 AND t9 .stationid=s.stationid AND t9 .year=d.year AND t9 .month=d.month AND t9 .day=d.day " + 
				"LEFT  JOIN hour_value AS t10 ON t10.hour_parameterid=5 AND t10.hour=10 AND t10.stationid=s.stationid AND t10.year=d.year AND t10.month=d.month AND t10.day=d.day " + 
				"LEFT  JOIN hour_value AS t11 ON t11.hour_parameterid=5 AND t11.hour=11 AND t11.stationid=s.stationid AND t11.year=d.year AND t11.month=d.month AND t11.day=d.day " + 
				"LEFT  JOIN hour_value AS t12 ON t12.hour_parameterid=5 AND t12.hour=12 AND t12.stationid=s.stationid AND t12.year=d.year AND t12.month=d.month AND t12.day=d.day " + 
				"LEFT  JOIN hour_value AS t13 ON t13.hour_parameterid=5 AND t13.hour=13 AND t13.stationid=s.stationid AND t13.year=d.year AND t13.month=d.month AND t13.day=d.day " + 
				"LEFT  JOIN hour_value AS t14 ON t14.hour_parameterid=5 AND t14.hour=14 AND t14.stationid=s.stationid AND t14.year=d.year AND t14.month=d.month AND t14.day=d.day " + 
				"LEFT  JOIN hour_value AS t15 ON t15.hour_parameterid=5 AND t15.hour=15 AND t15.stationid=s.stationid AND t15.year=d.year AND t15.month=d.month AND t15.day=d.day " + 
				"LEFT  JOIN hour_value AS t16 ON t16.hour_parameterid=5 AND t16.hour=16 AND t16.stationid=s.stationid AND t16.year=d.year AND t16.month=d.month AND t16.day=d.day " + 
				"LEFT  JOIN hour_value AS t17 ON t17.hour_parameterid=5 AND t17.hour=17 AND t17.stationid=s.stationid AND t17.year=d.year AND t17.month=d.month AND t17.day=d.day " + 
				"LEFT  JOIN hour_value AS t18 ON t18.hour_parameterid=5 AND t18.hour=18 AND t18.stationid=s.stationid AND t18.year=d.year AND t18.month=d.month AND t18.day=d.day " + 
				"LEFT  JOIN hour_value AS t19 ON t19.hour_parameterid=5 AND t19.hour=19 AND t19.stationid=s.stationid AND t19.year=d.year AND t19.month=d.month AND t19.day=d.day " + 
				"LEFT  JOIN hour_value AS t20 ON t20.hour_parameterid=5 AND t20.hour=20 AND t20.stationid=s.stationid AND t20.year=d.year AND t20.month=d.month AND t20.day=d.day " + 
				"LEFT  JOIN hour_value AS t21 ON t21.hour_parameterid=5 AND t21.hour=21 AND t21.stationid=s.stationid AND t21.year=d.year AND t21.month=d.month AND t21.day=d.day " + 
				"LEFT  JOIN hour_value AS t22 ON t22.hour_parameterid=5 AND t22.hour=22 AND t22.stationid=s.stationid AND t22.year=d.year AND t22.month=d.month AND t22.day=d.day " + 
				"LEFT  JOIN hour_value AS t23 ON t23.hour_parameterid=5 AND t23.hour=23 AND t23.stationid=s.stationid AND t23.year=d.year AND t23.month=d.month AND t23.day=d.day " + 
				"LEFT  JOIN hour_value AS t24 ON t24.hour_parameterid=5 AND t24.hour=24 AND t24.stationid=s.stationid AND t24.year=d.year AND t24.month=d.month AND t24.day=d.day");


		// Monthly Precipitation:
		createPreparedStatement(PSTMT_DELETE_MONTH_PRECIPITATION, "DELETE FROM month_precipitation");
		createPreparedStatement(PSTMT_GENERATE_MONTH_PRECIPITATION,
				"INSERT INTO month_precipitation(year,month,days_registered,days_total) " + 
				"SELECT year,month,COUNT(v.value),SUM(v.value) " +
				"FROM day_value AS v " +
				"WHERE v.day_parameterid=21 " +
				"GROUP BY year,month " +
				"ORDER BY year,month");

		createPreparedStatement(PSTMT_GENERATE_MONTH_PRECIPITATION_EXTREME_DAYS,
				"INSERT INTO month_precipitation(year,month,days_high_registered,days_high_over_30,days_high_over_40,days_high_over_50,days_high_over_60,days_high_over_70,days_high_over_80,days_high_over_90,days_high_over_100) " +
				"SELECT v.year,v.month,COUNT(v.value)," +
					"SUM(v.value> 30)," +
					"SUM(v.value> 40)," +
					"SUM(v.value> 50)," +
					"SUM(v.value> 60)," +
					"SUM(v.value> 70)," +
					"SUM(v.value> 80)," +
					"SUM(v.value> 90)," +
					"SUM(v.value>100) " +
				"FROM day_value AS v " +
				"WHERE v.day_parameterid=22 " +
				"GROUP BY v.year,v.month " +
				"ON DUPLICATE KEY UPDATE days_high_registered=VALUES(days_high_registered)" +
								",days_high_over_30 =VALUES(days_high_over_30 )" +
								",days_high_over_40 =VALUES(days_high_over_40 )" +
								",days_high_over_50 =VALUES(days_high_over_50 )" +
								",days_high_over_60 =VALUES(days_high_over_60 )" +
								",days_high_over_70 =VALUES(days_high_over_70 )" +
								",days_high_over_80 =VALUES(days_high_over_80 )" +
								",days_high_over_90 =VALUES(days_high_over_90 )" +
								",days_high_over_100=VALUES(days_high_over_100)");

		createPreparedStatement(PSTMT_GENERATE_MONTH_PRECIPITATION_EXTREME_HOURS,
				"INSERT INTO month_precipitation(year,month,hours_registered,hours_over_30,hours_over_40,hours_over_50,hours_over_60,hours_over_70,hours_over_80,hours_over_90,hours_over_100) " + 
				"SELECT v.year,v.month,COUNT(v.value)," +
						"SUM(v.value> 30)," +
						"SUM(v.value> 40)," +
						"SUM(v.value> 50)," +
						"SUM(v.value> 60)," +
						"SUM(v.value> 70)," +
						"SUM(v.value> 80)," +
						"SUM(v.value> 90)," +
						"SUM(v.value>100) " +
				"FROM hour_value AS v " +
				"WHERE v.hour_parameterid=11 " +
				"GROUP BY v.year,v.month " +
				"ON DUPLICATE KEY UPDATE hours_registered=VALUES(hours_registered)" +
						",hours_over_30 =VALUES(hours_over_30 )" +
						",hours_over_40 =VALUES(hours_over_40 )" +
						",hours_over_50 =VALUES(hours_over_50 )" +
						",hours_over_60 =VALUES(hours_over_60 )" +
						",hours_over_70 =VALUES(hours_over_70 )" +
						",hours_over_80 =VALUES(hours_over_80 )" +
						",hours_over_90 =VALUES(hours_over_90 )" +
						",hours_over_100=VALUES(hours_over_100)");
		
		// select count(*) from month_precipitation where days_registered is null OR days_total is null;
		
		/*
		SELECT month, sum(days_high_over_100) / sum(days_high_registered) AS f
		FROM month_precipitation
		WHERE year>=1901 AND year<1941
		GROUP BY month
		ORDER BY month
		
		SELECT year,month, days_high_over_100, days_high_registered
		FROM month_precipitation
		WHERE year>=1901 AND year<1941
		GROUP BY month
		ORDER BY month
		*/
	}
	
	public void restoreDays(int maxYear) throws SQLException
	{
		if (maxYear < 1900)
			throw new IllegalArgumentException();
		
		//getPStmt(PSTMT_DELETE_DAY).executeUpdate();
		
		PreparedStatement pStmt = getPStmt(PSTMT_INSERT_DAY);
		
		GregorianCalendar gc = new GregorianCalendar(1900, 0, 1);
		for(int dayNumber=1;; ++dayNumber) {
			int year = gc.get(Calendar.YEAR);
			if (year >= maxYear)
				break;
			
			int month = gc.get(Calendar.MONTH) + 1;
			int day   = gc.get(Calendar.DAY_OF_MONTH);
			
			pStmt.setInt(1, dayNumber);
			pStmt.setInt(2, year);
			pStmt.setInt(3, month);
			pStmt.setInt(4, day);
			pStmt.executeUpdate();
			
			// Next day:
			gc.add(Calendar.DATE, 1);
		}
	}
	
	public void clearHourData() throws SQLException
	{
		getPStmt(PSTMT_DELETE_HOUR_VALUE).executeUpdate();
	}

	public void clearDayData() throws SQLException
	{
		getPStmt(PSTMT_DELETE_DAY_VALUE    ).executeUpdate();
		getPStmt(PSTMT_DELETE_DAY_PARAMETER).executeUpdate();
	}

	public void clearAll() throws SQLException
	{
		clearHourData();
		clearDayData();
		
		getPStmt(PSTMT_DELETE_STATION).executeUpdate();
	}
	
	public void insertStation(int stationId, double longitude, double latitude, double altitude, String name) throws SQLException
	{
		PreparedStatement pStmt = getPStmt(PSTMT_INSERT_STATION);
		pStmt.setInt(1, stationId);
		pStmt.setDouble(2, longitude);
		pStmt.setDouble(3, latitude);
		pStmt.setDouble(4, altitude);
		pStmt.setString(5, name);
		pStmt.executeUpdate();
	}

	public void insertDayParameter(int day_parameterId, String code, String description) throws SQLException
	{
		PreparedStatement pStmt = getPStmt(PSTMT_INSERT_DAY_PARAMETER);
		pStmt.setInt(1, day_parameterId);
		pStmt.setString(2, code);
		pStmt.setString(3, description);
		pStmt.executeUpdate();
	}

	public void insertHourParameter(int hour_parameterId, String code, String description) throws SQLException
	{
		PreparedStatement pStmt = getPStmt(PSTMT_INSERT_HOUR_PARAMETER);
		pStmt.setInt(1, hour_parameterId);
		pStmt.setString(2, code);
		pStmt.setString(3, description);
		pStmt.executeUpdate();
	}
	
	private static class InsertDayValueData
	{
		protected int stationId;
		protected int year;
		protected int month;
		protected int day;
		protected int parameterId;
		
		protected int value;
		
		protected InsertDayValueData(int stationId, int year, int month, int day, int parameterId, int value)
		{
			this.stationId   = stationId;
			this.year        = year;
			this.month       = month;
			this.day         = day;
			this.parameterId = parameterId;
			this.value       = value;
		}
	}
	
	private ArrayList<InsertDayValueData> toInsertDayValues = new ArrayList<InsertDayValueData>(INSERT_VALUE_MUL_NUM + 1);
	
	public void insertDayValueBuffered(int stationId, int year, int month, int day, int parameterId, int value) throws SQLException
	{
		toInsertDayValues.add(new InsertDayValueData(stationId, year, month, day, parameterId, value));
		
		// Buffer full?
		if (toInsertDayValues.size() >= INSERT_VALUE_MUL_NUM) {
			if (toInsertDayValues.size() > INSERT_VALUE_MUL_NUM)
				throw new IllegalStateException();
			
			// Write the buffer in one batch to the database:
			PreparedStatement pStmt = getPStmt(PSTMT_INSERT_DAY_VALUE_MUL);
			int index = 0;
			for(InsertDayValueData data : toInsertDayValues) {
				pStmt.setInt(++index, data.stationId);
				pStmt.setInt(++index, data.year);
				pStmt.setInt(++index, data.month);
				pStmt.setInt(++index, data.day);
				pStmt.setInt(++index, data.parameterId);
				pStmt.setInt(++index, data.value);
			}
			pStmt.executeUpdate();
			
			// Continue with a clear buffer:
			toInsertDayValues.clear();
		}
	}
	
	private static class InsertHourValueData extends InsertDayValueData
	{
		protected int hour;
		
		protected InsertHourValueData(int stationId, int year, int month, int day, int parameterId, int hour, int value)
		{
			super(stationId, year, month, day, parameterId, value);
			
			this.hour = hour;
		}
	}
	
	private ArrayList<InsertHourValueData> toInsertHourValues = new ArrayList<InsertHourValueData>(INSERT_VALUE_MUL_NUM + 1);
	
	public void insertHourValueBuffered(int stationId, int year, int month, int day, int parameterId, int hour, int value) throws SQLException
	{
		toInsertHourValues.add(new InsertHourValueData(stationId, year, month, day, parameterId, hour, value));
		
		// Buffer full?
		if (toInsertHourValues.size() >= INSERT_VALUE_MUL_NUM) {
			if (toInsertHourValues.size() > INSERT_VALUE_MUL_NUM)
				throw new IllegalStateException();
			
			// Write the buffer in one batch to the database:
			PreparedStatement pStmt = getPStmt(PSTMT_INSERT_HOUR_VALUE_MUL);
			int index = 0;
			for(InsertHourValueData data : toInsertHourValues) {
				pStmt.setInt(++index, data.stationId);
				pStmt.setInt(++index, data.year);
				pStmt.setInt(++index, data.month);
				pStmt.setInt(++index, data.day);
				pStmt.setInt(++index, data.parameterId);
				pStmt.setInt(++index, data.hour);
				pStmt.setInt(++index, data.value);
			}
			pStmt.executeUpdate();
			
			// Continue with a clear buffer:
			toInsertHourValues.clear();
		}
	}
	
	public void flushInsertValues() throws SQLException
	{
		// Day data:
		for(InsertDayValueData data : toInsertDayValues) {
			PreparedStatement pStmt = getPStmt(PSTMT_INSERT_HOUR_VALUE);
			pStmt.setInt(1, data.stationId);
			pStmt.setInt(2, data.year);
			pStmt.setInt(3, data.month);
			pStmt.setInt(4, data.day);
			pStmt.setInt(5, data.parameterId);
			pStmt.setInt(6, data.value);
			pStmt.executeUpdate();
		}
		
		// Continue with a clear buffer:
		toInsertDayValues.clear();
		
		
		// Hour data:
		for(InsertHourValueData data : toInsertHourValues) {
			PreparedStatement pStmt = getPStmt(PSTMT_INSERT_HOUR_VALUE);
			pStmt.setInt(1, data.stationId);
			pStmt.setInt(2, data.year);
			pStmt.setInt(3, data.month);
			pStmt.setInt(4, data.day);
			pStmt.setInt(5, data.parameterId);
			pStmt.setInt(6, data.hour);
			pStmt.setInt(7, data.value);
			pStmt.executeUpdate();
		}
		
		// Continue with a clear buffer:
		toInsertHourValues.clear();
	}

	public void regenerateDerivedMonthTemperaturesData() throws SQLException
	{
		getPStmt(PSTMT_DELETE_MONTH_AVERAGE_TEMPS              ).executeUpdate();
		getPStmt(PSTMT_GENERATE_MONTH_AVERAGE_TEMPS_DAY_AVERAGE).executeUpdate();
		getPStmt(PSTMT_GENERATE_MONTH_AVERAGE_TEMPS_DAY_MINIMUM).executeUpdate();
		getPStmt(PSTMT_GENERATE_MONTH_AVERAGE_TEMPS_DAY_MAXIMUM).executeUpdate();
	}
	
	public void regenerateDerivedDayData() throws SQLException
	{
		regenerateDerivedMonthTemperaturesData();
	
		getPStmt(PSTMT_DELETE_MONTH_PRECIPITATION  ).executeUpdate();
		getPStmt(PSTMT_GENERATE_MONTH_PRECIPITATION).executeUpdate();

	}

	public void regenerateDerivedHourData() throws SQLException
	{
		getPStmt(PSTMT_GENERATE_STATION_DAY_TEMPS                ).executeUpdate();
		getPStmt(PSTMT_GENERATE_MONTH_PRECIPITATION_EXTREME_DAYS ).executeUpdate();
		getPStmt(PSTMT_GENERATE_MONTH_PRECIPITATION_EXTREME_HOURS).executeUpdate();
	}

	public void regenerateDerivedPrecipitationDataOnly() throws SQLException
	{
		System.out.println("Emptying precipitation table...");
		getPStmt(PSTMT_DELETE_MONTH_PRECIPITATION  ).executeUpdate();
		System.out.println("Generating precipitation data from total rain fall from day data...");
		getPStmt(PSTMT_GENERATE_MONTH_PRECIPITATION).executeUpdate();
		
		System.out.println("Augmenting precipitation data with highest rain fall of day data...");
		getPStmt(PSTMT_GENERATE_MONTH_PRECIPITATION_EXTREME_DAYS ).executeUpdate();
		System.out.println("Augmenting precipitation data with highest rain fall of hour data...");
		getPStmt(PSTMT_GENERATE_MONTH_PRECIPITATION_EXTREME_HOURS).executeUpdate();
	}
}
