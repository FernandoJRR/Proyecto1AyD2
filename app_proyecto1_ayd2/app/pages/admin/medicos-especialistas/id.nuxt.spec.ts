import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'

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

// Stubs simples para los componentes Button
const ButtonStub = {
  template: '<button class="button">{{ label }}</button>',
  props: ['label', 'icon', 'text']
}

// Importa el componente después de los mocks
import VerMedicoEspecialista from '~/pages/admin/medicos-especialistas/[id].vue'

describe('VerMedicoEspecialista [id].vue', () => {
  beforeEach(() => {
    // Resetea el estado de mock antes de cada prueba
    mockUseQueryReturn = { state: ref({}) }
  })

  it('muestra el estado de carga cuando status es "pending"', () => {
    mockUseQueryReturn.state = ref({ status: 'pending' })

    const wrapper = mount(VerMedicoEspecialista, {
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

    const wrapper = mount(VerMedicoEspecialista, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub
        }
      }
    })

    expect(wrapper.text()).toContain('Ocurrió un error inesperado')
  })

  it('muestra los detalles del médico especialista cuando la carga es exitosa', () => {
    const specialistData = {
      nombres: 'Ana',
      apellidos: 'González',
      dpi: '1234567890123'
    }

    mockUseQueryReturn.state = ref({
      status: 'success',
      data: specialistData
    })

    const wrapper = mount(VerMedicoEspecialista, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub
        }
      }
    })

    // Verifica que el RouterLink esté renderizado
    const routerLink = wrapper.findComponent(RouterLinkStub)
    expect(routerLink.exists()).toBe(true)

    // Verifica el título del nombre completo del especialista
    const nameHeader = wrapper.find('h1.text-3xl')
    expect(nameHeader.exists()).toBe(true)
    expect(nameHeader.text()).toContain('Ana González')

    // Verifica el DPI mostrado
    expect(wrapper.text()).toContain('DPI:')
    expect(wrapper.text()).toContain('1234567890123')
  })
})
