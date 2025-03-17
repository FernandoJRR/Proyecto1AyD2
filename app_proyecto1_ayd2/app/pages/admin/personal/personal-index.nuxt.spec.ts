import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'

// se crea una variable para mockear
let mockQueryReturn: any

// se hace mock de pinia colada
vi.mock('@pinia/colada', () => ({
  useQuery: () => mockQueryReturn
}))

import { mount } from '@vue/test-utils'
import PersonalIndex from '~/pages/admin/personal/index.vue'

// se crea un estado para probar
beforeEach(() => {
  mockQueryReturn = {
    state: ref({
      data: [
        {
          id: '1',
          firstName: 'John',
          lastName: 'Doe',
          employeeType: { name: 'Developer' }
        },
        {
          id: '2',
          firstName: 'Jane',
          lastName: 'Smith',
          employeeType: { name: 'Manager' }
        }
      ]
    }),
    asyncStatus: 'success'
  }
})

// se hace stub de los componentes de primevue
const globalConfig = {
  stubs: {
    DataTable: {
      template: `<div class="data-table" :data-loading="loading">
                  <slot name="header"></slot>
                  <slot></slot>
                  <slot name="footer"></slot>
                </div>`,
      props: ['value', 'tableStyle', 'stripedRows', 'loading']
    },
    Column: {
      template: '<div class="column"><slot /></div>'
    },
    Button: {
      template: '<button class="button"><slot /></button>'
    },
    Tag: {
      template: '<span class="tag">{{ value }}</span>',
      props: ['value']
    },
    RouterLink: {
      template: '<a class="router-link"><slot /></a>',
      props: ['to']
    }
  }
}

describe('PersonalIndex', () => {
  it('renderiza footer con la cantidad correcta de usuarios', () => {
    const wrapper = mount(PersonalIndex, { global: globalConfig })
    expect(wrapper.text()).toContain('Hay en total 2 usuarios.')
  })

  it('se usa el prop de loading cuando la pagina esta cargando', () => {
    mockQueryReturn = {
      state: ref({ data: [] }),
      asyncStatus: 'loading'
    }
    const wrapper = mount(PersonalIndex, { global: globalConfig })
    const dataTable = wrapper.find('.data-table')
    expect(dataTable.attributes('data-loading')).toBe('true')
  })
})
