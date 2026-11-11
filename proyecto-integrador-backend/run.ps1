# Descarga Maven (si no esta instalado) y ejecuta la aplicacion

$mavenVersion = "3.9.9"
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$mavenRoot = "$env:USERPROFILE\.maven"
$mavenHome = "$mavenRoot\apache-maven-$mavenVersion"
$mavenZip = "$mavenRoot\apache-maven-$mavenVersion-bin.zip"

if (-not (Test-Path $mavenRoot)) {
    New-Item -ItemType Directory -Path $mavenRoot | Out-Null
}

if (-not (Test-Path $mavenHome)) {
    Write-Host "Descargando Maven $mavenVersion..." -ForegroundColor Green
    try {
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenZip -UseBasicParsing
    }
    catch {
        Write-Host "Error: No se pudo descargar Maven ($($_.Exception.Message))" -ForegroundColor Red
        exit 1
    }

    Write-Host "Extrayendo Maven..." -ForegroundColor Green
    Expand-Archive -Path $mavenZip -DestinationPath $mavenRoot -Force
    Remove-Item $mavenZip
}

Write-Host "Ejecutando aplicacion en http://localhost:8080 ..." -ForegroundColor Green
$env:PATH = "$mavenHome\bin;$env:PATH"
& mvn clean spring-boot:run
