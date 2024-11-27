#!/bin/sh -xe

sm2 --stop CENTRALISED_AUTHORISATION_ALL
sleep 5
sm2 --start CENTRALISED_AUTHORISATION_ALL --wait 60 --noprogress
if [ $? != 0 ]
then
    echo "Failed to start all services"
    exit 1
fi

sbt -DrunLocal=true -Dperftest.runSmokeTest=true -DjourneysToRun.0=reauth-login-journey Gatling/test


