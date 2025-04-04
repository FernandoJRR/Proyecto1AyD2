import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'

// Mocks
const refetchMock = vi.fn()

vi.mock('~/lib/api/surgeries/surgeries', () => ({
  getSurgeriesTypes: vi.fn()
}))

vi.mock('vue-router', () => ({
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  }
}))

vi.mock('~/composables/useCustomQuery', () => ({
  useCustomQuery: () => ({
    state: ref({
      data: [
        {
          id: '1',
          type: 'Laparoscopia',
          description: 'Cirugía mínima invasiva',
          specialistPayment: 1500,
          hospitalCost: 2000,
          surgeryCost: 3500
        }
      ]
    }),
    asyncStatus: 'success',
    refetch: refetchMock
  })
}))

const stubs = {
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  },
  InputText: {
    template: '<input @input="$emit(\'update:modelValue\', $event.target.value)" @keyup.enter="$emit(\'keyup.enter\')" />',
    props: ['modelValue'],
    emits: ['update:modelValue', 'keyup.enter']
  },
  Button: {
    template: '<button @click="$emit(\'click\')"><slot />{{ label }}</button>',
    props: ['label', 'icon', 'severity', 'rounded', 'raised', 'outlined']
  },
  DataTable: {
    template: '<div><slot name="header" /><slot /><slot name="footer" /></div>',
    props: ['value', 'loading']
  },
  Column: {
    props: ['field', 'header'],
    template: `
      <div>
        <slot name="body" :data="mockData">
          <span>{{ mockData[field] }}</span>
        </slot>
      </div>
    `,
    data() {
      return {
        mockData: {
          id: '1',
          type: 'Laparoscopia',
          description: 'Cirugía mínima invasiva',
          specialistPayment: 1500,
          hospitalCost: 2000,
          surgeryCost: 3500
        }
      }
    }
  }
}

import SurgeryTypeList from '~/pages/cirugias/index.vue'

describe('SurgeryTypeList.vue', () => {
  beforeEach(() => {
    refetchMock.mockClear()
  })

  it('renderiza título y botones principales', () => {
    const wrapper = mount(SurgeryTypeList, { global: { stubs } })
    expect(wrapper.text()).toContain('Tipos de Cirugías')
    expect(wrapper.text()).toContain('Crear Tipo')
    expect(wrapper.text()).toContain('Recargar Página')
  })

  it('renderiza los datos de tipos de cirugía', () => {
    const wrapper = mount(SurgeryTypeList, { global: { stubs } })
    expect(wrapper.text()).toContain('Laparoscopia')
    expect(wrapper.text()).toContain('3500')
  })

  it('llama refetch al hacer clic en "Buscar"', async () => {
    const wrapper = mount(SurgeryTypeList, { global: { stubs } })
    const buscarBtn = wrapper.findAll('button').find(b => b.text().includes('Buscar'))
    await buscarBtn?.trigger('click')
    expect(refetchMock).toHaveBeenCalled()
  })

  it('llama refetch al hacer clic en "Recargar Página"', async () => {
    const wrapper = mount(SurgeryTypeList, { global: { stubs } })
    const recargarBtn = wrapper.findAll('button').find(b => b.text().includes('Recargar'))
    await recargarBtn?.trigger('click')
    expect(refetchMock).toHaveBeenCalled()
  })

  it('muestra el total de tipos de cirugía', () => {
    const wrapper = mount(SurgeryTypeList, { global: { stubs } })
    expect(wrapper.text()).toContain('Hay en total 1 tipos de cirugía.')
  })
})
