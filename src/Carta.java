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

    /**
     * Devuelve el palo de la carta (CORAZONES, DIAMANTES, TREBOLES o PICAS)
     */
    public Palo getPalo() {
        return palo;
    }

    /**
     * Devuelve el valor numérico de la carta (2-10 para cartas numéricas, 11-J, 12-Q, 13-K, 14-A)
     */
    public int getValor() {
        return valor;
    }

    /**
     * Devuelve la representación textual del valor (convierte 11-14 en J, Q, K, A)
     */
    public String getNombreValor() {
        return switch (valor) {
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            case 14 -> "A";
            default -> String.valueOf(valor);
        };
    }

    /**
     * Determina el color de la carta: Rojo para CORAZONES/DIAMANTES, Negro para TREBOLES/PICAS
     */
    public String getColor() {
        return (palo == Palo.CORAZONES || palo == Palo.DIAMANTES) ? "Rojo" : "Negro";
    }

    /**
     * Formato de representación de la carta: "VALOR_palo" (ej: "A_corazones", "10_treboles")
     */
    @Override
    public String toString() {
        return getNombreValor() + "_" + palo.name().toLowerCase();
    }

    /**
     * Compara cartas por su valor numérico (para ordenación)
     *
     * @return negativo si esta carta es menor, 0 si igual, positivo si mayor
     */
    @Override
    public int compareTo(Carta otra) {
        return Integer.compare(this.valor, otra.valor);
    }
}
