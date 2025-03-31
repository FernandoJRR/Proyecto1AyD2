<template>
  <div class="p-8 max-w-4xl mx-auto">
    <!-- encabezado y navegación -->
    <div class="mb-6">
      <router-link to="/habitaciones/">
        <Button label="Ver Todos" icon="pi pi-arrow-left" text />
      </router-link>

    </div>
    <h1 class="text-4xl font-bold mb-6">Editar una habitación</h1>
    <!-- formulario principal -->
    <Form v-slot="$form" v-if="isRoomReady" :initialValues="formInitialValues" :resolver @submit="onFormSubmit"
      class="space-y-8 bg-white shadow-md rounded-2xl p-6 border border-gray-200">
      <div class="space-y-6">
        <h2 class="text-2xl font-semibold">Datos de la habitación</h2>

        <div class="w-full">
          <FloatLabel>
            <label for="number">Codigo de habitación</label>
            <InputText id="number" name="number" type="text" class="w-full" />
          </FloatLabel>
          <Message v-if="$form.number?.invalid" severity="error" size="small" variant="simple">
            {{ $form.number.error?.message }}
          </Message>
        </div>


        <div class="w-full">
          <FloatLabel>
            <label for="dailyMaintenanceCost">Costo de mantenimiento</label>
            <InputNumber id="dailyMaintenanceCost" name="dailyMaintenanceCost" class="w-full" currency="GTQ"
              mode="currency" :min="0" :minFractionDigits="2" :maxFractionDigits="2"
              placeholder="Costo de mantenimiento" />
          </FloatLabel>
          <Message v-if="$form.dailyMaintenanceCost?.invalid" severity="error" size="small" variant="simple">
            {{ $form.dailyMaintenanceCost.error?.message }}
          </Message>
        </div>

        <div class="w-full">
          <FloatLabel>
            <label for="dailyPrice">Precio diario</label>
            <InputNumber id="dailyPrice" name="dailyPrice" class="w-full" currency="GTQ" mode="currency" :min="0"
              :minFractionDigits="2" :maxFractionDigits="2" placeholder="Precio diario" />
          </FloatLabel>
          <Message v-if="$form.dailyPrice?.invalid" severity="error" size="small" variant="simple">
            {{ $form.dailyPrice.error?.message }}
          </Message>
        </div>
      </div>

      <!-- Botón de envío -->
      <div class="pt-4">
        <Button type="submit" label="Editar" icon="pi pi-pencil" severity="secondary" />
      </div>
    </Form>
  </div>
</template>

<script setup lang="ts">
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { FloatLabel } from 'primevue';
import { toast } from 'vue-sonner';
import { number, z } from 'zod';;
import { updateRoom, type Room, type RoomPayLoad } from '~/lib/api/habitaciones/room';
import { getRoomById } from '~/lib/api/habitaciones/room';


//este es el controlador de caches de las queries de pinia colada
const queryCache = useQueryCache()
//traemos el id de la ruta
const roomId = useRoute().params.id as string;

//estos son los valores inciales del form, marcados como ref para que vue implemente reactividad
const formInitialValues = ref<{
  number: string,
  dailyMaintenanceCost: number,
  dailyPrice: number
}>({
  number: '',
  dailyMaintenanceCost: 0,
  dailyPrice: 0
});
const isRoomReady = ref(false);


//mandamos a traer la informacion de la habitacion por id
const { data: foundedRoom } = useCustomQuery<Room>({
  key: ['getRoomById', roomId /**roomId identifica la consulta y su guardado en cache, si este cambia entonces se vuelve ha hacer la consulta */],
  query: () => getRoomById(roomId)
});


// observa cambios en employeeType cuando la data llegue del backend
watch(
  () => foundedRoom.value,
  (data) => {
    if (data) {
      // establecemos los valores iniciales del formulario,  como es reactivo entonces se cargan automaticamnte los valores
      formInitialValues.value = {
        number: data.number,
        dailyMaintenanceCost: data.dailyMaintenanceCost,
        dailyPrice: data.dailyPrice
      };
      // habilitamos el renderizado del formulario
      isRoomReady.value = true;
    }
  },
  { immediate: true } // ejecuta la lógica inmediatamente si employeeType ya tiene valor
);

const resolver = ref(zodResolver(
  z.object({
    number: z.string()
      .min(1, 'El número de habitación es obligatorio')
      .max(100, 'El número de habitación no puede exceder los 100 caracteres'),


    //el preprocesor se encarga de convertir el valor a un numero, si no es un numero lanza un error
    dailyMaintenanceCost: z.number({
      required_error: 'El costo de mantenimiento diario es obligatorio',
      invalid_type_error: 'El costo de mantenimiento diario debe ser un número',
    }).min(0, 'El costo de mantenimiento diario debe ser mayor o igual a 0'),

    //el preprocesor se encarga de convertir el valor a un numero, si no es un numero lanza un error
    dailyPrice: z.number({
      required_error: 'El precio diario es obligatorio',
      invalid_type_error: 'El precio diario debe ser un número',
    }).min(0, 'El precio diario debe ser mayor o igual a 0')
  })
));


const onFormSubmit = (e: any) => {
  if (e.valid) {

    let payload: RoomPayLoad = {
      number: e.values.number,
      dailyMaintenanceCost: e.values.dailyMaintenanceCost,
      dailyPrice: e.values.dailyPrice
    }


    mutate(payload)
  }
};

const { mutate } = useMutation({
  mutation: (employeeData: RoomPayLoad) => updateRoom(roomId, employeeData),
  onError(error) {
    toast.error('Ocurrió un error al editar la habitación', {
      description: `${(error.message)}`
    })
  },
  onSuccess() {
    toast.success('La habitación se ha editado correctamente')
    //marcar como invalida la query que manda a traer al room para que asi vue cargue la nueva data al vovlerle dar al boton de editar
    queryCache.invalidateQueries({ key: ["getRoomById"] });
    //marcamos como invalida la de taer todos los rooms para que siempre actualice la info e la tabla principals
    queryCache.invalidateQueries({ key: ["getAllRooms"] });
    navigateTo('/habitaciones/')
  }
})


</script>