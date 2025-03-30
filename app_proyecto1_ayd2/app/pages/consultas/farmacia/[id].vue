<template>
  <div class="m-2">
    <div class="flex justify-between items-center mb-4 gap-4">
      <Button
        icon="pi pi-arrow-left"
        label="Volver"
        rounded
        outlined
        severity="secondary"
        @click="useRouter().back()"
      />
    </div>

    <h1 class="text-black mb-2 text-2xl">Farmacia Consulta - Iniciar Venta</h1>

    <div class="flex justify-between items-center mb-4 gap-4">
      <div class="flex items-center gap-2">
        <InputText
          v-model="searchTerm"
          placeholder="Buscar por nombre..."
          class="p-inputtext-sm w-64"
          @keyup.enter="buscarMedicamentos"
        />
        <Button
          icon="pi pi-search"
          label="Buscar"
          @click="buscarMedicamentos"
          rounded
          outlined
          severity="info"
        />
      </div>

      <div class="flex items-center gap-2">
        <Button
          icon="pi pi-refresh"
          label="Recargar Página"
          @click="recargarDatos"
          rounded
          outlined
          severity="help"
        />
      </div>
    </div>

    <DataTable
      :value="medicinesState.data as any[]"
      tableStyle="min-width: 50rem"
      stripedRows
      :loading="asyncStatus === 'loading'"
      class="mb-10"
    >
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold">Medicamentos Disponibles</span>
        </div>
      </template>

      <Column field="id" header="ID" />
      <Column field="name" header="Nombre" />
      <Column field="quantity" header="Existencias" />
      <Column field="price" header="Precio" />

      <Column header="Disponible">
        <template #body="slotProps">
          <Tag v-if="slotProps.data.quantity === 0" severity="danger"
            >Agotado</Tag
          >
          <Tag
            v-else-if="slotProps.data.quantity <= slotProps.data.minQuantity"
            severity="warn"
          >
            Cantidad Muy Baja
          </Tag>
          <Tag v-else severity="success">Disponible</Tag>
        </template>
      </Column>

      <Column header="Acciones">
        <template #body="slotProps">
          <Button
            label="Agregar al Carrito"
            severity="success"
            rounded
            text
            :disabled="slotProps.data.quantity === 0"
            @click="agregarAlCarrito(slotProps.data)"
          />
        </template>
      </Column>

      <template #footer>
        Hay en total
        {{ medicinesState.data ? (medicinesState.data as any[]).length : 0 }}
        medicamentos.
      </template>
    </DataTable>

    <DataTable :value="carrito" tableStyle="min-width: 50rem" stripedRows>
      <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
          <span class="text-xl font-bold text-green-600">Carrito de Venta</span>
        </div>
      </template>

      <Column field="id" header="ID" />
      <Column field="name" header="Nombre" />
      <Column field="availableQuantity" header="Existencias" />

      <Column header="Cantidad a Comprar">
        <template #body="slotProps">
          <InputNumber
            v-model="slotProps.data.quantity"
            :min="1"
            :max="slotProps.data.availableQuantity"
            showButtons
            buttonLayout="horizontal"
            decrementButtonClass="p-button-secondary"
            incrementButtonClass="p-button-secondary"
            incrementButtonIcon="pi pi-plus"
            decrementButtonIcon="pi pi-minus"
            @update:modelValue="actualizarCantidad(slotProps.data)"
          />
        </template>
      </Column>

      <Column field="price" header="Precio" />

      <Column header="Subtotal">
        <template #body="slotProps">
          {{ slotProps.data.total.toFixed(2) }}
        </template>
      </Column>

      <Column header="Acciones">
        <template #body="slotProps">
          <Button
            icon="pi pi-trash"
            severity="danger"
            text
            rounded
            @click="eliminarDelCarrito(slotProps.data.id)"
          />
        </template>
      </Column>

      <template #footer>
        <div class="flex justify-between items-center">
          <span
            >Total a Pagar: Q.
            {{
              carrito.reduce((sum, item) => sum + item.total, 0).toFixed(2)
            }}</span
          >
          <Button
            icon="pi pi-check"
            label="Generar Venta"
            severity="success"
            rounded
            raised
            :disabled="
              carrito.length === 0 || ventaVariosFarmaciaStatus === 'loading'
            "
            @click="mostrarDialogoVenta = true"
          />
        </div>
      </template>
    </DataTable>
    <!-- Dialogo Confirmar Venta -->
    <Dialog
      v-model:visible="mostrarDialogoVenta"
      modal
      header="Confirmar Venta"
    >
      <p>
        ¿Estás seguro que deseas realizar la venta por un total de
        <strong>Q.{{ totalVenta }}</strong
        >?
      </p>
      <template #footer>
        <Button label="Cancelar" text @click="mostrarDialogoVenta = false" />
        <Button
          label="Confirmar Venta"
          severity="success"
          @click="confirmarVenta"
        />
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { toast } from "vue-sonner";
import {
  getAllMedicines,
  ventaVariosFarmacia,
  mapLineaVentaMedicineToPayloadSaleMedicineFarmacia,
  type LineaVentaMedicine,
  type PayloadSaleMedicineFarmacia,
  type PayloadSaleMedicineConsulta,
  ventaVariosConsulta,
  mapLineaVentaMedicineToPayloadSaleMedicineConsulta,
} from "~/lib/api/medicines/medicine";
import { InputText, Button, Tag, InputNumber } from "primevue";

const searchTerm = ref("");
const carrito = ref<LineaVentaMedicine[]>([]);
const mostrarDialogoVenta = ref(false);

const totalVenta = computed(() =>
  carrito.value.reduce((sum, item) => sum + item.total, 0).toFixed(2)
);

const {
  state: medicinesState,
  asyncStatus,
  refetch: refetchMedicines,
} = useCustomQuery({
  key: ["medicines-sale-consult"],
  query: () =>
    getAllMedicines(searchTerm.value === "" ? null : searchTerm.value),
});

const {
  mutate: ventaVariosFarmaciaMutate,
  asyncStatus: ventaVariosFarmaciaStatus,
} = useMutation({
  mutation: (payload: PayloadSaleMedicineConsulta[]) =>
    ventaVariosConsulta(payload),
  onError: (error) => {
    toast.error("Ocurrió un error al crear la venta", {
      description: error.message,
    });
  },
  onSuccess: () => {
    toast.success("Venta realizada con éxito");
    carrito.value = [];
    refetchMedicines();
  },
});

const buscarMedicamentos = () => {
  refetchMedicines();
};

const recargarDatos = () => {
  refetchMedicines();
};

const agregarAlCarrito = (medicamento: any) => {
  const existe = carrito.value.find((item) => item.id === medicamento.id);

  if (!existe) {
    carrito.value.push({
      id: medicamento.id,
      name: medicamento.name,
      price: medicamento.price,
      availableQuantity: medicamento.quantity,
      quantity: 1,
      total: medicamento.price,
    });
  }
};

const actualizarCantidad = (item: LineaVentaMedicine) => {
  item.total = item.quantity * item.price;
};

const eliminarDelCarrito = (id: string) => {
  carrito.value = carrito.value.filter((item) => item.id !== id);
};
defineExpose({
  carrito,
  agregarAlCarrito,
  eliminarDelCarrito,
  actualizarCantidad,
});

const confirmarVenta = () => {
  if (carrito.value.length === 0) return;
  const payload = mapLineaVentaMedicineToPayloadSaleMedicineConsulta(
    carrito.value,
    useRoute().params.id as string
  );
  ventaVariosFarmaciaMutate(payload);
  mostrarDialogoVenta.value = false;
};
</script>
