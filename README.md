# Store sale event producer simulator

Updated 4/20/2022

The store sales simulator application aims to demonstrate an end-to-end real-time inventory solution based on event-driven architecture
and data streaming capabilities. It supports the following features:

* Expose a simple user interface to simulate store selling items which are sent to Queue or Kafka Topic. 
* Randomly create item sale events ( includes restocks) and send them to Kafka or RabbitMQ or IBM MQ depending of the application configuration.
* Send predefined set of sale events to have a better control of the demonstration results.

This implementation is done with Java 11 and [Quarkus](https://quarkus.io) 2.7.1 with the AMQP reactive messaging extension to send messages to RabbitMQ, or to Kafka, or using JMS to send messages to IBM MQ. 

This tool is now used in different scenarios:

* Integrated with Kafka Streams agents to build item inventory cross stores, see
project [refarch-eda-item-inventory](https://github.com/ibm-cloud-architecture/refarch-eda-item-inventory) 
and store inventory, see project [refarch-eda-store-inventory](https://github.com/ibm-cloud-architecture/refarch-eda-store-inventory).
* Flink SQL query to compute ... see project [refarch-eda-item-inventory-flink](https://github.com/hmedney/refarch-eda-item-inventory-flink)
* Demonstrate MQ Source to Kafka connector, see gitops solution in this repo: [store-mq-gitops](https://github.com/ibm-cloud-architecture/store-mq-gitops)

For the Kafka Streams demo, the end-to-end solution demonstration script is in a separate deep dive lab described in [this article](https://ibm-cloud-architecture.github.io/refarch-eda/scenarios/realtime-inventory/).

With this repository, you can validate sending message to the different back-ends using a single User Interface. All the images are in `quay.io` registry, but for maintenance and development purpose we recommend to clone this repository. 

```shell
git clone https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator
```

Updates:

* 01/06/2021: Quarkus 1.10.5- Rabbit MQ 3.8 on local docker deployment, and Kafka 2.6. IBM MQ 9.2.
* 04/01/2021: Quarkus 1.13, Add Kustomize for gitops deployment
* 05/04/2021: Quarkus 1.13.2, Simplify environment folder, add codeql-analysis git workflow.
* 09/30/2021: Quarkus 2.2.3: add view of existing inventory
* 11/02/2021: Quarkus 2.3.1, + add view with controlled items sell scenario
* 02/22/2022: Quarkus 2.7.1, change diagramm change vue@cli version, frontend-maven-plugin version
* 02/28/2022: Add JMScorrelationID as store name to send to MQ so kafka connector can use if for record key
* 03/29/2022: Move Kafka producer to use reactive messaging.
* 04/20/2022: Add close button on messages window.

## Build the application locally

The docker image for this application is already available on quay.io registry as [ibmcase/eda-store-simulator](https://quay.io/ibmcase/eda-store-simulator).

The `buildAll.sh` scripts runs Maven packaging which include the vuejs app build (from the frontend folder), docker build and push the image to `quay.io`. 
You may need to change this script to push to your own registry.

```shell
docker login quay.io -u ...
./scripts/buildAll.sh 
```

```shell
# under this project
mvn package
```

To build backend and frontend separately:

```sh
mvn -U clean install -pl :store-simulator-frontend
mvn -U clean install -pl :store-simulator-backend -DskipTests
```

### User Interface development

The user interface is done in Vue.js under the `frontend` folder, so it is possible via proxy configuration 
see ([vue.config.js file](https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator/blob/master/frontend/vue.config.js)) 
to start `yarn serve` and access the UI connected to the simulator backend.

If you have the last release of nvm and node:

```sh
# under the frontend folder
nvm use node
yarn serve
# Go to http://localhost:4545/
```

But if you do not want any conflict with your node environment, we recommend using 
docker to run nodejs. 
So the first time you start the node container do the following:

The following was lastly tested on 9/30/2021 so migrating to vuejs 4.5.14 and nodejs v14.15.0

```sh
# Under frontend folder
docker run -v $(pwd):/home -ti node bash
# In the bash shell
yarn global add @vue/cli
# May be needed to updated the vue app with
vue upgrade --next
# in another terminal
# get the container id
docker ps
# commit the new image. Change ibmcase to your own docker or quay.io name
docker commit <container_id> quay.io/ibmcase/vue_node
# Verify
docker images
```

From the image created above, or reusing our image (quay.io/ibmcase/vue_node) starts the image by exposing the port 4545

```shell
# under frontend folder
docker run -v $(pwd):/home -ti -p 4545:4545 quay.io/ibmcase/vue_node bash
# Under /home 
# Compiles and hot-reloads for development
yarn serve
```

Go the [http://localhost:4545/#/](http://localhost:4545/#/) to see the UI. 

Any development under the frontend will be automatically visible in the browser and any changes to the Quarkus app 
are also reflected to make the end to end development very efficient.


### Potential build issues

* "Node version 0.10.48 is not supported, please use Node.js 4.0 or higher": 
On mac the `brew install nodejs`  will get the `v0.10.48` version. We need another version, so use `nvm install v14.15.0` to get a working
compatible version with the vue CLI used in this project.

### Develop backend

We use quarkus dev mode.

```sh
# under backend folder
quarkus dev
```

## Run the application locally

To run this application locally and assess all the different integration middleware (Kafka, RabbitMQ or MQ),
you first need to start all the components using:

```shell
cd environment/all
docker-compose up -d 
```

### For Rabbit MQ

Normally the queue is created automatically when running the app, but if you want to create it upfront, the following steps can be done:

* Download `rabbitmqadmin` client from http://localhost:15672/cli/rabbitmqadmin. One version of this script is available in the `environment` folder.

* Declare the queue:

```shell
# under environment folder
./rabbitmqadmin declare queue name=items durable=true -u rabbit-user -p rabbit-pass
```

* List the available queues:

```shell
# under environment folder
./rabbitmqadmin list declara queue name=items durable=false -u rabbit-user -p rabbit-pass
```

See more `rabbitmqadmin` CLI options [in https://www.rabbitmq.com/management-cli.html](https://www.rabbitmq.com/management-cli.html).

To validate sending message to RabbitMQ do the following steps:

* Go to the App console - simulator tab [http://localhost:8080/#/simulator](http://localhost:8080/#/simulator)
* Select RabbitMQ toggle and then the number of message to send.

![1](docs/rmq-simulator.png)

* Access Rabbit MQ Console: [http://localhost:15672/#/](http://localhost:15672/#/) user RabbitMQ, to verify messages are in the queue,

![2](docs/rmq-items-msg.png)

### For IBM MQ

Using the same approach as above, we can select to send to IBM MQ: 
use the IBM MQ toggle and send some messages: 

![3](docs/mq-simulator.png)

The simulator trace should display messages like:

```trace
sent to MQ:{"id":0,"price":25.15,"quantity":4,"sku":"Item_2","storeName":"Store_2","timestamp":"2020-10-13T16:48:35.455027","type":"RESTOCK"}

sent to MQ:{"id":1,"price":73.09,"quantity":3,"sku":"Item_1","storeName":"Store_1","timestamp":"2020-10-13T16:48:35.455082","type":"RESTOCK"}

sent to MQ:{"id":2,"price":68.85,"quantity":2,"sku":"Item_4","storeName":"Store_4","timestamp":"2020-10-13T16:48:35.455123","type":"RESTOCK"}

sent to MQ:{"id":3,"price":60.31,"quantity":9,"sku":"Item_2","storeName":"Store_5","timestamp":"2020-10-13T16:48:35.455168","type":"RESTOCK"}
```

And connecting to IBM MQ console [https://localhost:9443](https://localhost:9443/ibmmq/console/#/qmgr/QM1/queue/local/DEV.QUEUE.1/view), using admin/passw0rd credential:

![4](docs/MQ-message.png)

Verify messages are sent to the `DEV.QUEUE.1`.

The producer code uses JMS and JMS TextMessage.

```java
 private void sendToMQ(Item item) {
      try { 
        String msg = parser.toJson(item);
        TextMessage message = jmsContext.createTextMessage(msg);
        message.setJMSCorrelationID(item.storeName);
        producer.send(destination, message);
        logger.info("sent to MQ:" + msg);
      } catch( Exception e) {
        e.printStackTrace();
      }
        
    }
```


### For Kafka

First be sure the `items` topic is created, if not, run the following command:

```shell
./scripts/createTopics.sh
```

Finally same process applies for Kafka, from the simulator:

![6](docs/Kafka-simulator.png)

Looking at the Simulator trace, you can see the record offset for the message sent.

```trace
sending to items item {"id":5,"price":44.12,"quantity":4,"sku":"Item_4","storeName":"Store_5","timestamp":"2020-10-13T16:15:30.790437","type":"SALE"}

(kafka-producer-network-thread | StoreProducer-1) The offset of the record just sent is: 6
```

Or using the following command to consumer all the messages from the `items` topic:

```shell
docker run -ti strimzi/kafka:latest-kafka-2.8.0 bash -c "/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka:29092 --topic items --from-beginning"
```

## Development mode - run locally

When developing the application, you may want to test against only one backend. 
As the simulator can use three potential environments: kafka with local strimzi, Rabbitmq and IBM MQ, 
we have setup different docker compose configurations to run those middleware separately.

### RabbitMQ alone, running the application in dev mode

Go under the `environments/rabbitmq` folder and start the two docker containers:

```shell
# under environments/rabbitmq folder
docker-compose docker-compose up
# Start quarkus
quarkus dev
# If needed in the frontend folder, start the UI
yarn serve
```

Access the API via: [http://localhost:8080/swagger-ui/#/](http://localhost:8080/swagger-ui/#/)

In the Queues page, see the content of the `items` queue, and use the 'get messages' to see the queue content.


### Kafka only (for development)

The compose file is under `environment/kafka` folder.

```shell
# under environments/kafka folder
docker-compose up -d

# Under frontend folder
# Compiles and hot-reloads for development
yarn serve

# under the root folder:
quarkus dev
```

To work on the UI development and test go the [http://localhost:4545/#/](http://localhost:4545/#/) to see the UI. 

To work on the backend API test using swagger UI: []()

### IBM MQ only for development

```shell
# under environments/mq folder
docker-compose  up -d
```

Then same commands as above.

### Packaging the UI and Quarkus app

```shell
# under frontend
yarn build
# under root folder
mvn package
docker build -f src/main/docker/Dockerfile.jvm -t ibmcase/eda-store-simulator .
```

## Implementation approach

The application is using one REST resource for defining the needed APIs: 

![8](docs/api.png)

The messages sent are defined in the [domain/Item.java](https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator/blob/master/src/main/java/ibm/gse/eda/stores/domain/Item.java) class.

The items sold are part of a simple predefined list of item with SKU defined as

```java
 static transient String[] skus = { "Item_1", "Item_2", "Item_3", "Item_4", "Item_5" };
 
```

The following extensions were added to add metrics, health end points, and OpenShift deployment manifests creation:

```shell
./mvnw quarkus:add-extension -Dextensions="quarkus-smallrye-openapi"
./mvnw quarkus:add-extension -Dextensions="smallrye-health"
./mvnw quarkus:add-extension -Dextensions="smallrye-metrics"
./mvnw quarkus:add-extension -Dextensions="openshift"
```

See the [pom.xml](https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator/blob/master/pom.xml) file detail.

Each integration is done in a separate class under the infrastructure package:

* [Kafka producer with basic API](https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator/blob/master/src/main/java/ibm/gse/eda/stores/infrastructure/kafka/KafkaItemGenerator.java)
* [Rabbit MQ with reactive messaging and AMQP](https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator/blob/master/src/main/java/ibm/gse/eda/stores/infrastructure/rabbitmq/RabbitMQItemGenerator.java)
* [IBM MQ with JMS producer](https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator/blob/master/src/main/java/ibm/gse/eda/stores/infrastructure/mq/MQItemGenerator.java)

## Deploy and run on OpenShift

To package the app as docker images with a build performed within the OpenShift cluster, 
we can use the source to image approach, by running the following command:

```shell
./mvnw clean package -Dui.deps -Dui.dev -Dquarkus.container-image.build=true -Dquarkus.container-image.group=ibmcase -Dquarkus.container-image.tag=1.0.0 -Dquarkus.kubernetes.deploy=true -DskipTests
```

As an alternate to deploy this application, we can use our Kustomize declarations from the `src/main/kubernetes` folder.

The configmap 

```sh
# log to OpenShift
oc login ....
# Create a new project
oc new-project test-store-simul
# deploy
oc apply -k src/main/kubernetes/store-simulator
```
