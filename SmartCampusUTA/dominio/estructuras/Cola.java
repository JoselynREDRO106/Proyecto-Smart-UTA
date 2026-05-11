package dominio.estructuras;

import java.util.NoSuchElementException;

public class Cola<T> {
    private final ListaSimple<T> datos = new ListaSimple<>();

    public void encolar(T valor) {
        datos.agregarFinal(valor);
    }

    public T desencolar() {
        if (datos.estaVacia()) {
            throw new NoSuchElementException("Cola vacia");
        }
        return datos.eliminarInicio();
    }

    public boolean estaVacia() {
        return datos.estaVacia();
    }

    public int tamano() {
        return datos.tamano();
    }
}
