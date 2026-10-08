# ServiTec CRM

Aplicación web para la gestión de clientes, equipos asignados y pagos mensuales de la empresa ServiTec Lima S.A.C.

Proyecto del curso Lenguaje de Programación II - Cibertec 2026.

**Autor:** Jaime Adán Muñoz Cuzco

## Tecnologías

- Java 21
- Spring Boot 3.5 (Spring MVC, Spring Data JPA, Thymeleaf)
- Hibernate
- MySQL / MariaDB
- JasperReports 6.21
- Maven
- GitHub Actions y Microsoft Azure

## Funcionalidades

- Inicio de sesión y roles (ADMINISTRADOR y ASESOR)
- Mantenimiento de clientes, productos y usuarios
- Registro de contratos con equipos asignados y cronograma de cuotas (transacción)
- Registro de pagos de cuotas
- Consulta de estado de cuenta por cliente
- Consulta de cobranzas por rango de fechas
- Reporte PDF de deudas pendientes por cliente
- Reporte PDF de equipos y servicios asignados

## Ejecución local

1. Ejecutar el script `src/main/resources/db/crm_servitec.sql` en MySQL.
2. Ejecutar la clase `CrmApplication`.
3. Ingresar a `http://localhost:8080`.

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | ADMINISTRADOR |
| asesor | asesor123 | ASESOR |
