public class Carta implements Comparable<Carta> {
    public enum Palo {
        CORAZONES, DIAMANTES, TREBOLES, PICAS
    }

    private final Palo palo;
    private final int valor;

    public Carta(int valor, Palo palo) {
        this.valor = valor;
        this.palo = palo;
    }

    public Palo getPalo() {
        return palo;
    }

    public int getValor() {
        return valor;
    }

    public String getNombreValor() {
        return switch (valor) {
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            case 14 -> "A";
            default -> String.valueOf(valor);
        };
    }

    public String getColor() {
        return (palo == Palo.CORAZONES || palo == Palo.DIAMANTES) ? "Rojo" : "Negro";
    }

    @Override
    public String toString() {
        return getNombreValor() + "_" + palo.name().toLowerCase();
    }

    //Implementación del método compareTo que es util para ordenar mis cartas.
    @Override
    public int compareTo(Carta otra) {
        return Integer.compare(this.valor, otra.valor);
    }
}
