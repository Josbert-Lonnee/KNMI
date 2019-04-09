package com.josbertlonnee.knmi.file_import;

import com.josbertlonnee.VERIFY;

abstract class AbstractParameter
{
	public int id;
	public String code;
	public String description;
	
	public void fromDataLine(String line)
	{
    	String[] lineParts = line.split(" = ");
    	
    	VERIFY.equals("parameter line number of parts", lineParts.length, 2);
    	
    	String[] lineStartParts = lineParts[0].split("\\s+");
    	
    	VERIFY.equals("first station line part", lineStartParts.length, 2);
    	VERIFY.equals("line start part", lineStartParts[0], "#");
    	
		this.code = lineStartParts[1];
    	this.description = lineParts[1].trim();
    	
    	if (this.description.endsWith(";"))
    		this.description = this.description.substring(0, this.description.length() - 1);
	}
	
	public final String toString()
	{
		return "Parameter " + id + ":" + code + " (" + description + ")";
	}

	protected abstract void writeTo(KNMI_DatabaseManager databaseManager) throws Exception;
}