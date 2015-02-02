#!/bin/bash

if [[ $TRAVIS_BRANCH == 'master' ]]
	mvn deploy -Papi -B -V -s settings.xml
fi