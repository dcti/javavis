@echo off
del *.class
del *.jar
javac RC5graph.java
if errorlevel 1 goto end
jar cvfm RC5graph.jar MANIFEST.MF *.class

:end
