Minecraft Modpack Updater
====

This is a Minecraft mod utility, installed as a tweak, to automatically download or update mods in a modpack. It doesn't rely on Minecraft code, so it should be compatible with all versions.

It works similarly to VoxelUpdate, but with key differences.  Namely, forge compatibility and the ability to specify an absolute URL instead of a relative one.

Install
===
To install this on the client, copy the built .jar to .minecraft/libraries/updater/mcupdater/${version}/mcupdater-${version}.jar
Afterwards, edit the mc version json which you want to install this in.  Add the library to the libraries and the arguments `--tweakClass mcupdater.Updater` BEFORE forge in the minecraftArguments element.

Finally, copy the modpack.json you were either provided or created to the minecraft game directory.

To install on a server, simply copy the file to the directory your server jar is in and script to run before it starts.  The updater uses libraries in the server jar, so it will need to load the jar. Its name needs to start with `minecraft_server` in order for it to work.

Setting Up the Jsons
===
Examples of the setup are in the test folder.

Setup for the client modpack.json is simple.  Just provide the modpack name, version, url (repo), and minecraft version, then throw it in your game directory.

For the remote pack.json, you will need to provide a "mcversion" object and a "mods" object array.  In the mods array, you may provide as many or as little mods as you like.  Just make sure each object has a "modid", "version", "file", and "md5" element.  For the file, you may have it be relative or absolute, meaning you can link offsite.

Place the file at repo/modpack/version/pack.json.
