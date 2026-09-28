// Necesito delay para simular la espera
import kotlinx.coroutines.delay


class SistemaPetCare(val nombre: String, val capacidad: Int) {

    // ---------- MIS DATOS ----------

    // La lista de boxes (del 1 al 10 en el sistema)
    val boxes = crearBoxes(capacidad)

    // El historial parte vacío y le voy agregando tickets. Por eso es mutable
    val historial = mutableListOf<Ticket>()

    // Lo recaudado en el turno. Es var porque va sumando
    var totalRecaudado = 0.0

    // Un Map (clave → valor) para lo recaudado por tipo de paciente
    val recaudadoPorTipo = mutableMapOf("Canino" to 0.0, "Felino" to 0.0, "Exotico" to 0.0)

    // Contador para numerar los tickets. Lo dejo private porque solo lo uso aquí adentro
    private var contadorTickets = 0

    // ---------- FUNCIONES CON EL ESTADO DEL BOX (when + sealed class) ----------

    // Me dice si un box está libre. Con el when reviso los 4 estados
    // "is" pregunta "¿el estado es de este tipo?"
    fun estaLibre(box: Box): Boolean {
        return when (box.estado) {
            is EstadoBox.Libre -> true
            is EstadoBox.EnAtencion -> false
            is EstadoBox.EnProceso -> false
            is EstadoBox.FueraDeServicio -> false
        }
    }

    // Me devuelve el paciente que está en un box, o null si no hay ninguno
    // El "?" en "Paciente?" significa que puede venir vacío
    fun pacienteDelBox(box: Box): Paciente? {
        // Guardo el estado en una val para que Kotlin me deje usar estado.paciente adentro del when
        val estado = box.estado
        return when (estado) {
            is EstadoBox.EnAtencion -> estado.paciente
            is EstadoBox.Libre -> null
            is EstadoBox.EnProceso -> null
            is EstadoBox.FueraDeServicio -> null
        }
    }

    // Deja un box fuera de servicio, guardando el motivo
    fun inhabilitarBox(numero: Int, motivo: String) {
        // find busca el primer box con ese número (o me da null si no existe)
        val box = boxes.find { it.numero == numero }
        // Solo lo inhabilito si existe Y está libre (no saco a un paciente que se está atendiendo)
        if (box != null && estaLibre(box)) {
            box.estado = EstadoBox.FueraDeServicio(motivo)
        }
    }

    // ---------- ENTRADA ASÍNCRONA (suspend fun + delay) ----------
    // "suspend" significa que la función puede quedar en PAUSA (con delay) sin congelar el resto del programa
    // Devuelvo el número de box donde quedó el paciente: ese es el resultado de la corrutina
    suspend fun registrarEntrada(paciente: Paciente): Int {

        // 1. Antes de cualquier cosa, reviso que el código y el tipo de dueño estén bien
        //    Si están mal, validarPaciente lanza el error y no se registra nada
        validarPaciente(paciente)

        // 2. Busco el primer box libre
        val box = boxes.find { estaLibre(it) }
        if (box == null) {
            throw Exception("$nombre está lleno: no hay boxes libres para ${paciente.nombre}. No se registró la entrada.")
        }

        // 3. Mientras espero, el box queda "En proceso"
        //    Lo marco antes de esperar, para que otra entrada no me quite este box
        box.estado = EstadoBox.EnProceso("Registrando entrada")

        // 4. Espero 3 segundos (3000 milisegundos), como pide el caso
        delay(3000)

        // 5. El programa confirmó: el box queda con el paciente
        box.estado = EstadoBox.EnAtencion(paciente)
        return box.numero
    }

    // ---------- SALIDA ASÍNCRONA ----------
    // Recibo el código y los minutos que estuvo, y devuelvo el ticket
    suspend fun registrarSalida(codigo: String, minutos: Int): Ticket {

        // 1. Busco el box donde está el paciente con ese código
        //    El "?." significa: "si hay paciente, dame su código; si es null, no te caigas (por favor)"
        val box = boxes.find { pacienteDelBox(it)?.codigo == codigo }
        if (box == null) {
            throw Exception("No se encontró ningún paciente con código '$codigo' en el sistema.")
        }

        // Saco el paciente de ese box. Kotlin me pide revisar otra vez que no sea null
        val paciente = pacienteDelBox(box)
        if (paciente == null) {
            throw Exception("No se encontró ningún paciente con código '$codigo' en el sistema.")
        }

        // 2. Primero calculo el monto. Si la tarifa sale inválida, aquí salta el error
        //    y el box NO cambia: el paciente sigue en su box y no se pierde nada
        val monto = calcularMontoFinal(paciente, minutos)

        // 3. Mientras proceso la salida, el box queda "En proceso". Espero 6,5 segundos
        box.estado = EstadoBox.EnProceso("Calculando tarifa")
        delay(6500)

        // 4. Emito el ticket y lo guardo en el historial
        contadorTickets = contadorTickets + 1
        val ticket = Ticket(contadorTickets, paciente, minutos, monto)
        historial.add(ticket)

        // 5. Actualizo lo recaudado: el total y el de su tipo
        totalRecaudado = totalRecaudado + monto
        val recaudadoAntes = recaudadoPorTipo[paciente.tipo]   // puede venir null
        if (recaudadoAntes != null) {
            recaudadoPorTipo[paciente.tipo] = recaudadoAntes + monto
        }

        // 6. Libero el box
        box.estado = EstadoBox.Libre
        return ticket
    }

    // ---------- CONSULTAS DEL NEGOCIO (R4) con funciones de orden superior ----------

    // ¿Cuántos boxes están disponibles?
    // filter se queda con los libres, y .size me dice cuántos quedaron
    fun boxesDisponibles(): Int {
        return boxes.filter { estaLibre(it) }.size
    }

    // ¿Qué pacientes del historial son de convenio?
    // filter se queda con los tickets de convenio, y map saca el paciente de cada ticket
    fun pacientesConvenio(): List<Paciente> {
        return historial.filter { it.paciente.tipoDueno == "convenio" }.map { it.paciente }
    }

    // ¿Cuál es el ingreso promedio por paciente?
    fun ingresoPromedio(): Double {
        // Si no hay nadie atendido, devuelvo 0 para no dividir por cero
        if (historial.isEmpty()) {
            return 0.0
        }
        // sumOf suma el monto de todos los tickets, y lo divido por cuántos son
        return historial.sumOf { it.monto } / historial.size
    }

    // ¿Cuáles son los códigos de los pacientes que ya salieron?
    // map convierte cada ticket en su código
    fun codigosFinalizados(): List<String> {
        return historial.map { it.paciente.codigo }
    }

    // ¿Qué paciente tuvo más tiempo de uso?
    // sortedBy ordena de menor a mayor por minutos, y last() me da el último (el mayor)
    fun ticketConMasTiempo(): Ticket? {
        if (historial.isEmpty()) {
            return null
        }
        return historial.sortedBy { it.minutos }.last()
    }

    // ¿Qué tipo de paciente generó más ingresos?
    // Recorro el Map con forEach
    fun tipoConMasIngresos(): String {
        var tipoGanador = "Sin datos"
        var mayorMonto = 0.0
        recaudadoPorTipo.forEach { (tipo, monto) ->
            if (monto > mayorMonto) {
                mayorMonto = monto
                tipoGanador = tipo
            }
        }
        return tipoGanador
    }
}