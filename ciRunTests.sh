#!/bin/sh

./gradlew installDebug

adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
adb shell appops set org.bspb.smartbirds.pro android:mock_location allow

./gradlew connectedCheck