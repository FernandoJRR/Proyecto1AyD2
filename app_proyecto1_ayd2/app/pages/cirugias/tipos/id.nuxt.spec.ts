import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import DetalleTipoCirugia from '~/pages/cirugias/tipos/[id].vue'

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

// Mocks
vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } }),
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  }
}))

vi.mock('~/lib/api/surgeries/surgeries', () => ({
  getSurgeryType: vi.fn()
}))

vi.mock('~/composables/useCustomQuery', () => ({
  useCustomQuery: () => ({
    state: surgeryTypeStateMock
  })
}))

// Stubs
const stubs = {
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  },
  InputText: {
    props: ['modelValue'],
    template: '<input :value="modelValue" readonly />'
  },
  InputNumber: {
    props: ['modelValue'],
    template: '<input :value="modelValue" readonly />'
  },
  Button: {
    props: ['label'],
    template: '<button>{{ label }}</button>'
  }
}

describe('DetalleTipoCirugia.vue', () => {
  beforeEach(() => {
    surgeryTypeStateMock.value = { status: 'pending' }
  })

  it('muestra "Cargando..." cuando el estado es pending', () => {
    surgeryTypeStateMock.value = { status: 'pending' }
    const wrapper = mount(DetalleTipoCirugia, { global: { stubs } })
    expect(wrapper.text()).toContain('Cargando...')
  })

  it('muestra error cuando el estado es error', () => {
    surgeryTypeStateMock.value = { status: 'error' }
    const wrapper = mount(DetalleTipoCirugia, { global: { stubs } })
    expect(wrapper.text()).toContain('Ocurrió un error inesperado')
  })

  it('muestra los datos cuando el estado es success', () => {
    surgeryTypeStateMock.value = {
      status: 'success',
      data: {
        type: 'Laparoscopia',
        description: 'Cirugía mínimamente invasiva',
        specialistPayment: 1200,
        hospitalCost: 1800,
        surgeryCost: 3000
      }
    }

    const wrapper = mount(DetalleTipoCirugia, { global: { stubs } })
    expect(wrapper.text()).toContain('Detalle del Tipo de Cirugía')
    expect(wrapper.html()).toContain('Laparoscopia')
    expect(wrapper.html()).toContain('Cirugía mínimamente invasiva')
    expect(wrapper.html()).toContain('1200')
    expect(wrapper.html()).toContain('1800')
    expect(wrapper.html()).toContain('3000')
  })
})
