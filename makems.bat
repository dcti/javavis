@echo off

rem This is a batch file to compile the project using the Microsoft SDK for Java compiler.
rem http://www.microsoft.com/java/download/dl_sdk40.htm

if exist *.class del *.class
if exist *.jar del *.jar

"D:\Program Files\Microsoft SDK for Java 4.0\Bin\jvc" /cp:p . JavaVis.java

if errorlevel 1 goto end
jar cvfm JavaVis.jar MANIFEST.MF *.class *.gif

:end
