#!/bin/sh

rm -rf package
mkdir package
yarn prepack
tar -cf - \
    -vz \
    --exclude '.DS_Store' \
    --exclude '*/__test__' \
    --exclude '._*' \
    --exclude '*/build' \
    --exclude '*/.idea' \
    --exclude '*/xcuserdata' \
    README.md \
    android \
    ios \
    src \
    lib \
    frameworks \
    package.json \
    theta-ble-client-react-native.podspec |
     (cd package; tar -zxf -)

