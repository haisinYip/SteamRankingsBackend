#!/bin/bash

if [[ $TRAVIS_BRANCH == 'master' ]]
	then
	mvn deploy -Papi -B -V -s settings.xml
fi