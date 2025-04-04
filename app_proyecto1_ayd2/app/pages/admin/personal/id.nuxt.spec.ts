import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import EmployeeDetail from '~/pages/admin/personal/[id].vue'

let mockUseQueryReturn: any = {}

vi.mock('@pinia/colada', () => ({
  useQuery: () => mockUseQueryReturn
}))

vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } })
}))

const InputTextStub = {
  template: '<input class="inputtext" />'
}

const RouterLinkStub = {
  template: '<a class="router-link"><slot /></a>',
  props: ['to']
}

const ButtonStub = {
  template: '<button class="button">{{ label }}</button>',
  props: ['label', 'icon', 'text']
}

const TagStub = {
  props: ['value', 'severity', 'rounded'],
  template: `<span class="tag">{{ value }}<slot /></span>`
}

const SelectStub = {
  props: ['modelValue', 'options', 'placeholder'],
  template: `<select class="select-stub"><slot /></select>`
}

const DataTableStub = {
  props: ['value', 'dataKey'],
  template: `
    <div class="datatable">
      <slot name="header" />
      <slot />
      <div class="table-content">
        <slot name="body" :data="value[0]" />
      </div>
    </div>
  `
}

const ColumnStub = {
  props: ['field', 'header'],
  template: `
    <div class="column">
      <slot name="body" :data="{
        historyType: { type: 'Ingreso' },
        commentary: 'Inicio de contrato',
        historyDate: '2024-01-01'
      }" />
    </div>
  `
}

describe('EmployeeDetail [id].vue', () => {
  beforeEach(() => {
    mockUseQueryReturn = { state: ref({}) }
  })

  it('muestra el estado de carga cuando status es "pending"', () => {
    mockUseQueryReturn.state = ref({ status: 'pending' })
    const wrapper = mount(EmployeeDetail, {
      global: {
        stubs: { RouterLink: RouterLinkStub, Button: ButtonStub, Tag: TagStub }
      }
    })
    expect(wrapper.text()).toContain('Cargando...')
  })

  it('muestra el estado de error cuando status es "error"', () => {
    mockUseQueryReturn.state = ref({ status: 'error' })
    const wrapper = mount(EmployeeDetail, {
      global: {
        stubs: { RouterLink: RouterLinkStub, Button: ButtonStub, Tag: TagStub }
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
          Tag: TagStub,
          DataTable: DataTableStub,
          Column: ColumnStub,
          InputText: InputTextStub,
          Select: SelectStub
        }
      }
    });

    expect(wrapper.find('h1.text-3xl').text()).toContain('Alice Smith')
    expect(wrapper.text()).toContain('Q.5000')
    expect(wrapper.text()).toContain('10%')
    expect(wrapper.text()).toContain('No Aplica')
    expect(wrapper.text()).toContain('Ingreso')
    expect(wrapper.text()).toContain('Inicio de contrato')
    expect(wrapper.text()).toContain('2024-01-01')
  })
})
