// Importo LocalDateTime porque se pide guardar la fecha y hora exacta de ingreso
import java.time.LocalDateTime


// Le pongo "open" porque si no, Kotlin no me deja heredar de esta clase
// Todos los datos son "val" porque no cambian una vez registrados
open class Paciente(
    val codigo: String,        // el código de atención, por ejemplo "CA12CD"
    val nombre: String,        // el nombre de la mascota, por ejemplo "Max"
    val especie: String,       // la raza o especie, por ejemplo "Pastor Aleman"
    val tipoDueno: String,     // "particular", "convenio" o "municipal"
    val tipo: String,          // "Canino", "Felino" o "Exotico"
    val tarifaPorHora: Double  // uso Double porque es plata (la pauta dice que Int para un precio es error)
) {

    // Aquí guardo la fecha y hora del momento en que se crea el paciente
    // Es val porque no puede cambiar
    val fechaIngreso: LocalDateTime = LocalDateTime.now()

    // Este es el cálculo general que hereda cada hijo: horas por tarifa
    // Le pongo "open" porque, el metodo lo pone el papá
    // también tiene que ser open para que los hijos lo puedan sobrescribir
    open fun calcularCosto(minutos: Int): Double {
        // Divido por 60.0 y no por 60, porque 75 / 60 me daría 1 (división entera)
        // en cambio 75 / 60.0 me da 1.25, que es lo correcto.
        val horas = minutos / 60.0
        return horas * tarifaPorHora
    }

    // Esta función arma un texto con los datos del paciente para mostrarlo en pantalla
    // También es open, porque el Exotico la va a cambiar
    open fun describir(): String {
        return "$tipo | $codigo | $nombre ($especie) | dueño: $tipoDueno"
    }
}

// ---------- HIJO 1: CANINO ----------
// Con : Paciente "xxx" digo que Canino hereda de Paciente
// Al papá le paso los 4 datos que recibo, más los 2 que son fijos para todos
// los perros: el tipo "Canino" y la tarifa de 12000.0 por hora
class Canino(codigo: String, nombre: String, especie: String, tipoDueno: String) :
    Paciente(codigo, nombre, especie, tipoDueno, "Canino", 12000.0) {

    // Con "override" reemplazo el cálculo del papá por la regla del perro
    override fun calcularCosto(minutos: Int): Double {
        // Con super.calcularCosto() le pido al papá que haga el cálculo normal
        // así no tengo que escribirlo de nuevo
        val costo = super.calcularCosto(minutos)

        // Regla: si el dueño tiene convenio, le hago un 20% de descuento
        // Cobrar el 80% (0.80) es lo mismo que descontar el 20%
        if (tipoDueno == "convenio") {
            return costo * 0.80
        } else {
            return costo
        }
    }
}

// ---------- HIJO 2: FELINO ----------
// Igual que el perro, pero con tarifa de 9000.0
class Felino(codigo: String, nombre: String, especie: String, tipoDueno: String) :
    Paciente(codigo, nombre, especie, tipoDueno, "Felino", 9000.0) {

    override fun calcularCosto(minutos: Int): Double {
        // Regla: si estuvo menos de 20 minutos, no se cobra nada
        // sin importar el tipo de dueño
        if (minutos < 20) {
            return 0.0
        } else {
            return super.calcularCosto(minutos)
        }
    }
}

// ---------- HIJO 3: EXOTICO ----------
// El exótico tiene un dato extra que los otros no tienen: si es silvestre o no
// Le pongo "val" porque es un dato nuevo de esta clase y no cambia
class Exotico(
    codigo: String, nombre: String, especie: String, tipoDueno: String,
    val esSilvestre: Boolean
) : Paciente(codigo, nombre, especie, tipoDueno, "Exotico", 20000.0) {

    override fun calcularCosto(minutos: Int): Double {
        val costo = super.calcularCosto(minutos)

        // Regla del exótico: si es silvestre, le sumo un 30% de recargo
        // Cobrar el 130% (1.30) es lo mismo que sumar el 30%
        if (esSilvestre) {
            return costo * 1.30
        } else {
            return costo
        }
    }

    // se pide que en pantalla se vea si es silvestre o no
    override fun describir(): String {
        var descripcion = super.describir()
        if (esSilvestre) {
            descripcion += " | silvestre: sí"
        } else {
            descripcion += " | silvestre: no"
        }
        return descripcion
    }
}