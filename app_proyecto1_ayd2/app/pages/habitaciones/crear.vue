<template>
  <div class="p-8 max-w-4xl mx-auto">
    <!-- encabezado y navegación -->
    <div class="mb-6">
      <router-link to="/habitaciones/">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>

    </div>
    <h1 class="text-4xl font-bold mb-6">Crear una habitación</h1>
    <!-- formulario principal -->
    <Form v-slot="$form" :initialValues :resolver @submit="onFormSubmit"
      class="space-y-8 bg-white shadow-md rounded-2xl p-6 border border-gray-200">
      <div class="space-y-6">
        <h2 class="text-2xl font-semibold">Datos de la habitación</h2>

        <div class="w-full">
          <FloatLabel>
            <label for="number">Codigo de habitación</label>
            <InputText id="number" name="number" type="text" class="w-full" />
          </FloatLabel>
          <Message v-if="$form.number?.invalid" severity="error" size="small" variant="simple">
            {{ $form.number.error?.message }}
          </Message>
        </div>


        <div class="w-full">
          <FloatLabel>
            <label for="dailyMaintenanceCost">Costo de mantenimiento</label>
            <InputNumber id="dailyMaintenanceCost" name="dailyMaintenanceCost" class="w-full" currency="GTQ"
              mode="currency" :min="0" :minFractionDigits="2" :maxFractionDigits="2"
              placeholder="Costo de mantenimiento" />
          </FloatLabel>
          <Message v-if="$form.dailyMaintenanceCost?.invalid" severity="error" size="small" variant="simple">
            {{ $form.dailyMaintenanceCost.error?.message }}
          </Message>
        </div>

        <div class="w-full">
          <FloatLabel>
            <label for="dailyPrice">Precio diario</label>
            <InputNumber id="dailyPrice" name="dailyPrice" class="w-full" currency="GTQ" mode="currency" :min="0"
              :minFractionDigits="2" :maxFractionDigits="2" placeholder="Precio diario" />
          </FloatLabel>
          <Message v-if="$form.dailyPrice?.invalid" severity="error" size="small" variant="simple">
            {{ $form.dailyPrice.error?.message }}
          </Message>
        </div>
      </div>

      <!-- Botón de envío -->
      <div class="pt-4">
        <Button type="submit" label="Crear" icon="pi pi-save" severity="secondary" />
      </div>
    </Form>
  </div>
</template>

<script setup lang="ts">
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { FloatLabel } from 'primevue';
import { toast } from 'vue-sonner';
import { z } from 'zod';;
import { createRoom, type RoomPayLoad } from '~/lib/api/habitaciones/room';

const initialValues = reactive({
  name: 'dd',
  cost: 0,
  price: 0,
});


const resolver = ref(zodResolver(
  z.object({
    number: z.string()
      .min(1, 'El número de habitación es obligatorio')
      .max(100, 'El número de habitación no puede exceder los 100 caracteres'),


    //el preprocesor se encarga de convertir el valor a un numero, si no es un numero lanza un error
    dailyMaintenanceCost: z.number({
      required_error: 'El costo de mantenimiento diario es obligatorio',
      invalid_type_error: 'El costo de mantenimiento diario debe ser un número',
    }).min(0, 'El costo de mantenimiento diario debe ser mayor o igual a 0'),

    //el preprocesor se encarga de convertir el valor a un numero, si no es un numero lanza un error
    dailyPrice: z.number({
      required_error: 'El precio diario es obligatorio',
      invalid_type_error: 'El precio diario debe ser un número',
    }).min(0, 'El precio diario debe ser mayor o igual a 0')
  })
));



const onFormSubmit = (e: any) => {
  if (e.valid) {
    console.log(e.values)

    let payload: RoomPayLoad = {
      number: e.values.number,
      dailyMaintenanceCost: e.values.dailyMaintenanceCost,
      dailyPrice: e.values.dailyPrice
    }


    mutate(payload)
  }
};

const { mutate, asyncStatus } = useMutation({
  mutation: (employeeData: RoomPayLoad) => createRoom(employeeData),
  onError(error) {
    toast.error('Ocurrió un error al crear la habitación', {
      description: `${(error.message)}`
    })
  },
  onSuccess() {
    toast.success('La habitación se ha creado correctamente')
    navigateTo('/habitaciones/')
  }
})


</script>