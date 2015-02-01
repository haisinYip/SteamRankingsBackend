#!/bin/bash

if [[ $TRAVIS_BRANCH == 'travis_test' ]]
  cd test/dummy
  rake db:schema:load
else
  cd spec/dummy
  rake db:schema:load
fi