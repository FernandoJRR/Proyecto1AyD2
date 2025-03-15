<template>
  <div class="flex items-center justify-center">
    <div class="w-full">
      <h1 class="text-2xl font-bold mb-1 text-center">Bienvenido!</h1>
      <p class="mb-8 text-center text-gray-600">Ingresa tus datos</p>
      <Form>
        <div class="flex flex-col gap-8 mb-6">
          <div class="">
            <FloatLabel>
              <label>Username</label>
              <InputText class="w-full" />
            </FloatLabel>
          </div>
          <div>
            <FloatLabel>
              <Password :feedback="false" toggleMask class="w-full" />
              <label>Password</label>
            </FloatLabel>
          </div>
        </div>
        <Button class="w-full py-2">Ingresar</Button>
      </Form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { FloatLabel, InputText, Password } from 'primevue';
definePageMeta({ layout: 'login' })
</script>
