bash gradlew make makePluginsJson ensureJarCompatibility -x lint --continue
cp LayarKaca/build/LayarKaca.cs3 builds/
cp Pencurimovie/build/Pencurimovie.cs3 builds/
cp build/plugins.json builds/
ls builds/

