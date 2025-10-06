# 🧩 Microservicios: Order Service & Product Service

## 📖 Descripción del Proyecto

Este proyecto implementa un sistema backend distribuido compuesto por **dos microservicios** diseñados para gestionar el flujo de pedidos de una tienda en línea.  
Los servicios se comunican entre sí mediante **APIs REST** y aplican **buenas prácticas de arquitectura y desarrollo backend**.

### 🛒 **Product Service**
- Administra el **catálogo de productos** y su **inventario (stock)**.  
- Expone endpoints REST para:
  - Consultar productos disponibles.  
  - Verificar la disponibilidad de stock.  
  - Actualizar el stock al procesar pedidos.

### 📦 **Order Service**
- Gestiona la **creación y procesamiento de pedidos**.  
- Se comunica con el Product Service para **validar disponibilidad** antes de confirmar un pedido.  
- Calcula el **total del pedido** y **ajusta automáticamente** el stock en el Product Service.

La comunicación entre ambos microservicios es **síncrona**, utilizando `RestTemplate`.  
Cada servicio incluye **pruebas unitarias** con **JUnit y Mockito**, y **documentación de API** generada con **Swagger / OpenAPI**.

---

## 🗂️ Estructura del Proyecto

```
microservicios-prueba-tecnica/
├─ product-service/
│  ├─ src/main/java/com/product/service/
│  ├─ src/test/java/com/product/service/test/
│  ├─ pom.xml
│  └─ README.md
│
├─ order-service/
│  ├─ src/main/java/com/order/service/
│  ├─ src/test/java/com/order/service/test/
│  ├─ pom.xml
│  └─ README.md
│
└─ README.md
```

---

## ⚙️ Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/gloriavargasfernandez95/microservicios-prueba-tecnica.git
```

### 2. Configurar puertos de los microservicios

#### Product Service
Editar el archivo `src/main/resources/application.properties`:
```properties
server.port=8080
```

#### Order Service
Editar el archivo `src/main/resources/application.properties`:
```properties
server.port=8085
```

> ⚠️ Asegúrate de que cada microservicio se ejecute en un **puerto diferente**.

---

## 🏗️ Compilación del Proyecto

Desde la raíz del repositorio, ejecutar los siguientes comandos:

```bash
cd product-service
mvn clean install

cd ../order-service
mvn clean install
```

---

## 🚀 Ejecución de los Microservicios

### Product Service
```bash
cd product-service
mvn spring-boot:run
```

### Order Service
```bash
cd order-service
mvn spring-boot:run
```

Ambos servicios se ejecutarán en los puertos configurados previamente (por defecto: **8080** y **8085**).

---

## 🧪 Ejecución de Pruebas Unitarias

Cada microservicio incluye **tests unitarios** desarrollados con **JUnit 5 y Mockito**.

```bash
# Product Service
cd product-service
mvn test

# Order Service
cd ../order-service
mvn test
```

---

## 📘 Documentación de la API (Swagger UI)

Cada microservicio cuenta con documentación generada automáticamente mediante **Swagger/OpenAPI**.  
Para visualizarla, accede a la siguiente URL en tu navegador:

```
http://localhost:{PORT}/swagger-ui/index.html#/
```

> Reemplaza `{PORT}` por el puerto correspondiente al servicio (`8080` o `8085`).

---

## 🧱 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.3.13**
- **Maven**
- **RestTemplate** (comunicación síncrona)
- **JUnit 5 / Mockito** (pruebas unitarias)
- **Swagger / OpenAPI** (documentación de API)

---

## 👩‍💻 Autora

**Gloria Vargas Fernández**  
Repositorio: [github.com/gloriavargasfernandez95](https://github.com/gloriavargasfernandez95)
