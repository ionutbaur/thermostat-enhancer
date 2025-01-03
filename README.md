# thermostat-enhancer
A brand aware API based on Quarkus and async calls with Mutiny that can manage smart thermostat devices 
(currently only Tado implemented but can easily integrate others, for example Zigbee)
providing features that their official apps do not (yet) support.

The app can be easily started/stopped with docker (required as pre-requisite).
Start the app executing `./run-docker.sh` and stop it with `./stop-docker.sh`.

App will be available at http://localhost:9080 and swagger UI at http://localhost:9080/q/swagger-ui/