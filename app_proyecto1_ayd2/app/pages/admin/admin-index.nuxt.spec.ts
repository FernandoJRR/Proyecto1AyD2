// index.nuxt.spec.ts
import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import IndexPage from '~/pages/admin/index.vue'

describe('Admin Index Page', () => {
  const globalConfig = {
    stubs: {
      MenuShortcutCard: {
        template: '<div class="menu-shortcut-card" />'
      }
    }
  }

  it('renderiza los componentes de MenuShortcutCard', () => {
    const wrapper = mount(IndexPage, { global: globalConfig })
    const menuLength = wrapper.vm.menus.length
    const cards = wrapper.findAll('.menu-shortcut-card')
    expect(cards.length).toBe(menuLength)
  })
})
