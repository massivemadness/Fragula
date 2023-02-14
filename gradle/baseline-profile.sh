#!/bin/bash

# ./gradle/baseline-profile.sh

PROJECT_DIR="$PWD"
PROFILE_FOLDER="$PROJECT_DIR/benchmark/build/outputs/connected_android_test_additional_output/benchmark/connected"

cd $PROFILE_FOLDER
cd "$(ls)"

APP_PROFILE="$PROJECT_DIR/app/src/main/baseline-prof.txt"
XML_PROFILE="BaselineProfileBenchmark_generateXmlProfile-baseline-prof.txt"
COMPOSE_PROFILE="BaselineProfileBenchmark_generateComposeProfile-baseline-prof.txt"

cp $COMPOSE_PROFILE $APP_PROFILE # Compose baseline profile

awk '/fragula2\/compose/||/fragula2\/common/' $COMPOSE_PROFILE > $PROJECT_DIR/fragula-compose/src/main/baseline-prof.txt
awk '/fragula2/&&!/fragula2\/sample/' $XML_PROFILE > $PROJECT_DIR/fragula-core/src/main/baseline-prof.txt