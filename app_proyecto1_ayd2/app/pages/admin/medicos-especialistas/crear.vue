<template>
  <div class="m-6 ml-12">
    <div class="mb-6">
      <router-link to="/admin/medicos-especialistas">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>
    </div>

    <h1 class="text-4xl font-bold mb-6">Crear Médico Especialista</h1>

    <div
      class="space-y-8 bg-white shadow-md rounded-2xl p-6 border border-gray-200"
    >
      <Form
        v-slot="$form"
        :initialValues
        :resolver
        @submit="onFormSubmit"
        class="mt-8 flex justify-center"
      >
        <div class="flex flex-col gap-1 w-full">
          <h1 class="text-2xl font-semibold mb-6 text-black">
            Datos del Médico Especialista
          </h1>

          <div class="flex flex-row gap-4">
            <div class="w-full">
              <FloatLabel>
                <label for="nombres">Nombres</label>
                <InputText name="nombres" type="text" fluid />
              </FloatLabel>
              <Message
                v-if="$form.nombres?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.nombres.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="apellidos">Apellidos</label>
                <InputText name="apellidos" type="text" fluid />
              </FloatLabel>
              <Message
                v-if="$form.apellidos?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.apellidos.error?.message }}
              </Message>
            </div>
          </div>

          <div class="flex flex-row gap-4 mt-8">
            <div class="w-full">
              <FloatLabel>
                <label for="dpi">DPI</label>
                <InputText name="dpi" type="text" fluid />
              </FloatLabel>
              <Message
                v-if="$form.dpi?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.dpi.error?.message }}
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
import { zodResolver } from "@primevue/forms/resolvers/zod";
import { z } from "zod";
import { FloatLabel, InputText, Button, Message } from "primevue";
import { reactive } from "vue";
import { toast } from "vue-sonner";
import {
  createSpecialistEmployee,
  type CreateSpecialistEmployeeRequestDTO,
} from "~/lib/api/admin/specialist-employee";

const initialValues = reactive({
  nombres: "",
  apellidos: "",
  dpi: "",
});

const resolver = ref(
  zodResolver(
    z.object({
      nombres: z.string().min(1, "Los nombres son obligatorios."),
      apellidos: z.string().min(1, "Los apellidos son obligatorios."),
      dpi: z.string().min(13, "El DPI debe tener al menos 13 caracteres."),
    })
  )
);

const onFormSubmit = (e: any) => {
  if (e.valid) {
    const payload: CreateSpecialistEmployeeRequestDTO = {
      nombres: e.values.nombres,
      apellidos: e.values.apellidos,
      dpi: e.values.dpi,
    };

    mutate(payload);
  }
};

const { mutate, asyncStatus } = useMutation({
  mutation: (specialistData: CreateSpecialistEmployeeRequestDTO) =>
    createSpecialistEmployee(specialistData),
  onError(error) {
    console.log(error);
    toast.error("Ocurrió un error al crear el médico especialista", {
      description: error,
    });
  },
  onSuccess() {
    toast.success("Médico especialista creado correctamente");
    navigateTo("/admin/medicos-especialistas");
  },
});
</script>
