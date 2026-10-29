

import java.util.LinkedList;
import java.util.Queue;

public class Jugador {

    private final String nombre;
    private final Queue<Ficha> fichas;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.fichas = new LinkedList<>();
    }

    public String getNombre() {
        return nombre;
    }
    public Queue<Ficha> getFichas() {
       return fichas;
    }

    public void recibirFicha(Ficha f) {
        if (f != null){
            fichas.offer(f);
        }
    }

    public Ficha buscarFicha(int valor1, int valor2) {
        int totalFichasRevisar = fichas.size();

        for (int i = 0; i < totalFichasRevisar; i++) {
            Ficha f = fichas.poll();
            if (f.getLadoA() == valor1 || f.getLadoB() == valor1 ||
                    f.getLadoA() == valor2 || f.getLadoB() == valor2) {
                return f;
            }
            fichas.offer(f);
        }
        return null;
    }


    public boolean tieneFichas() {
        return !fichas.isEmpty();
    }

    public int calcularPuntosRestantes() {
        int suma = 0;
        for (Ficha f:fichas){
            suma += f.getPuntosTotales();
        }
        return suma;
    }

    @Override
    public String toString() {
        return nombre + " " + fichas;
    }
}
