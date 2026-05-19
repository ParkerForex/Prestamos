# 💸 PréstamosPRO — Sistema de Gestión de Préstamos

Sistema de microservicios desarrollado para gestionar préstamos diarios, cobradores, rutas y pagos en tiempo real. Construido con **Java 21 + Spring Boot 3**, **JWT**, **Apache Kafka**, **SQL Server** y **Docker Compose**.

> Desarrollado para una empresa real de préstamos en Barranquilla, Colombia 🇨🇴

---

## 🧩 Arquitectura

```
                    ┌─────────────────┐
                    │   App Móvil     │
                    │   (Flutter)     │
                    └────────┬────────┘
                             │ JWT Token
              ┌──────────────┼──────────────┐
              │              │              │
    ┌─────────▼──┐  ┌────────▼───┐  ┌──────▼──────┐
    │auth-service│  │cliente-svc │  │prestamo-svc │
    │  :8081     │  │   :8082    │  │    :8083    │
    └────────────┘  └────────────┘  └──────┬──────┘
                                           │ Kafka
    ┌────────────┐  ┌────────────┐  ┌──────▼──────┐
    │pago-service│  │reporte-svc │  │notif-service│
    │   :8084    │  │   :8085    │  │    :8086    │
    └────────────┘  └────────────┘  └─────────────┘
              │              │
    ┌─────────▼──────────────▼──────┐
    │         SQL Server            │
    │          :1433                │
    └───────────────────────────────┘
              │
    ┌─────────▼──────────┐
    │   Apache Kafka     │
    │       :9092        │
    └────────────────────┘
```

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| **Java 21** | Lenguaje principal |
| **Spring Boot 3.4.5** | Framework de microservicios |
| **Spring Security 6** | Autenticación y autorización |
| **JWT (JJWT 0.12.3)** | Tokens de acceso |
| **Spring Data JPA** | Acceso a base de datos |
| **Hibernate 6** | ORM |
| **SQL Server 2022** | Base de datos principal |
| **Apache Kafka** | Mensajería entre servicios |
| **Docker + Docker Compose** | Contenedores y orquestación |
| **Lombok** | Reducción de boilerplate |
| **Maven** | Gestión de dependencias |
| **IntelliJ IDEA** | IDE de desarrollo |
| **Postman** | Pruebas de API |
| **DBeaver** | Cliente de base de datos |
| **Kafka UI** | Monitoreo de topics Kafka |

---

## 📁 Estructura del repositorio

```
Prestamos/
├── auth-service/           # Autenticación y JWT
├── cliente-service/        # Gestión de clientes
├── prestamo-service/       # Gestión de préstamos
├── pago-service/           # Registro de pagos diarios
├── reporte-service/        # Reportes y dashboard
├── notif-service/          # Consumer de eventos Kafka
├── docker-compose.yml      # Infraestructura completa
└── README.md
```

---

## 🚀 Cómo levantar el proyecto

### Requisitos previos
- Java 21
- Maven 3.x
- Docker Desktop

### 1. Clonar el repositorio
```bash
git clone https://github.com/ParkerForex/Prestamos.git
cd Prestamos
```

### 2. Compilar todos los servicios
```bash
cd auth-service && mvn clean package -DskipTests && cd ..
cd cliente-service && mvn clean package -DskipTests && cd ..
cd prestamo-service && mvn clean package -DskipTests && cd ..
cd pago-service && mvn clean package -DskipTests && cd ..
cd reporte-service && mvn clean package -DskipTests && cd ..
cd notif-service && mvn clean package -DskipTests && cd ..
```

### 3. Levantar todo con Docker Compose
```bash
docker-compose up -d
```

### 4. Crear la base de datos
```bash
docker exec -it sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "PrestamosP@ss123" \
  -No -Q "CREATE DATABASE prestamospro"
```

### 5. Verificar que todo esté corriendo
```bash
docker ps
```

| Servicio | Puerto | Estado |
|---|---|---|
| SQL Server | 1433 | ✅ |
| Apache Kafka | 9092 | ✅ |
| Kafka UI | 8090 | ✅ |
| auth-service | 8081 | ✅ |
| cliente-service | 8082 | ✅ |
| prestamo-service | 8083 | ✅ |
| pago-service | 8084 | ✅ |
| reporte-service | 8085 | ✅ |
| notif-service | 8086 | ✅ |

**Kafka UI:** http://localhost:8090

---

## 👥 Roles del sistema

| Rol | Descripción |
|---|---|
| **ADMIN** | Dueño — ve todo, gestiona cobradores, reportes globales |
| **COBRADOR** | Opera su ruta — solo ve sus clientes y pagos |

---

## 🔑 Endpoints completos

> Todos los endpoints (excepto `/api/auth/login` y `/api/auth/usuarios/{id}`) requieren header:
> ```
> Authorization: Bearer {token}
> ```

---

### 📌 auth-service — Puerto 8081

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "admin@prestamospro.com",
    "password": "admin123"
}
```
**Respuesta:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "nombre": "Admin",
    "rol": "ADMIN",
    "email": "admin@prestamospro.com"
}
```

#### Obtener usuario por ID (interno)
```http
GET /api/auth/usuarios/{id}
```
**Respuesta:**
```json
{
    "id": 1,
    "nombre": "Admin",
    "email": "admin@prestamospro.com",
    "rol": "ADMIN"
}
```

---

### 📌 cliente-service — Puerto 8082

#### Crear cliente
```http
POST /api/clientes
Authorization: Bearer {token}
Content-Type: application/json

{
    "nombre": "Katherine Torres",
    "telefono": "300 456 7890",
    "direccion": "Calle 12 # 34-56",
    "rutaId": 1,
    "cobradorId": 1
}
```
**Respuesta:**
```json
{
    "id": 1,
    "nombre": "Katherine Torres",
    "telefono": "300 456 7890",
    "direccion": "Calle 12 # 34-56",
    "fotoUrl": null,
    "rutaId": 1,
    "cobradorId": 1,
    "activo": true,
    "tieneFoto": false,
    "tieneDireccion": true,
    "puedeCrearPrestamo": false
}
```

> ⚠️ `puedeCrearPrestamo` es `true` solo cuando el cliente tiene **foto Y dirección**.

#### Subir foto del cliente
```http
POST /api/clientes/{id}/foto
Authorization: Bearer {token}
Content-Type: multipart/form-data

foto: [archivo de imagen]
```

#### Listar todos los clientes activos
```http
GET /api/clientes
Authorization: Bearer {token}
```

#### Obtener cliente por ID
```http
GET /api/clientes/{id}
Authorization: Bearer {token}
```

#### Clientes por cobrador
```http
GET /api/clientes/cobrador/{cobradorId}
Authorization: Bearer {token}
```

#### Clientes por ruta
```http
GET /api/clientes/ruta/{rutaId}
Authorization: Bearer {token}
```

#### Actualizar cliente
```http
PUT /api/clientes/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
    "nombre": "Katherine Torres",
    "telefono": "300 456 7890",
    "direccion": "Calle 12 # 34-56"
}
```

---

### 📌 prestamo-service — Puerto 8083

#### Crear préstamo
```http
POST /api/prestamos
Authorization: Bearer {token}
Content-Type: application/json

{
    "clienteId": 1,
    "cobradorId": 1,
    "rutaId": 1,
    "cuotaDiaria": 20000,
    "dias": 33,
    "fechaInicio": "2026-05-16"
}
```
**Respuesta:**
```json
{
    "id": 1,
    "clienteId": 1,
    "cobradorId": 1,
    "rutaId": 1,
    "monto": 660000,
    "cuotaDiaria": 20000,
    "saldo": 660000,
    "fechaInicio": "2026-05-16",
    "fechaFin": "2026-06-23",
    "diasTotales": 33,
    "diasRestantes": 32,
    "estado": "ACTIVO"
}
```

> ℹ️ La **fecha fin** se calcula automáticamente saltando domingos.
> El **monto total** = `cuotaDiaria × dias`.

**Reglas de negocio:**
- ❌ Sin foto del cliente → error
- ❌ Sin dirección del cliente → error
- ❌ Ya tiene préstamo activo → error

#### Listar préstamos activos
```http
GET /api/prestamos
Authorization: Bearer {token}
```

#### Listar préstamos vencidos
```http
GET /api/prestamos/vencidos
Authorization: Bearer {token}
```

#### Obtener préstamo por ID
```http
GET /api/prestamos/{id}
Authorization: Bearer {token}
```

#### Préstamos por cobrador
```http
GET /api/prestamos/cobrador/{cobradorId}
Authorization: Bearer {token}
```

#### Préstamos por cliente
```http
GET /api/prestamos/cliente/{clienteId}
Authorization: Bearer {token}
```

#### Renovar préstamo
```http
POST /api/prestamos/renovar/{clienteId}
Authorization: Bearer {token}
Content-Type: application/json

{
    "cobradorId": 1,
    "rutaId": 1,
    "cuotaDiaria": 20000,
    "dias": 33,
    "fechaInicio": "2026-06-24"
}
```
> Cierra el préstamo actual (PAGADO) y abre uno nuevo.

---

### 📌 pago-service — Puerto 8084

#### Registrar pago del día
```http
POST /api/pagos
Authorization: Bearer {token}
Content-Type: application/json

{
    "prestamoId": 1,
    "clienteId": 1,
    "cobradorId": 1,
    "montoPagado": 20000,
    "observacion": "pago del día"
}
```
**Respuesta:**
```json
{
    "id": 1,
    "prestamoId": 1,
    "clienteId": 1,
    "cobradorId": 1,
    "montoPagado": 20000,
    "fechaPago": "2026-05-16",
    "observacion": "pago del día",
    "saldoActual": 640000,
    "estadoPrestamo": "ACTIVO"
}
```

> ℹ️ Si `saldoActual = 0` el estado cambia a **PAGADO** automáticamente.
> Se puede registrar `montoPagado: 0` cuando el cliente no paga ese día.

#### Resumen del día por cobrador
```http
GET /api/pagos/resumen/{cobradorId}
Authorization: Bearer {token}
```
**Respuesta:**
```json
{
    "fecha": "2026-05-16",
    "cobradorId": 1,
    "totalRecaudado": 60000,
    "clientesPagaron": 3,
    "clientesNoPagaron": 1,
    "pagos": [...]
}
```

#### Total recaudado hoy por cobrador
```http
GET /api/pagos/total/{cobradorId}
Authorization: Bearer {token}
```

#### Historial de pagos de un préstamo
```http
GET /api/pagos/prestamo/{prestamoId}
Authorization: Bearer {token}
```

---

### 📌 reporte-service — Puerto 8085

#### Reporte global (ADMIN)
```http
GET /api/reportes/global
Authorization: Bearer {token admin}
```
**Respuesta:**
```json
{
    "fecha": "2026-05-16",
    "carteraTotal": 12400000,
    "recaudadoHoy": 840000,
    "prestamosActivos": 87,
    "prestamosVencidos": 14,
    "prestamosPagados": 0,
    "porCobrador": [...],
    "clientesEnMora": [...]
}
```

#### Reporte por cobrador
```http
GET /api/reportes/cobrador/{cobradorId}
Authorization: Bearer {token}
```
**Respuesta:**
```json
{
    "cobradorId": 1,
    "nombreCobrador": "Antonio Cobrador",
    "recaudadoHoy": 280000,
    "carteraRuta": 3200000,
    "clientesActivos": 18,
    "clientesEnMora": 2
}
```

---

## ⚡ Eventos Kafka

| Topic | Publicado por | Cuándo |
|---|---|---|
| `prestamo.creado` | prestamo-service | Al crear un préstamo |
| `pago.recibido` | pago-service | Al registrar un pago |
| `prestamo.en-mora` | prestamo-service | Al vencer un préstamo |
| `prestamo.pagado` | pago-service | Al saldar un préstamo |

Todos los eventos son consumidos por **notif-service** en tiempo real.

---

## 🔐 Seguridad

- Autenticación basada en **JWT** con expiración de 24 horas
- Roles: `ADMIN` y `COBRADOR`
- El rol viaja dentro del token — Spring Security filtra automáticamente
- Cada microservicio valida el token de forma independiente
- BCrypt para encriptación de contraseñas
- Comunicación interna entre servicios usando el mismo token

---

## 🗄️ Modelo de datos

```
usuarios
├── id, nombre, email, password (BCrypt), rol, activo

clientes
├── id, nombre, telefono, direccion, foto_url, ruta_id, cobrador_id, activo

prestamos
├── id, cliente_id, cobrador_id, ruta_id
├── monto, cuota_diaria, saldo
├── fecha_inicio, fecha_fin, dias_totales
└── estado (ACTIVO/VENCIDO/PAGADO)

pagos
├── id, prestamo_id, cliente_id, cobrador_id
├── monto_pagado, fecha_pago, observacion
```

---

## 📱 App móvil (próximamente)

La app móvil será desarrollada en **Flutter** y consumirá exactamente los mismos endpoints.

**Funcionalidades planeadas:**
- Login con JWT
- Vista de ruta del cobrador
- Registro de pagos diarios (lunes a sábado)
- Tomar foto del cliente con cámara nativa
- GPS para registrar dirección
- Dashboard del dueño con reportes
- Notificaciones push de mora (desde Kafka)

**Plataformas:** Android e iOS desde un solo código.

---


## 📋 Usuarios de prueba

| Email | Password | Rol |
|---|---|---|
| admin@prestamospro.com | admin123 | ADMIN |
| antonio@prestamospro.com | cobrador123 | COBRADOR |

---

## 👨‍💻 Autor

**Camilo** — [@ParkerForex](https://github.com/ParkerForex)

Barranquilla, Colombia 🇨🇴

