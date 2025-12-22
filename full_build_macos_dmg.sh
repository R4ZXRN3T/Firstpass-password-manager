mvn clean install -P jlink clean
rm -rf ./jre
unzip ./jre.zip

jpackage \
	--type dmg \
	--name Firstpass \
	--app-version 2.1.1 \
	--vendor "R4ZXRN3T" \
	--description "A simple, secure password manager." \
	--input "." \
	--main-jar "Firstpass.jar" \
	--main-class "org.R4ZXRN3T.firstpass.Main" \
	--runtime-image "./jre" \
	--resource-dir "." \
	--dest "./dist"

rm ./jre.zip