<template>
  <v-row>
    <v-row>
      <v-col sm="8" offset-sm="2">
        <v-data-table
          :headers="headers"
          :items="messagesIn"
          sort-by="Id"
          class="elevation-1"
        >
          <template v-slot:top>
            <v-toolbar flat color="white">
              <v-toolbar-title>Messages Sent</v-toolbar-title>
              <v-divider class="mx-4" inset vertical></v-divider>
              <v-spacer></v-spacer>
            </v-toolbar>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
    <v-row> 
       <v-col class="mx-5" width="400px">
           <v-btn color="primary" @click="close">
                <v-icon>mdi-close</v-icon>
              </v-btn>
         </v-col>
    </v-row>
  </v-row>
</template>

<script>
export default {
  name: "Messages",
  props: {
    messagesIn: [],
  },
  data: () => ({
    headers: [
      { text: "Time", value: "timestamp", align: "start", sortable: true },
      { text: "Store", value: "storeName", align: "start", sortable: true },
      { text: "Sku", value: "sku", align: "start", sortable: true },
      { text: "Type", value: "type", sortable: true },
      { text: "Quantity", value: "quantity", sortable: true },
      { text: "Price", value: "price" },
    ],
  }),
  methods: {
    close(){
      
    }
  }
};
</script>