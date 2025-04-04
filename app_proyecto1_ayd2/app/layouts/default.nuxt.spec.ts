import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import { describe, it, expect } from 'vitest'
import DefaultLayout from '~/layouts/default.vue'

const MenubarStub = {
  name: 'MenubarStub',
  props: ['model'],
  template: `
    <div class="menubar">
      <template v-for="(item, index) in model" :key="index">
        <!-- We pass a dummy props object and false for hasSubmenu -->
        <slot name="item" :item="item" :props="{ action: {} }" :hasSubmenu="false" />
      </template>
    </div>
  `
}

const RouterLinkStub = {
  name: 'RouterLinkStub',
  props: ['to', 'custom'],
  template: `<a class="router-link" :href="to"><slot /></a>`
}

const NuxtPageStub = {
  name: 'NuxtPageStub',
  template: `<div class="nuxt-page">Page Content</div>`
}

describe('Default Layout', () => {
  it('renders initial menu items', () => {
    const wrapper = mount(DefaultLayout, {
      global: {
        stubs: {
          Menubar: MenubarStub,
          RouterLink: RouterLinkStub,
          NuxtPage: NuxtPageStub
        }
      }
    })
    
    const menubarText = wrapper.find('.menubar').text()
    expect(menubarText).toContain('Inicio')
    expect(menubarText).toContain('Administracion')
  })
  
  it('updates menu items dynamically when options change', async () => {
    const wrapper = mount(DefaultLayout, {
      global: {
        stubs: {
          Menubar: MenubarStub,
          RouterLink: RouterLinkStub,
          NuxtPage: NuxtPageStub
        }
      }
    })

    const newOptions = [
      { label: 'Dashboard', icon: 'pi pi-dashboard', route: '/dashboard' },
      { label: 'Settings', icon: 'pi pi-cog', route: '/settings' }
    ]
    wrapper.vm.options = newOptions
    
    await nextTick()

    const menubarText = wrapper.find('.menubar').text()
    expect(menubarText).toContain('Dashboard')
    expect(menubarText).toContain('Settings')
    expect(menubarText).not.toContain('Inicio')
    expect(menubarText).not.toContain('Administracion')
  })
})
