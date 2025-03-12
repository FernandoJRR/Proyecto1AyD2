<template>
      <Card>
        <template #title>{{ menu.title }}</template>
        <template #content>
            <p class="m-0">
              {{ menu.description }}
            </p>
        </template>
        <template #footer>
          <NuxtLink :to="menu.route">
            <div class="flex gap-4 mt-1">
                <Button label="Ir" class="w-full">
                  Ir
                </Button>
            </div>
          </NuxtLink>
        </template>
      </Card>
</template>
<script setup lang="ts">
defineProps<{
  menu: { title: string, description: string, route: string }
}>()
</script>
