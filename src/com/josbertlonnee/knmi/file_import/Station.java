package com.josbertlonnee.knmi.file_import;

import com.josbertlonnee.VERIFY;

class Station
{
	public int id;
	public double longitude;
	public double latitude;
	public double altitude;
	public String name;
	
	public Station(String line)
	{
    	String[] lineParts = line.split("\\s{2,}");
    	
    	VERIFY.equals("station line number of parts", lineParts.length, 5);
    	
    	String[] lineStartParts = lineParts[0].split("\\s+");
    	
    	VERIFY.equals("first station line part", lineStartParts.length, 2);
    	VERIFY.equals("line start part", lineStartParts[0], "#");
    	
    	String[] idParts = lineStartParts[1].split("\\:");
    	
    	VERIFY.equals("station id number of parts", idParts.length, 1);
    	
    	this.id = Integer.parseInt(idParts[0]);
    	this.longitude = Double.parseDouble(lineParts[1]);
    	this.latitude  = Double.parseDouble(lineParts[2]);
    	this.altitude  = Double.parseDouble(lineParts[3]);
    	this.name = lineParts[4];
	}
	
	public String toString()
	{
		return"Station " + id + " (" + name + ")";
	}

	public void writeTo(KNMI_DatabaseManager databaseManager) throws Exception
	{
		databaseManager.insertStation(id, longitude, latitude, altitude, name);
	}
}