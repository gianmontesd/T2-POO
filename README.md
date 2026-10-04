# Sistema de Administración de Certificados — Peru Consulting SAC

Proyecto Final de **Técnicas de Programación Orientada a Objetos** (Grupo 5).
Aplicación de escritorio en Java 17 con interfaz **Swing** y base de datos **MySQL**,
que automatiza el registro de alumnos, cursos, empresas, inscripciones, notas y la
emisión de certificados en PDF.

## Requisitos

- JDK 17 o superior (probado con OpenJDK 25)
- MySQL 5.7 o superior, y MariaDB 10 o superior.
- Única dependencia externa: el conector JDBC, ya incluido en `lib/`

## Puesta en marcha

### 1. Crear la base de datos

```bash
mysql -u root < sql/01_esquema.sql
mysql -u root < sql/02_datos_demo.sql
```

El primer script crea la base `pcsac_certificados` con 7 tablas y 7 claves foráneas.
El segundo carga datos de prueba: 3 empresas mineras, 4 cursos, 3 instructores,
7 trabajadores, 3 capacitaciones y 7 inscripciones con sus notas.

Si su MySQL usa otro usuario o contraseña, copie `db.properties.ejemplo` como
`db.properties` y ajuste los valores. Sin ese archivo se usa `root` sin contraseña
en `localhost:3306`.

### 2A. Ejecutar desde el editor (VS Code o Cursor)

1. Instalar la extensión **Extension Pack for Java** (el editor la sugiere al abrir).
2. Abrir **esta carpeta** (`codigo-java`) como carpeta raíz del editor.
3. Pulsar **F5** y elegir una de las dos configuraciones:
   - *Interfaz gráfica (Main)* — abre la aplicación.
   - *Escenario de demostración (consola)* — recorre los 10 RF por consola.

La carpeta `.vscode` ya declara `src` como fuentes, `bin` como salida y `lib/*.jar`
como librería referenciada.

### 2B. Ejecutar desde la terminal

```bash
javac -encoding UTF-8 -cp "lib/mysql-connector-j-9.1.0.jar" -d out $(find src -name "*.java")
java -cp "out:lib/mysql-connector-j-9.1.0.jar" pe.pcsac.app.Main        # interfaz gráfica
java -cp "out:lib/mysql-connector-j-9.1.0.jar" pe.pcsac.app.Main demo   # escenario por consola
```

En Windows (PowerShell) el separador del classpath es `;` en lugar de `:`.

## Arquitectura

```
pe.pcsac.modelo     7 entidades + 1 enum, con atributos private y setters validados
pe.pcsac.dao        ConexionDB + 7 DAO; todo el SQL vive aquí y usa PreparedStatement
pe.pcsac.servicio   reglas de negocio, importación de CSV y generación del PDF
pe.pcsac.vista      5 ventanas Swing; no contienen SQL ni lógica de negocio
pe.pcsac.app        Main (GUI o demo) y DemoRequerimientos
```

La regla que ordena el proyecto: **las ventanas no hablan con la base de datos**.
Piden a la capa de servicio, que a su vez usa los DAO. Ninguna sentencia SQL aparece
fuera de `pe.pcsac.dao`.

## Dónde se implementa cada requerimiento

| RF | Dónde |
|---|---|
| RF-01 Registrar alumnos | `AlumnoDAO`, `VentanaAlumnos` |
| RF-02 Registrar cursos | `CursoDAO`, `VentanaCapacitaciones` |
| RF-03 Registrar empresas | `EmpresaDAO` |
| RF-04 Inscribir sin duplicados | `InscripcionServicio.inscribir()`, restricción `uq_ins_alumno_cap` |
| RF-05 Inscripción con voucher | `Inscripcion.voucher`, `VentanaInscripciones` |
| RF-06 Notas manuales o por CSV | `InscripcionServicio.registrarNota()`, `ImportadorNotasCsv` |
| RF-07 Estado automático | `Inscripcion.registrarNota()` compara con la nota mínima |
| RF-08 Consultar certificados | `CertificadoServicio.listar()`, `VentanaCertificados` |
| RF-09 Generar certificado | `CertificadoServicio.emitir()`, `GeneradorPdf` |
| RF-10 Certificados por empresa | `CertificadoServicio.listarPorEmpresa()` |

## Certificados

Los PDF se escriben en la carpeta `certificados/`, que se crea sola en la primera
emisión. El generador compone el PDF directamente, sin librerías externas.
