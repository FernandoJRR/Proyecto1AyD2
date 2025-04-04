
/********************** SOLO LACREAMOS SI NO EXISTE ******************************/
CREATE DATABASE IF NOT EXISTS hospital_ayd2;

USE hospital_ayd2;

/********************** SE CREA LA TABLA DE PACIENTES ******************************/
DROP TABLE IF EXISTS patient;

CREATE TABLE
  patient (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    dpi varchar(13) UNIQUE NOT NULL,
    firstnames varchar(250) NOT NULL,
    lastnames varchar(250) NOT NULL,
    PRIMARY KEY (id)
  );

/********************** SE CREA LA TABLA CONSULTAS ******************************/
DROP TABLE IF EXISTS consult;

CREATE TABLE
  consult (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    costo_consulta decimal(38, 2) NOT NULL,
    costo_total decimal(38, 2) NOT NULL,
    is_internado bit (1) NOT NULL,
    is_paid bit (1) NOT NULL,
    patient_id varchar(50) DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id) REFERENCES patient (id)
  );

/********************** SE CREA LA TABLA DE TIPOS DE EMPLEADO ******************************/
DROP TABLE IF EXISTS employee_type;

CREATE TABLE
  employee_type (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    name varchar(100) UNIQUE DEFAULT NULL,
    PRIMARY KEY (id)
  );

/********************** SE CREA LA TABLA DE EMPLEADOS ******************************/
DROP TABLE IF EXISTS employee;

CREATE TABLE
  employee (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    cui varchar(100) UNIQUE NOT NULL,
    desactivated_at date DEFAULT NULL,
    first_name varchar(100) DEFAULT NULL,
    igss_percentage decimal(5, 2) DEFAULT NULL,
    irtra_percentage decimal(5, 2) DEFAULT NULL,
    last_name varchar(100) DEFAULT NULL,
    salary decimal(38, 2) DEFAULT NULL,
    employee_type_id varchar(50) DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (employee_type_id) REFERENCES employee_type (id)
  );

/********************** SE CREA LA TABLA DE TIPOS DE CONSULTA ******************************/
DROP TABLE IF EXISTS employee_consult;

CREATE TABLE
  employee_consult (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    consult_id varchar(50) NOT NULL,
    employee_id varchar(50) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (consult_id) REFERENCES consult (id),
    FOREIGN KEY (employee_id) REFERENCES employee (id)
  );

/********************** SE CREA LA TABLA TIPOS DE HISTORIAL ******************************/
DROP TABLE IF EXISTS history_type;

CREATE TABLE
  history_type (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    type varchar(100) DEFAULT NULL,
    PRIMARY KEY (id)
  );

/********************** SE CREA LA TABLA ASOCIATIVA ENTRE EMPLEADO  HISTORIAL ******************************/
DROP TABLE IF EXISTS employee_history;

CREATE TABLE
  employee_history (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    commentary varchar(200) DEFAULT NULL,
    history_date date DEFAULT NULL,
    employee_id varchar(50) DEFAULT NULL,
    history_type_id varchar(50) DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (employee_id) REFERENCES employee (id),
    FOREIGN KEY (history_type_id) REFERENCES history_type (id)
  );

/********************** SE CREA LA TABLA DE PERMISOS ******************************/
DROP TABLE IF EXISTS permission;

CREATE TABLE
  permission (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    action varchar(100) UNIQUE DEFAULT NULL,
    name varchar(100) UNIQUE DEFAULT NULL,
    PRIMARY KEY (id)
  );

/********************** SE CREA LA TABLA ASOSIATIVA ENTORE LOS PERMISOS Y EL ROL O TIPO DE EMPLEADO ******************************/
DROP TABLE IF EXISTS employee_type_permissions;

CREATE TABLE
  employee_type_permissions (
    employee_type_id varchar(50) NOT NULL,
    permissions_id varchar(50) NOT NULL,
    FOREIGN KEY (employee_type_id) REFERENCES employee_type (id),
    FOREIGN KEY (permissions_id) REFERENCES permission (id)
  );

/********************** SE CREA LA TABLA DE MEDICINAS ******************************/
DROP TABLE IF EXISTS medicine;

CREATE TABLE
  medicine (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    cost decimal(38, 2) NOT NULL,
    description text NOT NULL,
    min_quantity int NOT NULL,
    name varchar(100) UNIQUE NOT NULL,
    price decimal(38, 2) NOT NULL,
    quantity int NOT NULL,
    PRIMARY KEY (id)
  );

/********************** SE CREA LA TABLA DE PARAMETROS ******************************/
DROP TABLE IF EXISTS parameter;

CREATE TABLE
  parameter (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    name varchar(255) DEFAULT NULL,
    parameter_key varchar(255) UNIQUE DEFAULT NULL,
    value varchar(255) DEFAULT NULL,
    PRIMARY KEY (id)
  );

/********************** SE CREA LA TABLA DE HABITACIONES ******************************/
DROP TABLE IF EXISTS room;

CREATE TABLE
  room (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    daily_maintenance_cost decimal(38, 2) NOT NULL,
    daily_price decimal(38, 2) NOT NULL,
    number varchar(100) UNIQUE NOT NULL,
    status tinyint NOT NULL,
    PRIMARY KEY (id)
  );

/********************** SE CREA LA TABLA DE HOISTORIAL PARA LAS HABITACIONES ******************************/
DROP TABLE IF EXISTS room_history;

CREATE TABLE
  room_history (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    date date DEFAULT NULL,
    patient_id varchar(50) NOT NULL,
    room_id varchar(50) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (room_id) REFERENCES room (id),
    FOREIGN KEY (patient_id) REFERENCES patient (id)
  );

/********************** SE CREA LA TABLA DE ACUPACION DE HABITACIONES ******************************/
DROP TABLE IF EXISTS room_usage;

CREATE TABLE
  room_usage (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    daily_room_maintenance_cost decimal(38, 2) NOT NULL,
    daily_room_price decimal(38, 2) NOT NULL,
    usage_days int NOT NULL,
    consult_id varchar(50) UNIQUE DEFAULT NULL,
    room_id varchar(50) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (consult_id) REFERENCES consult (id),
    FOREIGN KEY (room_id) REFERENCES room (id)
  );

/********************** SE CREA LA TABLA DE VENTAS DE MEDICINAS ******************************/
DROP TABLE IF EXISTS sale_medicine;

CREATE TABLE
  sale_medicine (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    medicine_cost decimal(38, 2) NOT NULL,
    price decimal(38, 2) NOT NULL,
    profit decimal(38, 2) NOT NULL,
    quantity int NOT NULL,
    total decimal(38, 2) NOT NULL,
    consult_id varchar(50) DEFAULT NULL,
    employee_id varchar(50) DEFAULT NULL,
    medicine_id varchar(50) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (consult_id) REFERENCES consult (id),
    FOREIGN KEY (medicine_id) REFERENCES medicine (id),
    FOREIGN KEY (employee_id) REFERENCES employee (id)
  );

/********************** SE CREA LA TABLA DE ESPECIALISTAS ******************************/
DROP TABLE IF EXISTS specialist_employee;

CREATE TABLE
  specialist_employee (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    apellidos varchar(100) NOT NULL,
    dpi varchar(13) UNIQUE NOT NULL,
    nombres varchar(100) NOT NULL,
    PRIMARY KEY (id)
  );

/********************** SE CREA LA TABLA DE TIPOS DE CIRUGIAS ******************************/
DROP TABLE IF EXISTS surgery_type;

CREATE TABLE
  surgery_type (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    description text NOT NULL,
    hospital_cost decimal(38, 2) NOT NULL,
    specialist_payment decimal(38, 2) NOT NULL,
    surgery_cost decimal(38, 2) NOT NULL,
    type varchar(100) UNIQUE NOT NULL,
    PRIMARY KEY (id)
  );

/********************** SE CREA LA TABLA DE CRIUGIAS HECHAS ******************************/
DROP TABLE IF EXISTS  surgery;

CREATE TABLE
  surgery (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    hospital_cost decimal(38, 2) NOT NULL,
    performed_date date DEFAULT NULL,
    surgery_cost decimal(38, 2) NOT NULL,
    consult_id varchar(50) NOT NULL,
    surgery_type_id varchar(50) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (surgery_type_id) REFERENCES surgery_type (id),
    FOREIGN KEY (consult_id) REFERENCES consult (id)
  );

/********************** SE CREA LA TABLA DE ASIGNACIONES DE EMPLEASDOS A CIRUGIAS ******************************/
DROP TABLE IF EXISTS surgery_employee;

CREATE TABLE
  surgery_employee (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    specialist_payment decimal(38, 2) NOT NULL,
    employee_id varchar(50) DEFAULT NULL,
    specialist_employee_id varchar(50) DEFAULT NULL,
    surgery_id varchar(50) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (surgery_id) REFERENCES surgery (id),
    FOREIGN KEY (employee_id) REFERENCES employee (id),
    FOREIGN KEY (specialist_employee_id) REFERENCES specialist_employee (id)
  );

/********************** SE CREA LA TABLA DE USUARIOS ******************************/
DROP TABLE IF EXISTS `user`;

CREATE TABLE
 `user` (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    desactivated_at date DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    username varchar(100) UNIQUE DEFAULT NULL,
    employee_id varchar(50) UNIQUE DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (employee_id) REFERENCES employee (id)
  );

/********************** SE CREA LA TABLA DE VACACIONES ******************************/
DROP TABLE IF EXISTS vacations;

CREATE TABLE
  vacations (
    id varchar(50) NOT NULL,
    created_at date NOT NULL,
    update_at date DEFAULT NULL,
    begin_date date DEFAULT NULL,
    end_date date DEFAULT NULL,
    period_year int DEFAULT NULL,
    was_used bit (1) DEFAULT NULL,
    working_days int DEFAULT NULL,
    employee_id varchar(50) DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (employee_id) REFERENCES employee (id)
  );