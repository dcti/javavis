@echo off

rem This is a batch file to compile the project using the Sun Java SDK.
rem http://java.sun.com/j2se/1.3/

if exist *.class del *.class
if exist *.jar del *.jar
javac -O -classpath .;./MRJToolkitStubs.zip JavaVis.java
if errorlevel 1 goto end
jar cvfm JavaVis.jar MANIFEST.MF *.class *.gif *.zip

:end
