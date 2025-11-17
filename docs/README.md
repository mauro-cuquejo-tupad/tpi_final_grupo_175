# 📚 Documentación - Sistema Gestor de Envíos

Bienvenido a la documentación técnica del Sistema Gestor de Envíos del Grupo 175.

## 📑 Índice de Documentos

### 🎯 Documentos Principales

| Archivo | Descripción | Para Quién |
|---------|-------------|------------|
| **[RESUMEN_EJECUTIVO.md](RESUMEN_EJECUTIVO.md)** | Resumen del proyecto completo | Profesores, evaluadores |
| **[ESTRUCTURA_PROYECTO.md](ESTRUCTURA_PROYECTO.md)** | Estructura detallada del código | Desarrolladores |
| **[README principal](../README.md)** | Guía de uso del sistema | Usuarios, desarrolladores |

### 🎯 Diagramas UML

| Archivo | Descripción | Formato | Uso Recomendado |
|---------|-------------|---------|-----------------|
| **[DIAGRAMA_CLASES.md](DIAGRAMA_CLASES.md)** | Diagrama completo con Mermaid | Markdown + Mermaid | Visualización en GitHub/GitLab |
| **[diagrama_clases.puml](diagrama_clases.puml)** | Fuente PlantUML | PlantUML | Generar imágenes PNG/SVG/PDF |
| **[DIAGRAMA_ASCII.txt](DIAGRAMA_ASCII.txt)** | Vista rápida en texto | ASCII Art | Visualización en terminal |

---

## 🔍 Descripción de Cada Formato

### 📊 DIAGRAMA_CLASES.md (Mermaid)
**Ventajas:**
- ✅ Se visualiza automáticamente en GitHub, GitLab y editores modernos
- ✅ No requiere instalación de software adicional
- ✅ Incluye documentación completa integrada
- ✅ Interactivo y navegable

**Cómo usar:**
1. Abre el archivo en GitHub → se renderiza automáticamente
2. En VS Code: Instala "Markdown Preview Mermaid Support" y presiona `Ctrl+Shift+V`
3. Online: Copia el código a https://mermaid.live/

---

### 🎨 diagrama_clases.puml (PlantUML)
**Ventajas:**
- ✅ Genera imágenes de alta calidad (PNG, SVG, PDF)
- ✅ Personalizable con themes y estilos
- ✅ Compatible con documentación impresa
- ✅ Integrable en LaTeX, Word, PowerPoint

**Cómo generar imágenes:**

```powershell
# Instalar PlantUML
# Descargar desde: https://plantuml.com/download

# Generar PNG
java -jar plantuml.jar diagrama_clases.puml

# Generar SVG (vectorial, escalable)
java -jar plantuml.jar -tsvg diagrama_clases.puml

# Generar PDF
java -jar plantuml.jar -tpdf diagrama_clases.puml
```

**Con Docker:**
```powershell
docker run --rm -v ${PWD}:/data plantuml/plantuml diagrama_clases.puml
```

---

### 📋 DIAGRAMA_ASCII.txt (Texto Plano)
**Ventajas:**
- ✅ Visualización instantánea en cualquier editor de texto
- ✅ No requiere ninguna herramienta especial
- ✅ Perfecto para documentación en terminal
- ✅ Fácil de incluir en emails o documentos de texto

**Cómo usar:**
```powershell
# En PowerShell
Get-Content DIAGRAMA_ASCII.txt

# En Linux/Mac
cat DIAGRAMA_ASCII.txt

# En cualquier editor de texto
notepad DIAGRAMA_ASCII.txt
```

---

## 🛠️ Herramientas Recomendadas

### Visual Studio Code (Recomendado)
```
Extensiones:
✓ Markdown Preview Mermaid Support
✓ PlantUML
✓ Markdown All in One
```

### IntelliJ IDEA / JetBrains
```
Plugins:
✓ PlantUML Integration
✓ Mermaid
```

### Navegadores Web
- **Mermaid Live Editor**: https://mermaid.live/
- **PlantUML Server**: http://www.plantuml.com/plantuml/

---

## 📐 Cómo Visualizar el Diagrama de Clases

## 🌐 Visualización en Navegador (Mermaid)

### Opción 1: GitHub/GitLab
Simplemente abre `DIAGRAMA_CLASES.md` en GitHub o GitLab para ver el diagrama renderizado automáticamente.

### Opción 2: Editor Visual Studio Code
1. Instala la extensión "Markdown Preview Mermaid Support"
2. Abre `DIAGRAMA_CLASES.md`
3. Presiona `Ctrl+Shift+V` para vista previa

### Opción 3: Visor Online
Copia el código Mermaid y pégalo en: https://mermaid.live/

## 🖼️ Generar Imagen desde PlantUML

### Requisitos
- Java JRE instalado
- PlantUML JAR descargado de: https://plantuml.com/download

### Generar PNG
```powershell
# Desde el directorio docs/
java -jar plantuml.jar diagrama_clases.puml

# O especificando formato:
java -jar plantuml.jar -tpng diagrama_clases.puml
```

### Generar SVG (vectorial)
```powershell
java -jar plantuml.jar -tsvg diagrama_clases.puml
```

### Generar PDF
```powershell
java -jar plantuml.jar -tpdf diagrama_clases.puml
```

---

## 📊 Contenido del Diagrama

El diagrama de clases incluye:

### 🎯 Capas del Sistema
1. **Entidades (Entities)**
   - Pedido, Envío
   - Enumeraciones: EstadoPedido, EstadoEnvio, EmpresaEnvio, TipoEnvio

2. **Capa DAO (Data Access Objects)**
   - GenericDAO (interfaz)
   - PedidoDAO, EnvioDAO

3. **Capa de Servicios (Services)**
   - GenericService, GenericPedidosService, GenericEnviosService
   - PedidoServiceImpl, EnvioServiceImpl

4. **Capa de Configuración (Config)**
   - DatabaseConnection
   - TransactionManager
   - ApplicationConfig

5. **Capa UI (User Interface)**
   - Controllers: AppMenu, MenuHandler, PedidoConsoleController, EnvioConsoleController
   - Input: InputReader, ConsoleInputReader, MenuDisplay
   - Output: PedidoPrinter, EnvioPrinter
   - Utils: Paginador

6. **Excepciones (Exceptions)**
   - CreacionEntityException
   - ConsultaEntityException
   - ActualizacionEntityException
   - EliminacionEntityException

### 🔗 Relaciones Principales
- **Composición**: Pedido tiene Envío (1:1)
- **Herencia**: Jerarquía de interfaces y excepciones
- **Dependencia**: Services → DAOs → DatabaseConnection
- **Implementación**: Clases concretas implementan interfaces

---

## 🎨 Personalizar el Diagrama

### Editar Mermaid
1. Abre `DIAGRAMA_CLASES.md`
2. Busca el bloque ```mermaid
3. Edita según la sintaxis de Mermaid: https://mermaid.js.org/syntax/classDiagram.html

### Editar PlantUML
1. Abre `diagrama_clases.puml`
2. Edita según la sintaxis de PlantUML: https://plantuml.com/class-diagram
3. Regenera la imagen

---

## 💡 Uso Rápido con Docker (PlantUML)

Si tienes Docker instalado:

```powershell
docker run --rm -v ${PWD}:/data plantuml/plantuml diagrama_clases.puml
```

Esto genera la imagen en el mismo directorio.

---

## 🆘 Soporte

Para más información sobre los formatos:
- **Mermaid**: https://mermaid.js.org/
- **PlantUML**: https://plantuml.com/

---

## 📖 Recursos Adicionales

### Sintaxis y Tutoriales
- [Mermaid Class Diagram Syntax](https://mermaid.js.org/syntax/classDiagram.html)
- [PlantUML Class Diagram Guide](https://plantuml.com/class-diagram)
- [UML Class Diagram Tutorial](https://www.visual-paradigm.com/guide/uml-unified-modeling-language/uml-class-diagram-tutorial/)

### Herramientas Online
- [Mermaid Live Editor](https://mermaid.live/) - Editor interactivo de Mermaid
- [PlantUML Online](http://www.plantuml.com/plantuml/) - Generador online de PlantUML
- [Draw.io](https://app.diagrams.net/) - Editor gráfico de diagramas UML

---

**Grupo 175 - TPI Final Programación 2**
**UTN - Universidad Tecnológica Nacional**


