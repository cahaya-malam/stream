bash gradlew make makePluginsJson ensureJarCompatibility -x lint --continue
cp LayarKaca/build/LayarKaca.cs3 builds/
cp Pencurimovie/build/Pencurimovie.cs3 builds/
cp Dubbindo/build/Dubbindo.cs3 builds/
cp build/plugins.json builds/
ls builds/
rm -rf ~/github1/stream
bash ~/github1/pusfilee.sh /storage/emulated/0/APK/TES/stream/builds/ cahaya-malam/stream builds
