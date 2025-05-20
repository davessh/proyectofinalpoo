import java.util.*;
import java.util.stream.Collectors;

public class Mano implements Comparable<Mano> {
    private final List<Carta> cartas;

    public Mano(List<Carta> cartas) {
        this.cartas = new ArrayList<>(cartas);
        Collections.sort(this.cartas);
    }

    /**
     * Añade una carta a la mano y reordena las cartas
     */
    public void agregarCarta(Carta carta) {
        cartas.add(carta);
        Collections.sort(cartas);
    }

    /**
     * Obtiene la lista de cartas de la mano
     */
    public List<Carta> getCartas() {
        return cartas;
    }

    /**
     * Muestra las cartas de la mano como cadena de texto separadas por espacios
     */
    public String mostrar() {
        return cartas.stream()
                .map(Carta::toString)
                .collect(Collectors.joining(" "));
    }

    /**
     * Calcula el valor numérico de la mano según su fuerza en poker (1=carta alta, 9=escalera color)
     */
    public int getValorMano() {
        if (esEscaleraColor()) return 9;
        if (esPoker()) return 8;
        if (esFullHouse()) return 7;
        if (esColor()) return 6;
        if (esEscalera()) return 5;
        if (esTrio()) return 4;
        if (esDoblePareja()) return 3;
        if (esPareja()) return 2;
        return 1; // Carta alta
    }

    /**
     * Compara dos manos por su valor (para ordenación)
     */
    @Override
    public int compareTo(Mano otra) {
        return Integer.compare(this.getValorMano(), otra.getValorMano());
    }

    /**
     * Comprueba si la mano contiene exactamente una pareja
     */
    public boolean esPareja() {
        Map<Integer,Integer> mapaFrecuencias = obtenerFrecuencias();
        return mapaFrecuencias.values().stream().filter(v -> v == 2).count() == 1;
    }

    /**
     * Comprueba si la mano contiene dos parejas diferentes
     */
    public boolean esDoblePareja() {
        Map<Integer,Integer> mapaFrecuencias = obtenerFrecuencias();
        return mapaFrecuencias.values().stream().filter(v -> v == 2).count() == 2;
    }

    /**
     * Comprueba si la mano contiene un trío (3 cartas del mismo valor)
     */
    public boolean esTrio() {
        Map<Integer,Integer> mapaFrecuencias = obtenerFrecuencias();
        return mapaFrecuencias.values().stream().anyMatch(v -> v == 3);
    }

    /**
     * Comprueba si la mano contiene un full house (trío + pareja)
     */
    public boolean esFullHouse() {
        Map<Integer,Integer> mapaFrecuencias = obtenerFrecuencias();
        return mapaFrecuencias.values().stream().anyMatch(v -> v == 3)
                && mapaFrecuencias.values().stream().anyMatch(v -> v == 2);
    }

    /**
     * Comprueba si la mano contiene un póker (4 cartas del mismo valor)
     */
    public boolean esPoker() {
        return obtenerFrecuencias().values().stream().anyMatch(v -> v == 4);
    }

    /**
     * Comprueba si todas las cartas son del mismo palo (color)
     */
    private boolean esColor() {
        return cartas.stream().allMatch(c -> c.getPalo() == cartas.getFirst().getPalo());
    }

    /**
     * Comprueba si las cartas forman una escalera (valores consecutivos)
     */
    private boolean esEscalera() {
        for (int i = 0; i < cartas.size() - 1; i++) {
            if (cartas.get(i + 1).getValor() - cartas.get(i).getValor() != 1) return false;
        }
        return true;
    }

    /**
     * Comprueba si la mano es escalera de color (cartas consecutivas del mismo palo)
     */
    private boolean esEscaleraColor() {
        return esEscalera() && esColor();
    }

    /**
     * Genera un mapa con las frecuencias de cada valor de carta en la mano
     */
    private Map<Integer, Integer> obtenerFrecuencias() {
        Map<Integer, Integer> frecuencia = new HashMap<>();
        for (Carta c : cartas) {
            int val = c.getValor();
            frecuencia.put(val, frecuencia.getOrDefault(val, 0) + 1);
        }
        return frecuencia;
    }

    /**
     * Representación completa de la mano: cartas + valor de la jugada
     */
    @Override
    public String toString() {
        return "Mano: " + mostrar() + " valor de la mano: " + getValorMano();
    }
}