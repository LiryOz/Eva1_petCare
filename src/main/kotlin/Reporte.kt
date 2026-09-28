// Convierto un monto en texto de pesos sin decimales: 14280.0 → "$14280"
// "%.0f" significa "número con 0 decimales".
fun formatearPesos(monto: Double): String {
    return "$" + String.format("%.0f", monto)
}

// Convierto un estado en texto. Otra vez uso when con los 4 estados de la sealed class
fun describirEstado(estado: EstadoBox): String {
    return when (estado) {
        is EstadoBox.Libre -> "Libre"
        is EstadoBox.EnAtencion -> "En atención: ${estado.paciente.nombre} (${estado.paciente.codigo})"
        is EstadoBox.EnProceso -> "En proceso: ${estado.motivo}"
        is EstadoBox.FueraDeServicio -> "Fuera de servicio: ${estado.motivo}"
    }
}

// Muestro todos los boxes con un ciclo for
fun mostrarEstadoBoxes(sistema: SistemaPetCare) {
    println("\n----- Estado de los boxes -----")   // "\n" es un salto de línea
    for (box in sistema.boxes) {
        println("Box ${box.numero}: ${describirEstado(box.estado)}")
    }
}

// Muestro un ticket recién emitido
fun mostrarTicket(ticket: Ticket) {
    println("   TICKET #${ticket.numero}")
    println("   ${ticket.paciente.describir()}")
    println("   Tiempo: ${ticket.minutos} min | Total pagado: ${formatearPesos(ticket.monto)}")
}

// Muestro las respuestas a las preguntas del negocio
fun mostrarConsultas(sistema: SistemaPetCare) {
    println("\n===== CONSULTAS DEL NEGOCIO =====")
    println("Boxes disponibles ahora: ${sistema.boxesDisponibles()}")

    println("Pacientes con convenio:")
    // forEach recorre la lista; "it" es cada paciente
    sistema.pacientesConvenio().forEach { println("   - ${it.describir()}") }

    println("Ingreso promedio por paciente: ${formatearPesos(sistema.ingresoPromedio())}")
    println("Códigos que finalizaron: ${sistema.codigosFinalizados()}")

    // Esta consulta puede devolver null, así que reviso antes de usarla
    val masTiempo = sistema.ticketConMasTiempo()
    if (masTiempo != null) {
        println("Paciente con más tiempo: ${masTiempo.paciente.nombre} (${masTiempo.paciente.codigo}) con ${masTiempo.minutos} min")
    } else {
        println("Paciente con más tiempo: todavía no hay atenciones")
    }
}

// Muestro el reporte de cierre del turno
fun mostrarReporteCierre(sistema: SistemaPetCare) {
    println("\n========== REPORTE DE CIERRE DE TURNO - ${sistema.nombre} ==========")

    // Una línea por cada paciente atendido: ticket, tipo, código, tiempo y monto
    for (ticket in sistema.historial) {
        println(
            "Ticket #${ticket.numero} | ${ticket.paciente.tipo} | ${ticket.paciente.codigo} | " +
                    "${ticket.minutos} min | ${formatearPesos(ticket.monto)}"
        )
    }

    println("--------------------------------------------------")
    println("Total recaudado: ${formatearPesos(sistema.totalRecaudado)}")

    // Recorro el Map de lo recaudado por tipo
    sistema.recaudadoPorTipo.forEach { (tipo, monto) ->
        println("   Recaudado $tipo: ${formatearPesos(monto)}")
    }

    println("Pacientes atendidos: ${sistema.historial.size}")
    println("Ingreso promedio: ${formatearPesos(sistema.ingresoPromedio())}")
    println("Tipo con más ingresos: ${sistema.tipoConMasIngresos()}")
    println("Boxes disponibles al cierre: ${sistema.boxesDisponibles()}")
}