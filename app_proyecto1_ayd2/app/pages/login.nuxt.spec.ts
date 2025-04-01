import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import LoginView from '~/pages/login.vue'

// Mock de login del authStore
const loginMock = vi.fn()

vi.mock('~/stores/auth', () => ({
  useAuthStore: () => ({ login: loginMock })
}))

vi.mock('vue-sonner', () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn()
  }
}))

const FormStub = {
  props: ['initialValues', 'resolver'],
  emits: ['submit'],
  template: `
    <form @submit.prevent="$emit('submit', {
      valid: true,
      values: { username: 'admin', password: '123456' }
    })">
      <slot :$form="{ username: { invalid: false }, password: { invalid: false } }"></slot>
      <button type="submit" class="submit-btn">Submit</button>
    </form>
  `
}

const stubs = {
  Form: FormStub,
  FloatLabel: { template: '<div><slot /></div>' },
  InputText: { template: '<input />', props: ['name'] },
  Password: { template: '<input type="password" />', props: ['name'] },
  Button: { template: '<button><slot /></slot>', props: ['type'] },
  Message: { template: '<div class="message"><slot /></div>' }
}

describe('LoginView.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renderiza el título y el mensaje de bienvenida', () => {
    const wrapper = mount(LoginView, { global: { stubs } })

    expect(wrapper.text()).toContain('Bienvenido!')
    expect(wrapper.text()).toContain('Ingresa tus datos')
  })

  it('envía el formulario correctamente cuando es válido', async () => {
    const wrapper = mount(LoginView, { global: { stubs } })

    const submitBtn = wrapper.find('.submit-btn')
    await submitBtn.trigger('submit')

    expect(loginMock).toHaveBeenCalledWith({
      username: 'admin',
      password: '123456'
    })
  })

  it('no llama login si el formulario no es válido', async () => {
    const InvalidFormStub = {
      props: ['initialValues', 'resolver'],
      emits: ['submit'],
      template: `
        <form @submit.prevent="$emit('submit', {
          valid: false,
          values: {}
        })">
          <slot :$form="{ username: { invalid: true, error: { message: 'El username es obligatorio.' } }, password: { invalid: true, error: { message: 'La password es obligatoria.' } } }"></slot>
          <button type="submit" class="submit-btn">Submit</button>
        </form>
      `
    }

    const wrapper = mount(LoginView, {
      global: {
        stubs: {
          ...stubs,
          Form: InvalidFormStub
        }
      }
    })

    const submitBtn = wrapper.find('.submit-btn')
    await submitBtn.trigger('submit')

    expect(loginMock).not.toHaveBeenCalled()
  })
})
