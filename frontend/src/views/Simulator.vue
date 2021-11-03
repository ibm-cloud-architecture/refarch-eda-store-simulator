<template>
  <v-card>
    <h3>Store Simulator</h3>
    <v-row align-content="center">
      <v-container>
        Current backend is: {{ backend }}
        <v-radio-group v-model="backend">
          <v-radio
            v-for="be in backends"
            :key="be"
            :label="`${be}`"
            :value="be"
            @click="resetTable"
          ></v-radio>
        </v-radio-group>
      </v-container>
    </v-row>
    <v-row align-content="center">
      <v-col cols="12" md="6" sm="3">
        <v-container>
          <v-row>
            <h3>Random</h3>
            <br/><p>Number of records to send:</p>
          </v-row>
          <v-row>
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
            v-model="records"></v-text-field>
          </v-row>
          <v-row>
            <v-btn color="primary" @click="start">
              <v-icon>mdi-restart</v-icon>
            </v-btn>
          </v-row>
      </v-container>
    </v-col>
    <v-col  cols="12" md="6" sm="3">
       <v-row>
        <h1>Controlled scenario</h1>
      </v-row>
      <v-row>
            <v-btn color="primary" @click="startControlled">
              <v-icon>mdi-restart</v-icon>
            </v-btn>
          </v-row>
    </v-col>
    </v-row>
    <v-row v-if="messages.length > 0">
      <Messages :messagesIn="messages"></Messages>
    </v-row>
  </v-card>
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
      axios
        .post("/api/stores/v1/startControlled/")
        .then((resp) => (this.messages = resp.data));
    },
    resetTable() {
      this.messages = [];
    },
  },
};
</script>