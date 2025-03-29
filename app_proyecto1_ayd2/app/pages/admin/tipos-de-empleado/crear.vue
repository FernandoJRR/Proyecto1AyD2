<template>
  <div class="p-8 max-w-4xl mx-auto">
    <!-- encabezado y navegación -->
    <div class="mb-6">
      <router-link to="/admin/tipos-de-empleado">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>

    </div>
    <h1 class="text-4xl font-bold mb-6">Crear Tipo de Empleado</h1>
    <!-- formulario principal -->
    <Form
      v-slot="$form"
      :initialValues
      :resolver
      @submit="onFormSubmit"
    class="space-y-8 bg-white shadow-md rounded-2xl p-6 border border-gray-200"
    >
      <div class="space-y-6">
        <h2 class="text-2xl font-semibold">Datos del Tipo de Empleado</h2>

        <div class="w-full">
          <FloatLabel>
            <label for="name">Nombre</label>
            <InputText id="name" name="name" type="text" class="w-full" />
          </FloatLabel>
          <Message
            v-if="$form.name?.invalid"
            severity="error"
            size="small"
            variant="simple"
          >
            {{ $form.name.error?.message }}
          </Message>
        </div>
      </div>

      <!-- Permisos -->
      <div class="space-y-4">
        <h2 class="text-2xl font-semibold">Permisos Asociados</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-2">
          <div
            v-for="permission in permissions.data"
            :key="permission.id"
            class="flex items-center"
          >
          <!--Si nosotros establecemos el v-model con nuestro ref entonces view hace el insert y eliminacion en el array (usa el value)-->
            <Checkbox
              :value="permission.id"
              v-model="selectedPermissions"

            />
            <label :for="permission.id" class="ml-2">
              {{ permission.name }}
            </label>
          </div>
          <Message
            v-if="permissionsError"
            severity="error"
            size="small"
            variant="simple"
            >
            {{ permissionsError }}
          </Message>
        </div>
      </div>

      <!-- Botón de envío -->
      <div class="pt-4">
        <Button type="submit" label="Crear" icon="pi pi-save" severity="secondary" />
      </div>
    </Form>
  </div>
</template>

<script setup lang="ts">
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { FloatLabel, InputNumber, Password, ToggleSwitch } from 'primevue';
import { toast } from 'vue-sonner';
import { z } from 'zod';;
import { createEmployeeType, type EmployeeTypePayLoad } from '~/lib/api/admin/employee-type';
import { getAllPermissions, type Permission } from '~/lib/api/admin/permission';



//con ref indicamos a view que es un valor reactivo y por lo tanto que restee los cambios
const selectedPermissions = ref([]);

//mensaje de error para cuando no se haya seleccinado ningun permiso
const permissionsError = ref('');

//en este caso no hay valores iniciales
const initialValues = reactive({
    name: ''
});


const resolver = ref(zodResolver(
    z.object({
        name: z.string().min(1, 'El nombre del tipo de empleado no puede estar vacío y es obligatorio.').
            max(100, "El nombre del tipo de empleado no puede tener más de 100 caracteres."),

    }).superRefine((data, ctx) => {

        if (selectedPermissions.value.length === 0) {
            permissionsError.value = 'Debes seleccionar al menos un permiso.';
            //se agrega el error a sistema de validaciones para que el boton este bloqueado
            ctx.addIssue({
              path: ['permissions'],
              code: z.ZodIssueCode.custom
            });
        }else{
            permissionsError.value = '';
        }

    })
))

const onFormSubmit = (e: any) => {
    if (e.valid) {
        console.log(e.values)

        let payload: EmployeeTypePayLoad = {
            name: e.values.name,
            permissions: selectedPermissions.value.map(id => ({ id }))
        };

        mutate(payload)
    }
};

const { state: permissions } = useCustomQuery({
    key: ['permissions'],
    query: () => getAllPermissions()
})

const { mutate, asyncStatus } = useMutation({
    mutation: (employeeData: EmployeeTypePayLoad) => createEmployeeType(employeeData),
    onError(error:any) {
        toast.error('Ocurrió un error al crear el tipo de empleado', {
            description: `${(error.response._data)}`
        })
    },
    onSuccess() {
        toast.success('Tipo de empleado creado correctamente')
        navigateTo('/admin/tipos-de-empleado')
    }
})


</script>