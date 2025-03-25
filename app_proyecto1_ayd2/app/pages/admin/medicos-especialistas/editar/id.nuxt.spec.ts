import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import EditSpecialist from '~/pages/admin/medicos-especialistas/editar/[id].vue'

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
        nombres: 'Juan',
        apellidos: 'Pérez',
        dpi: '1234567890123'
      },
      form: {
        nombres: { invalid: false },
        apellidos: { invalid: false },
        dpi: { invalid: false }
      }
    }
  }
}

describe('EditSpecialist [id].vue', () => {
  beforeEach(() => {
    mockUseQueryReturn = {
      state: ref({})
    }
    mutateMock.mockClear()
  })

  it('muestra el estado de carga cuando status es "pending"', () => {
    mockUseQueryReturn.state = ref({ status: 'pending' })

    const wrapper = mount(EditSpecialist, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
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

    const wrapper = mount(EditSpecialist, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub
        }
      }
    })

    expect(wrapper.text()).toContain('Error al cargar los datos del especialista')
  })

  it('renderiza el formulario con los datos cargados correctamente', () => {
    mockUseQueryReturn.state = ref({
      status: 'success',
      data: {
        nombres: 'Laura',
        apellidos: 'García',
        dpi: '9876543210123'
      }
    })

    const wrapper = mount(EditSpecialist, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
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
    expect(wrapper.find('h1.text-4xl').text()).toContain('Editar Médico Especialista')

    // Formulario visible
    expect(wrapper.find('form').exists()).toBe(true)
  })

  it('envía el formulario correctamente al hacer submit', async () => {
    mockUseQueryReturn.state = ref({
      status: 'success',
      data: {
        nombres: 'Laura',
        apellidos: 'García',
        dpi: '9876543210123'
      }
    })

    const wrapper = mount(EditSpecialist, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
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
      nombres: 'Juan',
      apellidos: 'Pérez',
      dpi: '1234567890123'
    })
  })

  it('no envía el formulario si los datos son inválidos', async () => {
    mockUseQueryReturn.state = ref({
      status: 'success',
      data: {
        nombres: 'Laura',
        apellidos: 'García',
        dpi: '9876543210123'
      }
    })

    const InvalidFormStub = {
      template: `
        <form @submit.prevent="$emit('submit', { valid: false, values: {} })">
          <slot :$form="{}" />
        </form>`
    }

    const wrapper = mount(EditSpecialist, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          InputText: InputTextStub,
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
