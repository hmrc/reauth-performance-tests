#!/bin/sh -xe

sm2 --stop OLJ_ALL IDENTITY_PROVIDER_ACCOUNT_CONTEXT
#sleep 5
sm2 --start OLJ_ALL IDENTITY_PROVIDER_ACCOUNT_CONTEXT --wait 120 --noprogress \

if [ $? != 0 ]
then
    echo "Failed to start all services"
    exit 1
fi

sbt -DrunLocal=true -Dperftest.runSmokeTest=true -DjourneysToRun.0=reauth-login-journey Gatling/test


