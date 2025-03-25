import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import PatientDetail from '~/pages/pacientes/[id].vue'

// Variable mutable para simular los estados de useQuery
let mockUseQueryReturn: any = {}

// Mock de useQuery de @pinia/colada
vi.mock('@pinia/colada', () => ({
  useQuery: () => mockUseQueryReturn
}))

// Stub de useRoute de vue-router para retornar un id fijo
const RouterLinkStub = {
  template: '<a class="router-link"><slot /></a>',
  props: ['to']
}

vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } }),
  RouterLink: RouterLinkStub
}))

// Stub para el componente Button
const ButtonStub = {
  template: '<button class="button">{{ label }}</button>',
  props: ['label', 'icon', 'text']
}

describe('PatientDetail [id].vue', () => {
  beforeEach(() => {
    // Resetea el estado del mock antes de cada prueba
    mockUseQueryReturn = { state: ref({}) }
  })

  it('muestra el estado de carga cuando status es "pending"', () => {
    mockUseQueryReturn.state = ref({ status: 'pending' })

    const wrapper = mount(PatientDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub
        }
      }
    })

    expect(wrapper.text()).toContain('Cargando...')
  })

  it('muestra el estado de error cuando status es "error"', () => {
    mockUseQueryReturn.state = ref({ status: 'error' })

    const wrapper = mount(PatientDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub
        }
      }
    })

    expect(wrapper.text()).toContain('Ocurrió un error inesperado')
  })

  it('muestra los detalles del paciente cuando la carga es exitosa', () => {
    const patientData = {
      firstnames: 'Ana María',
      lastnames: 'Gómez López',
      dpi: '1234567890123'
    }

    mockUseQueryReturn.state = ref({
      status: 'success',
      data: patientData
    })

    const wrapper = mount(PatientDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub
        }
      }
    })

    // Verifica que el botón "Ver Todos" se renderiza
    const routerLink = wrapper.findComponent(RouterLinkStub)
    expect(routerLink.exists()).toBe(true)

    // Verifica el nombre del paciente
    const nameHeader = wrapper.find('h1.text-3xl')
    expect(nameHeader.exists()).toBe(true)
    expect(nameHeader.text()).toContain('Ana María Gómez López')

    // Verifica el DPI
    expect(wrapper.text()).toContain('DPI:')
    expect(wrapper.text()).toContain('1234567890123')
  })
})
