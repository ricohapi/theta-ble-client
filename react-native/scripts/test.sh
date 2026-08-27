#!/bin/sh
# `yarn test` runs the TS suite (jest) only. `yarn test --all` additionally
# runs the native bridge UTs (Android JUnit, iOS XCTest) -- see test:android
# and test:ios in package.json.
if [ "$1" = "--all" ] || [ "$1" = "-all" ]; then
  yarn jest && yarn test:android && yarn test:ios
else
  yarn jest
fi
