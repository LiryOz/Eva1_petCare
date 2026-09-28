// Cada box tiene un número que nunca cambia (val)
class Box(val numero: Int) {

    // El estado sí cambia (de Libre a En Proceso, a En Atención), por eso es var
    // Todos los boxes parten libres
    var estado: EstadoBox = EstadoBox.Libre
}

// Esta función me crea la lista de boxes enumerados
// Uso un ciclo for con rango
fun crearBoxes(cantidad: Int): MutableList<Box> {
    val lista = mutableListOf<Box>()      // parto con una lista vacía
    for (numero in 1..cantidad) {         // número va tomando 1, 2, 3... hasta cantidad
        lista.add(Box(numero))            // en cada vuelta creo un box y lo agrego
    }
    return lista
}