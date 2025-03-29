<template>
  <div class="m-6 ml-12">
    <div class="mb-6">
      <router-link to="/admin/personal">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>
    </div>


    <h1 class="text-4xl font-bold mb-6">Deshabilitar Empleado</h1>
    <Form v-slot="$form" :initialValues :resolver @submit="onFormSubmit" class="mt-8 flex flex-col justify-center gap-8">
      <div class="w-full">
        <template v-if="deactivationTypes.status === 'success'">
          <FloatLabel>
            <label>Razon de Desactivacion</label>
            <Select name="type" v-model="selectedType" optionLabel="type" optionValue="id" :options="deactivationTypes.data"
              placeholder="Selecciona una razon" fluid />
          </FloatLabel>
          <Message v-if="$form.type?.invalid" severity="error" size="small" variant="simple">{{
            $form.type.error?.message }}</Message>
        </template>
      </div>
      <div class="w-full">
        <FloatLabel>
          <label>Fecha de Desactivacion</label>
          <DatePicker name="deactivation_date" class="w-full" />
        </FloatLabel>
        <Message v-if="$form.deactivation_date?.invalid" severity="error" size="small" variant="simple">{{
          $form.deactivation_date.error?.message
          }}</Message>
      </div>
      <Button type="submit" severity="secondary" label="Desactivar" />
    </Form>
  </div>
</template>
<script setup lang="ts">
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { toast } from 'vue-sonner';
import { z } from 'zod';
import { deactivateEmployee, type EmployeeDeactivationPayload } from '~/lib/api/admin/employee';
import { getDeactivationHistoryTypes } from '~/lib/api/admin/history-type';

const resolver = ref(zodResolver(
  z.object({
    type: z.string(),
    deactivation_date: z.date()
  })
))

const initialValues = reactive({
  type: '',
  deactivation_date: new Date()
});

const selectedType = ref('')

const { state: deactivationTypes } = useCustomQuery({
  key: ['historyTypes-deactivation'],
  query: () => getDeactivationHistoryTypes()
})

const onFormSubmit = (e: any) => {
  if (e.valid) {
    console.log(e.values)

    let payload: EmployeeDeactivationPayload = {
      deactivationDate: e.values.deactivation_date,
      historyTypeId: { id: e.values.type },
    }
    console.log(payload)

    deactivateEmployeeMutate(payload)
  }
};

const { mutate: deactivateEmployeeMutate } = useMutation({
  mutation: (employeeData: EmployeeDeactivationPayload) => deactivateEmployee(employeeData, useRoute().params.id as string),
  onError(error) {
    toast.error('Ocurrió un error al desactivar al empleado', {
      description: error.message
    })
  },
  onSuccess() {
    toast.success('Empleado desactivado correctamente')
    navigateTo('/admin/personal')
  }
})
</script>
