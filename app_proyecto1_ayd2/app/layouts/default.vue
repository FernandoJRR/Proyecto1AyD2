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
      <template #end>
        <div class="flex flex-row">
          <div class="flex items-center gap-2">
            <RouterLink to="/perfil">
              <Button severity="secondary" variant="text"><i class="pi pi-user" />Ver Perfil</Button>
            </RouterLink>
          </div>
          <div class="flex items-center gap-2">
            <Button variant="text" @click="logout"><i class="pi pi-sign-out" />Cerrar Sesion</Button>
          </div>
        </div>
      </template>
    </Menubar>
    <div class="flex items-center justify-center bg-gray-100 pl-28 pr-28">
      <div class="w-410 bg-white p-4 h-225">
        <NuxtPage />
      </div>
    </div>
  </main>
</template>
<script setup lang="ts">
import { Menubar } from 'primevue';

const authStore = useAuthStore()
const { logout } = authStore

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

defineExpose({ options })
</script>
