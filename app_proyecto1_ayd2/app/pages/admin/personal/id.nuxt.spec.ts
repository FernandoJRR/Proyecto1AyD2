import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import EmployeeDetail from '~/pages/admin/personal/[id].vue'

// We'll use a mutable variable to simulate different states for useQuery.
let mockUseQueryReturn: any = {}

// Mock the useQuery hook from @pinia/colada.
vi.mock('@pinia/colada', () => ({
  useQuery: () => mockUseQueryReturn
}))

// Stub useRoute from vue-router to supply a dummy id.
const RouterLinkStub = {
  template: '<a class="router-link"><slot /></a>',
  props: ['to']
}
vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } }),
  RouterLink: RouterLinkStub
}))

// Simple stubs for Button and Tag.
const ButtonStub = {
  template: '<button class="button">{{ label }}</button>',
  props: ['label', 'icon', 'text']
}
const TagStub = {
  props: ['value', 'severity'],
  // If a "value" is passed, show it; otherwise render the default slot content.
  template: `<span class="tag">{{ value ? value : '' }}<slot /></span>`
}

describe('EmployeeDetail [id].vue', () => {
  beforeEach(() => {
    // Reset the mock state before each test.
    mockUseQueryReturn = { state: ref({}) }
  })

  it('displays the loading state when status is "pending"', () => {
    mockUseQueryReturn.state = ref({ status: 'pending' })
    const wrapper = mount(EmployeeDetail, {
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

  it('displays the error state when status is "error"', () => {
    mockUseQueryReturn.state = ref({ status: 'error' })
    const wrapper = mount(EmployeeDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          Tag: TagStub
        }
      }
    })
    expect(wrapper.text()).toContain('Ocurrio un error inesperado')
  })

  it('muestra detalles del empleado cuando status es "success"', () => {
    mockUseQueryReturn.state = ref({
      status: 'success',
      data: {
        employee: {
          firstName: 'Alice',
          lastName: 'Smith',
          salary: 5000,
          igssPercentage: 10,
          irtraPercentage: 0,
          employeeType: { name: 'Manager' }
        },
        histories: [
          {
            historyType: { type: 'Ingreso' },
            commentary: 'Inicio de contrato',
            historyDate: '2024-01-01'
          }
        ]
      }
    })

    const wrapper = mount(EmployeeDetail, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          Tag: TagStub
        }
      }
    })

    // Check the router-link (the "Ver Todos" button) is rendered.
    const routerLink = wrapper.findComponent(RouterLinkStub)
    expect(routerLink.exists()).toBe(true)
    expect(wrapper.find('.button').text()).toContain('Ver Todos')

    // Verify the employee's name is rendered.
    const nameHeader = wrapper.find('h1.text-3xl')
    expect(nameHeader.exists()).toBe(true)
    expect(nameHeader.text()).toContain('Alice Smith')

    // Verify the salary is rendered.
    expect(wrapper.text()).toContain('Q.5000')

    // Find all Tag components.
    const tagComponents = wrapper.findAllComponents(TagStub)
    // Expect three Tag instances:
    //   • Employee type tag
    //   • IGGS tag (should show "10%")
    //   • IRTRA tag (should show "No Aplica")
    expect(tagComponents.length).toBe(3)

    // Employee type tag: its value prop should be "Manager".
    expect(tagComponents[0].text()).toContain('Manager')

    // IGGS tag: since iggsPercentage is 10, slot text should be "10%" and severity would be 'success'.
    // Our stub renders slot content as plain text.
    const iggsTag = tagComponents.find(tag => tag.text().includes('10%'))
    expect(iggsTag).toBeTruthy()

    // IRTRA tag: with a falsy percentage, the slot should display "No Aplica".
    const irtraTag = tagComponents.find(tag => tag.text().includes('No Aplica'))
    expect(irtraTag).toBeTruthy()
  })
})
