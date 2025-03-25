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

// Stubs de componentes usados en el template
const RouterLinkStub = {
  template: `<a class="router-link"><slot /></a>`,
  props: ['to']
}

const ButtonStub = {
  template: `<button class="button">{{ label }}</button>`,
  props: ['label', 'icon', 'text', 'rounded', 'raised', 'severity', 'type']
}

const FloatLabelStub = {
  template: `<div class="float-label"><slot /></div>`
}

const InputTextStub = {
  template: `<input class="input-text" />`,
  props: ['name', 'type', 'fluid']
}

const MessageStub = {
  template: `<div class="message"><slot /></div>`,
  props: ['severity', 'size', 'variant']
}

// Stub de Form que simula el envío de formulario
const FormStub = {
  props: ['initialValues', 'resolver'],
  emits: ['submit'],
  template: `
    <form @submit.prevent="$emit('submit', {
      valid: true,
      values: {
        nombres: 'Carlos',
        apellidos: 'Ramirez',
        dpi: '1234567890123'
      }
    })">
      <slot :$form="{}"></slot>
      <button type="submit" class="submit-button">Submit</button>
    </form>
  `
}

// Importar el componente DESPUÉS del mock
import CrearMedicoEspecialista from '~/pages/admin/medicos-especialistas/crear.vue'

describe('CrearMedicoEspecialista Page', () => {
  beforeEach(() => {
    mutateMock.mockClear()
  })

  it('renderiza los elementos principales', () => {
    const wrapper = mount(CrearMedicoEspecialista, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          FloatLabel: FloatLabelStub,
          InputText: InputTextStub,
          Message: MessageStub,
          Form: FormStub
        }
      }
    })

    expect(wrapper.text()).toContain('Crear Médico Especialista')
    expect(wrapper.find('.router-link').exists()).toBe(true)
    expect(wrapper.findAll('.button').some(b => b.text().includes('Ver Todos'))).toBe(true)
  })

  it('llama a mutate con el payload correcto al enviar el formulario', async () => {
    const wrapper = mount(CrearMedicoEspecialista, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          Button: ButtonStub,
          FloatLabel: FloatLabelStub,
          InputText: InputTextStub,
          Message: MessageStub,
          Form: FormStub
        }
      }
    })

    const submitButton = wrapper.find('.submit-button')
    await submitButton.trigger('submit')

    const expectedPayload = {
      nombres: 'Carlos',
      apellidos: 'Ramirez',
      dpi: '1234567890123'
    }

    expect(mutateMock).toHaveBeenCalledWith(expectedPayload)
  })
})
