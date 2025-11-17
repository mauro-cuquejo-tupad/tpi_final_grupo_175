# 📁 Estructura del Proyecto

```
tpi_final_grupo_175/
│
├── 📄 LICENSE                           # Licencia MIT
├─��� 📄 README.md                         # Documentación principal del proyecto
├── 📄 tpi_final_grupo_175.iml          # Configuración IntelliJ IDEA
│
├── 📂 docs/                             # 📚 DOCUMENTACIÓN COMPLETA
│   ├── 📄 README.md                     # Índice de documentación
│   ├── 📊 RESUMEN_EJECUTIVO.md          # Resumen del proyecto
│   ├── 📊 DIAGRAMA_CLASES.md            # Diagrama UML con Mermaid
│   ├── 🎨 diagrama_clases.puml          # Fuente PlantUML
│   └── 📋 DIAGRAMA_ASCII.txt            # Diagrama en texto ASCII
│
├── 📂 lib/                              # Librerías externas
│   └── 📂 mysql-connector-j-9.5.0/
│       └── 📦 mysql-connector-j-9.5.0.jar
│
└── 📂 GestorEnvios/                     # 🚀 APLICACIÓN PRINCIPAL
    │
    ├── 📄 build.xml                     # Script de compilación Apache Ant
    ├── 📄 manifest.mf                   # Manifiesto JAR
    │
    ├── 📂 nbproject/                    # Configuración NetBeans
    │   ├── build-impl.xml
    │   ├── genfiles.properties
    │   ├── project.properties
    │   └── project.xml
    │
    └── 📂 src/                          # CÓDIGO FUENTE
        │
        ├── 📂 gestorenvios/
        │   │
        │   ├── 📂 config/               # ⚙️ CONFIGURACIÓN
        │   │   ├── ApplicationConfig.java          # Carga propiedades
        │   │   ├── DatabaseConnection.java         # Conexión a BD (Singleton)
        │   │   └── TransactionManager.java         # Gestión de transacciones
        │   │
        │   ├── 📂 dao/                  # 💾 DATA ACCESS OBJECTS
        │   │   ├── GenericDAO.java                 # Interfaz genérica DAO
        │   │   ├── PedidoDAO.java                  # DAO de Pedidos
        │   │   └── EnvioDAO.java                   # DAO de Envíos
        │   │
        │   ├── 📂 entities/             # 🎯 ENTIDADES DEL MODELO
        │   │   ├── Pedido.java                     # Entidad Pedido
        │   │   ├── Envio.java                      # Entidad Envío
        │   │   ├── EstadoPedido.java               # Enum: NUEVO, FACTURADO, ENVIADO
        │   │   ├── EstadoEnvio.java                # Enum: EN_PREPARACION, EN_TRANSITO, ENTREGADO
        │   │   ├── EmpresaEnvio.java               # Enum: ANDREANI, OCA, CORREO_ARG
        │   │   └── TipoEnvio.java                  # Enum: ESTANDAR, EXPRES
        │   │
        │   ├── 📂 main/                 # 🚪 PUNTO DE ENTRADA
        │   │   └── GestorEnvios.java               # Clase main()
        │   │
        │   ├── 📂 models/
        │   │   └── 📂 exceptions/       # ⚠️ EXCEPCIONES PERSONALIZADAS
        │   │       ├── CreacionEntityException.java
        │   │       ├── ConsultaEntityException.java
        │   │       ├── ActualizacionEntityException.java
        │   │       └── EliminacionEntityException.java
        │   │
        │   ├── 📂 services/             # 🔧 LÓGICA DE NEGOCIO
        │   │   ├── GenericService.java             # Interfaz base de servicios
        │   │   ├── GenericPedidosService.java      # Interfaz servicios Pedidos
        │   │   ├── GenericEnviosService.java       # Interfaz servicios Envíos
        │   │   ├── PedidoServiceImpl.java          # Implementación Pedidos
        │   │   └── EnvioServiceImpl.java           # Implementación Envíos
        │   │
        │   └── 📂 ui/                   # 🖥️ INTERFAZ DE USUARIO
        │       └── 📂 console/
        │           │
        │           ├── AppMenu.java                # Menú principal de la aplicación
        │           │
        │           ├── 📂 controllers/  # 🎮 CONTROLADORES
        │           │   ├── MenuHandler.java        # Coordinador del menú
        │           │   ├── PedidoConsoleController.java
        │           │   └── EnvioConsoleController.java
        │           │
        │           ├── 📂 input/        # ⌨️ GESTIÓN DE ENTRADA
        │           │   ├── InputReader.java        # Interfaz lectura
        │           │   ├── ConsoleInputReader.java # Implementación consola
        │           │   └── MenuDisplay.java        # Visualización de menús
        │           │
        │           ├── 📂 output/       # 🖨️ GESTIÓN DE SALIDA
        │           │   ├── PedidoPrinter.java      # Impresión de pedidos
        │           │   └── EnvioPrinter.java       # Impresión de envíos
        │           │
        │           └── 📂 utils/        # 🛠️ UTILIDADES
        │               └── Paginador.java          # Sistema de paginación
        │
        └── 📂 resources/                # 📋 RECURSOS
            ├── application.properties              # Configuración BD
            └── 📂 db/
                └── example.sql                     # Scripts SQL
```

---

## 📊 Resumen por Categorías

### Entidades y Modelo (6 archivos)
```
entities/
├── Pedido.java              # Entidad principal con relación 1:1 a Envío
├── Envio.java               # Entidad de envío
├── EstadoPedido.java        # 3 estados posibles
├── EstadoEnvio.java         # 3 estados posibles
├── EmpresaEnvio.java        # 3 empresas disponibles
└── TipoEnvio.java           # 2 tipos de servicio
```

### Capa de Datos (3 archivos)
```
dao/
├── GenericDAO.java          # Interfaz con operaciones CRUD + Tx
├── PedidoDAO.java           # 12+ métodos de consulta
└── EnvioDAO.java            # 10+ métodos de consulta
```

### Capa de Servicios (5 archivos)
```
services/
├── GenericService.java              # CRUD básico
├── GenericPedidosService.java       # Servicios específicos pedidos
├── GenericEnviosService.java        # Servicios específicos envíos
├── PedidoServiceImpl.java           # Lógica de negocio + validaciones
└── EnvioServiceImpl.java            # Transacciones complejas
```

### Configuración (3 archivos)
```
config/
├── ApplicationConfig.java           # Singleton de propiedades
├── DatabaseConnection.java          # Pool de conexiones
└── TransactionManager.java          # ACID transactions
```

### Interfaz de Usuario (11 archivos)
```
ui/console/
├── AppMenu.java                     # Loop principal
├── controllers/
│   ├── MenuHandler.java             # 22+ métodos de menú
│   ├── PedidoConsoleController.java # CRUD de pedidos
│   └── EnvioConsoleController.java  # CRUD de envíos
├── input/
│   ├── InputReader.java             # Interfaz
│   ├── ConsoleInputReader.java      # Implementación
│   └── MenuDisplay.java             # Formateo de menús
├── output/
│   ├── PedidoPrinter.java           # Pretty print pedidos
│   └── EnvioPrinter.java            # Pretty print envíos
└── utils/
    └── Paginador.java               # Genérico reutilizable
```

### Excepciones (4 archivos)
```
models/exceptions/
├── CreacionEntityException.java
├── ConsultaEntityException.java
├── ActualizacionEntityException.java
└── EliminacionEntityException.java
```

### Documentación (5 archivos + README)
```
docs/
├── README.md                        # Índice de documentación
├── RESUMEN_EJECUTIVO.md             # Vista general del proyecto
├── DIAGRAMA_CLASES.md               # UML con Mermaid
├── diagrama_clases.puml             # UML con PlantUML
└── DIAGRAMA_ASCII.txt               # Visualización rápida
```

---

## 📈 Estadísticas del Proyecto

### Total de Archivos Java
- **Clases concretas**: 23
- **Interfaces**: 8
- **Enumeraciones**: 4
- **Excepciones**: 4
- **Total**: ~39 archivos Java

### Líneas de Código Aproximadas
- **Entities**: ~600 líneas
- **DAO**: ~800 líneas
- **Services**: ~500 líneas
- **Config**: ~300 líneas
- **UI**: ~1200 líneas
- **Total estimado**: ~3400 líneas (sin contar documentación)

### Distribución por Tipo
```
📊 Clases:        23 (59%)
📊 Interfaces:     8 (21%)
📊 Enums:          4 (10%)
📊 Excepciones:    4 (10%)
```

---

## 🎯 Puntos de Entrada Principales

### 1. Ejecutar la Aplicación
```java
// GestorEnvios/src/gestorenvios/main/GestorEnvios.java
public static void main(String[] args) {
    AppMenu app = new AppMenu();
    app.run();
}
```

### 2. Configurar Base de Datos
```properties
// GestorEnvios/src/resources/application.properties
db.url=jdbc:mysql://localhost:3306/tfi_bd_grupo175
db.user=root
db.password=tu_contraseña
```

### 3. Inicializar Esquema
```sql
-- GestorEnvios/src/resources/db/example.sql
CREATE DATABASE tfi_bd_grupo175;
USE tfi_bd_grupo175;
-- Ejecutar scripts de creación de tablas...
```

---

## 🔄 Flujo de Navegación del Código

### Para entender el proyecto, seguir este orden:

1. **Entidades** (`entities/`)
   - Empezar por `Pedido.java` y `Envio.java`
   - Revisar los enums para entender estados

2. **DAO** (`dao/`)
   - Leer `GenericDAO.java` (interfaz)
   - Ver implementación en `PedidoDAO.java`

3. **Servicios** (`services/`)
   - Leer `GenericService.java`
   - Ver lógica de negocio en `PedidoServiceImpl.java`

4. **Configuración** (`config/`)
   - Entender `DatabaseConnection.java`
   - Ver `TransactionManager.java` para transacciones

5. **UI** (`ui/console/`)
   - Empezar por `AppMenu.java` (main loop)
   - Seguir a `MenuHandler.java`
   - Revisar controllers para ver la interacción

---

## 🎨 Convenciones de Código

### Nomenclatura
- **Clases**: PascalCase (`PedidoServiceImpl`)
- **Interfaces**: PascalCase con sufijo si aplica (`GenericDAO`)
- **Métodos**: camelCase (`buscarPorId`)
- **Constantes**: UPPER_SNAKE_CASE (`SELECT_BY_ID_SQL`)
- **Packages**: lowercase (`gestorenvios.dao`)

### Estructura de Clases
```java
public class MiClase {
    // 1. Constantes estáticas
    private static final String CONSTANTE = "valor";
    
    // 2. Campos de instancia
    private TipoDato campo;
    
    // 3. Constructor(es)
    public MiClase() { }
    
    // 4. Métodos públicos
    public void metodoPublico() { }
    
    // 5. Métodos privados
    private void metodoPrivado() { }
}
```

---

**Grupo 175 - TPI Final Programación 2**  
**UTN - Universidad Tecnológica Nacional**

