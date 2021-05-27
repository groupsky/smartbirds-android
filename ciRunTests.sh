#!/bin/sh

./gradlew installDebug

adb shell appops set org.bspb.smartbirds.pro android:mock_location allow

./gradlew connectedCheck