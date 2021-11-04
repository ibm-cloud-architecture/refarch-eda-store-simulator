<template>
  <v-container>
    <v-card elevation="2" tile height=600px>
      <v-card-title>Store Simulator</v-card-title>
      <v-row align-content="center">
        <v-col  class="d-flex align-center" cols="12" md="6" sm="3">
          <v-row class="ma-2">
            Current backend is: {{ backend }}
          </v-row>
          <v-row>
          <v-radio-group v-model="backend">
            <v-radio
              v-for="be in backends"
              :key="be"
              :label="`${be}`"
              :value="be"
              class="pr-2"
              @click="resetTable"
            ></v-radio>
          </v-radio-group>
          </v-row>
        </v-col>
      </v-row>
      <v-row align="start">
        <v-col class="mx-5 d-flex align-center" height="300px">
          <v-container>
            <v-row>
              <h3 class="mx-auto">Random</h3>
            </v-row>
            <v-row>
              <p>Select the umber of records to send:</p>
            </v-row>
            <v-spacer></v-spacer>
            <v-row class="mt-12">
              <v-slider
                v-model="records"
                max="100"
                min="1"
                thumb-label="always"
              ></v-slider>
            </v-row>
            <v-row>
              <v-text-field
                label="Number of records"
                v-model="records"
              ></v-text-field>
            </v-row>
            
          </v-container>
        </v-col>
        <v-col class="mx-5"  height="300px">
          <v-row>
            <h3 class="mx-auto">Controlled scenario</h3>
          </v-row>
          <v-row>
          <p>Use this option to send predefined records - better for testing</p>
          </v-row>
        </v-col>
      </v-row>
      <v-row align-content="center">
         <v-col class="mx-5">
           <v-btn color="primary" @click="start">
                <v-icon>mdi-restart</v-icon>
              </v-btn>
         </v-col>
          <v-col class="mx-5"> 
            <v-btn color="primary" @click="startControlled">
              <v-icon>mdi-run</v-icon>
            </v-btn>
          </v-col>
      </v-row>
      <v-row v-if="messages.length > 0">
        <Messages :messagesIn="messages"></Messages>
      </v-row>
    </v-card>
  </v-container>
</template>
<script>
import axios from "axios";
import Messages from "./Message.vue";

export default {
  components: {
    Messages,
  },
  data: () => ({
    backend: "",
    backends: [],
    records: 1,
    messages: [],
  }),
  created() {
    this.initialize();
  },
  methods: {
    initialize() {
      axios
        .get("/api/stores/v1/backends")
        .then((resp) => (this.backends = resp.data));
    },
    start() {
      console.log(" start with " + this.records);
      let control = { records: this.records, backend: this.backend };
      axios
        .post("/api/stores/v1/start/", control)
        .then((resp) => (this.messages = resp.data));
    },
    startControlled() {
      console.log(" startControlled with ");
      let control = { records: this.records, backend: this.backend };
      axios
        .post("/api/stores/v1/startControlled/",control)
        .then((resp) => (this.messages = resp.data));
    },
    resetTable() {
      this.messages = [];
    },
  },
};
</script>