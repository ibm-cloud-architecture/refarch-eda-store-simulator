<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <div class="d-flex align-center">
        <v-img
          alt="IBM Cloud Logo"
          class="shrink mr-2"
          contain
          src="https://cdn.vuetifyjs.com/images/logos/vuetify-logo-dark.png"
          transition="scale-transition"
          width="40"
        />
      </div>
      <v-toolbar-title>EDA- Item Store Simulator - v 0.1.0 </v-toolbar-title>

      <v-spacer></v-spacer>
      <v-btn to="/" text>
          <v-icon left>mdi-home</v-icon>
          Home
        </v-btn>
        <v-btn to="/stores" text>
          <v-icon left>mdi-store</v-icon>
          Stores
        </v-btn>
         <v-btn to="/simulator" text>
          <v-icon left>mdi-table-settings</v-icon>
          Simulator
        </v-btn>
      <v-btn
        href="https://github.com/ibm-cloud-architecture/refarch-eda-store-simulator"
        target="_blank"
        text
      >
        <span class="mr-2">Git</span>
        <v-icon>mdi-git</v-icon>
      </v-btn>
    </v-app-bar>
    <v-content>
      <v-container>
        <router-view />
      </v-container>
    </v-content>
  </v-app>
</template>

<script>

export default {
  name: "App",
  data: () => ({
    //
  }),
};
</script>
