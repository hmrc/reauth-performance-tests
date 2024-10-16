#!/bin/sh -xe

sm2 --stop CENTRALISED_AUTHORISATION_ALL
sleep 5
sm2 --start CENTRALISED_AUTHORISATION_ALL --wait 60 --noprogress --appendArgs '{"IDENTITY_PROVIDER_GATEWAY_FRONTEND":["-Didentity-provider-gateway-frontend.features.newRoutesEnabled=true"],
                                                                                "IDENTITY_PROVIDER_GATEWAY":["-Didentity-provider-gateway.features.newRoutesEnabled=true"]}'

if [ $? != 0 ]
then
    echo "Failed to start all services"
    exit 1
fi

sbt -DrunLocal=true -Dperftest.runSmokeTest=true -DjourneysToRun.0=reauth-login-journey Gatling/test


