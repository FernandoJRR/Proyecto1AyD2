<template>
  <div class="flex items-center justify-center">
    <div class="w-full">
      <h1 class="text-2xl font-bold mb-1 text-center">Bienvenido!</h1>
      <p class="mb-8 text-center text-gray-600">Ingresa tus datos</p>
      <Form v-slot="$form" :initialValues :resolver @submit="onFormSubmit" >
        <div class="flex flex-col gap-8 mb-6">
          <div class="">
            <FloatLabel>
              <label>Username</label>
              <InputText name="username" class="w-full" />
            </FloatLabel>
            <Message v-if="$form.username?.invalid" severity="error" size="small" variant="simple">{{
              $form.username.error?.message }}</Message>
          </div>
          <div>
            <FloatLabel>
              <Password name="password" :feedback="false" toggleMask class="w-full" />
              <label>Password</label>
            </FloatLabel>
            <Message v-if="$form.password?.invalid" severity="error" size="small" variant="simple">{{
              $form.password.error?.message }}</Message>
          </div>
        </div>
        <Button type="submit" class="w-full py-2">Ingresar</Button>
      </Form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { FloatLabel, InputText, Password } from 'primevue';
import { toast } from 'vue-sonner';
import { z } from 'zod';
definePageMeta({ layout: 'login' })

const initialValues = reactive({
  username: '',
  password: '',
});

const resolver = ref(zodResolver(
  z.object({
    username: z.string().min(1, 'El username es obligatorio.'),
    password: z.string().min(1, 'La password es obligatoria.'),
  })
))

const onFormSubmit = (e: any) => {
  if (e.valid) {
    console.log(e.values)

    let payload: any = {
      username: e.values.username, password: e.values.password
    }

    mutate(payload)
  }
};

/*
const { mutate, asyncStatus } = useMutation({
  mutation: (loginData: any) => login(loginData),
  onError(error) {
    console.log(error)
    console.log(error.message)
    toast.error('Ocurrió un error iniciar sesion', {
      description: `
      Parece que los datos no son válidos:
      ${(error)}
      `
    })
  },
  onSuccess() {
    toast.success('Bienvenido!')
    navigateTo('/')
  }
})
*/
 function mutate(loginData: any) {
  if (loginData.username === 'frodriguez' && loginData.password === 'password') {
    const cookie = useCookie('proyecto1ayd2-user-token')

    cookie.value = loginData

    toast.success('Bienvenido!')
    navigateTo('/')
  } else {
    toast.error('Error al iniciar sesion')
  }
 }
</script>
