<template>
    <div class="m-6">
        <DataTable :value="state.data as any[]" tableStyle="min-width: 50rem" stripedRows
            :loading="asyncStatus == 'loading'">
            <template #header>
                <div class="flex flex-wrap items-center justify-between gap-2">
                    <span class="text-xl font-bold">Habitaciones</span>
                    <!--Bton de mas-->
                    <router-link to="/habitaciones/crear">
                        <Button icon="pi pi-plus" rounded raised />
                    </router-link>
                </div>
            </template>
            <Column header="Numero de habitación">
                <template #body="slotProps">
                    <template v-if="slotProps.data.number !== null">
                        {{ `${slotProps.data.number}` }}
                    </template>
                    <template v-else>
                        {{ `error` }}
                    </template>
                </template>
            </Column>

            <Column header="Costo de mantenimiento diario">
                <template #body="slotProps">
                    <template v-if="slotProps.data.dailyMaintenanceCost !== null">
                        {{ `Q${slotProps.data.dailyMaintenanceCost}` }}
                    </template>
                    <template v-else>
                        {{ `error` }}
                    </template>
                </template>
            </Column>

            <Column header="Precio diario">
                <template #body="slotProps">
                    <template v-if="slotProps.data.dailyPrice !== null">
                        {{ `Q${slotProps.data.dailyPrice}` }}
                    </template>
                    <template v-else>
                        {{ `error` }}
                    </template>
                </template>
            </Column>

            <Column header="Estado">
                <template #body="slotProps">
                    <Tag :value="slotProps.data.status" />
                </template>
            </Column>

            <!--Botones de acciones-->
            <Column header="Acciones">
                <template #body="slotProps">
                    <RouterLink :to="`/habitaciones/${slotProps.data.id}`">
                        <Button label="Editar" severity="info" rounded text />
                    </RouterLink>
                    <Button v-if="slotProps.data.name !== null" label="Inhabilitar" severity="danger" rounded text
                        @click="toggleAvailability(slotProps.data.id, slotProps.data.number)" />
                </template>
            </Column>
            <template #footer> Hay en total {{ state.data ? (state.data as any[]).length : 0 }} habitaciones.
            </template>
        </DataTable>
        <ConfirmDialog></ConfirmDialog>

    </div>
</template>
<!--Controlador-->
<script setup lang="ts">
import { useQuery } from '@pinia/colada';
import { RouterLink } from 'vue-router';
import ConfirmDialog from 'primevue/confirmdialog';
import { useConfirm } from "primevue/useconfirm";
import { toast } from 'vue-sonner';
import { getAllRooms } from '~/lib/api/habitaciones/room';
import { toggleRoomAvailability } from '~/lib/api/habitaciones/room';
//este es el controlador de caches de las queries de pinia colada
const queryCache = useQueryCache()
//esto es la constante que usa vue para manejar sus dialogs
const confirm = useConfirm();

const { state, asyncStatus } = useCustomQuery({
    key: ['getAllRooms'],
    query: () => getAllRooms()
})



const toggleAvailability = (id: string, number: string) => {
    confirm.require({
        message: `¿Estás seguro de que deseas cambiar el estado de disponibilidad de la habitación "${number}"?`,
        header: 'Confirmación',
        icon: 'pi pi-exclamation-triangle',
        rejectProps: {
            label: 'Cancelar',
            severity: 'secondary',
            outlined: true,
        },
        acceptProps: {
            label: 'Cambiar estado',
            severity: 'warning',
            icon: 'pi pi-refresh'
        },
        accept: () => {
            // si acepta, entonces ejecutamos la mutación para alternar disponibilidad
            mutate(id);
        },
        reject: () => {
            toast.warning('Operación cancelada. No se modificó el estado de la habitación.');
        }
    });

};


const { mutate } = useMutation({
    mutation: (id: string) => toggleRoomAvailability(id),
    onError(error) {
        console.log(error)
        console.log(error.cause)
        toast.error('Ocurrió un error al cambiar el estado de disponibilidad de la habitación.', {
            description: `${(error.message)}`
        })
    },
    onSuccess() {
        toast.success('Estado de disponibilidad de la habitación actualizado correctamente.')

        //invalidamos la query
        queryCache.invalidateQueries({ key: ["getAllRooms"] });
    }
})



</script>