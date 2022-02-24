<template>
  <div class="home">
    <h3>Welcome to the {{ title }} {{ version }}</h3>

   <v-card>
    <h4>Demonstration script</h4>
    <v-container>
    <ul>
      <li>Go to the stores view to see the existing stores we want to get the inventory from</li>
      <li>In the simulator panel you can start to send messages to a specific middleware you select. The list of middleware depends of configuration and deployment. At most you can hava Kafka, IBM MQ or Rabbit MQ.</li>
    </ul>
    </v-container>
   </v-card>
   <div>
      <p>This simulator is represented on the top left in the figure below. See project explanations in <a href="https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator">the ibm-cloud-architecture/refarch-eda-store-simulator repository</a></p>
      <v-img
          alt="IBM Cloud Logo"
          class="shrink mr-2"
          contain
          src="../assets/mq-es-demo.png"
          transition="scale-transition"
          width="900px"
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
      axios.get("/api/stores/v1/version").then((resp) => (this.version = resp.data.version));
      console.log(this.version)
    }
  }
}
</script>
