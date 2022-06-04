<template>
  <v-container>
    <v-card elevation="2" tile height=600px>
      <v-card-title>Store Simulator Controller</v-card-title>
      <v-row align-content="center">
        <v-col  class="d-flex align-center" cols="12" md="6" sm="3">
          <v-row class="ma-2">
            Current backend(s) are: {{ backend }}
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
              <p>Select the number of random records to send:</p>
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
        <v-col class="mx-5" height="300px">
          <v-row>
          <h3 class="mx-auto">Random non stop</h3>
          </v-row>
          <v-row>
          <p>Use this option to send random records continuously until you stop</p>
          </v-row>
        </v-col>
        <v-col class="mx-5"  height="300px">
          <v-row>
            <h3 class="mx-auto">Controlled scenario</h3>
          </v-row>
          <v-row>
          <p>Use this option to send predefined records - better for testing item and store inventories results</p>
          </v-row>
        </v-col>
      </v-row>
      <v-row align-content="center">
         <v-col class="mx-5" width="400px">
         <v-row justify="center">
             <v-dialog
               v-model="dialog"
               scrollable
               max-width="800px"
             >
             <template v-slot:activator="{ on, attrs }">
                  <v-btn
                       color="primary"
                       @click="start"
                       v-bind="attrs"
                       v-on="on">
                     <v-icon>mdi-restart</v-icon>
                  </v-btn>
              </template>
                <v-card>
                                     <v-card-actions>
                                               <v-btn
                                                 color="blue darken-1"
                                                 text
                                                 @click="dialog = false"
                                               >
                                                 Close
                                               </v-btn>

                                      </v-card-actions>
                    <v-row v-if="messages.length > 0">
                            <Messages :messagesIn="messages"></Messages>
                     </v-row>

               </v-card>
            </v-dialog>
         </v-row>
         </v-col>
         <v-col class="mx-5" width="400px">
           <v-col> 

            <v-btn
                class="ui button big toggle"
                color="primary"
                :class="{active:isActive}"
                @click="toggle"
                >
                  <v-icon v-if="isActive">mdi-stop</v-icon>
                   <v-icon v-else>mdi-restart</v-icon>
            </v-btn>
           </v-col>
          </v-col>
          <v-col class="mx-5" width="400px">
          <v-row justify="center">
                       <v-dialog
                         v-model="dialog"
                         scrollable
                         max-width="800px"
                       >
                       <template v-slot:activator="{ on, attrs }">
                            <v-btn
                                 color="primary"
                                 @click="startControlled"
                                 v-bind="attrs"
                                 v-on="on">
                               <v-icon>mdi-run</v-icon>
                            </v-btn>
                        </template>
                          <v-card>
                                <v-card-actions>
                                         <v-btn
                                            color="blue darken-1"
                                            text
                                            @click="dialog = false" >
                                            Close
                                         </v-btn>

                                </v-card-actions>
                              <v-row v-if="messages.length > 0">
                                      <Messages :messagesIn="messages"></Messages>
                               </v-row>

                         </v-card>
                      </v-dialog>
                   </v-row>


          </v-col>
      </v-row>


    </v-card>
  </v-container>
</template>
<script>
// eslint-disable-next-line no-unused-vars
/* eslint-disable */
import axios from "axios";
import Messages from "./Message.vue";

export default {
  components: {
    Messages,
  },
  data: () => ({
    dialogm1: '',
    dialog: false,

    backend: "",
    backends: [],
    records: 1,
    messages: [],
    isActive: false,
  }),
  created() {
    this.initialize();
  },
  methods: {
    toggle() {
      if (!this.isActive) {
        this.isActive = true;
        this.startRandom();
      } else {
        this.isActive = false;
       this.stop();
      }
    },
    initialize() {
      axios
        .get("/api/stores/v1/backends")
        .then((resp) => (this.backends = resp.data));
    },
    start() {
      console.log(" start with " + this.records + " records to " + this.backend);
      let control = { records: this.records, backend: this.backend, type: "randomMax" };
      axios
        .post("/api/stores/v1/start/", control)
        .then((resp) => (this.messages = resp.data));
    },
    startControlled() {
     
      let control = { records: this.records, backend: this.backend, type: "controlled" };
      console.log(" startControlled with " + control);
      axios
        .post("/api/stores/v1/start/",control)
        .then((resp) => (this.messages = resp.data));
    },
    startRandom() {
      console.log(" start random with " + this.records + " records to " + this.backend);
      let control = { records: this.records, backend: this.backend, type: "random" };
      axios
        .post("/api/stores/v1/start/", control)
        .then((resp) => (this.messages = resp.data));
    },
    stop() {
      console.log(" stop random ");
     
      let control = { records: this.records, backend: this.backend, type: "stop" };
      axios
        .post("/api/stores/v1/stop/", control)
        .then((resp) => (this.messages = resp.data));
    },
    resetTable() {
      this.messages = [];
    },
  },
};
</script>