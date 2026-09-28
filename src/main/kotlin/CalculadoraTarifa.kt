// Guardo los porcentajes en variables con nombre, así se entiende qué es cada número
val IVA = 0.19
val DESCUENTO_MUNICIPAL = 0.50

fun calcularMontoFinal(paciente: Paciente, minutos: Int): Double {

    // PASO 1: aquí pasa el POLIMORFISMO
    // No me importa si el paciente es Canino, Felino o Exotico
    // llamo calcularCosto() y cada uno usa SU propia regla
    val costo = paciente.calcularCosto(minutos)

    // PASO 2: le sumo el IVA
    val costoConIva = costo + (costo * IVA)

    // PASO 3: si es municipal, le descuento el 50% al monto con IVA
    // Uso var porque este valor puede cambiar dentro del if
    var montoFinal = costoConIva
    if (paciente.tipoDueno == "municipal") {
        montoFinal = costoConIva - (costoConIva * DESCUENTO_MUNICIPAL)
    }

    // VALIDACIÓN: el caso dice que la tarifa no puede ser negativa ni cero
    // La única excepción es el michi con menos de 20 minutos, que sí puede pagar $0
    // Aquí uso operadores lógicos: && (Y)
    val gatoGratis = paciente.tipo == "Felino" && minutos >= 0 && minutos < 20

    if (montoFinal < 0) {
        throw IllegalArgumentException(
            "Error de datos: la tarifa de ${paciente.codigo} salió negativa. No se puede cobrar."
        )
    } else if (montoFinal == 0.0 && !gatoGratis) {
        throw IllegalArgumentException(
            "Error de datos: la tarifa de ${paciente.codigo} salió en \$0 y eso no corresponde. Revisa el tiempo de atención."
        )
    }

    // Si no entró a ningún error, el monto está bien
    return montoFinal
}