import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Simulator from '../views/Simulator.vue'
import Stores from '../views/Stores.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/stores',
    name: 'Stores',
    component: Stores
  },
  {
    path: '/simulator',
    name: 'Simulator',
    component: Simulator
  },
]

const router = new VueRouter({
  routes
})

export default router
