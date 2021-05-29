#!/usr/bin/env sh

# configure the emulator
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0

# need to install first so we can grant mock_location permission
./gradlew installDebug
adb shell appops set org.bspb.smartbirds.pro android:mock_location allow

# run the tests
./gradlew connectedCheck --info --stacktrace

# need root to access the screenshots
adb root
adb pull /storage/emulated/0/Pictures/org.bspb.smartbirds.pro/espresso_screenshots
