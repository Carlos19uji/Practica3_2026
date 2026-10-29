
import java.util.*;

public class JuegoDomino {
    private final Stack<Ficha> pozo;
    private final Deque<Ficha> cadena;
    private final Jugador[] jugadores;
    private final int NJUGADORES;
    private final int NFICHAS;
    private int turno;

    public JuegoDomino (String nombresJugadores[], int nFichas) {
        if (nombresJugadores == null ||nombresJugadores.length < 2 || nombresJugadores.length > 4){
            throw new IllegalArgumentException("El número de jugadores debe estar entre 2 y 4.");
        }

        this.NJUGADORES = nombresJugadores.length;
        this.NFICHAS = nFichas;
        this.pozo = new Stack<>();
        this.cadena = new LinkedList<>();
        this.jugadores = new Jugador[NJUGADORES];
        for(int i=0; i<NJUGADORES; i++){
            this.jugadores[i] = new Jugador(nombresJugadores[i]);
        }
    }
        
    public JuegoDomino (String nombresJugadores[]) {
        this(nombresJugadores, 7);
    }

    public void prepararPartida() {
        pozo.clear();
        cadena.clear();
        for (Jugador j : jugadores) {
            j.getFichas().clear();
        }
        crearYMezclarFichas();
        repartirFichasIniciales();
        turno = 0;
    }

    public void crearYMezclarFichas() {
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                pozo.add(new Ficha(i, j));
            }
        }
        Collections.shuffle(pozo);
    }

    private void repartirFichasIniciales() {
        for(Jugador jugador: jugadores){
            for (int i=0; i<NFICHAS; i++){
                if(!pozo.isEmpty()){
                    jugador.recibirFicha(pozo.pop());
                }
            }
        }
    }

    public void jugar() {
        System.out.println("--- ESTADO INICIAL ---");
        for (int j=0; j<NJUGADORES; j++)
            System.out.println(jugadores[j]);

        System.out.println("Fichas en pozo (durmientes): " + pozo.size());
        System.out.println("----------------------\n");

        // Jugador 1 empieza colocando su primera ficha boca arriba
        Ficha primeraFicha = jugadores[turno].getFichas().poll();
        cadena.add(primeraFicha);
        System.out.println("\n--- TURNO DE " + jugadores[turno].getNombre().toUpperCase() + " ---");
        System.out.println(jugadores[turno].getNombre() + " inicia la cadena con la ficha " + primeraFicha);
        mostrarCadena();

        this.turno = (this.turno+1)%this.NJUGADORES; //pasa al siguiente jugador
        Jugador actual = this.jugadores[turno];
        int pasesConsecutivos = 0;

        boolean finJuego = false;
        while (!finJuego) {
            System.out.println("\n--- TURNO DE " + actual.getNombre().toUpperCase() + " ---");
            boolean colocoFicha = jugarTurno(actual);

            if (!actual.tieneFichas()) {
                System.out.println("\n¡" + actual.getNombre() + " se ha quedado sin fichas!");
                finJuego = true;
            }
            else {
                if (colocoFicha) {
                    pasesConsecutivos = 0;
                } else {
                    pasesConsecutivos++;
                    System.out.println(actual.getNombre() + " NO ha podido colocar ninguna ficha y pasa.");
                    if (pasesConsecutivos == 2) {
                        System.out.println("\n¡Ambos jugadores han pasado consecutivamente! El juego está cerrado.");
                        finJuego = true;
                    }
                }
            }

            mostrarCadena();

            if (!finJuego) {
                // Cambiar turno
                this.turno= (this.turno+1)%NJUGADORES;
                actual = this.jugadores[turno];
            }
        }
        determinarGanador();
    }

    public Jugador getJugadorActual(){
        return jugadores[turno];
    }


    public boolean jugarTurno(Jugador jugador) {
        Ficha f = null;
        if (cadena.isEmpty()) {
            f = jugador.getFichas().poll();
        } else {
            f = jugador.buscarFicha(cadena.peekFirst().getLadoA(),
                    cadena.peekLast().getLadoB());
        }
        if (f != null) {
            intentarColocar(f);
            System.out.println(jugador.getNombre() + " coloca la ficha " + f);
            return true;
        }

        while (!pozo.isEmpty()) {
            Ficha robada = pozo.pop();
            System.out.println(jugador.getNombre() + " roba la ficha " + robada);
            if (intentarColocar(robada)) {
                return true;
            }
            jugador.recibirFicha(robada);
        }

        return false;
    }

    public boolean intentarColocar(Ficha f) {
        if (f == null) {
            return false;
        }

        if (cadena.isEmpty()) {
            cadena.add(f);
            return true;
        }

        int valIzq = cadena.peekFirst().getLadoA();
        int valDer = cadena.peekLast().getLadoB();

        boolean coincideIzq = (f.getLadoA() == valIzq || f.getLadoB() == valIzq);
        boolean coincideDer = (f.getLadoA() == valDer || f.getLadoB() == valDer);

        if (coincideIzq && coincideDer) {
            if (valIzq < valDer) {
                colocarEnIzquierda(f, valIzq);
            } else {
                colocarEnDerecha(f, valDer);
            }
            return true;
        }
        if (coincideIzq) {
            colocarEnIzquierda(f, valIzq);
            return true;
        }
        if (coincideDer) {
            colocarEnDerecha(f, valDer);
            return true;
        }
        return false;
    }

    public void colocarEnIzquierda(Ficha f, int valorExtremo) {
        if (f.getLadoB() != valorExtremo){
            f = f.invertir();
        }
        cadena.addFirst(f);
    }

    public void colocarEnDerecha(Ficha f, int valorExtremo) {
        if (f.getLadoA() != valorExtremo){
            f = f.invertir();
        }
        cadena.addLast(f);
    }

    public void mostrarCadena() {
        StringBuilder sb = new StringBuilder("CADENA ACTUAL: ");
        for (Ficha f : cadena) {
            sb.append(f.toString()).append(" ");
        }
        System.out.println(sb.toString());
    }

    public Jugador determinarGanador() {
        Jugador ganador = null;
        int minPuntos = Integer.MAX_VALUE;
        int minFichas = Integer.MAX_VALUE;
        boolean empate = false;

        for (Jugador j : jugadores) {
            int puntos = j.calcularPuntosRestantes();
            int nFichas = j.getFichas().size();
            System.out.println(j.getNombre() + ": " + puntos + " puntos restantes (" + nFichas + " fichas)");

            if (puntos < minPuntos || (puntos == minPuntos && nFichas < minFichas)) {
                ganador = j;
                minPuntos = puntos;
                minFichas = nFichas;
                empate = false;
            } else if (puntos == minPuntos && nFichas == minFichas) {
                empate = true;
            }
        }

        if (empate) {
            System.out.println("Empate: no hay ganador.");
            return null;
        }
        System.out.println("Ganador: " + ganador.getNombre());
        return ganador;
    }

    public Jugador[] getJugadores() {
        return this.jugadores;
    }
    public int fichasEnPozo() {
        return this.pozo.size();
    }
    public Object[] getCadena() {
        return this.cadena.toArray();
    }
    public Stack<Ficha> getPozo(){
        return this.pozo;
    }
}
