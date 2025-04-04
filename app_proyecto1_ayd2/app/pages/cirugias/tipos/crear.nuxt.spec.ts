import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import CrearTipoCirugia from '~/pages/cirugias/tipos/crear.vue'

// Mocks
const mutateMock = vi.fn()

vi.mock('@pinia/colada', () => ({
  useMutation: () => ({
    mutate: mutateMock,
    asyncStatus: 'idle'
  })
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({ back: vi.fn() }),
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  }
}))

vi.mock('vue-sonner', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn()
  }
}))

// Form válido
const FormStub = {
  props: ['initialValues', 'resolver'],
  emits: ['submit'],
  template: `
    <form @submit.prevent="$emit('submit', {
      valid: true,
      values: {
        type: 'Laparoscopia',
        description: 'Cirugía mínimamente invasiva',
        specialistPayment: 1200,
        hospitalCost: 1800,
        surgeryCost: 3000
      }
    })">
      <slot :$form="{}" />
      <button class="submit-button" type="submit">Crear</button>
    </form>
  `
}

// Stubs generales
const stubs = {
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  },
  Button: {
    template: '<button @click="$emit(\'click\')"><slot />{{ label }}</button>',
    props: ['label', 'icon', 'severity', 'text', 'type']
  },
  FloatLabel: { template: '<div><slot /></div>' },
  InputText: { template: '<input />', props: ['name'] },
  InputNumber: { template: '<input />', props: ['name'] },
  Message: { template: '<div class="message"><slot /></div>' },
  Form: FormStub
}

describe('CrearTipoCirugia.vue', () => {
  beforeEach(() => {
    mutateMock.mockClear()
  })

  it('renderiza encabezado y botón "Ver Todos"', () => {
    const wrapper = mount(CrearTipoCirugia, { global: { stubs } })
    expect(wrapper.text()).toContain('Crear Tipo de Cirugía')
    expect(wrapper.text()).toContain('Ver Todos')
  })

  it('llama a mutate con los datos correctos al enviar el formulario', async () => {
    const wrapper = mount(CrearTipoCirugia, { global: { stubs } })
    const form = wrapper.find('form')
    await form.trigger('submit')

    expect(mutateMock).toHaveBeenCalledWith({
      type: 'Laparoscopia',
      description: 'Cirugía mínimamente invasiva',
      specialistPayment: 1200,
      hospitalCost: 1800,
      surgeryCost: 3000
    })
  })
})
