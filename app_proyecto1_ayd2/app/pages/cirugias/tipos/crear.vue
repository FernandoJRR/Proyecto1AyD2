<template>
  <div class="m-6 ml-12">
    <div class="mb-6">
      <router-link to="/cirugias">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>
    </div>

    <h1 class="text-4xl font-bold mb-6">Crear Tipo de Cirugía</h1>
    <div class="space-y-8 bg-white shadow-md rounded-2xl p-6 border border-gray-200">
      <Form
        v-slot="$form"
        :initialValues
        :resolver
        @submit="onFormSubmit"
        class="mt-8 flex justify-center"
      >
        <div class="flex flex-col gap-1 w-full">
          <h1 class="text-2xl font-semibold mb-6 text-black">
            Datos del Tipo de Cirugía
          </h1>

          <div class="flex flex-row gap-4">
            <div class="w-full">
              <FloatLabel>
                <label for="type">Nombre del Tipo</label>
                <InputText name="type" type="text" fluid />
              </FloatLabel>
              <Message v-if="$form.type?.invalid" severity="error" size="small" variant="simple">
                {{ $form.type.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="description">Descripción</label>
                <InputText name="description" type="text" fluid />
              </FloatLabel>
              <Message v-if="$form.description?.invalid" severity="error" size="small" variant="simple">
                {{ $form.description.error?.message }}
              </Message>
            </div>
          </div>

          <div class="flex flex-row gap-4 mt-8">
            <div class="w-full">
              <FloatLabel>
                <label for="specialistPayment">Pago al Especialista</label>
                <InputNumber
                  name="specialistPayment"
                  :min="0"
                  mode="currency"
                  currency="GTQ"
                  placeholder="Pago al especialista"
                  fluid
                />
              </FloatLabel>
              <Message v-if="$form.specialistPayment?.invalid" severity="error" size="small" variant="simple">
                {{ $form.specialistPayment.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="hospitalCost">Costo Hospital</label>
                <InputNumber
                  name="hospitalCost"
                  :min="0"
                  mode="currency"
                  currency="GTQ"
                  placeholder="Costo del hospital"
                  fluid
                />
              </FloatLabel>
              <Message v-if="$form.hospitalCost?.invalid" severity="error" size="small" variant="simple">
                {{ $form.hospitalCost.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="surgeryCost">Costo Total</label>
                <InputNumber
                  name="surgeryCost"
                  :min="0"
                  mode="currency"
                  currency="GTQ"
                  placeholder="Costo de la cirugía"
                  fluid
                />
              </FloatLabel>
              <Message v-if="$form.surgeryCost?.invalid" severity="error" size="small" variant="simple">
                {{ $form.surgeryCost.error?.message }}
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
import { FloatLabel, InputText, InputNumber, Button, Message } from 'primevue';
import { reactive } from 'vue';
import { toast } from 'vue-sonner';
import { createSurgeryType, type CreateSurgeryTypeRequest } from '~/lib/api/surgeries/surgeries';

const initialValues = reactive<CreateSurgeryTypeRequest>({
  type: '',
  description: '',
  specialistPayment: 0,
  hospitalCost: 0,
  surgeryCost: 0,
});

const resolver = ref(
  zodResolver(
    z.object({
      type: z.string().min(1, 'El tipo es obligatorio.'),
      description: z.string().min(1, 'La descripción es obligatoria.'),
      specialistPayment: z.number().min(0, 'Debe ser mayor o igual a 0.'),
      hospitalCost: z.number().min(0, 'Debe ser mayor o igual a 0.'),
      surgeryCost: z.number().min(0, 'Debe ser mayor o igual a 0.'),
    })
  )
);

const onFormSubmit = (e: any) => {
  if (e.valid) {
    const payload: CreateSurgeryTypeRequest = e.values;
    mutate(payload);
  }
};

const { mutate } = useMutation({
  mutation: (data: CreateSurgeryTypeRequest) => createSurgeryType(data),
  onError: (error) => {
    toast.error('Error al crear tipo de cirugía', { description: error.message });
  },
  onSuccess: () => {
    toast.success('Tipo de cirugía creado correctamente');
    navigateTo('/cirugias');
  },
});
</script>
