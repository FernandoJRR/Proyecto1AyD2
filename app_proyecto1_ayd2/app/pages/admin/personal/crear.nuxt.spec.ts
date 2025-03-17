import { ref } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'

// Create a mutable reference to spy on the mutation
const mutateMock = vi.fn()

// Mock the hooks used in the component
vi.mock('@pinia/colada', () => ({
  useQuery: () => ({
    // Simulate the userTypes query returning one option
    state: ref({ data: [{ id: '1', name: 'Admin' }] }),
    asyncStatus: 'success'
  }),
  // The mutation hook returns a mutate function that we can spy on
  useMutation: () => ({
    mutate: mutateMock,
    asyncStatus: 'idle'
  })
}))

// Define a Form stub that renders a real form element and emits a submit event
const FormStub = {
  props: ['initialValues', 'resolver'],
  emits: ['submit'],
  template: `
    <form @submit.prevent="$emit('submit', {
      valid: true,
      values: {
        firstName: 'John',
        lastName: 'Doe',
        salary: 1000,
        has_porcentaje_iggs: true,
        iggsPercentage: 10,
        has_porcentaje_irtra: true,
        irtraPercentage: 10,
        type: '1',
        username: 'john_doe',
        password: 'password123',
        password_repeat: 'password123'
      }
    })">
      <slot :$form="{}"></slot>
      <button type="submit" class="submit-button">Submit</button>
    </form>
  `
}

// Simple stubs for the other components used in the template.
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
    props: ['name', 'min', 'max', 'minFractionDigits', 'maxFractionDigits', 'mode', 'currency', 'placeholder', 'fluid', 'disabled']
  },
  ToggleSwitch: {
    template: `<div class="toggle-switch"></div>`,
    props: ['name', 'class']
  },
  Select: {
    template: `<select class="select"><slot /></select>`,
    props: ['name', 'optionLabel', 'optionValue', 'options', 'placeholder', 'fluid', 'modelValue'],
    model: {
      prop: 'modelValue',
      event: 'update:modelValue'
    }
  },
  Password: {
    template: `<input type="password" class="password" />`,
    props: ['name', 'feedback', 'fluid', 'toggleMask']
  },
  Message: {
    template: `<div class="message"><slot /></div>`,
    props: ['severity', 'size', 'variant']
  },
  Form: FormStub
}

// Import the component under test AFTER the mocks are defined.
import CrearPersonal from '~/pages/admin/personal/crear.vue'

describe('CrearPersonal Page', () => {
  beforeEach(() => {
    // Clear any previous calls to the mutation mock
    mutateMock.mockClear()
  })

  it('renders the main elements', () => {
    const wrapper = mount(CrearPersonal, { global: { stubs } })
    expect(wrapper.text()).toContain('Crear Usuario')
    expect(wrapper.find('.router-link').exists()).toBe(true)
    expect(wrapper.find('.button').text()).toContain('Ver Todos')
  })

  it('calls mutate with correct payload on form submission', async () => {
    const wrapper = mount(CrearPersonal, { global: { stubs } })
    // Find the submit button inside our Form stub and trigger a click.
    const submitButton = wrapper.find('.submit-button')
    await submitButton.trigger('submit')
    
    // Expected payload according to onFormSubmit logic in the component.
    const expectedPayload = {
      firstName: 'John',
      lastName: 'Doe',
      salary: 1000,
      iggsPercentage: 10,
      // Notice: both percentages use the same condition in the onFormSubmit code.
      irtraPercentage: 10,
      employeeTypeId: { id: '1' },
      createUserRequestDTO: { username: 'john_doe', password: 'password123' }
    }
    
    expect(mutateMock).toHaveBeenCalledWith(expectedPayload)
  })
})
