import { mount } from "@vue/test-utils";
import { ref } from "vue";
import { describe, it, expect, beforeEach, vi } from "vitest";
import EditPatient from "~/pages/pacientes/editar/[id].vue";

// Mocks globales
let mockUseQueryReturn: any = {};
const mutateMock = vi.fn();

// Mock de @pinia/colada
vi.mock("@pinia/colada", () => ({
  useQuery: () => mockUseQueryReturn,
  useMutation: vi.fn().mockImplementation(() => ({
    mutate: mutateMock,
    asyncStatus: "idle",
  })),
}));

// Mock de vue-router
const RouterLinkStub = {
  template: '<a class="router-link"><slot /></a>',
  props: ["to"],
};

vi.mock("vue-router", () => ({
  useRoute: () => ({ params: { id: "1" } }),
  RouterLink: RouterLinkStub,
  navigateTo: vi.fn(),
}));

// Mock de vue-sonner
vi.mock("vue-sonner", () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn(),
  },
}));

// Stubs de PrimeVue
const ButtonStub = {
  template: '<button class="button">{{ label }}</button>',
  props: ["label", "icon", "text", "severity", "type"],
};

const InputTextStub = {
  template: '<input class="input-text" />',
  props: ["name", "type", "fluid"],
};

const FloatLabelStub = {
  template: '<div class="float-label"><slot /></div>',
};

const MessageStub = {
  template: '<div class="message"><slot /></div>',
  props: ["severity", "size", "variant"],
};

// FormStub que simula el envío del formulario
const FormStub = {
  template: `
    <form @submit.prevent="$emit('submit', { valid: true, values: values })">
      <slot :$form="form" />
    </form>`,
  props: ["initialValues", "resolver"],
  data() {
    return {
      values: {
        firstnames: "Carlos",
        lastnames: "Ramirez",
        dpi: "1234567890123",
      },
      form: {
        firstnames: { invalid: false },
        lastnames: { invalid: false },
        dpi: { invalid: false },
      },
    };
  },
};

describe("EditPatient [id].vue", () => {
  beforeEach(() => {
    mockUseQueryReturn = {
      state: ref({}),
    };
    mutateMock.mockClear();
  });

  it('muestra el estado de carga cuando status es "pending"', () => {
    mockUseQueryReturn.state = ref({ status: "pending" });

    const wrapper = mount(EditPatient, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub,
        },
      },
    });

    expect(wrapper.text()).toContain("Cargando datos...");
  });

  it('muestra el estado de error cuando status es "error"', () => {
    mockUseQueryReturn.state = ref({ status: "error" });

    const wrapper = mount(EditPatient, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub,
        },
      },
    });

    expect(wrapper.text()).toContain("Error al cargar los datos del paciente");
  });

  it("renderiza el formulario con los datos cargados correctamente", () => {
    mockUseQueryReturn.state = ref({
      status: "success",
      data: {
        firstnames: "Ana",
        lastnames: "Gomez",
        dpi: "9876543210987",
      },
    });

    const wrapper = mount(EditPatient, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub,
        },
      },
    });

    // Verificar botón "Ver Todos"
    const routerLink = wrapper.findComponent(RouterLinkStub);
    expect(routerLink.exists()).toBe(true);

    // Encabezado de edición
    expect(wrapper.find("h1.text-4xl").text()).toContain("Editar Paciente");

    // Formulario visible
    expect(wrapper.find("form").exists()).toBe(true);
  });

  it("envía el formulario correctamente al hacer submit", async () => {
    mockUseQueryReturn.state = ref({
      status: "success",
      data: {
        firstnames: "Pedro",
        lastnames: "Pérez",
        dpi: "1111111111111",
      },
    });

    const wrapper = mount(EditPatient, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub,
        },
      },
    });

    const form = wrapper.findComponent(FormStub);
    expect(form.exists()).toBe(true);

    await form.trigger("submit");

    expect(mutateMock).toHaveBeenCalledWith({
      firstnames: "Carlos",
      lastnames: "Ramirez",
      dpi: "1234567890123",
    });
  });

  it("no envía el formulario si los datos son inválidos", async () => {
    mockUseQueryReturn.state = ref({
      status: "success",
      data: {
        firstnames: "Pedro",
        lastnames: "Pérez",
        dpi: "1111111111111",
      },
    });

    const InvalidFormStub = {
      template: `
        <form @submit.prevent="$emit('submit', { valid: false, values: {} })">
          <slot :$form="{}" />
        </form>`,
    };

    const wrapper = mount(EditPatient, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: InvalidFormStub,
        },
      },
    });

    const form = wrapper.findComponent(InvalidFormStub);
    await form.trigger("submit");

    expect(mutateMock).not.toHaveBeenCalled();
  });
});
