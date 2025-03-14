<template>
  <div class="m-6 ml-12">
    <router-link to="/admin/personal">
      <Button label="Ver Todos" icon="pi pi-arrow-left" text />
    </router-link>
    <h1 class="text-4xl">Crear Usuario</h1>
    <div>
      <Form v-slot="$form" :initialValues :resolver @submit="onFormSubmit" class="mt-8 flex justify-center">
        <div class="flex flex-col gap-1">
          <h1 class="text-2xl font-semibold mb-6">Datos del Empleado</h1>
          <div class="flex flex-row gap-4">
            <div class="w-full">
              <FloatLabel>
                <label>Nombres</label>
                <InputText name="nombres" type="text" fluid />
              </FloatLabel>
              <Message v-if="$form.nombres?.invalid" severity="error" size="small" variant="simple">{{
                $form.nombres.error?.message }}</Message>
            </div>
            <div class="w-full">
              <FloatLabel>
                <label>Apellidos</label>
                <InputText name="apellidos" type="text" fluid />
              </FloatLabel>
              <Message v-if="$form.apellidos?.invalid" severity="error" size="small" variant="simple">{{
                $form.apellidos.error?.message }}</Message>
            </div>
          </div>
          <div class="flex flex-row mt-8">
            <div>
              <FloatLabel>
                <label>Salario</label>
                <InputNumber name="salario" :min="1" :minFractionDigits="2" :maxFractionDigits="2" 
                  mode="currency" currency="GTQ" placeholder="Salario" fluid />
              </FloatLabel>
              <Message v-if="$form.salario?.invalid" severity="error" size="small" variant="simple">{{
                $form.salario.error?.message }}</Message>
            </div>
            <div>
              <div class="flex flex-row items-center gap-4 ml-4">
                <ToggleSwitch name="has_porcentaje_iggs" class="min-w-10" />
                <FloatLabel>
                  <label>Porcentaje IGGS</label>
                  <InputNumber name="porcentaje_iggs" :min="1" :max="100" suffix="%" placeholder="Porcentaje IGGS" fluid
                    :disabled="!$form.has_porcentaje_iggs?.value" />
                </FloatLabel>
              </div>
              <Message v-if="$form.porcentaje_iggs?.invalid" severity="error" size="small" variant="simple">{{
                $form.porcentaje_iggs.error?.message }}</Message>
            </div>
            <div>
              <div class="flex flex-row items-center gap-4 ml-4">
                <ToggleSwitch name="has_porcentaje_irtra" class="min-w-10" />
                <FloatLabel>
                  <label>Porcentaje IRTRA</label>
                  <InputNumber name="porcentaje_irtra" :min="1" :max="100" suffix="%" label="Porcentaje IRTRA" fluid
                    :disabled="!$form.has_porcentaje_irtra?.value" />
                </FloatLabel>
              </div>
              <Message v-if="$form.porcentaje_irtra?.invalid" severity="error" size="small" variant="simple">{{
                $form.porcentaje_irtra.error?.message }}</Message>
            </div>
          </div>
          <div class="mt-8">
            <template v-if="userTypes.status === 'success'">
              <FloatLabel>
                <label>Tipo de Usuario</label>
                <Select name="type" v-model="selectedType" optionLabel="name" optionValue="id" 
                  :options="userTypes.data" placeholder="Selecciona un tipo de usuario" fluid />
              </FloatLabel>
            </template>
            <Message v-if="$form.type?.invalid" severity="error" size="small" variant="simple">{{ $form.city.error.message }}</Message>
          </div>
          <h1 class="text-2xl font-semibold mb-6 mt-6">Datos del Usuario</h1>
          <div>
            <FloatLabel>
              <label>Username</label>
              <InputText name="username" type="text" fluid />
            </FloatLabel>
            <Message v-if="$form.username?.invalid" severity="error" size="small" variant="simple">{{
              $form.username.error?.message }}</Message>
          </div>
          <div class="mt-6">
            <FloatLabel>
              <Password name="password" :feedback="false" fluid toggleMask />
              <label for="over_label">Password</label>
            </FloatLabel>
            <Message v-if="$form.password?.invalid" severity="error" size="small" variant="simple">{{
              $form.password.error?.message }}</Message>
          </div>
          <div class="mt-6">
            <FloatLabel>
              <Password name="password_repeat" :feedback="false" fluid toggleMask />
              <label for="over_label">Repite Password</label>
            </FloatLabel>
            <Message v-if="$form.password_repeat?.invalid" severity="error" size="small" variant="simple">{{
              $form.password_repeat.error?.message }}</Message>
          </div>
          <Button type="submit" severity="secondary" label="Crear" />
        </div>
      </Form>
    </div>
  </div>
</template>
<script setup lang="ts">
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { FloatLabel, InputNumber, Password, ToggleSwitch } from 'primevue';
import { z } from 'zod';

const initialValues = reactive({
  nombres: '',
  apellidos: '',
  salario: 0,
  has_porcentaje_iggs: true,
  porcentaje_iggs: 5,
  has_porcentaje_irtra: true,
  porcentaje_irtra: 5,
  type: 0,

  username: '',
  password: '',
  password_repeat: '',
});

const selectedType = ref(0)

const resolver = ref(zodResolver(
  z.object({
    nombres: z.string().min(1, 'Los nombres son obligatorios.'),
    apellidos: z.string().min(1, 'Los apellidos son obligatorios.'),
    salario: z.number({ message: "El salario es obligatorio." }).min(1, 'El salario debe ser un numero positivo.'),

    has_porcentaje_iggs: z.boolean(),
    porcentaje_iggs: z.union([
      z.number().min(1, "El porcentaje debe ser mayor a 0.").max(100, "El porcentaje no puede ser mayor a 100"),
      z.literal(null) // Allows null when `has_porcentaje_iggs` is false
    ]).optional(),

    has_porcentaje_irtra: z.boolean(),
    porcentaje_irtra: z.union([
      z.number().min(1, "El porcentaje debe ser mayor a 0.").max(100, "El porcentaje no puede ser mayor a 100"),
      z.literal(null)
    ]).optional(),

    username: z.string().min(8, 'Debes ingresar un username con al menos 8 caracteres'),
    password: z.string().min(8, 'Debes ingresar un password con al menos 8 caracteres'),
    password_repeat: z.string({message: 'Debes confirmar el password'})
  }).superRefine((data, ctx) => {
   if (data.has_porcentaje_iggs && (data.porcentaje_iggs === null || data.porcentaje_iggs === undefined || data.porcentaje_iggs === 0)) {
      ctx.addIssue({
        path: ["porcentaje_iggs"],
        message: "Debe ingresar un porcentaje válido para IGGS.",
        code: z.ZodIssueCode.custom,
      });
    }
    if (data.has_porcentaje_irtra && (data.porcentaje_irtra === null || data.porcentaje_irtra === undefined || data.porcentaje_irtra === 0)) {
      ctx.addIssue({
        path: ["porcentaje_irtra"],
        message: "Debe ingresar un porcentaje válido para IRTRA.",
        code: z.ZodIssueCode.custom,
      });
    }
    if (data.password !== data.password_repeat) {
      ctx.addIssue({
        path: ["password_repeat"],
        message: "Las password no coinciden.",
        code: z.ZodIssueCode.custom,
      });
    }
  })
))

const onFormSubmit = (e: any) => {
  if (e.valid) {
    console.log(e.values)
  }
};

const {state: userTypes, status} = useQuery({
  key: ['optionsTypes'],
  query: () => $api<{id: number, name: string}[]>('/user-types')
})

watch(
  () => userTypes.value.data,
  (data) => {
    if (data && data.length > 0) {
      selectedType.value = data[0].id; // Update the select's reactive value
      initialValues.type = data[0].id;   // Update the form's initial value
    }
  }, {immediate: true}
);
</script>
