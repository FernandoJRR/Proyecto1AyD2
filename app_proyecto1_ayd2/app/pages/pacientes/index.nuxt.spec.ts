import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'

// Mocks para useQuery y refetch
const refetchPatientsMock = vi.fn()

vi.mock('@pinia/colada', () => ({
  useQuery: vi.fn().mockImplementation(({ key }) => {
    if (key[0] === 'patients') {
      return {
        state: ref({
          data: [
            { id: '1', firstnames: 'Ana', lastnames: 'López', dpi: '1234567890123' },
            { id: '2', firstnames: 'Carlos', lastnames: 'Martínez', dpi: '9876543210987' }
          ]
        }),
        asyncStatus: 'success',
        refetch: refetchPatientsMock
      }
    }
    return {}
  })
}))

// Stubs de componentes
const stubs = {
  RouterLink: {
    template: `<a class="router-link"><slot /></a>`,
    props: ['to']
  },
  Button: {
    template: `<button class="button" @click="$emit('click')">{{ label }}</button>`,
    props: ['label', 'icon', 'text', 'rounded', 'raised', 'severity']
  },
  InputText: {
    template: `<input class="input-text" @keyup.enter="$emit('keyup.enter')" />`,
    props: ['modelValue', 'placeholder', 'class']
  },
  DataTable: {
    props: ['value', 'loading', 'tableStyle', 'stripedRows'],
    template: `
      <div class="datatable">
        <slot name="header"></slot>
        <div v-for="item in value" :key="item.id" class="datatable-row">
          <div>ID: {{ item.id }}</div>
          <div>Nombres: {{ item.firstnames }}</div>
          <div>Apellidos: {{ item.lastnames }}</div>
          <div>DPI: {{ item.dpi }}</div>
        </div>
        <slot name="footer"></slot>
      </div>
    `
  },
  Column: {
    template: `<div class="column"></div>`,
    props: ['field', 'header']
  }
}

// Importar el componente después de los mocks
import Pacientes from '~/pages/pacientes/index.vue'

describe('Pacientes Page', () => {

  beforeEach(() => {
    refetchPatientsMock.mockClear()
  })

  it('renderiza los elementos principales', () => {
    const wrapper = mount(Pacientes, { global: { stubs } })

    expect(wrapper.text()).toContain('Pacientes')

    expect(wrapper.findAll('.button').some(b => b.text().includes('Buscar'))).toBe(true)
    expect(wrapper.findAll('.button').some(b => b.text().includes('Crear Paciente'))).toBe(true)
    expect(wrapper.findAll('.button').some(b => b.text().includes('Recargar Página'))).toBe(true)
  })

  it('llama a refetchPatients al buscar pacientes por enter', async () => {
    const wrapper = mount(Pacientes, { global: { stubs } })

    const input = wrapper.find('.input-text')
    await input.trigger('keyup.enter')

    expect(refetchPatientsMock).toHaveBeenCalled()
  })

  it('llama a refetchPatients al hacer click en "Buscar"', async () => {
    const wrapper = mount(Pacientes, { global: { stubs } })

    const buscarButton = wrapper.findAll('.button').find(b => b.text().includes('Buscar'))
    await buscarButton?.trigger('click')

    expect(refetchPatientsMock).toHaveBeenCalled()
  })

  it('llama a refetchPatients al hacer click en "Recargar Página"', async () => {
    const wrapper = mount(Pacientes, { global: { stubs } })

    const recargarButton = wrapper.findAll('.button').find(b => b.text().includes('Recargar Página'))
    await recargarButton?.trigger('click')

    expect(refetchPatientsMock).toHaveBeenCalled()
  })

  it('muestra los pacientes cargados en la tabla', () => {
    const wrapper = mount(Pacientes, { global: { stubs } })

    const text = wrapper.text()

    expect(text).toContain('Listado de Pacientes')
    expect(text).toContain('Ana')
    expect(text).toContain('Carlos')
    expect(text).toContain('1234567890123')
    expect(text).toContain('9876543210987')
  })
})
