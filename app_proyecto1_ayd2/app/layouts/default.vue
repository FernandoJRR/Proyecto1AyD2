<template>
  <main>
    <Menubar :model="options">
            <template #item="{ item, props, hasSubmenu }">
                <router-link v-if="item.route" v-slot="{ href, navigate }" :to="item.route" custom>
                    <a v-ripple :href="href" v-bind="props.action" @click="navigate">
                        <span :class="item.icon" />
                        <span>{{ item.label }}</span>
                    </a>
                </router-link>
                <a v-else v-ripple :href="item.url" :target="item.target" v-bind="props.action">
                    <span :class="item.icon" />
                    <span>{{ item.label }}</span>
                    <span v-if="hasSubmenu" class="pi pi-fw pi-angle-down" />
                </a>
            </template>
        </Menubar>
    <NuxtPage />
  </main>
</template>
<script setup lang="ts">
import { Menubar } from 'primevue';

const options = ref([
  {
    label: 'Inicio',
    icon: 'pi pi-home',
    route: '/'
  },
  {
    label: 'Administracion',
    icon: 'pi pi-cog',
    route: '/admin'
  }
])
</script>

