# 💸 Prestamos — Sistema de Microservicios

**Prestamos** es una plataforma modular desarrollada en **Java + Spring Boot**, diseñada para gestionar préstamos, clientes, pagos y cobradores, con autenticación segura basada en **JWT**, *KAFKA* y despliegue completo mediante **Docker Compose**.  
Cada módulo funciona como un microservicio independiente, lo que permite escalar fácilmente y mantener una arquitectura limpia y desacoplada.

---

## 🧩 Arquitectura del sistema

Tu monorepo contiene los siguientes servicios:

Servicio

Descripción

🔑 auth-service

Maneja autenticación, registro y login de usuarios con roles (ADMIN, COBRADOR).

👤 cliente-service

CRUD de clientes y gestión de información personal.

💰 prestamo-service

CRUD de préstamos, cálculo de cuotas y relación con clientes.

💵 pago-service

Registro y consulta de pagos realizados por los clientes.

📢 notif-service

Envío de notificaciones (por correo o sistema interno) sobre pagos y préstamos.

📊 reporte-service

Generación de reportes financieros y estadísticos del sistema.

🚀 Cómo levantar todo con Docker Compose

Clona el repositorio:

git clone https://github.com/ParkerForex/Prestamos.git
cd Prestamos

Asegúrate de tener Docker y Docker Compose instalados.

Levanta todos los servicios:

docker-compose up -d

Verifica que los contenedores estén corriendo:

docker ps

El archivo docker-compose.yml ya está configurado para levantar todos los microservicios junto con la base de datos SQL Server.

🔑 Endpoints principales

Auth Service

POST /api/auth/register → Crear usuario (ADMIN o COBRADOR).

POST /api/auth/login → Login, devuelve JWT.

GET /api/auth/me → Datos del usuario autenticado.

Cliente Service

GET /clientes → Listar clientes.

POST /clientes → Crear cliente.

GET /clientes/{id} → Obtener cliente.

PUT /clientes/{id} → Actualizar cliente.

DELETE /clientes/{id} → Eliminar cliente.

Prestamo Service

GET /prestamos → Listar préstamos.

POST /prestamos → Crear préstamo.

GET /prestamos/{id} → Detalle préstamo.

PUT /prestamos/{id} → Actualizar préstamo.

DELETE /prestamos/{id} → Eliminar préstamo.

Pago Service

POST /pago → Registrar pago (roles: ADMIN, COBRADOR).

GET /pago/{id} → Consultar pago.

GET /pago → Listar pagos.

Notif Service

POST /notificaciones → Enviar notificación.

GET /notificaciones → Listar notificaciones enviadas.

GET /notificaciones/{id} → Detalle de una notificación.

Reporte Service

GET /reportes → Generar reportes globales.

GET /reportes/{id} → Consultar reporte específico.

POST /reportes → Crear reporte personalizado.

⚙️ Seguridad

Autenticación basada en JWT.

Roles disponibles: ADMIN, COBRADOR.

Los endpoints sensibles (como pagos y reportes) requieren rol específico.

Cada microservicio valida el token antes de procesar la solicitud.

🗄️ Base de datos

Motor: SQL Server 2022

Configuración incluida en docker-compose.yml

Variables de entorno manejadas mediante .env (para contraseñas y claves JWT)

📂 Estructura del repo

Prestamos/
  auth-service/
  cliente-service/
  notif-service/
  pago-service/
  prestamo-service/
  reporte-service/
  docker-compose.yml
  .gitignore
  README.md

🧠 Flujo general del sistema

El usuario se autentica en auth-service y obtiene un JWT.

Con ese token accede a los endpoints de cliente, prestamo, pago, notif y reporte.

Cada servicio valida el token y ejecuta su lógica interna.

Los datos se guardan en SQL Server y se pueden consultar o reportar desde reporte-service.
