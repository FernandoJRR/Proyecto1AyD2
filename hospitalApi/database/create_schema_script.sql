-- Eliminar la base de datos si existe
DROP DATABASE IF EXISTS hospital_ayd2;

-- Crear la base de datos
CREATE DATABASE hospital_ayd2;

-- Usar la base de datos
USE hospital_ayd2;

-- Verificar si el usuario no existe y crearlo
CREATE USER IF NOT EXISTS 'user_hospital_ayd2'@'localhost' IDENTIFIED BY '123';

-- Otorgar permisos específicos sobre la base de datos
GRANT ALL PRIVILEGES ON hospital_ayd2.* TO 'user_hospital_ayd2'@'localhost';

-- Aplicar cambios de permisos
FLUSH PRIVILEGES;