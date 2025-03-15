import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import { describe, it, expect } from 'vitest'
import DefaultLayout from '~/layouts/default.vue'

// Stub for Menubar that renders the slot for each item in the model.
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

// Simple stub for router-link.
const RouterLinkStub = {
  name: 'RouterLinkStub',
  props: ['to', 'custom'],
  template: `<a class="router-link" :href="to"><slot /></a>`
}

// Stub for NuxtPage.
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
    
    // Check that the Menubar renders the two initial items.
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

    // The exposed options are available via wrapper.vm.options.
    // Since they were defined as a ref, we update their value.
    const newOptions = [
      { label: 'Dashboard', icon: 'pi pi-dashboard', route: '/dashboard' },
      { label: 'Settings', icon: 'pi pi-cog', route: '/settings' }
    ]
    // Update the ref value.
    wrapper.vm.options = newOptions
    
    // Wait for the DOM to update.
    await nextTick()

    // Verify that the new menu items are rendered.
    const menubarText = wrapper.find('.menubar').text()
    expect(menubarText).toContain('Dashboard')
    expect(menubarText).toContain('Settings')
    // Ensure the old items are no longer rendered.
    expect(menubarText).not.toContain('Inicio')
    expect(menubarText).not.toContain('Administracion')
  })
})
