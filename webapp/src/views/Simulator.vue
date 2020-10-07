<template>
    <v-card>
        <h3>Store Simulator</h3>
        <v-row>
            <v-container>
                Current backend is: {{ backend }}
            </v-container>
        </v-row>
        <v-row>
            <v-col cols="12" md="6" sm="4">
            Select number of records to send [1,1000]
            </v-col>
            <v-spacer></v-spacer>
            <v-col cols="12" md="6" sm="4">
            <v-slider
            v-model="records"
            max="1000"
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
                <v-btn
                    color="primary"
                    @click="start"
                >
                <v-icon>mdi-restart</v-icon>
                </v-btn>
            </v-col>
        </v-row>
    </v-card>
</template>
<script>
import axios from 'axios'

export default {
  data: () => ({
     backend: '', 
     records: 1,
     messages: []
  }),
  created () {
    this.initialize()
  },
  methods: {
    initialize () {
      axios.get("/api/v1/stores/backend").then((resp) => (this.backend = resp.data));
    },
    start() {
        console.log(" start with " + this.records)
        axios.post("/api/v1/stores/start/"+this.records).then(resp => this.messages = resp.data)
    }
  }
}
</script>