# Sistema de Turnos para Supermercado

API REST para gestionar los turnos de atención de un supermercado, con roles, prioridades y cálculo del tiempo de espera.

## Funcionalidades

- Roles de **cajero** y **supervisor** con Spring Security.
- Categorías de prioridad para los turnos.
- Ciclo de vida del turno: **En espera → Atendido → Finalizado**.
- Cálculo del tiempo de espera de cada turno.

## Tecnologías

- Java y Spring Boot
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL
- Lombok y Maven

## Arquitectura

Organizado por capas: `Controller`, `Service`, `Repository` y `Model`.

## Cómo probarlo

1. Crea la base de datos en MySQL:

   ```sql
   CREATE DATABASE supermercado;
   ```

2. Revisa el usuario y la contraseña de MySQL en `src/main/resources/application.properties`.
3. Inicia la aplicación con `mvn spring-boot:run`.
4. Flujo de prueba:
   1. Crea una categoría de prioridad.
   2. Como **cajero**, crea un turno (queda *en espera*).
   3. Llama el turno (cambia a *atendido*).
   4. Finalízalo (queda *finalizado* y se calcula el tiempo de espera).
   5. Como **supervisor**, puedes eliminar turnos.

## Autora

Lizet Mariana Colorado Esguerra · [GitHub](https://github.com/MarianaColorado01) · [LinkedIn](https://www.linkedin.com/in/lizet-colorado-92a71b1a0/)
