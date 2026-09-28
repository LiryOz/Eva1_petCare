// Importo lo necesario para las corrutinas
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() {
    // runBlocking crea el "espacio de corrutinas". Sin él no puedo llamar a funciones suspend desde main
    runBlocking {

        // Creo el sistema: se llama "PetCare" y tiene 10 boxes
        val petCare = SistemaPetCare("PetCare", 10)
        println("===== ${petCare.nombre} - Sistema de Atención de Boxes Veterinarios Miau! =====")

        // Dejo el box 10 fuera de servicio, para que se vean los 4 estados
        petCare.inhabilitarBox(10, "Mantención de la máquina de signos vitales")

        // ---------- 1. LOS PACIENTES QUE LLEGAN ----------
        // Guardo todos en una Lista. Aunque la lista es de Paciente, adentro hay
        // perros, gatos y exóticos mezclados
        val pacientes = listOf(
            Felino("FA12CD", "Liry", "Gata Romana", "particular"),
            Canino("CA99ZA", "Guinda", "Pastor Aleman", "convenio"),
            Felino("FE22TO", "Nahuel", "Gata Romana", "particular"),
            Felino("FB44RG", "Kali", "Carey", "municipal"),
            Exotico("EX64CS", "Patolin", "Pato", "particular", true),
            Exotico("EX77RG", "Pepe", "Ave Zorzal", "particular", false),
            Canino("123ABC", "Luk", "Quiltro", "particular"),   // PRUEBA: código inválido
            Felino("FE55NV", "Jill", "Calico", "Paola")            // PRUEBA: tipo de dueño inválido
        )

        // POLIMORFISMO: llamo describir() en cada uno y el Exotico
        // responde distinto (agrega si es silvestre), porque sobrescribió el metodo
        println("\n>> Pacientes que llegan hoy:")
        for (paciente in pacientes) {
            println("   ${paciente.describir()}")
        }

        // ---------- 2. ENTRADAS (asíncronas) ----------
        println("\n>> Registrando entradas...")
        // Con launch lanzo cada entrada al mismo tiempo, así el sistema no se queda
        // bloqueado esperando uno por uno (R5). Uso map para guardar todas las tareas
        val tareas = pacientes.map { paciente -> launch { ingresarPaciente(petCare, paciente) } }

        delay(500)                  // espero medio segundo...
        mostrarEstadoBoxes(petCare) // ...y muestro los boxes: se ven "En proceso"
        tareas.joinAll()            // joinAll espera a que todas las entradas terminen
        mostrarEstadoBoxes(petCare) // ahora se ven "En atención"

        // ---------- 3. SALIDAS (asíncronas, una por una) ----------
        println("\n>> Registrando salidas...")
        retirarPaciente(petCare, "CA12CD", 75)
        retirarPaciente(petCare, "CA99ZA", 180)
        retirarPaciente(petCare, "FE22TO", 18)
        retirarPaciente(petCare, "EX44RG", 120)
        retirarPaciente(petCare, "ZZ00ZZ", 30)   // PRUEBA: paciente no encontrado
        retirarPaciente(petCare, "EX77RG", 0)    // PRUEBA: tarifa $0, no corresponde
        retirarPaciente(petCare, "EX77RG", 45)   // ahora sí, con el tiempo correcto

        // ---------- 4. PRUEBA: SISTEMA SIN CAPACIDAD ----------
        probarSinCapacidad()

        // ---------- 5. CONSULTAS Y CIERRE ----------
        mostrarConsultas(petCare)
        mostrarReporteCierre(petCare)
        println("\nFin del turno. El programa terminó sin caerse.")
    }
}

// Registro la entrada de un paciente y muestro el resultado
// Es suspend porque adentro llamo a registrarEntrada, que también lo es
suspend fun ingresarPaciente(sistema: SistemaPetCare, paciente: Paciente) {
    // try: intento hacer la entrada.
    try {
        // Aquí MANEJO EL RESULTADO de la corrutina: me devuelve el número de box
        val numeroBox = sistema.registrarEntrada(paciente)
        println("[OK] ${paciente.nombre} (${paciente.codigo}) quedó en el Box $numeroBox. Ingreso: ${paciente.fechaIngreso}")
    } catch (e: Exception) {
        // catch: si algo falló, muestro el mensaje del error y el programa sigue
        // IllegalArgumentException también es un tipo de Exception, así que este catch atrapa todo.
        println("[ERROR] ${e.message}")
    }
}

// Registro la salida de un paciente y muestro su ticket
suspend fun retirarPaciente(sistema: SistemaPetCare, codigo: String, minutos: Int) {
    println("\nProcesando salida de $codigo ($minutos min)...")
    try {
        // El resultado de esta corrutina es el ticket
        val ticket = sistema.registrarSalida(codigo, minutos)
        mostrarTicket(ticket)
    } catch (e: Exception) {
        println("[ERROR] ${e.message}")
    }
}

// Pruebo el error "sistema sin capacidad" con un sistema chico de 1 solo box
suspend fun probarSinCapacidad() {
    println("\n>> Prueba: sistema lleno (sistema de prueba con 1 box)")
    val sistemaChico = SistemaPetCare("PetCare-Prueba", 1)
    ingresarPaciente(sistemaChico, Canino("PR01EB", "Sombra", "Pincher", "particular"))  // este entra
    ingresarPaciente(sistemaChico, Felino("PR02EB", "Pancho", "Domestico", "particular"))  // este no cabe
}