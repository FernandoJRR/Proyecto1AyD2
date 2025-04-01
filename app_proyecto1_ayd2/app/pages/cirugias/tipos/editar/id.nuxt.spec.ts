import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import EditarTipoCirugia from '~/pages/cirugias/tipos/editar/[id].vue'

// Mocks
const mutateMock = vi.fn()

interface SurgeryTypeState {
  status: 'pending' | 'error' | 'success';
  data?: {
    type: string;
    description: string;
    specialistPayment: number;
    hospitalCost: number;
    surgeryCost: number;
  };
}

let surgeryTypeStateMock = ref<SurgeryTypeState>({ status: 'pending' })

vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } }),
  useRouter: () => ({ back: vi.fn() }),
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  }
}))

vi.mock('~/lib/api/surgeries/surgeries', () => ({
  updateSurgeryType: vi.fn(),
  getSurgeryType: vi.fn()
}))

vi.mock('~/composables/useCustomQuery', () => ({
  useCustomQuery: () => ({
    state: surgeryTypeStateMock,
    asyncStatus: 'success',
    refetch: vi.fn()
  })
}))

vi.mock('@pinia/colada', () => ({
  useMutation: () => ({
    mutate: mutateMock,
    asyncStatus: 'idle'
  })
}))

const ValidFormStub = {
  props: ['initialValues', 'resolver'],
  emits: ['submit'],
  template: `
    <form @submit.prevent="$emit('submit', {
      valid: true,
      values: {
        type: 'Nueva Cirugía',
        description: 'Descripción actualizada',
        specialistPayment: 1600,
        hospitalCost: 2100,
        surgeryCost: 3700
      }
    })">
      <slot :$form="{}" />
      <button type="submit" class="submit-button">Guardar Cambios</button>
    </form>
  `
}

const InvalidFormStub = {
  props: ['initialValues', 'resolver'],
  emits: ['submit'],
  template: `
    <form @submit.prevent="$emit('submit', { valid: false, values: {} })">
      <slot :$form="{}" />
      <button type="submit" class="submit-button">Guardar Cambios</button>
    </form>
  `
}

const stubs = {
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  },
  Button: {
    props: ['label'],
    template: '<button><slot />{{ label }}</button>'
  },
  FloatLabel: { template: '<div><slot /></div>' },
  InputText: { props: ['name'], template: '<input />' },
  InputNumber: { props: ['name'], template: '<input />' },
  Message: { props: ['severity'], template: '<div><slot /></div>' }
}

describe('EditarTipoCirugia.vue', () => {
  beforeEach(() => {
    mutateMock.mockClear()
    surgeryTypeStateMock.value = { status: 'pending' }
  })

  it('muestra "Cargando datos..." si status es "pending"', () => {
    surgeryTypeStateMock.value = { status: 'pending' }
    const wrapper = mount(EditarTipoCirugia, { global: { stubs } })
    expect(wrapper.text()).toContain('Cargando datos...')
  })

  it('muestra mensaje de error si status es "error"', () => {
    surgeryTypeStateMock.value = { status: 'error' }
    const wrapper = mount(EditarTipoCirugia, { global: { stubs } })
    expect(wrapper.text()).toContain('Error al cargar los datos del tipo de cirugía')
  })

  it('muestra el formulario si status es "success"', () => {
    surgeryTypeStateMock.value = {
      status: 'success',
      data: {
        type: 'Cirugía General',
        description: 'Cirugía básica',
        specialistPayment: 1500,
        hospitalCost: 2000,
        surgeryCost: 3500
      }
    }

    const wrapper = mount(EditarTipoCirugia, {
      global: {
        stubs: {
          ...stubs,
          Form: ValidFormStub
        }
      }
    })

    expect(wrapper.text()).toContain('Editar Tipo de Cirugía')
    expect(wrapper.text()).toContain('Guardar Cambios')
  })

  it('llama mutate con los datos correctos al enviar el formulario válido', async () => {
    surgeryTypeStateMock.value = {
      status: 'success',
      data: {
        type: 'Cirugía General',
        description: 'Cirugía básica',
        specialistPayment: 1500,
        hospitalCost: 2000,
        surgeryCost: 3500
      }
    }

    const wrapper = mount(EditarTipoCirugia, {
      global: {
        stubs: {
          ...stubs,
          Form: ValidFormStub
        }
      }
    })

    const button = wrapper.find('.submit-button')
    await button.trigger('submit')

    expect(mutateMock).toHaveBeenCalledWith({
      type: 'Nueva Cirugía',
      description: 'Descripción actualizada',
      specialistPayment: 1600,
      hospitalCost: 2100,
      surgeryCost: 3700
    })
  })

  it('no llama mutate si el formulario es inválido', async () => {
    surgeryTypeStateMock.value = {
      status: 'success',
      data: {
        type: 'Cirugía General',
        description: 'Cirugía básica',
        specialistPayment: 1500,
        hospitalCost: 2000,
        surgeryCost: 3500
      }
    }

    const wrapper = mount(EditarTipoCirugia, {
      global: {
        stubs: {
          ...stubs,
          Form: InvalidFormStub
        }
      }
    })

    const button = wrapper.find('.submit-button')
    await button.trigger('submit')

    expect(mutateMock).not.toHaveBeenCalled()
  })
})
