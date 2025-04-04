import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import IndexPage from '~/pages/index.vue'

vi.mock('~/stores/auth', () => ({
  useAuthStore: () => ({
    user: ref({ username: 'usuario' })
  })
}))

describe('Index Page', () => {
  const globalConfig = {
    stubs: {
      MenuShortcutCard: {
        template: '<div class="menu-shortcut-card" />'
      }
    }
  }

  it('renderiza el mensaje de bienvenida', () => {
    const wrapper = mount(IndexPage, { global: globalConfig })
    expect(wrapper.find('h1').text()).toContain('Bienvenido, usuario!')
  })

  it('renderiza los componentes de MenuShortcutCard', () => {
    const wrapper = mount(IndexPage, { global: globalConfig })
    const menuLength = wrapper.vm.menus.length
    const cards = wrapper.findAll('.menu-shortcut-card')
    expect(cards.length).toBe(menuLength)
  })
})
