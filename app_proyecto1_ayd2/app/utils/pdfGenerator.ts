import type { Employee } from '~/lib/api/admin/employee';

export const generatePeriodVacationInvoicePDF = async (employees: Employee[], periodYear: number) => {
  const { jsPDF } = await import('jspdf'); // Dynamically import on client-side
  const doc = new jsPDF();

  const pageWidth = doc.internal.pageSize.getWidth();

  employees.forEach((employee, index) => {
    if (index > 0) {
      doc.addPage();
    }

    // Header: Centered Title
    doc.setFont("helvetica", "bold");
    doc.setFontSize(18);
    doc.text("Certificación de Vacaciones", 20, 20);

    // Main message: Centered content
    doc.setFont("helvetica", "normal");
    doc.setFontSize(12);
    const message = `Por este medio se hace constar que el empleado ${employee.firstName} ${employee.lastName}, ` +
      `con un salario de $${employee.salary} y perteneciente al área de ${employee.employeeType.name}, ` +
      `ha recibido sus vacaciones para el periodo ${periodYear}.`;

    const textLines = doc.splitTextToSize(message, pageWidth - 40); // leaving margins
    doc.text(textLines, pageWidth / 2, 40, { align: 'center' });

    // Footer: Centered closing statement
    doc.setFontSize(12);
    doc.text("Atentamente, Departamento de Recursos Humanos", pageWidth / 2, 280, { align: 'center' });
  });

  doc.save(`vacaciones-${periodYear}.pdf`);
};

export const generateEmployeePeriodInvoicePDF = async (employee: Employee, periodYear: number) => {
  const { jsPDF } = await import('jspdf'); // Dynamically import on client-side
  const doc = new jsPDF();
  const pageWidth = doc.internal.pageSize.getWidth();

  doc.setFont("helvetica", "bold");
  doc.setFontSize(18);
  doc.text("Certificación de Vacaciones", 20, 20);

  doc.setFont("helvetica", "normal");
  doc.setFontSize(12);
  const message = `Por este medio se hace constar que el empleado ${employee.firstName} ${employee.lastName}, ` +
    `con un salario de $${employee.salary} y perteneciente al área de ${employee.employeeType.name}, ` +
    `ha recibido sus vacaciones para el periodo ${periodYear}.`;

  const textLines = doc.splitTextToSize(message, pageWidth - 40);
  doc.text(textLines, pageWidth / 2, 40, { align: 'center' });

  doc.setFontSize(12);
  doc.text("Atentamente, Departamento de Recursos Humanos", pageWidth / 2, 280, { align: 'center' });

  doc.save(`vacaciones-${employee.id}-${periodYear}.pdf`);
}
