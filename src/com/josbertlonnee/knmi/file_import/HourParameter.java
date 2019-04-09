package com.josbertlonnee.knmi.file_import;

class HourParameter extends AbstractParameter
{
	@Override
	public final void writeTo(KNMI_DatabaseManager databaseManager) throws Exception
	{
		databaseManager.insertHourParameter(id, code, description);
	}
}