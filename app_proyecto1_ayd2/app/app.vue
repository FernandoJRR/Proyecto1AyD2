<template>
  <main>
    <NuxtLoadingIndicator
      :throttle="130"
      :height="4"
      color="var(--p-primary-400)"
    />
    <NuxtLayout />
    <Toaster
      position="top-center"
      :close-button="false"
      :toast-options="{
        unstyled: true,
        duration: 3000,
        classes: {
          toast:
            'w-full py-2 text-sm px-3 border border-black/85 rounded-lg text-current font-sans flex items-center gap-3 shadow-lg',
          default: 'bg-surface-50',
          description: 'text-gray-500 text-sm',
          warning:
            'bg-yellow-200 text-yellow-800',
          error: 'bg-red-200 text-red-800',
          info: 'bg-sky-200 text-sky-800',
          success:
            'bg-green-200 text-green-800',
          closeButton:
            '!text-surface-50 !border-black/85 !text-color'
        }
      }"
    >
      <template #error-icon>
        <i class="pi pi-exclamation-triangle" />
      </template>
      <template #info-icon>
        <i class="pi pi-info-circle" />
      </template>
      <template #success-icon>
        <i class="pi pi-check" />
      </template>
      <template #warning-icon>
        <i class="pi pi-exclamation-circle" />
      </template>
    </Toaster>
  </main>
</template>
<script setup lang="ts">
import { Toaster } from 'vue-sonner';
</script>
