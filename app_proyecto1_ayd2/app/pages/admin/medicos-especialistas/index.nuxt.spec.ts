import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'

// Mock de refetch
const refetchEspecialistasMock = vi.fn()

// Mock de useQuery
vi.mock('@pinia/colada', () => ({
  useQuery: vi.fn().mockImplementation(({ key }) => {
    if (key[0] === 'especialistas') {
      return {
        state: ref({
          data: [
            { id: '1', nombres: 'Carlos', apellidos: 'Pérez', dpi: '12345678' },
            { id: '2', nombres: 'Ana', apellidos: 'Gómez', dpi: '87654321' }
          ]
        }),
        asyncStatus: 'success',
        refetch: refetchEspecialistasMock
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
          <div>Nombres: {{ item.nombres }}</div>
          <div>Apellidos: {{ item.apellidos }}</div>
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

// Importa el componente después del mock
import Especialistas from '~/pages/admin/medicos-especialistas/index.vue'

describe('Médicos Especialistas Page', () => {

  beforeEach(() => {
    refetchEspecialistasMock.mockClear()
  })

  it('renderiza el título y botones principales', () => {
    const wrapper = mount(Especialistas, { global: { stubs } })

    const text = wrapper.text()

    expect(text).toContain('Médicos Especialistas')
    expect(wrapper.findAll('.button').some(b => b.text().includes('Buscar'))).toBe(true)
    expect(wrapper.findAll('.button').some(b => b.text().includes('Crear Especialista'))).toBe(true)
    expect(wrapper.findAll('.button').some(b => b.text().includes('Recargar Página'))).toBe(true)
  })

  it('llama a refetchEspecialistas al presionar enter en el buscador', async () => {
    const wrapper = mount(Especialistas, { global: { stubs } })

    const input = wrapper.find('.input-text')
    await input.trigger('keyup.enter')

    expect(refetchEspecialistasMock).toHaveBeenCalled()
  })

  it('llama a refetchEspecialistas al hacer click en "Buscar"', async () => {
    const wrapper = mount(Especialistas, { global: { stubs } })

    const buscarButton = wrapper.findAll('.button').find(b => b.text().includes('Buscar'))
    await buscarButton?.trigger('click')

    expect(refetchEspecialistasMock).toHaveBeenCalled()
  })

  it('llama a refetchEspecialistas al hacer click en "Recargar Página"', async () => {
    const wrapper = mount(Especialistas, { global: { stubs } })

    const recargarButton = wrapper.findAll('.button').find(b => b.text().includes('Recargar Página'))
    await recargarButton?.trigger('click')

    expect(refetchEspecialistasMock).toHaveBeenCalled()
  })

  it('muestra los especialistas cargados en la tabla', () => {
    const wrapper = mount(Especialistas, { global: { stubs } })

    const text = wrapper.text()

    expect(text).toContain('Carlos')
    expect(text).toContain('Pérez')
    expect(text).toContain('12345678')
    expect(text).toContain('Ana')
    expect(text).toContain('Gómez')
    expect(text).toContain('87654321')
  })

  it('muestra el número total de especialistas en el footer', () => {
    const wrapper = mount(Especialistas, { global: { stubs } })

    const text = wrapper.text()

    expect(text).toContain('Hay en total 2 especialistas.')
  })
})
