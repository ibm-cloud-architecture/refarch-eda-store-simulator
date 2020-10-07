<template>
  <v-app>
    <v-app-bar
      app
      color="primary"
      dark
    >
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
      <v-toolbar-title>EDA- Item Store Simulator</v-toolbar-title>

      <v-spacer></v-spacer>
      
      <v-btn
        href="https://github.com/ibm-cloud-architecture/store-sale-simulator"
        target="_blank"
        text
      >
        <span class="mr-2">Git</span>
        <v-icon>mdi-git</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main>
      <Home/>
    </v-main>
  </v-app>
</template>

<script>
import Home from './views/Home';
//import About from './views/About';

export default {
  name: 'App',

  components: {
    Home,
 //   About
  },

  data: () => ({
    //
  }),
};
</script>
