@echo コンパイル: 開始

set CLASSPATH=%CLASSPATH%;lib\commons-collections4-4.5.0.jar;lib\commons-io-2.22.0.jar;lib\commons-lang3-3.20.0.jar;lib\commons-math3-3.6.1.jar;lib\commons-codec-1.22.0.jar;lib\commons-compress-1.28.0.jar;lib\log4j-api-2.26.0.jar;lib\log4j-core-2.26.0.jar;lib\SparseBitSet-1.3.jar;lib\xmlbeans-5.3.0.jar;lib\poi-5.5.1.jar;lib\poi-ooxml-5.5.1.jar;lib\poi-ooxml-lite-5.5.1.jar

rd /s /q classes
@md classes

javac -encoding UTF-8 -d classes -sourcepath src src\com\toff_monaka\excel_to_csv\Main.java

jar -cvmf MANIFEST.MF bin\ExcelToCSV.jar -C classes .

rd /s /q classes

@echo コンパイル: 終了

pause
