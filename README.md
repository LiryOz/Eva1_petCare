# PetCare - Sistema de Gestión de Boxes Veterinarios

## Introducción

PetCare es una aplicación de consola desarrollada en Kotlin que simula la gestión
de una clínica veterinaria con 10 boxes de atención. El sistema permite registrar
la entrada y salida de pacientes (caninos, felinos y exóticos), calcular el monto
que paga cada dueño según las reglas de cobro de cada tipo de paciente, y entregar
un reporte de cierre con lo recaudado durante el turno.

El proyecto fue desarrollado para la Evaluación Parcial 1 de la asignatura
**DSY1105 - Desarrollo de Aplicaciones Móviles** (Duoc UC), y aplica:

- Variables, tipos de datos, operadores y condicionales (`if`, `when`).
- Programación orientada a objetos: una clase base (`open class Paciente`) con tres
  clases hijas que sobrescriben el cálculo del costo (herencia y polimorfismo).
- Tareas asíncronas con corrutinas (`suspend fun`, `delay`, `runBlocking`) para simular
  la espera de los sensores de entrada y salida.
- Una `sealed class` para los cuatro estados de un box: Libre, En atención,
  En proceso y Fuera de servicio.

## Requisitos

- IntelliJ IDEA (Community o Ultimate).
- JDK 17 o superior.
- Kotlin 1.9 o superior.
- Conexión a internet la primera vez, para que Gradle descargue la librería de corrutinas.

## Instrucciones para ejecutar

1. **Abrir el proyecto:** en IntelliJ IDEA, ir a `File → Open` y seleccionar la carpeta
   del proyecto (la que contiene `build.gradle.kts`).
2. **Cargar Gradle:** esperar a que IntelliJ termine de sincronizar el proyecto.
   Si aparece el ícono del elefante con flechas (*Load Gradle Changes*), hacer clic en él.
   Esto descarga la dependencia `kotlinx-coroutines-core`.
3. **Abrir `Main.kt`:** está en `src/main/kotlin/Main.kt`.
4. **Ejecutar:** hacer clic en el triángulo verde ▶ que aparece al lado de `fun main()`
   y elegir **Run 'MainKt'**.
5. **Ver el resultado:** la salida aparece en la consola (pestaña *Run*, abajo).

> **Nota:** la ejecución completa tarda aproximadamente un minuto


## Autor

- **Nombre:** Paola Pozo
- **Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles
