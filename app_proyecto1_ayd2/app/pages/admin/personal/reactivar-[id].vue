<template>
  <div class="m-6 ml-12">
    <div class="mb-6">
      <router-link to="/admin/personal">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>
    </div>


    <h1 class="text-4xl font-bold mb-6">Reactivar Empleado</h1>
    <Form v-slot="$form" :initialValues :resolver @submit="onFormSubmit" class="mt-8 flex flex-col justify-center gap-8">
      <div class="w-full">
        <FloatLabel>
          <label>Fecha de Recontratacion</label>
          <DatePicker name="reactivation_date" class="w-full" />
        </FloatLabel>
        <Message v-if="$form.reactivation_date?.invalid" severity="error" size="small" variant="simple">{{
          $form.reactivation_date.error?.message
          }}</Message>
      </div>
      <Button type="submit" severity="secondary" label="Reactivar" />
    </Form>
  </div>
</template>
<script setup lang="ts">
import { useMutation } from '@pinia/colada';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { toast } from 'vue-sonner';
import { z } from 'zod';
import { reactivateEmployee, type EmployeeReactivationPayload } from '~/lib/api/admin/employee';

const resolver = ref(zodResolver(
  z.object({
    reactivation_date: z.date()
  })
))

const initialValues = reactive({
  reactivation_date: new Date()
});

const onFormSubmit = (e: any) => {
  if (e.valid) {
    console.log(e.values)

    let payload: EmployeeReactivationPayload = {
      reactivationDate: e.values.reactivation_date,
    }

    reactivateEmployeeMutate(payload)
  }
};

const { mutate: reactivateEmployeeMutate } = useMutation({
  mutation: (employeeData: EmployeeReactivationPayload) => reactivateEmployee(employeeData, useRoute().params.id as string),
  onError(error) {
    toast.error('Ocurrió un error al reactivar al empleado', {
      description: error.message
    })
  },
  onSuccess() {
    toast.success('Empleado desactivado correctamente')
    navigateTo('/admin/personal')
  }
})
</script>
