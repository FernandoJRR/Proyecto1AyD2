<template>
  <div class="m-6 ml-12">
    <div class="mb-6">
      <Button label="Volver" icon="pi pi-arrow-left" text @click="router.back()" />
    </div>

    <h1 class="text-4xl font-bold mb-6">Crear Paciente</h1>

    <div class="space-y-8 bg-white shadow-md rounded-2xl p-6 border border-gray-200">
      <Form
        v-slot="$form"
        :initialValues
        :resolver
        @submit="onFormSubmit"
        class="mt-8 flex justify-center"
      >
        <div class="flex flex-col gap-1 w-full">
          <h1 class="text-2xl font-semibold mb-6 text-black">Datos del Paciente</h1>

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

import { zodResolver } from '@primevue/forms/resolvers/zod'
import { z } from 'zod'
import {
  FloatLabel,
  InputText,
  Button,
  Message
} from 'primevue'
import { reactive, ref } from 'vue'
import { toast } from 'vue-sonner'
import {
  createPatient,
  type CreatePatientRequestDTO
} from '~/lib/api/patients/patients'

const router = useRouter()

const initialValues = reactive({
  firstnames: '',
  lastnames: '',
  dpi: ''
})

const resolver = ref(
  zodResolver(
    z.object({
      firstnames: z.string().min(1, 'El nombre es obligatorio.'),
      lastnames: z.string().min(1, 'El apellido es obligatorio.'),
      dpi: z.string().min(13, 'El DPI debe tener al menos 13 dígitos.')
    })
  )
)

const onFormSubmit = (e: any) => {
  if (e.valid) {
    const payload: CreatePatientRequestDTO = {
      firstnames: e.values.firstnames,
      lastnames: e.values.lastnames,
      dpi: e.values.dpi
    }

    mutate(payload)
  }
}

const { mutate, asyncStatus } = useMutation({
  mutation: (patientData: CreatePatientRequestDTO) => createPatient(patientData),
  onError(error) {
    console.error(error)
    toast.error('Ocurrió un error al crear el paciente', {
      description: error
    })
  },
  onSuccess() {
    toast.success('Paciente creado correctamente')
    navigateTo('/pacientes')
  }
})
</script>
