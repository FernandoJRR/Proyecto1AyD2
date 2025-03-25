import { mount } from '@vue/test-utils'
import { ref, reactive } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import EditMedicine from '~/pages/farmacia/editar/[id].vue'

// Mocks globales
let mockUseQueryReturn: any = {}
const mutateMock = vi.fn()

// Mock de @pinia/colada
vi.mock('@pinia/colada', () => ({
  useQuery: () => mockUseQueryReturn,
  useMutation: vi.fn().mockImplementation(() => ({
    mutate: mutateMock,
    asyncStatus: 'idle'
  }))
}))

// Mock de vue-router
const RouterLinkStub = {
  template: '<a class="router-link"><slot /></a>',
  props: ['to']
}

vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } }),
  RouterLink: RouterLinkStub,
  navigateTo: vi.fn()
}))

// Mock de vue-sonner
vi.mock('vue-sonner', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn()
  }
}))

// Stubs de PrimeVue
const ButtonStub = {
  template: '<button class="button" @click="$emit(\'click\')">{{ label }}</button>',
  props: ['label', 'icon', 'text', 'severity', 'type']
}

const InputTextStub = {
  template: '<input class="input-text" />',
  props: ['name', 'type']
}

const InputNumberStub = {
  template: '<input class="input-number" />',
  props: ['name', 'min', 'minFractionDigits', 'maxFractionDigits', 'currency']
}

const FloatLabelStub = {
  template: '<div class="float-label"><slot /></div>'
}

const MessageStub = {
  template: '<div class="message"><slot /></div>',
  props: ['severity', 'size', 'variant']
}

const FormStub = {
  template: `
    <form @submit.prevent="$emit('submit', { valid: true, values: values })">
      <slot :$form="form" />
    </form>`,
  props: ['initialValues', 'resolver'],
  data() {
    return {
      values: {
        name: 'Amoxicilina',
        description: 'Antibiótico',
        price: 15.5,
        quantity: 10,
        minQuantity: 2
      },
      form: {
        name: { invalid: false },
        description: { invalid: false },
        price: { invalid: false },
        quantity: { invalid: false },
        minQuantity: { invalid: false }
      }
    }
  }
}

describe('EditMedicine [id].vue', () => {
  beforeEach(() => {
    mockUseQueryReturn = {
      state: ref({})
    }
    mutateMock.mockClear()
  })

  it('muestra el estado de carga cuando status es "pending"', () => {
    mockUseQueryReturn.state = ref({ status: 'pending' })

    const wrapper = mount(EditMedicine, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          InputNumber: InputNumberStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub
        }
      }
    })

    expect(wrapper.text()).toContain('Cargando datos...')
  })

  it('muestra el estado de error cuando status es "error"', () => {
    mockUseQueryReturn.state = ref({ status: 'error' })

    const wrapper = mount(EditMedicine, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          InputNumber: InputNumberStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub
        }
      }
    })

    expect(wrapper.text()).toContain('Error al cargar los datos del medicamento')
  })

  it('renderiza el formulario con los datos cargados correctamente', () => {
    mockUseQueryReturn.state = ref({
      status: 'success',
      data: {
        name: 'Ibuprofeno',
        description: 'Analgesico',
        price: 20,
        quantity: 50,
        minQuantity: 5
      }
    })

    const wrapper = mount(EditMedicine, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          InputNumber: InputNumberStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub
        }
      }
    })

    // Botón "Ver Todos"
    const routerLink = wrapper.findComponent(RouterLinkStub)
    expect(routerLink.exists()).toBe(true)

    // Encabezado de edición
    expect(wrapper.find('h1.text-4xl').text()).toContain('Editar Medicamento')

    // Formulario visible
    expect(wrapper.find('form').exists()).toBe(true)
  })

  it('envía el formulario correctamente al hacer submit', async () => {
    mockUseQueryReturn.state = ref({
      status: 'success',
      data: {
        name: 'Paracetamol',
        description: 'Antipirético',
        price: 5,
        quantity: 100,
        minQuantity: 10
      }
    })

    const wrapper = mount(EditMedicine, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          InputNumber: InputNumberStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub
        }
      }
    })

    const form = wrapper.findComponent(FormStub)
    expect(form.exists()).toBe(true)

    await form.trigger('submit')

    expect(mutateMock).toHaveBeenCalledWith({
      name: 'Amoxicilina',
      description: 'Antibiótico',
      price: 15.5,
      quantity: 10,
      minQuantity: 2
    })
  })

  it('no envía el formulario si los datos son inválidos', async () => {
    mockUseQueryReturn.state = ref({
      status: 'success',
      data: {
        name: 'Paracetamol',
        description: 'Antipirético',
        price: 5,
        quantity: 100,
        minQuantity: 10
      }
    })

    const InvalidFormStub = {
      template: `
        <form @submit.prevent="$emit('submit', { valid: false, values: {} })">
          <slot :$form="{}" />
        </form>`
    }

    const wrapper = mount(EditMedicine, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          InputNumber: InputNumberStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: InvalidFormStub
        }
      }
    })

    const form = wrapper.findComponent(InvalidFormStub)
    await form.trigger('submit')

    expect(mutateMock).not.toHaveBeenCalled()
  })
})
