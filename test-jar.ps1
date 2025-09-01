$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-11.0.28.6-hotspot"
$env:PATH = "C:\Program Files\Microsoft\jdk-11.0.28.6-hotspot\bin;" + $env:PATH

Write-Host "Java Version:"
java -version

Write-Host "`nStarting SGA Application..."
java -jar target\sga.jar
