package com.josbertlonnee.knmi.file_import;

class DayParameter extends AbstractParameter
{
	@Override
	public final void writeTo(KNMI_DatabaseManager databaseManager) throws Exception
	{
		databaseManager.insertDayParameter(id, code, description);
	}
}