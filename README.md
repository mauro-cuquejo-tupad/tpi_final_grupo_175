# 📦 Sistema Gestor de Envíos

**Trabajo Práctico Integrador Final - Programación 2**  
**Grupo 175**

---

## 📋 Descripción del Proyecto

Sistema de gestión de pedidos y envíos desarrollado en Java con arquitectura en capas. Permite administrar el ciclo completo de pedidos desde su creación hasta el seguimiento de envíos, implementando operaciones CRUD con persistencia en base de datos MySQL.

El proyecto aplica patrones de diseño y buenas prácticas de programación orientada a objetos, incluyendo manejo de transacciones, paginación de resultados y baja lógica de registros.

---

## 🚀 Características Principales

### Gestión de Pedidos
- ✅ Crear pedidos con generación automática de número
- ✅ Listar pedidos con paginación
- ✅ Buscar pedidos por:
  - ID
  - Número de pedido
  - Nombre de cliente
  - Número de tracking
- ✅ Actualizar estado y datos de pedidos
- ✅ Eliminar pedidos (baja lógica)

### Gestión de Envíos
- ✅ Crear envíos asociados a pedidos
- ✅ Listar envíos con paginación
- ✅ Buscar envíos por:
  - ID
  - Número de tracking
  - Número de pedido
- ✅ Actualizar información de envíos
- ✅ Eliminar envíos (baja lógica)
- ✅ Gestión de estados de envío (EN_PREPARACION, EN_TRANSITO, ENTREGADO)
- ✅ Múltiples empresas de envío (ANDREANI, OCA, CORREO_ARG)
- ✅ Tipos de envío (ESTANDAR, EXPRES)

---

## 🏗️ Arquitectura del Proyecto

> 📊 **[Ver Diagrama de Clases UML Completo](docs/DIAGRAMA_CLASES.md)**

### Estructura de Capas

```
┌─────────────────────────────────────┐
│        UI Layer (Console)           │
│  - AppMenu                          │
│  - MenuHandler                      │
│  - Controllers                      │
│  - Input/Output                     │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Service Layer                 │
│  - PedidoServiceImpl                │
│  - EnvioServiceImpl                 │
│  - Validaciones de negocio          │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         DAO Layer                   │
│  - PedidoDAO                        │
│  - EnvioDAO                         │
│  - GenericDAO                       │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Configuration Layer            │
│  - DatabaseConnection               │
│  - TransactionManager               │
│  - ApplicationConfig                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Database (MySQL)               │
│  - Pedidos                          │
│  - Envios                           │
└─────────────────────────────────────┘
```

### Estructura de Directorios

```
GestorEnvios/
├── src/
│   ├── gestorenvios/
│   │   ├── config/              # Configuración de BD y propiedades
│   │   ├── dao/                 # Acceso a datos
│   │   ├── entities/            # Entidades del modelo
│   │   ├── main/                # Clase principal
│   │   ├── models/exceptions/   # Excepciones personalizadas
│   │   ├── services/            # Lógica de negocio
│   │   └── ui/console/          # Interfaz de usuario
│   │       ├── controllers/     # Controladores de menú
│   │       ├── input/           # Gestión de entrada
│   │       ├── output/          # Gestión de salida
│   │       └── utils/           # Utilidades (paginación)
│   └── resources/
│       ├── application.properties
│       └── db/
│           └── example.sql      # Scripts de BD
└── lib/
    └── mysql-connector-j-9.5.0/ # Driver MySQL
```

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java (JDK 8+)
- **Base de Datos:** MySQL 8.0+
- **Driver JDBC:** MySQL Connector/J 9.5.0
- **Build Tool:** Apache Ant
- **IDE:** NetBeans

---

## 📦 Modelo de Datos

### Entidad Pedido
```java
- id: Long (PK)
- eliminado: Boolean
- numero: String (UNIQUE, ej: "PED-00000001")
- fecha: LocalDate
- clienteNombre: String
- total: Double
- estado: EstadoPedido (NUEVO, FACTURADO, ENVIADO)
- envio: Envio (relación 1:1)
```

### Entidad Envío
```java
- id: Long (PK)
- eliminado: Boolean
- tracking: String (UNIQUE)
- empresa: EmpresaEnvio (ANDREANI, OCA, CORREO_ARG)
- tipo: TipoEnvio (ESTANDAR, EXPRES)
- costo: Double
- fechaDespacho: LocalDate
- fechaEstimada: LocalDate
- estado: EstadoEnvio (EN_PREPARACION, EN_TRANSITO, ENTREGADO)
```

---

## ⚙️ Instalación y Configuración

### Prerrequisitos

1. **Java JDK 8 o superior**
   ```powershell
   java -version
   ```

2. **MySQL Server 8.0+**
   ```powershell
   mysql --version
   ```

3. **Apache Ant** (para compilar el proyecto)
   ```powershell
   ant -version
   ```

### Configuración de Base de Datos

1. **Crear la base de datos:**
   ```sql
   CREATE DATABASE tfi_bd_grupo175;
   USE tfi_bd_grupo175;
   ```

2. **Ejecutar el script de creación de tablas** ubicado en:
   ```
   GestorEnvios/src/resources/db/example.sql
   ```

3. **Configurar credenciales** en `application.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/tfi_bd_grupo175?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=tu_contraseña
   db.driver=com.mysql.cj.jdbc.Driver
   ```

### Compilación y Ejecución

#### Usando NetBeans
1. Abrir el proyecto en NetBeans
2. Ejecutar con F6 o botón "Run"

#### Usando Ant desde terminal
```powershell
cd GestorEnvios
ant compile
ant run
```

---

## 📖 Uso del Sistema

### Menú Principal

Al ejecutar la aplicación, se presenta un menú interactivo con las siguientes opciones:

```
=== MENÚ PRINCIPAL ===
1.  Crear Pedido
2.  Listar Pedidos
3.  Buscar Pedido por Número
4.  Buscar Pedido por Tracking
5.  Buscar Pedido por Cliente
6.  Buscar Pedido por ID
7.  Actualizar Pedido por Número
8.  Actualizar Pedido por ID
9.  Eliminar Pedido por Número
10. Eliminar Pedido por ID
11. Eliminar Envío de Pedido

12. Crear Envío
13. Listar Envíos
14. Buscar Envío por Tracking
15. Buscar Envío por Número de Pedido
16. Buscar Envío por ID
17. Actualizar Envío por Tracking
18. Actualizar Envío por Número de Pedido
19. Actualizar Envío por ID
20. Eliminar Envío por Tracking
21. Eliminar Envío por Número de Pedido
22. Eliminar Envío por ID

0.  Salir
```

### Ejemplos de Uso

#### Crear un Pedido
1. Seleccionar opción `1`
2. Ingresar nombre del cliente
3. Ingresar monto total
4. El sistema genera automáticamente el número de pedido

#### Crear un Envío
1. Seleccionar opción `12`
2. Ingresar número de pedido asociado
3. Seleccionar empresa de envío
4. Seleccionar tipo de envío
5. Ingresar costo
6. El sistema asigna tracking automáticamente

---

## 🎯 Patrones de Diseño Implementados

### 1. **DAO (Data Access Object)**
Separa la lógica de acceso a datos de la lógica de negocio.

```java
public interface GenericDAO<T> {
    void insertar(T entidad) throws Exception;
    T buscarPorId(Long id) throws Exception;
    List<T> buscarTodos(Long cantidad, Long pagina) throws Exception;
    void actualizar(T entidad) throws Exception;
    void eliminarLogico(Long id) throws Exception;
}
```

### 2. **Service Layer**
Encapsula la lógica de negocio y validaciones.

```java
public interface GenericService<T> {
    String crear(T entidad) throws CreacionEntityException;
    List<T> buscarTodos(Long cantidad, Long pagina) throws ConsultaEntityException;
    void actualizar(T entidad) throws ActualizacionEntityException;
    void eliminar(T entidad) throws EliminacionEntityException;
}
```

### 3. **Singleton**
Gestión única de configuración de aplicación.

```java
public class ApplicationConfig {
    private static Properties properties;
    // Única instancia de propiedades
}
```

### 4. **Dependency Injection (Manual)**
Inyección de dependencias en constructores para facilitar testing y mantener bajo acoplamiento.

### 5. **Transaction Script**
Manejo de transacciones con try-with-resources y `TransactionManager`.

---

## 🔒 Manejo de Transacciones

El sistema implementa transacciones ACID para operaciones críticas:

```java
public void crearEnvioYActualizarPedido(Envio envio, Pedido pedido) {
    try (Connection conn = DatabaseConnection.getConnection();
         TransactionManager tm = new TransactionManager(conn)) {
        
        tm.startTransaction();
        envioDAO.insertarTx(envio, conn);
        pedido.setEnvio(envio);
        pedido.setEstado(EstadoPedido.ENVIADO);
        pedidosService.actualizarTx(pedido, conn);
        tm.commit();
    } catch (Exception e) {
        // Rollback automático en close()
    }
}
```

---

## 🧪 Características Técnicas

### Paginación
Sistema de paginación implementado con `Paginador` utility:
- Navegación por páginas de resultados
- Tamaño de página configurable
- Contador total de registros

### Baja Lógica
Todos los registros se eliminan de forma lógica (campo `eliminado`), permitiendo:
- Auditoría de datos
- Recuperación de información
- Cumplimiento de normativas de retención

### Manejo de Excepciones
Jerarquía de excepciones personalizadas:
- `CreacionEntityException`
- `ConsultaEntityException`
- `ActualizacionEntityException`
- `EliminacionEntityException`

### Validaciones
- Validación de entrada de usuario
- Validación de reglas de negocio
- Validación de integridad referencial

---

## 👥 Equipo de Desarrollo

**Grupo 175**
- Programación 2
- Universidad Tecnológica Nacional (UTN)

---

## 📝 Licencia

Este proyecto está bajo la licencia especificada en el archivo `LICENSE`.

---

## 🤝 Contribuciones

Este es un proyecto académico. Para sugerencias o mejoras, contactar al equipo de desarrollo.

---

## 📚 Documentación

> 📊 **[Resumen Ejecutivo del Proyecto](docs/RESUMEN_EJECUTIVO.md)** - Vista general completa para evaluación

### Diagramas y Arquitectura
- **[📊 Diagrama de Clases UML Completo](docs/DIAGRAMA_CLASES.md)** - Diagrama detallado con Mermaid
  - Incluye todas las clases, interfaces y relaciones
  - Visualización interactiva en GitHub
  
- **[🎨 Diagrama PlantUML](docs/diagrama_clases.puml)** - Archivo fuente para generar imágenes
  - Formato PNG, SVG o PDF
  - Compatible con múltiples herramientas
  
- **[📋 Vista Rápida ASCII](docs/DIAGRAMA_ASCII.txt)** - Diagrama en formato texto
  - Visualización inmediata en consola
  - No requiere herramientas adicionales

### Guías
- **[📖 Cómo Visualizar Diagramas](docs/README.md)** - Instrucciones para ver y generar diagramas
- **Javadoc** - Documentación embebida en código fuente
- **Scripts SQL** - `GestorEnvios/src/resources/db/example.sql`

---

## 📞 Soporte

Para problemas técnicos o consultas sobre el proyecto, consultar la documentación del código o contactar a los miembros del Grupo 175.

---

**Desarrollado con ❤️ por el Grupo 175**
