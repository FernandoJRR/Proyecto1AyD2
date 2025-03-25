import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'

// Mock del mutate para espiar las llamadas
const mutateMock = vi.fn()

// Mock del hook de mutation
vi.mock('@pinia/colada', () => ({
  useMutation: () => ({
    mutate: mutateMock,
    asyncStatus: 'idle'
  })
}))

// Stub de Form que simula el envío de formulario
const FormStub = {
  props: ['initialValues', 'resolver'],
  emits: ['submit'],
  template: `
    <form @submit.prevent="$emit('submit', {
      valid: true,
      values: {
        name: 'Paracetamol',
        description: 'Analgésico y antipirético',
        price: 12.50,
        quantity: 100,
        minQuantity: 10
      }
    })">
      <slot :$form="{}"></slot>
      <button type="submit" class="submit-button">Submit</button>
    </form>
  `
}

// Stubs de componentes usados en el template
const stubs = {
  RouterLink: {
    template: `<a class="router-link"><slot /></a>`,
    props: ['to']
  },
  Button: {
    template: `<button class="button">{{ label }}</button>`,
    props: ['label', 'icon', 'text', 'rounded', 'raised', 'severity', 'type']
  },
  FloatLabel: {
    template: `<div class="float-label"><slot /></div>`
  },
  InputText: {
    template: `<input class="input-text" />`,
    props: ['name', 'type', 'fluid']
  },
  InputNumber: {
    template: `<input class="input-number" />`,
    props: ['name', 'min', 'minFractionDigits', 'maxFractionDigits', 'mode', 'currency', 'placeholder', 'fluid']
  },
  Message: {
    template: `<div class="message"><slot /></div>`,
    props: ['severity', 'size', 'variant']
  },
  Form: FormStub
}

// Importar el componente DESPUÉS del mock
import CrearMedicamento from '~/pages/farmacia/crear.vue'

describe('CrearMedicamento Page', () => {
  beforeEach(() => {
    mutateMock.mockClear()
  })

  it('renderiza los elementos principales', () => {
    const wrapper = mount(CrearMedicamento, { global: { stubs } })
    
    expect(wrapper.text()).toContain('Crear Medicamento')
    expect(wrapper.find('.router-link').exists()).toBe(true)
    expect(wrapper.find('.button').text()).toContain('Ver Todos')
  })

  it('llama a mutate con el payload correcto al enviar el formulario', async () => {
    const wrapper = mount(CrearMedicamento, { global: { stubs } })

    const submitButton = wrapper.find('.submit-button')
    await submitButton.trigger('submit')

    const expectedPayload = {
      name: 'Paracetamol',
      description: 'Analgésico y antipirético',
      price: 12.50,
      quantity: 100,
      minQuantity: 10
    }

    expect(mutateMock).toHaveBeenCalledWith(expectedPayload)
  })
})
