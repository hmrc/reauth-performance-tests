
# one-login-journeys-performance-tests

Performance test suite for the `one-login-gateway` and `one-login-gateway-frontend` services, using [performance-test-runner](https://github.com/hmrc/performance-test-runner) under the hood.

### Services

Start `OLJ_ALL` services as follows:

```bash
sm2 --start OLJ_ALL
```

### Logging

The default log level for all HTTP requests is set to `WARN`. Configure [logback.xml](src/test/resources/logback.xml) to update this if required.

### WARNING :warning:

Do **NOT** run a full performance test against staging from your local machine. Please [implement a new performance test job](https://confluence.tools.tax.service.gov.uk/display/DTRG/Practical+guide+to+performance+testing+a+digital+service#Practicalguidetoperformancetestingadigitalservice-SettingupabuildonJenkinstorunagainsttheStagingenvironment) and execute your job from the dashboard in [Performance Jenkins](https://performance.tools.staging.tax.service.gov.uk).

## Tests

Run smoke test (local terminal) as follows:
* ./olj-smoke-test-local.sh
* ./acf-smoke-test-local.sh
* ./api-logout-smoke-test-local.sh

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
