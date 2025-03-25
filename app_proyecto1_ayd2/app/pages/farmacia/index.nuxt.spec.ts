import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'

// Mock de useQuery y refetch
const refetchMedicinesMock = vi.fn()
const refetchLowStockMock = vi.fn()

vi.mock('@pinia/colada', () => ({
  useQuery: vi.fn().mockImplementation(({ key }) => {
    if (key[0] === 'medicines') {
      return {
        state: ref({
          data: [
            { id: 1, name: 'Ibuprofeno', quantity: 50, price: 5, minQuantity: 10 },
            { id: 2, name: 'Paracetamol', quantity: 0, price: 3, minQuantity: 5 }
          ]
        }),
        asyncStatus: 'success',
        refetch: refetchMedicinesMock
      }
    }

    if (key[0] === 'medicines-low-stock') {
      return {
        state: ref({
          data: [
            { id: 3, name: 'Amoxicilina', quantity: 5, minQuantity: 10 }
          ]
        }),
        asyncStatus: 'success',
        refetch: refetchLowStockMock
      }
    }

    return {}
  })
}))

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
          <div>Nombre: {{ item.name }}</div>
          <div>Cantidad: {{ item.quantity }}</div>
        </div>
        <slot name="footer"></slot>
      </div>
    `
  },
  Column: {
    template: `<div class="column"></div>`,
    props: ['field', 'header']
  },
  Tag: {
    template: `<div class="tag"><slot /></div>`,
    props: ['severity']
  }
}

// Importar el componente después del mock
import Farmacia from '~/pages/farmacia/index.vue'

describe('Farmacia Page', () => {

  beforeEach(() => {
    refetchMedicinesMock.mockClear()
    refetchLowStockMock.mockClear()
  })

  it('renderiza los elementos principales', () => {
    const wrapper = mount(Farmacia, { global: { stubs } })

    // Verifica que el título exista
    expect(wrapper.text()).toContain('Farmacia')

    // Verifica que los botones de "Buscar", "Crear", "Iniciar Venta" y "Recargar Página" estén
    expect(wrapper.findAll('.button').some(b => b.text().includes('Buscar'))).toBe(true)
    expect(wrapper.findAll('.button').some(b => b.text().includes('Crear'))).toBe(true)
    expect(wrapper.findAll('.button').some(b => b.text().includes('Iniciar Venta'))).toBe(true)
    expect(wrapper.findAll('.button').some(b => b.text().includes('Recargar Página'))).toBe(true)
  })

  it('llama a refetchMedicines al buscar medicamentos por enter', async () => {
    const wrapper = mount(Farmacia, { global: { stubs } })

    const input = wrapper.find('.input-text')
    await input.trigger('keyup.enter')

    expect(refetchMedicinesMock).toHaveBeenCalled()
  })

  it('llama a refetchMedicines al hacer click en "Buscar"', async () => {
    const wrapper = mount(Farmacia, { global: { stubs } })

    const buscarButton = wrapper.findAll('.button').find(b => b.text().includes('Buscar'))
    await buscarButton?.trigger('click')

    expect(refetchMedicinesMock).toHaveBeenCalled()
  })

  it('llama a refetchMedicines y refetchLowStock al hacer click en "Recargar Página"', async () => {
    const wrapper = mount(Farmacia, { global: { stubs } })

    const recargarButton = wrapper.findAll('.button').find(b => b.text().includes('Recargar Página'))
    await recargarButton?.trigger('click')

    expect(refetchMedicinesMock).toHaveBeenCalled()
    expect(refetchLowStockMock).toHaveBeenCalled()
  })

  it('muestra los medicamentos cargados en la tabla principal', () => {
    const wrapper = mount(Farmacia, { global: { stubs } })

    const text = wrapper.text()

    expect(text).toContain('Medicamentos')
    expect(text).toContain('Ibuprofeno')
    expect(text).toContain('Paracetamol')
  })

  it('muestra los medicamentos con stock bajo en la tabla secundaria', () => {
    const wrapper = mount(Farmacia, { global: { stubs } })

    const text = wrapper.text()

    expect(text).toContain('Medicamentos Low Stock')
    expect(text).toContain('Amoxicilina')
  })
})
