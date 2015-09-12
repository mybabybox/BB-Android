#!/bin/bash
jarsigner -verbose -keystore bb-android.keystore "$1" bb-android
echo ""
echo ""
echo "Checking if APK is verified..."
jarsigner -verify "$1"
