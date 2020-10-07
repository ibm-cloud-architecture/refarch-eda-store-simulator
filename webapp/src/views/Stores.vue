<template>
  <v-row>
    <v-col sm="8" offset-sm="2">
    <v-data-table
      :headers="headers"
      :items="stores"
      sort-by="Name"
      class="elevation-1"
    >
      <template v-slot:top>
        <v-toolbar flat color="white">
          <v-toolbar-title>Stores Table</v-toolbar-title>
          <v-divider class="mx-4" inset vertical></v-divider>
          <v-spacer></v-spacer>
        </v-toolbar>
      </template>
    </v-data-table>
    </v-col>
    <v-container>
       <p>We have the above stores available for the simulation, with items ready to be sold or to re-stock</p>

    </v-container>
  </v-row>
 </template>

<script>
import axios from "axios";
export default {
  name: "Stores",

  data: () => ({
    stores: [],
    headers: [
      { text: "Name", value: "name", align: "start", sortable: true },
      { text: "City", value: "city", sortable: true },
      { text: "State", value: "state", sortable: true },
      { text: "Zipcode", value: "zipcode" },
    ],
  }),
  created () {
    this.initialize()
  },
  methods: {
    initialize () {
      axios.get("/api/v1/stores").then((resp) => (this.stores = resp.data));
    }
  }
};
</script>
