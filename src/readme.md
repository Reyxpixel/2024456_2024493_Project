To compile:

if (!(Test-Path out)) { New-Item -ItemType Directory -Path out | Out-Null }
$sources = Get-ChildItem -Recurse -File -Path src -Filter *.java | ForEach-Object { $_.FullName }
javac -cp "lib\sqlite-jdbc-3.50.3.0.jar" -d out $sources


To run:

java --enable-native-access=ALL-UNNAMED -cp "out;lib\sqlite-jdbc-3.50.3.0.jar" Main


