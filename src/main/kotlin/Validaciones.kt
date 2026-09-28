// Reviso que el código tenga el formato: 2 letras, 2 números, 2 letras (ej: CA12CD)
// Devuelvo true si está bien y false si está mal
fun codigoEsValido(codigo: String): Boolean {

    // Primero reviso que tenga exactamente 6 caracteres
    // Si no los tiene, ni sigo revisando
    if (codigo.length != 6) {
        return false
    }

    // Ahora reviso cada posición. codigo[0] es el primer carácter, codigo[1] el segundo, etc.
    // isLetter() me dice si es una letra, isDigit() me dice si es un número
    // Uso && (Y) porque las dos cosas tienen que cumplirse al mismo tiempo
    val dosLetrasAlInicio = codigo[0].isLetter() && codigo[1].isLetter()
    val dosNumerosAlMedio = codigo[2].isDigit() && codigo[3].isDigit()
    val dosLetrasAlFinal = codigo[4].isLetter() && codigo[5].isLetter()

    // El código es válido solo si se cumplen las tres partes
    return dosLetrasAlInicio && dosNumerosAlMedio && dosLetrasAlFinal
}

// Reviso que el tipo de dueño sea uno de los tres permitidos
fun tipoDuenoEsValido(tipoDueno: String): Boolean {
    // Guardo los valores permitidos en una lista
    val tiposPermitidos = listOf("particular", "convenio", "municipal")
    // contains() me dice si el texto está dentro de la lista
    return tiposPermitidos.contains(tipoDueno)
}

// Esta función junta las dos revisiones
// Si algo está mal, lanzo (throw) un error con un mensaje claro
// Uso IllegalArgumentException
// significa "me pasaron un dato que no sirve"
fun validarPaciente(paciente: Paciente) {
    // El "!" significa NO. O sea: "si el código NO es válido..."
    if (!codigoEsValido(paciente.codigo)) {
        throw IllegalArgumentException(
            "Código de atención inválido: '${paciente.codigo}'. Debe tener 2 letras, 2 números y 2 letras (ej: CA12CD)."
        )
    }
    if (!tipoDuenoEsValido(paciente.tipoDueno)) {
        throw IllegalArgumentException(
            "Tipo de dueño inválido: '${paciente.tipoDueno}'. Solo se permite particular, convenio o municipal."
        )
    }
}