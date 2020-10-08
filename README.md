# Store sale event producer simulator

This component is a simulator application to demonstrate the end to end item inventory solution. 
It supports the following capabilities:

* Expose a simple user interface to support the demonstration scenario
* Randomly create events about item sales or restocks and send them to Kafka or RabbitMQ or MQ depending of the demo settings.

It uses [Quarkus](https://quarkus.io) with the AMQP reactive messaging extension to send messages to RabbitMQ. This code is used to demonstrate RabbitMQ to Kafka with the IBM Kafka Connect [RabbitMQ source connector](https://github.com/ibm-messaging/kafka-connect-rabbitmq-source).

Tested 10/06/2020 Quarkus 1.8.1 - Rabbit MQ 3.8 on local docker deployment
and Kafka 2.6

## Development mode - run locally

This section is used when developing the application. For pure demonstration of the application from pre-built images see [this section.](#demonstration)

As the simulator can use different backend for the messaging we have set up 3 potential environments: kafka with local strimzi, rabbitmq and IBM mq.

For each environment the call to `quarkus:dev` is done inside of a `maven` container so that the Quarkus application can access the target messaging product on the same docker network.

### RabbitMQ running the application in dev mode

Go under the `environments/rabbitmq` folder and start the two docker containers:

```shell
# under environments/rabbitmq folder
docker-compose -f dev-docker-compose up
```

Access the API via: [http://localhost:8080/swagger-ui/#/](http://localhost:8080/swagger-ui/#/)

Access Rabbit MQ Console: [http://localhost:15672/#/](http://localhost:15672/#/) user rabbitmq

In the Queues page, see the content of the `items` queue, and use the 'get messages' to see the queue content.

The user interface is done in Vue.js under the webapp folder, so it is possible via proxy configuration see ([vue.config.js file]()) to start `yarn serve` and access the UI connected to the simulator backend.

```shell
# under webapp folder
# Project setup
yarn install
# Compiles and hot-reloads for development
yarn serve
```

Go the [http://localhost:4545/#/](http://localhost:4545/#/) to see the UI. 

Any development under the webapp will be automatically visible in the browser and any change to the quarkus app are also refected to make the end to end development very efficient.

### Packaging the UI and Quarkus app

```shell
# under webapp
yarn build
# under root folder
mvn package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t ibmcase/eda-store-simulator .
```

To run one of the configuration with the image built:

```shell
# For rabbitmq only: under 
cd environment/rabbitmq
docker-compose up&
# for Kafka 
cd environment/kafka
docker-compose up&
```

Access the User interface at [http://localhost:8080/](http://localhost:8080/).


## Implementation approach

The application is using one REST resource for defining two simple API: 

* `GET /sales` to get the last item sold.
* `POST /sales/start/{records}` to start sending {records} number of message to MQ. 

The messages sent are defined in the [domain/ItemSaleMessage.java](https://github.com/jbcodeforce/eda-kconnect-lab/blob/master/store-sale-producer/src/main/java/ibm/gse/eda/stores/domain/ItemSaleMessage.java) class.

The items sold are part of a simple predefined list of item with SKU from 'IT01 to IT09'. The content is generated randomly. 

Below is an example as json object:

```json
{"id":9,"price":3.9131141142105355,"quantity":5,"sku":"IT05","storeName":"PT01"}
```

The [simple generator code](https://github.com/jbcodeforce/eda-kconnect-lab/blob/18c4fed416d92bb3cadce733e6d5352afafd1243/store-sale-producer/src/main/java/ibm/gse/eda/stores/infrastructure/ItemSaleGenerator.java#L76) is sending message to RabbitMQ using the AMQP API.

The following extensions were added to add metrics, health end points, and OpenShift deployment manifests creation:

```shell
./mvnw quarkus:add-extension -Dextensions="quarkus-smallrye-openapi"
./mvnw quarkus:add-extension -Dextensions="smallrye-health"
./mvnw quarkus:add-extension -Dextensions="smallrye-metrics"
./mvnw quarkus:add-extension -Dextensions="openshift"
```

### Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `store-sale-producer-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/store-sale-producer-1.0.0-SNAPSHOT-runner.jar`.

### Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/store-sale-producer-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## Deploy and run on OpenShift

We have defined a ConfigMap to deploy to the OpenShift project so environment variables can be loaded from the config map at runtime.

```
oc apply -f src/main/kubernetes/store-sale-cm.yaml
```

To package the app as docker images with a build on OpenShift, using the source to image approach run the following command:

```shell
./mvnw clean package -Dquarkus.container-image.build=true -Dquarkus.container-image.group=ibmcase -Dquarkus.container-image.tag=1.0.0
```

## Demonstration

### Demonstrating with Rabbit MQ
