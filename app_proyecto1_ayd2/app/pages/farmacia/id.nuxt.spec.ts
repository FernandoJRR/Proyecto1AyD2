import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import MedicineDetail from '~/pages/farmacia/[id].vue'

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

// Stubs simples para los componentes Button y Tag
const ButtonStub = {
  template: '<button class="button">{{ label }}</button>',
  props: ['label', 'icon', 'text']
}

const TagStub = {
  props: ['severity'],
  template: `<span class="tag"><slot /></span>`
}

describe('MedicineDetail [id].vue', () => {
  beforeEach(() => {
    // Resetea el estado de mock antes de cada prueba
    mockUseQueryReturn = { state: ref({}) }
  })

  it('muestra el estado de carga cuando status es "pending"', () => {
    mockUseQueryReturn.state = ref({ status: 'pending' })

    const wrapper = mount(MedicineDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          Tag: TagStub
        }
      }
    })

    expect(wrapper.text()).toContain('Cargando...')
  })

  it('muestra el estado de error cuando status es "error"', () => {
    mockUseQueryReturn.state = ref({ status: 'error' })

    const wrapper = mount(MedicineDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          Tag: TagStub
        }
      }
    })

    expect(wrapper.text()).toContain('Ocurrió un error inesperado')
  })

  it('muestra los detalles del medicamento cuando la carga es exitosa', () => {
    // Datos de prueba para el medicamento
    const medicineData = {
      name: 'Amoxicilina',
      description: 'Antibiótico de amplio espectro',
      price: 25.5,
      quantity: 8,
      minQuantity: 5
    }

    mockUseQueryReturn.state = ref({
      status: 'success',
      data: medicineData
    })

    const wrapper = mount(MedicineDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          Tag: TagStub
        }
      }
    })

    // Verifica que se renderiza el botón "Ver Todos"
    const routerLink = wrapper.findComponent(RouterLinkStub)
    expect(routerLink.exists()).toBe(true)
    expect(wrapper.find('.button').text()).toContain('Ver Todos')

    // Verifica que el nombre del medicamento aparece
    const nameHeader = wrapper.find('h1.text-3xl')
    expect(nameHeader.exists()).toBe(true)
    expect(nameHeader.text()).toContain('Amoxicilina')

    // Verifica descripción
    expect(wrapper.text()).toContain('Antibiótico de amplio espectro')

    // Verifica precio con formato de moneda
    expect(wrapper.text()).toContain('Q.25.50')

    // Verifica cantidad en stock
    expect(wrapper.text()).toContain('8')

    // Verifica cantidad mínima requerida
    expect(wrapper.text()).toContain('5')

    // Verifica que el Tag esté renderizado con el estado de stock correcto
    const tagComponents = wrapper.findAllComponents(TagStub)
    expect(tagComponents.length).toBe(1)

    // Debe mostrar el estado de stock en el Tag
    expect(tagComponents[0].text()).toContain('Disponible')
  })

  it('muestra el estado "Cantidad Muy Baja" si la cantidad es igual o menor a minQuantity', () => {
    const medicineData = {
      name: 'Ibuprofeno',
      description: 'Analgésico',
      price: 10,
      quantity: 5,
      minQuantity: 5
    }

    mockUseQueryReturn.state = ref({
      status: 'success',
      data: medicineData
    })

    const wrapper = mount(MedicineDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          Tag: TagStub
        }
      }
    })

    const tagComponents = wrapper.findAllComponents(TagStub)
    expect(tagComponents.length).toBe(1)

    expect(tagComponents[0].text()).toContain('Cantidad Muy Baja')
  })

  it('muestra el estado "Agotado" si la cantidad es 0', () => {
    const medicineData = {
      name: 'Paracetamol',
      description: 'Analgésico y antipirético',
      price: 5,
      quantity: 0,
      minQuantity: 5
    }

    mockUseQueryReturn.state = ref({
      status: 'success',
      data: medicineData
    })

    const wrapper = mount(MedicineDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          Tag: TagStub
        }
      }
    })

    const tagComponents = wrapper.findAllComponents(TagStub)
    expect(tagComponents.length).toBe(1)

    expect(tagComponents[0].text()).toContain('Agotado')
  })
})
