<template>
  <h1 class="text-3xl font-bold underline">Hola Mundo</h1>
  <div class="grid grid-cols-3 gap-4 bg-red-400">
    <Button label="Prueba"/>
    <Button label="Prueba"/>
    <Button label="Prueba"/>
  </div>
  <h2>Nombres</h2>
  <div v-for="user in state.data" class="bg-blue-100 mb-2">
    {{user}}
  </div>
  <Button label="Abrir toast generico" @click="toast('Abriendo un toast')"/>
  <Button label="Abrir toast info" @click="toast.info('Abriendo un toast de info')"/>
  <Button label="Abrir toast error" @click="toast.error('Abriendo un toast de error')"/>
</template>
<script setup lang="ts">
import { toast } from 'vue-sonner';

const {state, asyncStatus, status} = useCustomQuery({
  key: ['test1'],
  query: () => $api('/names')
})
</script>
