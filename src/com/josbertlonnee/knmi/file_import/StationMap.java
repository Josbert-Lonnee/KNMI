package com.josbertlonnee.knmi.file_import;

import java.util.HashMap;

class StationMap extends HashMap<Integer, Station>
{
	private static final long serialVersionUID = 1L;

	public Station add(Station station)
	{
		put(station.id, station);
		return station;
	}
}
