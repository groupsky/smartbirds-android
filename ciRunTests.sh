#!/bin/bash

# stop on command error and print commands
set -ex

# disable animations
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0

# need to install first so grant mock_location permission can be granted to the app
./gradlew installDebug
adb shell appops set org.bspb.smartbirds.pro android:mock_location allow

# run the tests without bailing on error and save the exit code
set +e
./gradlew connectedCheck --stacktrace
ret=$?
set -e

# get the screenshots if any from the emulator so they can be inspected
adb pull /storage/emulated/0/Android/data/org.bspb.smartbirds.pro/files/Pictures/espresso_screenshots || echo "No screenshots"

# exit with the exit code from tests
exit ret
