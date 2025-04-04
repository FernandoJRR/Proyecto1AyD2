import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount } from "@vue/test-utils";
import { ref } from "vue";

const refetchMedicinesMock = vi.fn();
const ventaVariosFarmaciaMutateMock = vi.fn();

vi.mock("@pinia/colada", () => ({
  useQuery: vi.fn().mockImplementation(({ key }) => {
    return {
      state: ref({
        data: [
          {
            id: "1",
            name: "Ibuprofeno",
            quantity: 50,
            price: 5,
            minQuantity: 10,
            availableQuantity: 50,
          },
          {
            id: "2",
            name: "Paracetamol",
            quantity: 0,
            price: 3,
            minQuantity: 5,
            availableQuantity: 0,
          },
        ],
      }),
      asyncStatus: "success",
      refetch: refetchMedicinesMock,
    };
  }),
  useMutation: vi.fn().mockImplementation(() => ({
    mutate: ventaVariosFarmaciaMutateMock,
    asyncStatus: "idle",
  })),
}));

vi.mock("vue-sonner", () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

const stubs = {
  RouterLink: {
    template: `<a class="router-link"><slot /></a>`,
    props: ["to"],
  },
  Button: {
    template: `
      <button
        class="button"
        @click="$emit('click')"
        :disabled="disabled"
      >
        {{ label }}
      </button>`,
    props: [
      "label",
      "icon",
      "text",
      "rounded",
      "raised",
      "severity",
      "disabled",
    ],
  },
  InputText: {
    template: `<input class="input-text" @keyup.enter="$emit('keyup.enter')" />`,
    props: ["modelValue", "placeholder", "class"],
  },
  InputNumber: {
    template: `<input class="input-number" @input="$emit('update:modelValue', 2)" />`,
    props: ["modelValue", "min", "max"],
  },
  DataTable: {
    props: ["value"],
    template: `
      <div class="datatable">
        <slot name="header"></slot>

        <div v-for="item in value" :key="item.id" class="datatable-row">
          <div>ID: {{ item.id }}</div>
          <div>Nombre: {{ item.name }}</div>

          <!-- Tabla de carrito -->
          <div v-if="item.availableQuantity !== undefined">
            <input class="input-number" @input="$emit('update:modelValue', 2)" />
            <div>Subtotal: {{ (item.quantity * item.price).toFixed(2) }}</div>
            <button class="button eliminar-del-carrito" @click="$emit('click')">Eliminar</button>
          </div>

          <!-- Tabla de medicamentos disponibles -->
          <div v-else>
            <div>Existencias: {{ item.quantity }}</div>
            <div>Precio: {{ item.price }}</div>
            <button class="button agregar-al-carrito" @click="$emit('click')">Agregar al Carrito</button>
          </div>
        </div>

        <slot name="footer"></slot>
      </div>
    `,
  },
  Column: {
    template: `<div class="column"></div>`,
    props: ["field", "header"],
  },
  Tag: {
    template: `<div class="tag"><slot /></div>`,
    props: ["severity"],
  },
};

import VentaFarmacia from "~/pages/farmacia/venta.vue";

describe("VentaFarmacia Page", () => {
  beforeEach(() => {
    refetchMedicinesMock.mockClear();
    ventaVariosFarmaciaMutateMock.mockClear();
  });

  it("renderiza los elementos principales", () => {
    const wrapper = mount(VentaFarmacia, { global: { stubs } });

    expect(wrapper.text()).toContain("Farmacia - Iniciar Venta");
    expect(
      wrapper.findAll(".button").some((b) => b.text().includes("Buscar"))
    ).toBe(true);
    expect(
      wrapper
        .findAll(".button")
        .some((b) => b.text().includes("Recargar Página"))
    ).toBe(true);
  });

  it("llama a refetchMedicines al buscar medicamentos", async () => {
    const wrapper = mount(VentaFarmacia, { global: { stubs } });

    const buscarButton = wrapper
      .findAll(".button")
      .find((b) => b.text().includes("Buscar"));
    await buscarButton?.trigger("click");

    expect(refetchMedicinesMock).toHaveBeenCalled();
  });

  it("llama a refetchMedicines al recargar datos", async () => {
    const wrapper = mount(VentaFarmacia, { global: { stubs } });

    const recargarButton = wrapper
      .findAll(".button")
      .find((b) => b.text().includes("Recargar Página"));
    await recargarButton?.trigger("click");

    expect(refetchMedicinesMock).toHaveBeenCalled();
  });

  it("agrega un medicamento al carrito", async () => {
    const wrapper = mount(VentaFarmacia, { global: { stubs } });

    wrapper.vm.agregarAlCarrito({
      id: "1",
      name: "Ibuprofeno",
      price: 5,
      quantity: 50,
    });

    await wrapper.vm.$nextTick();

    expect(wrapper.vm.carrito.length).toBe(1);
    expect(wrapper.vm.carrito[0].name).toBe("Ibuprofeno");
  });

  it("elimina un medicamento del carrito", async () => {
    const wrapper = mount(VentaFarmacia, { global: { stubs } });

    wrapper.vm.agregarAlCarrito({
      id: "1",
      name: "Ibuprofeno",
      price: 5,
      quantity: 50,
    });

    await wrapper.vm.$nextTick();

    expect(wrapper.vm.carrito.length).toBe(1);

    wrapper.vm.eliminarDelCarrito("1");
    await wrapper.vm.$nextTick();

    expect(wrapper.vm.carrito.length).toBe(0);
  });

  it("actualiza la cantidad en el carrito", async () => {
    const wrapper = mount(VentaFarmacia, { global: { stubs } });

    wrapper.vm.agregarAlCarrito({
      id: "1",
      name: "Ibuprofeno",
      price: 5,
      quantity: 50,
    });

    await wrapper.vm.$nextTick();

    const item = wrapper.vm.carrito[0];
    expect(item.quantity).toBe(1);
    expect(item.total).toBe(5);

    item.quantity = 3;
    wrapper.vm.actualizarCantidad(item);
    await wrapper.vm.$nextTick();

    expect(item.total).toBe(15);
  });

  it("realiza la venta llamando a mutate con el carrito lleno", async () => {
    const wrapper = mount(VentaFarmacia, { global: { stubs } });

    wrapper.vm.agregarAlCarrito({
      id: "1",
      name: "Ibuprofeno",
      price: 5,
      quantity: 50,
    });

    await wrapper.vm.$nextTick();

    expect(wrapper.vm.carrito.length).toBe(1);

    const generarVentaButton = wrapper
      .findAll(".button")
      .find((b) => b.text().includes("Generar Venta"));

    expect((generarVentaButton?.element as HTMLButtonElement).disabled).toBe(false);

    await generarVentaButton?.trigger("click");

    expect(ventaVariosFarmaciaMutateMock).toHaveBeenCalled();
  });

  it("no realiza la venta si el carrito está vacío", async () => {
    const wrapper = mount(VentaFarmacia, { global: { stubs } });

    expect(wrapper.vm.carrito.length).toBe(0);

    const generarVentaButton = wrapper
      .findAll(".button")
      .find((b) => b.text().includes("Generar Venta"));

    expect((generarVentaButton?.element as HTMLButtonElement).disabled).toBe(true);

    await generarVentaButton?.trigger("click");

    expect(ventaVariosFarmaciaMutateMock).not.toHaveBeenCalled();
  });
});
