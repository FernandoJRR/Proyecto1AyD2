<template>
  <div class="grid grid-cols-4 gap-4 m-8">
    <template v-for="menu in menus">
      <MenuShortcutCard :menu="menu" />
    </template>
  </div>
</template>
<script setup lang="ts">
import MenuShortcutCard from '~/components/cards/MenuShortcutCard.vue';

const menus = [
  {
    title: 'Personal',
    description: 'Administracion de Personal',
    route: '/admin/personal',
  },
  {
    title: 'Vacaciones',
    description: 'Administracion de Vacaciones',
    route: '/admin/personal/vacaciones',
  },
  {
    title: 'Medicos Especialistas',
    description: 'Administracion de Medicos Especialistas',
    route: '/admin/medicos-especialistas',
  },
  {
    title: 'Roles',
    description: 'Administracion de roles y permisos',
    route: '/admin/tipos-de-empleado',
  },
  {
    title: 'Tarifas',
    description: 'Administracion de Tarifas',
    route: '/admin/tarifas',
  },
]

defineExpose({ menus })
</script>
