@echo off
if exist *.class del *.class
if exist *.jar del *.jar
javac -classpath . -sourcepath . RC5graph.java
if errorlevel 1 goto end
jar cvfm JavaVis.jar MANIFEST.MF *.class *.gif

:end
