// Solo guarda datos, así que no tiene funciones. Todos son val porque un ticket emitido no puede cambiar
class Ticket(
    val numero: Int,          // número de ticket: 1, 2, 3...
    val paciente: Paciente,   // a quién se atendió
    val minutos: Int,         // cuánto tiempo estuvo
    val monto: Double         // cuánto pagó
)