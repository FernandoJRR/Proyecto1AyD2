<template>
  <div class="m-6 ml-12">
    <div class="mb-6">
      <router-link to="/farmacia">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>
    </div>

    <h1 class="text-4xl font-bold mb-6">Crear Medicamento</h1>
    <div class="space-y-8 bg-white shadow-md rounded-2xl p-6 border border-gray-200">
      <Form
        v-slot="$form"
        :initialValues
        :resolver
        @submit="onFormSubmit"
        class="mt-8 flex justify-center"
      >
        <div class="flex flex-col gap-1 w-full">
          <h1 class="text-2xl font-semibold mb-6 text-black" >Datos del Medicamento</h1>

          <div class="flex flex-row gap-4">
            <div class="w-full">
              <FloatLabel>
                <label for="name">Nombre del Medicamento</label>
                <InputText name="name" type="text" fluid />
              </FloatLabel>
              <Message
                v-if="$form.name?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.name.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="description">Descripción</label>
                <InputText name="description" type="text" fluid />
              </FloatLabel>
              <Message
                v-if="$form.description?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.description.error?.message }}
              </Message>
            </div>
          </div>

          <div class="flex flex-row gap-4 mt-8">
            <div class="w-full">
              <FloatLabel>
                <label for="price">Precio</label>
                <InputNumber
                  name="price"
                  :min="0.01"
                  :minFractionDigits="2"
                  :maxFractionDigits="2"
                  mode="currency"
                  currency="GTQ"
                  placeholder="Precio del Medicamento"
                  fluid
                />
              </FloatLabel>
              <Message
                v-if="$form.price?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.price.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="quantity">Cantidad</label>
                <InputNumber
                  name="quantity"
                  :min="1"
                  placeholder="Cantidad en Stock"
                  fluid
                />
              </FloatLabel>
              <Message
                v-if="$form.quantity?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.quantity.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="minQuantity">Cantidad Mínima</label>
                <InputNumber
                  name="minQuantity"
                  :min="1"
                  placeholder="Cantidad Mínima"
                  fluid
                />
              </FloatLabel>
              <Message
                v-if="$form.minQuantity?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.minQuantity.error?.message }}
              </Message>
            </div>
          </div>

          <Button
            type="submit"
            severity="secondary"
            label="Crear"
            icon="pi pi-save"
            class="mt-8"
          />
        </div>
      </Form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { z } from 'zod';
import {
  FloatLabel,
  InputText,
  InputNumber,
  Button,
  Message,
} from 'primevue';
import { reactive } from 'vue';
import { toast } from 'vue-sonner';
import {
  createMedicine,
  type MedicinePayload,
} from '~/lib/api/medicines/medicine';

// Valores iniciales
const initialValues = reactive({
  name: '',
  description: '',
  price: 0,
  quantity: 0,
  minQuantity: 0,
});

// Validaciones con Zod
const resolver = ref(
  zodResolver(
    z.object({
      name: z.string().min(1, 'El nombre es obligatorio.'),
      description: z.string().min(1, 'La descripción es obligatoria.'),
      price: z
        .number({ message: 'El precio es obligatorio.' })
        .min(0.01, 'El precio debe ser mayor a 0.01.'),
      quantity: z
        .number({ message: 'La cantidad es obligatoria.' })
        .min(1, 'La cantidad debe ser mayor a 0.'),
      minQuantity: z
        .number({ message: 'La cantidad mínima es obligatoria.' })
        .min(1, 'La cantidad mínima debe ser mayor a 0.'),
    })
  )
);

// Acción de submit
const onFormSubmit = (e: any) => {
  if (e.valid) {
    let payload: MedicinePayload = {
      name: e.values.name,
      description: e.values.description,
      price: e.values.price,
      quantity: e.values.quantity,
      minQuantity: e.values.minQuantity,
    };

    mutate(payload);
  }
};

const { mutate, asyncStatus } = useMutation({
  mutation: (medicineData: MedicinePayload) => createMedicine(medicineData),
  onError(error) {
    console.log(error);
    toast.error('Ocurrió un error al crear el medicamento', {
      description: error,
    });
  },
  onSuccess() {
    toast.success('Medicamento creado correctamente');
    navigateTo('/farmacia');
  },
});
</script>
