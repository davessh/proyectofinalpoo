import java.util.*;
import java.util.stream.Collectors;

public class Mano implements Comparable<Mano> {
    private final List<Carta> cartas;

    public Mano(List<Carta> cartas) {
        this.cartas = new ArrayList<>(cartas);
        Collections.sort(this.cartas);
    }

    public void agregarCarta(Carta carta) {
        cartas.add(carta);
        Collections.sort(cartas);
    }

    public List<Carta> getCartas() {
        return cartas;
    }

    public String mostrar() {
        return cartas.stream()
                //Se reutiliza toString de Carta
                .map(Carta::toString)
                //Se separan con un espacio vacio " "
                .collect(Collectors.joining(" "));
    }


    // Asignar un valor según la jugada
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

    @Override
    public int compareTo(Mano otra) {
        return Integer.compare(this.getValorMano(), otra.getValorMano());
    }

    public boolean esPareja() {
        Map<Integer,Integer> mapaFrecuencias = obtenerFrecuencias();
        // existe exactamente un valor con frecuencia 2
        return mapaFrecuencias.values().stream().filter(v -> v == 2).count() == 1;
    }

    public boolean esDoblePareja() {
        Map<Integer,Integer> mapaFrecuencias = obtenerFrecuencias();
        // existen dos valores con freq=2
        return mapaFrecuencias.values().stream().filter(v -> v == 2).count() == 2;
    }

    public boolean esTrio() {
        Map<Integer,Integer> mapaFrecuencias = obtenerFrecuencias();
        return mapaFrecuencias.values().stream().anyMatch(v -> v == 3);
    }

    public boolean esFullHouse() {
        Map<Integer,Integer> mapaFrecuencias = obtenerFrecuencias();
        return mapaFrecuencias.values().stream().anyMatch(v -> v == 3)
                && mapaFrecuencias.values().stream().anyMatch(v -> v == 2);
    }

    public boolean esPoker() {
        return obtenerFrecuencias().values().stream().anyMatch(v -> v == 4);
    }

    private boolean esColor() {
        return cartas.stream().allMatch(c -> c.getPalo() == cartas.getFirst().getPalo());
    }

    private boolean esEscalera() {
        for (int i = 0; i < cartas.size() - 1; i++) {
            if (cartas.get(i + 1).getValor() - cartas.get(i).getValor() != 1) return false;
        }
        return true;
    }

    private boolean esEscaleraColor() {
        return esEscalera() && esColor();
    }

    private Map<Integer, Integer> obtenerFrecuencias() {
        Map<Integer, Integer> frecuencia = new HashMap<>();
        for (Carta c : cartas) {
            int val = c.getValor();
            frecuencia.put(val, frecuencia.getOrDefault(val, 0) + 1);
        }
        return frecuencia;
    }

    private boolean hayRepetido(int cantidad) {
        for (Carta c : cartas) {
            long count = cartas.stream().filter(x -> x.getValor() == c.getValor()).count();
            if (count == cantidad) return true;
        }
        return false;
    }

    @Override
    public String toString() {
            return "Mano: " + mostrar() + " valor de la mano: " + getValorMano();
        }
}
