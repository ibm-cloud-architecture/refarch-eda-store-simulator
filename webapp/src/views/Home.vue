<template>
  <div class="home">
    <h3>Welcome to the store simulator</h3>

   <v-card>
    <h4>Demonstration script</h4>
    <v-container>
    <ul>
      <li>Go to the stores view to see the existing stores we want to get the inventory from</li>
      <li>In the simulator panel you can start to send message to a specific middleware you select. The list of middleware depends of configuration and deployment. At most you can hava Kafka, IBM MQ or Rabbit MQ.</li>
    </ul>
    </v-container>
   </v-card>
  </div>
</template>

<script>
// @ is an alias to /src

export default {
  name: 'Home',
}
</script>
