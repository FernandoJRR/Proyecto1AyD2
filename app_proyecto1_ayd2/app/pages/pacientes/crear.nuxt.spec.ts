import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'

const mutateMock = vi.fn()
const backMock = vi.fn()

// Mocks
vi.mock('@pinia/colada', () => ({
  useMutation: () => ({
    mutate: mutateMock,
    asyncStatus: 'idle'
  })
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({
    back: backMock
  })
}))

vi.mock('vue-sonner', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn()
  }
}))

// Stubs
const ButtonStub = {
  template: `<button @click="$emit('click')"><slot />{{ label }}</button>`,
  props: ['label', 'icon', 'text', 'severity', 'type']
}

const InputTextStub = {
  template: `<input />`,
  props: ['name', 'type', 'fluid']
}

const FloatLabelStub = {
  template: `<div><slot /></div>`
}

const MessageStub = {
  template: `<div class="message"><slot /></div>`,
  props: ['severity', 'size', 'variant']
}

const FormStub = {
  template: `
    <form @submit.prevent="$emit('submit', { valid: true, values: values })">
      <slot :$form="form" />
    </form>`,
  props: ['initialValues', 'resolver'],
  data() {
    return {
      values: {
        firstnames: 'Juan',
        lastnames: 'Pérez',
        dpi: '1234567890123'
      },
      form: {
        firstnames: { invalid: false },
        lastnames: { invalid: false },
        dpi: { invalid: false }
      }
    }
  }
}

const InvalidFormStub = {
  template: `
    <form @submit.prevent="$emit('submit', { valid: false, values: {} })">
      <slot :$form="{}" />
    </form>`
}

import CrearPaciente from '~/pages/pacientes/crear.vue'

describe('CrearPaciente.vue', () => {
  beforeEach(() => {
    mutateMock.mockClear()
    backMock.mockClear()
  })

  it('renderiza el encabezado y el botón "Volver"', () => {
    const wrapper = mount(CrearPaciente, {
      global: {
        stubs: {
          Button: ButtonStub,
          InputText: InputTextStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub
        }
      }
    })

    expect(wrapper.text()).toContain("Crear Paciente")

    const botones = wrapper.findAllComponents(ButtonStub)
    const volverButton = botones.find(b => b.text().includes("Volver"))

    expect(volverButton).toBeTruthy()
    expect(volverButton?.text()).toContain("Volver")
  })

  it('llama a mutate al enviar el formulario válido', async () => {
    const wrapper = mount(CrearPaciente, {
      global: {
        stubs: {
          Button: ButtonStub,
          InputText: InputTextStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: FormStub
        }
      }
    })

    const form = wrapper.find('form')
    await form.trigger('submit')

    expect(mutateMock).toHaveBeenCalledWith({
      firstnames: 'Juan',
      lastnames: 'Pérez',
      dpi: '1234567890123'
    })
  })

  it('no llama a mutate si el formulario es inválido', async () => {
    const wrapper = mount(CrearPaciente, {
      global: {
        stubs: {
          Button: ButtonStub,
          InputText: InputTextStub,
          FloatLabel: FloatLabelStub,
          Message: MessageStub,
          Form: InvalidFormStub
        }
      }
    })

    const form = wrapper.find('form')
    await form.trigger('submit')

    expect(mutateMock).not.toHaveBeenCalled()
  })
})
