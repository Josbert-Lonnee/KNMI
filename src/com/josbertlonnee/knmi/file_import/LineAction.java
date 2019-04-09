package com.josbertlonnee.knmi.file_import;

interface LineAction
{
	void processLine(String line) throws Exception;
}
