package com.josbertlonnee.knmi.file_import;

interface LineCondition
{
	public boolean checkLine(String line) throws Exception;
}