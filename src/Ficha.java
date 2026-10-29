public class Ficha {

    private final int ladoA;
    private final int ladoB;

    public Ficha(int ladoA, int ladoB) {
        if (ladoA < 0 || ladoA > 6 || ladoB < 0 || ladoB > 6) {
            throw new IllegalArgumentException("Los puntos de la ficha deben estar comprendidos entre 0 y 6");
        }
        this.ladoA = ladoA;
        this.ladoB = ladoB;
    }

    public int getLadoA() {
        return ladoA;
    }
    public int getLadoB() {
        return ladoB;
    }

    public int getPuntosTotales() {
        return ladoA + ladoB;
    }

    public Ficha invertir() {
        return new Ficha(ladoB, ladoA);
    }

    @Override
    public String toString() {
        return "[" + ladoA + "|" + ladoB + "]";
    }
}
