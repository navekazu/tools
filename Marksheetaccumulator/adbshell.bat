@echo off

@echo run-as tools.marksheetaccumulator
@echo cd databases/
@echo sqlite3 ./MarksheetDatabase.db

@echo select * from sqlite_master;

"C:\User\k-watanabe\Projects\local\Android\sdk\platform-tools\adb" shell

@echo on
