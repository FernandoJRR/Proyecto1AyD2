import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { ref, nextTick } from 'vue'
import SurgeryEdit from '~/pages/cirugias/editar/[id].vue'

let surgeryStateMock = ref({
  status: 'success',
  data: {
    id: '123',
    performedDate: '2024-12-01',
    hospitalCost: 5000,
    surgeryCost: 8000,
    surgeryType: {
      id: 'type1',
      description: 'Cirugía abdominal',
      hospitalCost: 5000,
      specialistPayment: 1500,
      surgeryCost: 8000,
      type: 'Laparoscopia'
    },
    surgeryEmployees: [
      {
        employeeName: 'Laura',
        employeeLastName: 'Pérez',
        specialistPayment: 1200,
        specialistEmployeeId: 'esp1'
      }
    ]
  }
})

vi.mock('vue-router', () => ({
  useRoute: () => ({ params: { id: '123' } }),
  useRouter: () => ({ back: vi.fn() }),
  RouterLink: {
    template: '<a><slot /></slot></a>',
    props: ['to']
  }
}))

vi.mock('vue-sonner', () => ({
  toast: { error: vi.fn(), success: vi.fn() }
}))

vi.mock('~/lib/api/surgeries/surgeries', () => ({
  getSurgery: vi.fn(),
  addDoctorSurgery: vi.fn(),
  deleteDoctorSurgery: vi.fn()
}))

vi.mock('~/lib/api/admin/employee', () => ({ getDoctors: vi.fn() }))
vi.mock('~/lib/api/admin/specialist-employee', () => ({ getAllSpecialistEmployees: vi.fn() }))

vi.mock('@pinia/colada', () => ({
  useQuery: () => ({
    state: surgeryStateMock,
    asyncStatus: surgeryStateMock.value.status,
    refetch: vi.fn()
  }),
  useMutation: () => ({
    mutate: vi.fn(),
    asyncStatus: 'idle'
  })
}))

vi.mock('~/composables/useCustomQuery', () => ({
  useCustomQuery: () => ({
    state: ref({ data: [] }),
    asyncStatus: 'success',
    refetch: vi.fn()
  })
}))

const stubs = {
  RouterLink: true,
  InputText: {
    props: ['modelValue'],
    template: `<input :value="modelValue" readonly />`
  },
  InputNumber: {
    props: ['modelValue'],
    template: `<input :value="modelValue" readonly />`
  },
  Button: {
    props: ['label'],
    template: `<button><slot />{{ label }}</button>`
  },
  DataTable: {
    props: ['value'],
    template: `
      <div>
        <div v-for="item in value" :key="item.employeeName">
          <div>{{ item.employeeName }}</div>
          <div>{{ item.employeeLastName }}</div>
        </div>
        <slot name="footer" />
      </div>
    `
  },
  Column: {
    props: ['field', 'header'],
    template: `<div><slot name="body" :data="{ [field]: field }" /></div>`
  },
  Dialog: {
    template: `<div><slot /><slot name="footer" /></div>`,
    props: ['visible', 'header']
  }
}

describe('[id].vue', () => {
  it('muestra inputs vacíos si status es "pending"', async () => {
    surgeryStateMock.value = {
      status: 'pending',
      data: {
        id: '',
        performedDate: '',
        hospitalCost: 0,
        surgeryCost: 0,
        surgeryType: {
          id: '',
          description: '',
          hospitalCost: 0,
          specialistPayment: 0,
          surgeryCost: 0,
          type: ''
        },
        surgeryEmployees: []
      }
    }
    const wrapper = mount(SurgeryEdit, { global: { stubs } })
    await nextTick()
    expect(wrapper.html()).toContain('Detalle de la Cirugía')
    expect(wrapper.html()).toContain('value=""')
  })

  it('muestra inputs vacíos si status es "error"', async () => {
    surgeryStateMock.value = {
      status: 'error',
      data: {
        id: '',
        performedDate: '',
        hospitalCost: 0,
        surgeryCost: 0,
        surgeryType: {
          id: '',
          description: '',
          hospitalCost: 0,
          specialistPayment: 0,
          surgeryCost: 0,
          type: ''
        },
        surgeryEmployees: []
      }
    }
    const wrapper = mount(SurgeryEdit, { global: { stubs } })
    await nextTick()
    expect(wrapper.html()).toContain('Detalle de la Cirugía')
    expect(wrapper.html()).toContain('value=""')
  })

  it('muestra los datos de la cirugía correctamente', async () => {
    surgeryStateMock.value = {
      status: 'success',
      data: {
        id: '123',
        performedDate: '2024-12-01',
        hospitalCost: 5000,
        surgeryCost: 8000,
        surgeryType: {
          id: 'type1',
          description: 'Cirugía abdominal',
          hospitalCost: 5000,
          specialistPayment: 1500,
          surgeryCost: 8000,
          type: 'Laparoscopia'
        },
        surgeryEmployees: [
          {
            employeeName: 'Laura',
            employeeLastName: 'Pérez',
            specialistPayment: 1200,
            specialistEmployeeId: 'esp1'
          }
        ]
      }
    }
    const wrapper = mount(SurgeryEdit, { global: { stubs } })
    await nextTick()
    expect(wrapper.text()).toContain('Detalle de la Cirugía')
    expect(wrapper.html()).toContain('Laparoscopia')
    expect(wrapper.html()).toContain('Laura')
    expect(wrapper.html()).toContain('Pérez')
  })
})
