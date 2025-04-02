<template>
    <!-- Filtros principales -->
    <div class="p-4 rounded-2xl bg-white shadow mb-5 border border-slate-200">
        <div class="grid grid-cols-3 gap-4 mb-4">
            <Dropdown v-model="reportType" :options="availableReports" optionLabel="label" optionValue="value"
                placeholder="Seleccionar reporte" class="w-full" />

            <!-- medicamentos/ganancia medicamento -->
            <InputText v-if="reportType === 'MEDICAMENTOS' || reportType === 'MEDICATION_PROFIT'" v-model="nameToSearch"
                placeholder="Filtrar por nombre de medicamento" class="w-full" />

            <!-- ganancia por empleado -->
            <template v-if="reportType === 'EMPLOYEES_PROFIT'">
                <InputText v-model="nameToSearch" placeholder="Filtrar por nombre de empleado" class="w-full" />
                <InputText v-model="dpiToSearch" placeholder="Filtrar por DPI" class="w-full" />
            </template>

            <!-- movimiento de personal -->
            <Dropdown v-if="reportType === 'EMPLOYEES_LIFECYCLE'" name="type" v-model="employeeTypeToSearch"
                optionLabel="name" optionValue="id" :options="employeeTypes" class="w-full"
                placeholder="Selecciona un tipo de usuario" />

            <!-- fechas para ganancia por medicamento -->
            <template v-if="reportType === 'MEDICATION_PROFIT' || reportType === 'EMPLOYEES_LIFECYCLE'">
                <DatePicker v-model="startDate" placeholder="Fecha de inicio" dateFormat="dd/mm/yy" class="w-full" />
                <DatePicker v-model="endDate" placeholder="Fecha de fin" dateFormat="dd/mm/yy" class="w-full" />
            </template>
        </div>

        <!-- filtros por tipo de historial -->
        <template v-if="reportType === 'EMPLOYEES_LIFECYCLE'">
            <div class="p-4 bg-slate-50 border border-slate-300 rounded-xl shadow-sm mt-2">
                <h4 class="text-slate-700 font-semibold mb-3 ">Tipos de historial</h4>
                <div class="grid grid-cols-3 gap-3">
                    <div v-for="type in historyTypes" :key="type.id" class="flex items-center space-x-2">
                        <Checkbox v-model="selectedHistoryTypes" :inputId="type.id" :value="type.id" />
                        <label :for="type.id" class="text-sm text-slate-700">
                            {{ type.type }}
                        </label>
                    </div>
                </div>
            </div>
        </template>

        <template v-if="reportType === 'DOCTORS_REPORT'">
            <div class="p-4 bg-slate-50 border border-slate-300 rounded-xl shadow-sm mt-2">
                <h4 class="text-slate-700 font-semibold mb-3 ">Filtra por estado de asignaciones del doctor</h4>
                <div class="grid grid-cols-3 gap-3">
                    <div class="flex items-center space-x-2">
                        <Checkbox v-model="onlyAssigneds" :binary="true" id="x" />
                        <label class="text-sm text-slate-700" for="x">
                            Solo doctores con consultas
                        </label>
                        <Checkbox v-model="onlyNotAssigneds" :binary="true" id="y" />
                        <label class="text-sm text-slate-700" for="y">
                            Solo doctores sin consultas
                        </label>
                    </div>
                </div>
            </div>
            <div class="flex items-center space-x-2">

            </div>
        </template>

        <!-- botones -->
        <div class="flex flex-wrap gap-3 mt-4">
            <Button icon="pi pi-search" label="Filtrar" @click="filtrar" rounded outlined severity="info" />
            <Button icon="pi pi-refresh" label="Quitar Filtros" @click="recargarDatos" rounded outlined
                severity="help" />
        </div>
    </div>

    <!-- tabla de resultados -->
    <DataTable :value="reportData.data" v-model:expandedRows="expandedRows" rowExpansionMode="single"
        :dataKey="tableConfig.dataKey" v-if="tableConfig"
        :rowClass="(row: any) => tableConfig.rowClass ? tableConfig.rowClass(row) : ''"
        class="shadow-md border border-slate-200 rounded-xl ">
        <template #header>
            <div class=" px-4 py-2">
                <span class="text-lg font-bold text-slate-800">
                    {{ tableConfig.reportHeader }}
                </span>
            </div>
        </template>

        <!-- columna para expandir -->
        <Column v-if="tableConfig.showSubList" expander headerStyle="width: 3rem" />

        <!-- columnas dinámicas -->
        <Column v-for="col in tableConfig.columns" :key="col.field" :field="col.field" :header="col.header">
            <template #body="slotProps">
                {{ col.render ? col.render(slotProps.data) : slotProps.data[col.field] }}
            </template>
        </Column>

        <!-- sbtabla -->
        <template #expansion="slotProps">
            <div class="p-3">
                <h3 class="font-semibold mb-2 text-slate-700">
                    {{ tableConfig.subReportHeader }}
                </h3>
                <DataTable :value="slotProps.data[tableConfig.subReportKey]" responsiveLayout="scroll"
                    tableStyle="min-width: 30rem" size="small" class="border border-slate-200 rounded-lg">
                    <Column v-for="subCol in tableConfig.subReportColumns" :key="subCol.field" :field="subCol.field"
                        :header="subCol.header">
                        <template #body="slotProps">
                            {{ subCol.render ? subCol.render(slotProps.data) : slotProps.data[subCol.field] }}
                        </template>
                    </Column>
                </DataTable>
            </div>
        </template>
    </DataTable>
</template>


<script setup lang="ts">
import { ref, onMounted, render } from 'vue'
import { toast } from 'vue-sonner';
import { boolean } from 'zod';
import { getAllEmployeeTypes } from '~/lib/api/admin/employee-type';
import { getAllHistoryTypes } from '~/lib/api/admin/history-type';
import { getDoctorAssignmentReport, getEmployeeLifecycleReport, getEmployeeProfitReport, getMedicationProfitReport, getMedicinesReport } from '~/lib/api/reportes/reporte';


//esto indica que las rows seran reactivas, se podran modificar, y por lo tanto se puede cambiar el valor y vue lo detectara
const expandedRows = ref({});
//esto indica que el tipo de reporte sera reactivo, y por lo tanto se puede cambiar el valor y vue lo detectara
const reportType = ref('MEDICAMENTOS');

//inputs que siven para el filtrado de los reportes
const selectedHistoryTypes = ref<string[]>([]);
const employeeTypeToSearch = ref()
const nameToSearch = ref('');
const startDate = ref<Date | null>(null)
const endDate = ref<Date | null>(null)
const dpiToSearch = ref('')
const onlyAssigneds = ref<boolean>(false)
const onlyNotAssigneds = ref<boolean>(false)

watch(
    () =>
        onlyAssigneds.value,
    (data) => {
        if (data) {
            onlyNotAssigneds.value = false;
        }
    }
)

watch(
    () =>
        onlyNotAssigneds.value,
    (data) => {
        if (data) {
            onlyAssigneds.value = false;
        }
    }
)


/**
 * Ref que contiene la data del reporte, es reactivo y se va a modificar dependiendo del tipo de reporte seleccionado.
 * Por defecto la data es un array vacio.
 */
const reportData = ref<{
    data: Array<any>
}>({
    data: [
    ],
});

/**
 * Array que contiene los tipos de reportes disponibles, cada uno tiene un valor y una etiqueta 
 * el valor es el que se va a usar para filtrar la data y la etiqueta es la que se va a mostrar en el dropdown
 */
const availableReports = [
    { value: 'MEDICAMENTOS', label: 'Reporte de Medicamentos' },
    { value: 'MEDICATION_PROFIT', label: 'Reporte de ganancia por medicamento' },
    { value: 'EMPLOYEES_PROFIT', label: 'Reporte de ventas por empleado' },
    { value: 'EMPLOYEES_LIFECYCLE', label: 'Reporte de Movimientos de Personal' },
    { value: 'DOCTORS_REPORT', label: 'Reporte de doctores' }
]

const medicinesReportTableConfig =
{
    dataKey: 'name',
    reportHeader: 'Reporte de Medicamentos',
    columns: [
        { field: 'name', header: 'Medicamento' },
        {
            field: 'price', header: 'Precio',
            render: (row: any) => "Q " + row.price
        },
        {
            field: 'cost', header: 'Costo',
            render: (row: any) => "Q " + row.cost
        },
        {
            field: 'quantity',
            header: 'No.Existencias',
        },
        { field: 'minQuantity', header: 'Min.Existencias' }
    ],
    subReportColumns: [],
    subReportKey: '',
    subReportHeader: '',
    showSubList: false,
    rowClass: (row: any) => {

        return { '!bg-red-100 !text-red-800 font-semibold': row.quantity <= row.minQuantity }
    }

}


const medicationProfitReportTableConfig =
{
    dataKey: 'medicationName',
    reportHeader: 'Reporte de ganancia por medicamento',
    columns: [
        { field: 'medicationName', header: 'Medicamento' },
        {
            header: 'Ventas Totales',
            field: 'financialSummaryDTO',
            render: (row: any) => {
                const summary = row.financialSummaryDTO;
                return summary ? "Q " + summary.totalSales : 'Sin datos';
            }
        },
        {
            header: 'Costos Totales',
            field: 'financialSummaryDTO',
            render: (row: any) => {
                const summary = row.financialSummaryDTO;
                return summary ? "Q " + summary.totalCost : 'Sin datos';
            }
        },
        {
            header: 'Ganancia total',
            field: 'financialSummaryDTO',
            render: (row: any) => {
                const summary = row.financialSummaryDTO;
                return summary ? "Q " + summary.totalProfit : 'Sin datos';
            }
        },
    ],
    subReportColumns: [
        {
            field: 'createdAt', header: 'Fecha de la venta',
        },
        {
            field: 'price', header: 'Precio del Articulo',
            render: (row: any) => "Q " + row.price
        },
        {
            field: 'medicineCost', header: 'Costo del Articulo',
            render: (row: any) => "Q " + row.medicineCost
        },
        {
            field: 'quantity', header: 'No.Articulos Vendidos',
        },

        {
            field: 'total', header: 'Total de la venta',
            render: (row: any) => "Q " + row.total
        },

        {
            field: 'profit', header: 'Ganacia de la venta',
            render: (row: any) => "Q " + row.profit
        }
    ],
    subReportKey: 'sales',
    subReportHeader: 'Ventas del medicamento',
    showSubList: true
}

const employeesProfitReportTableConfig =
{
    dataKey: 'cui',
    reportHeader: 'Reporte de ventas por empleado',
    columns: [

        { field: 'employeeFullName', header: 'Empleado' },
        { field: 'cui', header: 'CUI' },
        {
            header: 'Ventas Totales',
            field: 'financialSummaryDTO',
            render: (row: any) => {
                const summary = row.financialSummaryDTO;
                return summary ? "Q " + summary.totalSales : 'Sin datos';
            }
        },
        {
            header: 'Costos Totales',
            field: 'financialSummaryDTO',
            render: (row: any) => {
                const summary = row.financialSummaryDTO;
                return summary ? "Q " + summary.totalCost : 'Sin datos';
            }
        },
        {
            header: 'Ganancia total',
            field: 'financialSummaryDTO',
            render: (row: any) => {
                const summary = row.financialSummaryDTO;
                return summary ? "Q " + summary.totalProfit : 'Sin datos';
            }
        },
    ],
    subReportColumns: [
        {
            field: 'createdAt', header: 'Fecha de la venta',
        },
        {
            field: 'price', header: 'Precio del Articulo',
            render: (row: any) => "Q " + row.price
        },
        {
            field: 'medicineCost', header: 'Costo del Articulo',
            render: (row: any) => "Q " + row.medicineCost
        },
        {
            field: 'quantity', header: 'No.Articulos Vendidos',
        },

        {
            field: 'total', header: 'Total de la venta',
            render: (row: any) => "Q " + row.total
        },

        {
            field: 'profit', header: 'Ganacia de la venta',
            render: (row: any) => "Q " + row.profit
        }
    ],
    subReportKey: 'sales',
    subReportHeader: 'Ventas del medicamento',
    showSubList: true
}


const employeesLifeCicleReportTableConfig =
{
    dataKey: 'employee',
    reportHeader: 'Reporte de Movimientos de Personal',
    columns: [

        {
            field: 'employee', header: 'Empleado',
            render: (row: any) => {
                const employee = row.employee;
                return employee ? employee.firstName + ' ' + employee.lastName : 'Sin datos';
            }
        },
        {
            field: 'employee', header: 'CUI',
            render: (row: any) => {
                const employee = row.employee;
                return employee ? employee.cui : 'Sin datos';
            }
        },

        {
            field: 'employee', header: 'Tipo de empleado',
            render: (row: any) => {
                const employee = row.employee;
                return employee ? employee.employeeType.name : 'Sin datos';
            }
        },
        {
            field: 'employee', header: 'Salario',
            render: (row: any) => {
                const employee = row.employee;
                return employee ? 'Q ' + employee.salary : 'Sin datos';
            }
        },
        {
            header: 'Accion realizada',
            field: 'historyType',
            render: (row: any) => {
                const historyType = row.historyType;
                return historyType ? historyType.type : 'Sin datos';
            }
        },

        {
            header: 'Fecha del suceso',
            field: 'historyDate',
        },
    ],
    subReportColumns: [],
    subReportKey: '',
    subReportHeader: '',
    showSubList: false
}

const doctorsReportTableConfig =
{
    dataKey: 'cui',
    reportHeader: 'Reporte de doctores',
    columns: [

        { field: 'employeeFullName', header: 'Empleado' },
        { field: 'cui', header: 'CUI' },
        {
            field: 'employeeType', header: 'Tipo de empleado',
        },
        {
            field: 'salary', header: 'Salario',
            render: (row: any) => {
                return "Q " + row.salary;
            }
        }
    ],
    subReportColumns: [
        {
            field: 'patient', header: 'Paciente',
            render: (row: any) => {
                const patient = row.patient;
                return patient ? patient.firstnames + " " + patient.lastnames : 'Sin datos';
            }
        },
        {
            field: 'patient', header: 'CUI',
            render: (row: any) => {
                const patient = row.patient;
                return patient ? patient.dpi : 'Sin datos';
            }
        },
        {
            field: 'isInternado', header: 'Internado',
            render: (row: any) => {
                return row.isInternado ? 'Si' : 'No'
            }
        }
        ,
        {
            field: 'isPaid', header: 'Pagado',
            render: (row: any) => row.isPaid ? 'Si' : 'No'
        },
        {
            field: 'createdAtString', header: 'Fecha',
        }

    ],
    subReportKey: 'assignedConsults',
    subReportHeader: 'Consultas asignadas',
    showSubList: true
}

/**
 * Ref que define la configuracion de la tabla, es reactivo y se va a modificar dependiendo del tipo de reporte seleccionado
 */
const tableConfig = ref<{
    dataKey: string,
    reportHeader: string;//esto va indicar el header del listado general
    columns: {
        field: string;
        header: string;
        render?: (row: any) => string | VNode;
    }[];//las coumnas del reporte general
    subReportColumns: {
        field: string; header: string,
        render?: (row: any) => string | VNode;
    }[];//las columnas del sublistado
    subReportKey: string;//indica como se llama la propiedad que contiene el sublistado
    subReportHeader: string;//indica el header del sublistado
    showSubList: boolean;//esta bandera va a indicar si el reporte muestra sub listados o nel
    rowClass?: (row: any) => any;

}>(medicinesReportTableConfig);

/**
 * Metodo que se ejecuta al cargar el componente, se encarga de cargar automarticamente el reporte seleccionado
 * en el dropdown.
 */
onMounted(async () => {
    await cargarReporteActual()
})

/**
 * Observa los cambios en el tipo de reporte seleccionado y manda a recargar la data
 */
watch(reportType, async () => {
    nameToSearch.value = '';//siempre limpiamos los filtros al cambiar de reporte
    await cargarReporteActual();
});

/**
 * Configura la distribucion de la tabla, manda a trear la data segun e ltipo de reporte seleccionado
 * y la setea en el objeto reportData
 */
const cargarReporteActual = async () => {
    try {
        //cargamos el reporte actual dependiendo del tipo de reporte seleccionado
        switch (reportType.value) {
            case 'MEDICAMENTOS':
                tableConfig.value = medicinesReportTableConfig;
                reportData.value.data = await getMedicinesReport(nameToSearch.value);
                break;

            case 'MEDICATION_PROFIT':
                tableConfig.value = medicationProfitReportTableConfig;
                const response = await getMedicationProfitReport(nameToSearch.value,
                    startDate.value,
                    endDate.value
                );
                reportData.value.data = response.salePerMedication;
                break;

            case 'EMPLOYEES_PROFIT':
                tableConfig.value = employeesProfitReportTableConfig;
                const response2 = await getEmployeeProfitReport(nameToSearch.value,
                    dpiToSearch.value
                );
                reportData.value.data = response2.salePerEmployee;
                break;

            case 'EMPLOYEES_LIFECYCLE':
                tableConfig.value = employeesLifeCicleReportTableConfig;
                const response3 = await getEmployeeLifecycleReport(
                    employeeTypeToSearch.value,
                    startDate.value,
                    endDate.value,
                    selectedHistoryTypes.value
                );
                reportData.value.data = response3;
                break;

            case 'DOCTORS_REPORT':
                tableConfig.value = doctorsReportTableConfig;
                const response4 = await getDoctorAssignmentReport(
                    onlyAssigneds.value,
                    onlyNotAssigneds.value
                );
                reportData.value.data = response4;
                break;
            default:
                reportData.value.data = []
        }
    } catch (error: any) {
        toast.error('Error', { description: `${(error.message)}` })
    }

}

const recargarDatos = async () => {
    //elimina cualquier filtro y manda a cargar el reporte seleccionado de nuevo
    nameToSearch.value = '';
    dpiToSearch.value = '';
    startDate.value = null;
    endDate.value = null;

    // limpiar filtros específicos
    employeeTypeToSearch.value = undefined;
    selectedHistoryTypes.value = [];

    onlyAssigneds.value = false;
    onlyNotAssigneds.value = false;

    // volvemos a cargar el reporte con filtros vacíos
    await cargarReporteActual();
};


/**
 * Manda a recargar el reporte para que tome en cuenta los filtros
 */
const filtrar = async () => {
    await cargarReporteActual();
};


//estoas son las request que no van a cambiar (mandar a traer los tipos de empleados o los tipos de historia)



/**
 * Obtiene todos tipos de historial en el sistema
 */
const { data: historyTypes } = useCustomQuery({
    key: ['getAllHistoryTypes'],
    query: () => getAllHistoryTypes()
})


/**
 * Obtiene todos tipos los tipos de empleados en el sistema
 */
const { data: employeeTypes } = useCustomQuery({
    key: ['getAllEmployeeTypes'],
    query: () => getAllEmployeeTypes()
})




</script>