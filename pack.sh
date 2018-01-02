#!/bin/bash
mkdir jt 
mkdir jt/lib
cp lib/* ./jt/lib
cp out/artifacts/JTelephonyServer/JTelephonyServer.jar jt/
cp run-on-srv.sh jt/
cp setup.sh jt/
7z a jt-srv.7z jt/*
rm -rf jt
