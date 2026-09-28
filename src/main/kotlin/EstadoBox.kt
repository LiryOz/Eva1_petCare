// Una "sealed class" (clase sellada) es una clase cuyos hijos son SOLO
// los que escribo aquí adentro. Nadie puede inventar otro estado
// ¿Para qué me sirve? Cuando uso un "when" con el estado, Kotlin revisa
// que yo haya puesto los 4 casos. Si se me olvida uno, no compila
// Eso cumple lo que pide el caso: "toda lógica que dependa del estado
// debe contemplar los cuatro estados"
sealed class EstadoBox {

    // Libre no necesita guardar ningún dato. Por eso lo hago "object"
    // que significa que existe uno solo y no hay que crearlo con paréntesis
    object Libre : EstadoBox()

    // En Atención tiene que saber qué paciente está en el box
    class EnAtencion(val paciente: Paciente) : EstadoBox()

    // En Proceso es el estado de la operación asíncrona
    // Guardo el motivo: "Registrando entrada" o "Calculando tarifa"
    class EnProceso(val motivo: String) : EstadoBox()

    // Fuera De Servicio guarda por qué el box está inhabilitado
    class FueraDeServicio(val motivo: String) : EstadoBox()
}