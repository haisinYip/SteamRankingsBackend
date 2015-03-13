To build the backend and API:

`mvn package`

To install the API (and the backend) to the local computer so that any API changes you make **not** pushed to master can be compiled into the website:

`mvn install`

To run the backend (from the `backend` folder):

`java -jar target/standalone-backend-1.1-SNAPSHOT.jar`

You can also run/debug the backend from Eclipse, but it has a tendency to do weird things so I can't write anything about it.  It should work fine, though, and did last time I tested it. You may want to run `mvn eclipse:eclipse` if Eclipse gets confused as to the folder structure.

The `master` branch is built by Jenkins, and should be stable.

Below is the build status for the dev branch.

[![Build Status](http://mikemontreal.ignorelist.com:58722/buildStatus/icon?job=SteamRankingsServiceDEV)](http://mikemontreal.ignorelist.com:58722/job/SteamRankingsServiceDEV/)