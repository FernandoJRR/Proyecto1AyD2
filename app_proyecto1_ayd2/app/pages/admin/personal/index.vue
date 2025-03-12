<template>
  <DataTable :value="state.data as any[]" tableStyle="min-width: 50rem" :loading="asyncStatus == 'loading'">
    <template #header>
        <div class="flex flex-wrap items-center justify-between gap-2">
            <span class="text-xl font-bold">Usuarios</span>
            <Button icon="pi pi-refresh" rounded raised />
        </div>
    </template>
    <Column field="id" header="ID"></Column>
    <Column field="name" header="Name"></Column>
    <template #footer> Hay en total {{ state.data ? (state.data as any[]).length : 0 }} usuarios. </template>
</DataTable>
</template>
<script setup lang="ts">
const { state, asyncStatus } = useQuery({
  key: ['usuarios'],
  query: () => $api('/names')
})
</script>
