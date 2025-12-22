mvn clean install -P jlink clean

if (Test-Path ".\jre")
{
    Remove-Item ".\jre" -Recurse -Force
}

New-Item -ItemType Directory -Force -Path ".\jre"
Set-Location ".\jre"
7z x ".\..\jre.zip"
Set-Location ..
Remove-Item ".\jre.zip" -Recurse -Force

Set-Location ".\installer files\"
launch4jc.exe ".\launch4j_setup.xml"
iscc.exe ".\Firstpass.iss"
pause