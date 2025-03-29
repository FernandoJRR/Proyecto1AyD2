<template>
  <div class="m-6 ml-12">
    <div class="mb-6">
      <router-link to="/cirugias">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>
    </div>

    <h1 class="text-4xl font-bold mb-6">Editar Tipo de Cirugía</h1>

    <div
      v-if="surgeryTypeState.status === 'pending'"
      class="flex justify-center items-center h-64 text-black"
    >
      Cargando datos...
    </div>

    <div
      v-else-if="surgeryTypeState.status === 'error'"
      class="flex justify-center items-center h-64 text-red-600"
    >
      Error al cargar los datos del tipo de cirugía
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
            Datos del Tipo de Cirugía
          </h1>

          <div class="flex flex-row gap-4">
            <div class="w-full">
              <FloatLabel>
                <label for="type">Nombre</label>
                <InputText name="type" type="text" fluid />
              </FloatLabel>
              <Message
                v-if="$form.type?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.type.error?.message }}
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
                <label for="specialistPayment">Pago Especialista</label>
                <InputNumber
                  name="specialistPayment"
                  :min="0.01"
                  mode="currency"
                  currency="GTQ"
                  fluid
                />
              </FloatLabel>
              <Message
                v-if="$form.specialistPayment?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.specialistPayment.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="hospitalCost">Costo Hospital</label>
                <InputNumber
                  name="hospitalCost"
                  :min="0.01"
                  mode="currency"
                  currency="GTQ"
                  fluid
                />
              </FloatLabel>
              <Message
                v-if="$form.hospitalCost?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.hospitalCost.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="surgeryCost">Costo Cirugía</label>
                <InputNumber
                  name="surgeryCost"
                  :min="0.01"
                  mode="currency"
                  currency="GTQ"
                  fluid
                />
              </FloatLabel>
              <Message
                v-if="$form.surgeryCost?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.surgeryCost.error?.message }}
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
import { FloatLabel, InputText, InputNumber, Button, Message } from "primevue";
import { reactive, ref, watch } from "vue";
import { toast } from "vue-sonner";
import { useQuery } from "@pinia/colada";
import {
  getSurgeryType,
  createSurgeryType,
  type CreateSurgeryTypeRequest,
  type UpdateSurgeryTypeRequestDTO,
  updateSurgeryType,
} from "~/lib/api/surgeries/surgeries";

const { state: surgeryTypeState } = useCustomQuery({
  key: ["surgery-type-edit", useRoute().params.id as string],
  query: () => getSurgeryType(useRoute().params.id as string),
});

const initialValues = reactive({
  type: "",
  description: "",
  specialistPayment: 0,
  hospitalCost: 0,
  surgeryCost: 0,
});

watch(
  () => surgeryTypeState.value,
  (value) => {
    if (surgeryTypeState.value.status === "success" && value.data) {
      initialValues.type = value.data.type;
      initialValues.description = value.data.description;
      initialValues.specialistPayment = value.data.specialistPayment;
      initialValues.hospitalCost = value.data.hospitalCost;
      initialValues.surgeryCost = value.data.surgeryCost;
    }
  },
  { immediate: true }
);

const resolver = ref(
  zodResolver(
    z.object({
      type: z.string().min(1, "El nombre es obligatorio."),
      description: z.string().min(1, "La descripción es obligatoria."),
      specialistPayment: z.number().min(0.01, "Pago especialista requerido."),
      hospitalCost: z.number().min(0.01, "Costo hospital requerido."),
      surgeryCost: z.number().min(0.01, "Costo cirugía requerido."),
    })
  )
);

const onFormSubmit = (e: any) => {
  if (e.valid) {
    const payload: UpdateSurgeryTypeRequestDTO = {
      type: e.values.type,
      description: e.values.description,
      specialistPayment: e.values.specialistPayment,
      hospitalCost: e.values.hospitalCost,
      surgeryCost: e.values.surgeryCost,
    };
    mutate(payload);
  }
};

const { mutate, asyncStatus } = useMutation({
  mutation: (data: UpdateSurgeryTypeRequestDTO) =>
    updateSurgeryType(useRoute().params.id as string, data),
  onError(error) {
    toast.error("Ocurrió un error al actualizar el tipo de cirugía", {
      description: error.message,
    });
  },
  onSuccess() {
    toast.success("Tipo de cirugía actualizado correctamente");
    navigateTo("/cirugias");
  },
});
</script>
