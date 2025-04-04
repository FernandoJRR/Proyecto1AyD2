<template>
  <div class="m-6 ml-12">
    <div class="mb-6">
      <router-link to="/pacientes">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>
    </div>

    <h1 class="text-4xl font-bold mb-6">Editar Paciente</h1>

    <div
      v-if="patientState.status === 'pending'"
      class="flex justify-center items-center h-64 text-black"
    >
      Cargando datos...
    </div>

    <div
      v-else-if="patientState.status === 'error'"
      class="flex justify-center items-center h-64 text-red-600"
    >
      Error al cargar los datos del paciente
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
            Datos del Paciente
          </h1>

          <div class="flex flex-row gap-4">
            <div class="w-full">
              <FloatLabel>
                <label for="firstnames">Nombres</label>
                <InputText name="firstnames" type="text" fluid />
              </FloatLabel>
              <Message
                v-if="$form.firstnames?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.firstnames.error?.message }}
              </Message>
            </div>

            <div class="w-full">
              <FloatLabel>
                <label for="lastnames">Apellidos</label>
                <InputText name="lastnames" type="text" fluid />
              </FloatLabel>
              <Message
                v-if="$form.lastnames?.invalid"
                severity="error"
                size="small"
                variant="simple"
              >
                {{ $form.lastnames.error?.message }}
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
import { zodResolver } from '@primevue/forms/resolvers/zod'
import { z } from 'zod'
import {
  FloatLabel,
  InputText,
  Button,
  Message,
} from 'primevue'
import { reactive, ref, watch } from 'vue'
import { toast } from 'vue-sonner'
import { useQuery } from '@pinia/colada'
import {
  getPatient,
  updatePatient,
  type UpdatePatientRequestDTO
} from '~/lib/api/patients/patients'

const { state: patientState } = useCustomQuery({
  key: ['patient-edit', useRoute().params.id as string],
  query: () => getPatient(useRoute().params.id as string),
})

const initialValues = reactive({
  firstnames: '',
  lastnames: '',
  dpi: ''
})

watch(
  () => patientState.value,
  (value) => {
    if (patientState.value.status === 'success' && value.data) {
      initialValues.firstnames = value.data.firstnames
      initialValues.lastnames = value.data.lastnames
      initialValues.dpi = value.data.dpi
    }
  },
  { immediate: true }
)

const resolver = ref(
  zodResolver(
    z.object({
      firstnames: z.string().min(1, 'El nombre es obligatorio.'),
      lastnames: z.string().min(1, 'El apellido es obligatorio.'),
      dpi: z.string().min(1, 'El DPI es obligatorio.')
    })
  )
)

const onFormSubmit = (e: any) => {
  if (e.valid) {
    const payload: UpdatePatientRequestDTO = {
      firstnames: e.values.firstnames,
      lastnames: e.values.lastnames,
      dpi: e.values.dpi
    }
    mutate(payload)
  }
}

const { mutate, asyncStatus } = useMutation({
  mutation: (data: UpdatePatientRequestDTO) =>
    updatePatient(useRoute().params.id as string, data),
  onError(error) {
    console.error(error)
    toast.error('Ocurrió un error al actualizar el paciente', {
      description: error.message
    })
  },
  onSuccess() {
    toast.success('Paciente actualizado correctamente')
    navigateTo('/pacientes')
  }
})
</script>

<style scoped></style>
