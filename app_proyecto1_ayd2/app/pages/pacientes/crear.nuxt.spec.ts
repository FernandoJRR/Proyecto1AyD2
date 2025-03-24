import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'

// Mock del mutate para espiar las llamadas
const mutateMock = vi.fn()

// Mock del hook de mutation de @pinia/colada
vi.mock('@pinia/colada', () => ({
  useMutation: () => ({
    mutate: mutateMock,
    asyncStatus: 'idle'
  })
}))

// Stub del Form que simula el envío del formulario
const FormStub = {
  props: ['initialValues', 'resolver'],
  emits: ['submit'],
  template: `
    <form @submit.prevent="$emit('submit', {
      valid: true,
      values: {
        firstnames: 'Juan',
        lastnames: 'Pérez',
        dpi: '1234567890123'
      }
    })">
      <slot :$form="{}"></slot>
      <button type="submit" class="submit-button">Submit</button>
    </form>
  `
}

// Stubs de los componentes usados en el template
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
  Message: {
    template: `<div class="message"><slot /></div>`,
    props: ['severity', 'size', 'variant']
  },
  Form: FormStub
}

// Importa el componente DESPUÉS del mock
import CrearPaciente from '~/pages/pacientes/crear.vue'

describe('CrearPaciente Page', () => {
  beforeEach(() => {
    mutateMock.mockClear()
  })

  it('renderiza los elementos principales', () => {
    const wrapper = mount(CrearPaciente, { global: { stubs } })

    expect(wrapper.text()).toContain('Crear Paciente')
    expect(wrapper.find('.router-link').exists()).toBe(true)
    expect(wrapper.find('.button').text()).toContain('Ver Todos')
  })

  it('llama a mutate con el payload correcto al enviar el formulario', async () => {
    const wrapper = mount(CrearPaciente, { global: { stubs } })

    const submitButton = wrapper.find('.submit-button')
    await submitButton.trigger('submit')

    const expectedPayload = {
      firstnames: 'Juan',
      lastnames: 'Pérez',
      dpi: '1234567890123'
    }

    expect(mutateMock).toHaveBeenCalledWith(expectedPayload)
  })

  it('no llama a mutate si el formulario no es válido', async () => {
    // Sobrescribimos el FormStub para simular un formulario inválido
    const InvalidFormStub = {
      props: ['initialValues', 'resolver'],
      emits: ['submit'],
      template: `
        <form @submit.prevent="$emit('submit', { valid: false, values: {} })">
          <slot :$form="{}"></slot>
          <button type="submit" class="submit-button">Submit</button>
        </form>
      `
    }

    const wrapper = mount(CrearPaciente, {
      global: {
        stubs: { ...stubs, Form: InvalidFormStub }
      }
    })

    const submitButton = wrapper.find('.submit-button')
    await submitButton.trigger('submit')

    expect(mutateMock).not.toHaveBeenCalled()
  })
})
