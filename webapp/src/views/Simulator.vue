<template>
    <v-card>
        <h3>Store Simulator</h3>
        <v-row>
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
        <v-row>
            <v-col cols="12" md="6" sm="4">
            Select number of records to send with the slider [1,100] or input field
            </v-col>
            <v-spacer></v-spacer>
            <v-col cols="12" md="6" sm="4">
            <v-slider
            v-model="records"
            max="100"
            min="1"
            thumb-label="always"
            ></v-slider>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="2" md="2" sm="2"
            align-self="center"
            offset=6
            >
            <v-text-field label="Number of records" v-model="records"></v-text-field>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="2" md="2" sm="2"
            align-self="center"
            offset=6
            >
                <v-btn
                    color="primary"
                    @click="start"
                >
                <v-icon>mdi-restart</v-icon>
                </v-btn>
            </v-col>
        </v-row>
        <v-row v-if="messages.length > 0">
            <Messages :messagesIn="messages"></Messages>
        </v-row>
    </v-card>
</template>
<script>
import axios from 'axios'
import Messages from './Message.vue'

export default {
    components: {
       Messages
    },
  data: () => ({
     backend: '', 
     backends: [],
     records: 1,
     messages: []
  }),
  created () {
    this.initialize()
  },
  methods: {
    initialize () {
      axios.get("/api/v1/stores/backends").then((resp) => (this.backends = resp.data));
    },
    start() {
        console.log(" start with " + this.records)
        let control = {records: this.records, backend: this.backend}
        axios.post("/api/v1/stores/start/",control).then(resp => this.messages = resp.data)
    },
    resetTable() {
      this.messages = []
    }
  }
}
</script>