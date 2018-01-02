# JTelephonyServer
Simple app for voice communications over Internet written in Java.

This is a server side; client side is available at https://github.com/Programmer74/JTelephonyGUI

## Features
 
 - Voice communication
 - Text chat
 - Images
 
 Created with Hibernate and stuff. Supports Redis.
 Tested on Oracle and Postgres. 
 **Works with Postgres by default.**
 
## How-to on a dedicated server

**Assuming that you are running Debian/Ubuntu**
__**This will install Oracle Java 8 and PostgreSQL on your server **__

- Compile this (with the help of IntelliJ Idea or somehow else)
- Check that the jar is in out/artifacts/etc
- SSH to server, install 7Zip there
- Run ```pack.sh```
- Transfer ```jt-srv.7z``` on a server
- On server: Run ```7z x jt-srv.7z```
- On server: Run ```setup.sh```, follow the instructions
- On server: Run ```run-on-srv.sh```
- Fill the required user profiles info on the first launch
- If necessary, launch JT server with ```--register-contacts``` and setup contact relations
- Enjoy!