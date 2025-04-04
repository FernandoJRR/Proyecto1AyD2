import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import App from '~/app.vue'

// Stub para NuxtLoadingIndicator
const NuxtLoadingIndicatorStub = {
  name: 'NuxtLoadingIndicatorStub',
  props: ['throttle', 'height', 'color'],
  template: `<div class="nuxt-loading-indicator"></div>`
}

// Stub para NuxtLayout
const NuxtLayoutStub = {
  name: 'NuxtLayoutStub',
  template: `<div class="nuxt-layout">Layout Content</div>`
}

// Stub para el  toaster
const ToasterStub = {
  name: 'ToasterStub',
  props: ['position', 'closeButton', 'toastOptions'],
  template: `
    <div class="toaster">
      <div class="error-icon"><slot name="error-icon" /></div>
      <div class="info-icon"><slot name="info-icon" /></div>
      <div class="success-icon"><slot name="success-icon" /></div>
      <div class="warning-icon"><slot name="warning-icon" /></div>
    </div>
  `
}

describe('App.vue', () => {
  it('renders NuxtLoadingIndicator, NuxtLayout, and Toaster with correct props and slots', () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          NuxtLoadingIndicator: NuxtLoadingIndicatorStub,
          NuxtLayout: NuxtLayoutStub,
          Toaster: ToasterStub
        }
      }
    })

    // se verifica NuxtLoadingIndicator
    const loadingIndicator = wrapper.findComponent(NuxtLoadingIndicatorStub)
    expect(loadingIndicator.exists()).toBe(true)
    expect(loadingIndicator.props('throttle')).toBe(130)
    expect(loadingIndicator.props('height')).toBe(4)
    expect(loadingIndicator.props('color')).toBe('var(--p-primary-400)')

    // se verifica NuxtLayout
    const layout = wrapper.findComponent(NuxtLayoutStub)
    expect(layout.exists()).toBe(true)
    expect(layout.text()).toContain('Layout Content')

    // se verifica Toaster props
    const toaster = wrapper.findComponent(ToasterStub)
    expect(toaster.exists()).toBe(true)
    expect(toaster.props('position')).toBe('top-center')
    expect(toaster.props('closeButton')).toBe(false)
    expect(toaster.props('toastOptions')).toEqual({
      unstyled: true,
      duration: 3000,
      classes: {
        toast:
          'w-full py-2 text-sm px-3 border border-black/85 rounded-lg text-current font-sans flex items-center gap-3 shadow-lg',
        default: 'bg-surface-50',
        description: 'text-gray-500 text-sm',
        warning: 'bg-yellow-200 text-yellow-800',
        error: 'bg-red-200 text-red-800',
        info: 'bg-sky-200 text-sky-800',
        success: 'bg-green-200 text-green-800',
        closeButton: '!text-surface-50 !border-black/85 !text-color'
      }
    })

    // se verifica el slot con los contenidos
    const errorIcon = toaster.find('.error-icon i')
    expect(errorIcon.exists()).toBe(true)
    expect(errorIcon.classes()).toContain('pi')
    expect(errorIcon.classes()).toContain('pi-exclamation-triangle')

    const infoIcon = toaster.find('.info-icon i')
    expect(infoIcon.exists()).toBe(true)
    expect(infoIcon.classes()).toContain('pi')
    expect(infoIcon.classes()).toContain('pi-info-circle')

    const successIcon = toaster.find('.success-icon i')
    expect(successIcon.exists()).toBe(true)
    expect(successIcon.classes()).toContain('pi')
    expect(successIcon.classes()).toContain('pi-check')

    const warningIcon = toaster.find('.warning-icon i')
    expect(warningIcon.exists()).toBe(true)
    expect(warningIcon.classes()).toContain('pi')
    expect(warningIcon.classes()).toContain('pi-exclamation-circle')
  })
})
