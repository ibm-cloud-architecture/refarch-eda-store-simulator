<template>
  <div class="home">
    <Stores/>
  </div>
</template>

<script>
// @ is an alias to /src
import Stores from '@/components/Stores.vue'

export default {
  name: 'Home',
  components: {
    Stores
  }
}
</script>
