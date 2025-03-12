<template>
  <h1 class="text-3xl font-bold underline">Hola Mundo</h1>
  <div class="grid grid-cols-3 gap-4 bg-red-400">
    <Button label="Prueba"/>
    <Button label="Prueba"/>
    <Button label="Prueba"/>
  </div>
  <h2>Nombres</h2>
  <div v-for="name in state.data" class="bg-blue-100 mb-2">
    {{name}}
  </div>
</template>
<script setup lang="ts">
const {state, asyncStatus, status} = useQuery({
  key: ['test1'],
  query: () => $api('/names')
})
</script>
