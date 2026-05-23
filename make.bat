@echo コンパイル: 開始

set CLASSPATH=%CLASSPATH%;lib\poi-5.5.1.jar

rd /s /q classes
@md classes

javac -encoding UTF-8 -d classes -sourcepath src src\com\toff_monaka\excel_to_csv\Main.java

jar -cvmf MANIFEST.MF bin\ExcelToCSV.jar -C classes .

rd /s /q classes

@echo コンパイル: 終了

pause
