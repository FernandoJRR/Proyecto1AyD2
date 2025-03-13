<template>
  <div class="m-6 ml-12">
    <router-link to="/admin/personal">
      <Button label="Ver Todos" icon="pi pi-arrow-left" text />
    </router-link>
    <div>
      <Form v-slot="$form" :initialValues :resolver @submit="onFormSubmit" class="mt-4">
        <div class="flex flex-col gap-1">
          <div class="flex flex-row gap-4">
            <div>
              <FloatLabel>
                <label>Nombres</label>
                <InputText name="nombres" type="text" fluid />
              </FloatLabel>
              <Message v-if="$form.nombres?.invalid" severity="error" size="small" variant="simple">{{
                $form.nombres.error?.message }}</Message>
            </div>
            <div>
              <FloatLabel>
                <label>Apellidos</label>
                <InputText name="apellidos" type="text" fluid />
              </FloatLabel>
              <Message v-if="$form.apellidos?.invalid" severity="error" size="small" variant="simple">{{
                $form.apellidos.error?.message }}</Message>
            </div>
          </div>
          <div class="flex flex-row">
            <InputText name="apellidos" type="text" placeholder="Salario" fluid />
            <Message v-if="$form.apellidos?.invalid" severity="error" size="small" variant="simple">{{
              $form.apellidos.error?.message }}</Message>
            <div class="flex flex-row items-center gap-4 ml-4">
              <ToggleSwitch name="has_porcentaje_iggs" class="min-w-10"/>
              <InputNumber name="porcentaje_iggs" :min="1" :max="100" suffix="%" placeholder="Porcentaje IGGS" 
                fluid :disabled="!$form.has_porcentaje_iggs?.value"/>
            </div>
            <Message v-if="$form.porcentaje_iggs?.invalid" severity="error" size="small" variant="simple">{{
              $form.porcentaje_iggs.error?.message }}</Message>
            <div class="flex flex-row items-center gap-4 ml-4">
              <ToggleSwitch name="has_porcentaje_irtra" class="min-w-10"/>
              <InputNumber name="porcentaje_irtra" :min="1" :max="100" suffix="%" label="Porcentaje IRTRA" 
                fluid :disabled="!$form.has_porcentaje_irtra?.value"/>
            </div>
            <Message v-if="$form.porcentaje_irtra?.invalid" severity="error" size="small" variant="simple">{{
              $form.porcentaje_irtra.error?.message }}</Message>
          </div>
          <Button type="submit" severity="secondary" label="Crear" />
        </div>
      </Form>
    </div>
  </div>
</template>
<script setup lang="ts">
import { FloatLabel, InputNumber, ToggleSwitch } from 'primevue';

const initialValues = reactive({
    nombres: '',
    apellidos: '',
    has_porcentaje_iggs: true,
    porcentaje_iggs: 0,
    has_porcentaje_irtra: true,
    porcentaje_irtra: 0,
});

const onFormSubmit = (e: any) => {
    if (e.valid) {
      //Crea el usuario
    }
};
</script>
