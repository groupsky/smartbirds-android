#!/bin/bash

set -x +e

# configure the emulator
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0

# need to install first so we can grant mock_location permission
./gradlew installDebug
adb shell appops set org.bspb.smartbirds.pro android:mock_location allow

# run the tests
./gradlew connectedCheck --stacktrace

# get the screenshots if any from the emulator so they can be inspected
adb pull /storage/emulated/0/Pictures/org.bspb.smartbirds.pro/espresso_screenshots
