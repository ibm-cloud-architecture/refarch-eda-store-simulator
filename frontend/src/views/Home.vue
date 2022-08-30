<template>
  <div class="home">
    <h3>Welcome to the {{ title }} {{ version }}</h3>

   <v-card>
    <h4>Context</h4>
    <v-container>
    <ul>
      <li>This demonstration illustrates multiple components of a data pipeline, event streamings, with Kafka sources and sink connectors</li>
      <li>The demo script can be found in the GitOps Repository at <a href="https://ibm-cloud-architecture.github.io/eda-rt-inventory-gitops/demo-script/">this link</a></li>
      <li>Go to the stores view to see the existing stores we want to get the inventory from.</li>
      <li>In the simulator panel you can start to send messages to a specific middleware you select. The list of middleware depends of configuration and deployment. At most you can have Kafka, IBM MQ or Rabbit MQ.</li>
      <li>To learn more on Event Driven Solution, Architecture and best practices see <a href="https://ibm-cloud-architecture.github.io/refarch-eda">the EDA main content</a></li>.
    </ul>
    </v-container>
   </v-card>
   <div>
      <p>This simulator application is represented on the top left in the figure below. 
      <ul>
        <li>See project explanations in <a href="https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator">the ibm-cloud-architecture refarch-eda-store-simulator repository</a></li>
        <li>The Item-aggregator, based on Kafka Stream APIs, is in this project: <a href="https://github.com/ibm-cloud-architecture/refarch-eda-store-inventory">refarch-eda-store-inventory</a></li>
        <li>The Store-aggregator, also based on Kafka Stream APIs, is in this project: <a href="https://github.com/ibm-cloud-architecture/refarch-eda-item-inventory">refarch-eda-item-inventory</a></li>
      </ul> 
      </p>
      <v-img
          alt="IBM Cloud Logo"
          class="shrink mr-2"
          contain
          src="../assets/mq-es-demo.png"
          transition="scale-transition"
          width="1000px"
        />
      </div> 
  </div>
</template>

<script>
import axios from "axios";
// @ is an alias to /src

export default {
  name: 'Home',
  data: () => { 
      return {
        version: {},
        title: "Store Item Sell Simulator"
      }
  },
  created() {
    this.initialize();
  },
  methods: {
    initialize() {
      axios.get("/api/v1/version").then((resp) => (this.version = resp.data.version));
      console.log(this.version)
    }
  }
}
</script>
