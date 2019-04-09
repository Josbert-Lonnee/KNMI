package com.josbertlonnee.knmi.file_import;

import java.util.ArrayList;

class ParameterList<P extends AbstractParameter> extends ArrayList<P>
{
	private static final long serialVersionUID = 1L;

	public ParameterList()
	{
		super(100);
	}
}