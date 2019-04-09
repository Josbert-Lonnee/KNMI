# KNMI
Project with Java for importing files exported by the KNMI and saving them in a DB. Also derived data is generated.

This project/repository depends on my Util project/reposittory.

The Koninklijk Nederlands Meteorologisch Instituut (KNMI) is the Dutch royal metreoliogical institute:
https://nl.wikipedia.org/wiki/Koninklijk_Nederlands_Meteorologisch_Instituut

Dayly data of the KNMI can get downloaded here:
http://projects.knmi.nl/klimatologie/daggegevens/selectie.cgi

Houtly data of the KNMI can get downloaded here:
https://projects.knmi.nl/klimatologie/uurgegevens/selectie.cgi

The files need no pre-processing; the Java code reas the as-is.

The main-functions are in classes:
com.josbertlonnee.knmi.file_import.DayDataImport and
com.josbertlonnee.knmi.file_import.HourDataImport
But these classes first need locatl adaptions; see the code.

The data is writteh to a MariaDB. Tables can get created by running create_database_tables.sql
