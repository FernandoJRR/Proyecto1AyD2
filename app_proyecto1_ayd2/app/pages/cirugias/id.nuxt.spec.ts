import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref } from 'vue'
import SurgeryDetail from '~/pages/cirugias/[id].vue'

interface SurgeryState {
  status: string;
  data?: {
    id: string;
    performedDate: string;
    hospitalCost: number;
    surgeryCost: number;
    surgeryType: {
      type: string;
      description: string;
      specialistPayment: number;
    };
    surgeryEmployees: {
      employeeName: string;
      employeeLastName: string;
      specialistPayment: number;
      specialistEmployeeId: string;
    }[];
  };
}

let surgeryMockState = ref<SurgeryState>({ status: 'pending' })

vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '1' } }),
  useRouter: () => ({ back: vi.fn() }),
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  }
}))

vi.mock('~/lib/api/surgeries/surgeries', () => ({
  getSurgery: vi.fn()
}))

vi.mock('@pinia/colada', () => ({
  useQuery: () => ({
    state: surgeryMockState,
    asyncStatus: 'success',
    refetch: vi.fn()
  })
}))

const stubs = {
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
    template: '<button><slot />{{ label }}</button>'
  }
}

describe('SurgeryDetail.vue', () => {
  beforeEach(() => {
    surgeryMockState.value = { status: 'pending' }
  })

  it('muestra mensaje de carga si status es "pending"', () => {
    surgeryMockState.value = { status: 'pending' }
    const wrapper = mount(SurgeryDetail, { global: { stubs } })
    expect(wrapper.text()).toContain('Cargando...')
  })

  it('muestra mensaje de error si status es "error"', () => {
    surgeryMockState.value = { status: 'error' }
    const wrapper = mount(SurgeryDetail, { global: { stubs } })
    expect(wrapper.text()).toContain('Ocurrió un error inesperado')
  })

  it('muestra los datos de la cirugía si status es "success"', () => {
    surgeryMockState.value = {
      status: 'success',
      data: {
        id: '123',
        performedDate: '2025-01-01',
        hospitalCost: 1000,
        surgeryCost: 2000,
        surgeryType: {
          type: 'Laparoscopia',
          description: 'Cirugía mínima invasiva',
          specialistPayment: 500
        },
        surgeryEmployees: [
          {
            employeeName: 'Ana',
            employeeLastName: 'Gómez',
            specialistPayment: 500,
            specialistEmployeeId: 'emp1'
          }
        ]
      }
    }

    const wrapper = mount(SurgeryDetail, { global: { stubs } })

    expect(wrapper.text()).toContain('Detalle de la Cirugía')
    expect(wrapper.html()).toContain('Laparoscopia')
    expect(wrapper.html()).toContain('Ana Gómez')
    expect(wrapper.html()).toContain('Q500')
  })
})
