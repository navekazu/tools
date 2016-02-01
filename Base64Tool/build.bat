javac -classpath "lib\commons-codec-1.9.jar" -d classes src\*.java
cd classes
jar cf ..\lib\Base64Tool.jar *
cd ..
