<template>
  <div class="flex flex-col items-center justify-center min-h-screen">
    <h1 class="font-bold text-2xl mb-20">Bienvenido, {{ user?.username }}!</h1>
    <div class="grid grid-cols-4 gap-4">
      <template v-for="menu in menus">
        <MenuShortcutCard :menu="menu" />
      </template>
    </div>
  </div>
</template>
<script setup lang="ts">
import MenuShortcutCard from "~/components/cards/MenuShortcutCard.vue";

const { user } = storeToRefs(useAuthStore());

const menus = [
  {
    title: "Administracion",
    description: "Administracion General del Hospital",
    route: "/admin",
  },
  {
    title: "Pacientes",
    description: "Administracion de Pacientes",
    route: "/pacientes",
  },
  {
    title: "Farmacia",
    description: "Administracion de Farmacia",
    route: "/farmacia",
  },
  {
    title: 'Habitaciones',
    description: 'Administracion de Habitaciones',
    route: '/habitaciones',
  },
  {
    title: "Cirugías",
    description: "Administracion de tipos de Cirugía",
    route: "/cirugias",
  },
  {
    title: "Consultas",
    description: "Administracion de Consultas",
    route: "/consultas",
  },
  {
    title: "Reportes",
    description: "Generación de Reportes",
    route: "/reportes",
  },
];

defineExpose({ menus });
</script>
