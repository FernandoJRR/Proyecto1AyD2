<template>
  <div class="m-6 ml-12">
    <div class="mb-6">
      <router-link to="/admin/medicos-especialistas">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>
    </div>

    <h1 class="text-4xl font-bold mb-6">Editar Médico Especialista</h1>

    <div
      v-if="specialistState.status === 'pending'"
      class="flex justify-center items-center h-64 text-black"
    >
      Cargando datos...
    </div>

    <div
      v-else-if="specialistState.status === 'error'"
      class="flex justify-center items-center h-64 text-red-600"
    >
      Error al cargar los datos del especialista
    </div>

    <div
      v-else
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
            label="Guardar Cambios"
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
import { reactive, ref, watch } from "vue";
import { toast } from "vue-sonner";
import { useQuery } from "@pinia/colada";
import {
  getSpecialistEmployeeById,
  updateSpecialistEmployee,
  type UpdateSpecialistEmployeeRequestDTO,
} from "~/lib/api/admin/specialist-employee";

const { state: specialistState } = useCustomQuery({
  key: ["specialist-edit",useRoute().params.id as string],
  query: () => getSpecialistEmployeeById(useRoute().params.id as string),
});

const initialValues = reactive({
  nombres: "",
  apellidos: "",
  dpi: "",
});

watch(
  () => specialistState.value,
  (value) => {
    if (specialistState.value.status === "success" && value.data) {
      initialValues.nombres = value.data.nombres;
      initialValues.apellidos = value.data.apellidos;
      initialValues.dpi = value.data.dpi;
    }
  },
  { immediate: true }
);

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
    const payload: UpdateSpecialistEmployeeRequestDTO = {
      nombres: e.values.nombres,
      apellidos: e.values.apellidos,
      dpi: e.values.dpi,
    };
    mutate(payload);
  }
};

const { mutate, asyncStatus } = useMutation({
  mutation: (specialistData: UpdateSpecialistEmployeeRequestDTO) =>
    updateSpecialistEmployee(useRoute().params.id as string, specialistData),
  onError(error) {
    console.error(error);
    toast.error("Ocurrió un error al actualizar el especialista", {
      description: error.message,
    });
  },
  onSuccess() {
    toast.success("Médico especialista actualizado correctamente");
    navigateTo("/admin/medicos-especialistas");
  },
});
</script>

<style scoped></style>
