import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baraja {
    private final List<Carta> cartas;

    public Baraja() {
        cartas = new ArrayList<>(52);
        for (Carta.Palo palo : Carta.Palo.values()) {
            for (int valor = 2; valor <= 14; valor++) {
                cartas.add(new Carta(valor, palo));
            }
        }
    }

    /**
     * Mezcla aleatoriamente todas las cartas de la baraja
     */
    public void barajar() {
        Collections.shuffle(cartas);
    }

    /**
     * Reparte una carta de la parte superior de la baraja (la elimina de la baraja)
     * @return La carta repartida o null si no hay cartas
     */
    public Carta repartirUna() {
        if (cartas.isEmpty()) {
            System.out.println("No hay cartas");
        }
        return cartas.remove(0);
    }

    /**
     * Reparte un conjunto de cartas para formar una mano
     * @param cantidad Número de cartas a repartir
     * @return Lista con las cartas repartidas
     */
    public List<Carta> repartirMano(int cantidad) {
        if (cantidad > cartas.size()) {
            System.out.println("No hay cartas suficientes");
        }
        List<Carta> mano = new ArrayList<>(cartas.subList(0, cantidad));
        cartas.subList(0, cantidad).clear();
        return mano;
    }

    /**
     * Obtiene el número de cartas restantes en la baraja
     * @return Cantidad de cartas disponibles
     */
    public int getCartasRestantes() {
        return cartas.size();
    }

    /**
     * Genera el nombre del archivo de imagen para una carta (para posible GUI)
     * @param carta Carta a representar
     * @return String con el formato "valor_palo.png"
     */
    public static String getNombreRuta(Carta carta) {
        String valor = carta.getNombreValor();
        String palo = carta.getPalo().name().toLowerCase();
        return valor + "_" + palo + ".png";
    }

    /**
     * Obtiene una copia de la lista de cartas actual de la baraja
     * @return Lista completa de cartas
     */
    public List<Carta> getBaraja() {
        return cartas;
    }

    /**
     * Representación textual de todas las cartas en la baraja
     * @return String con todas las cartas
     */
    @Override
    public String toString() {
        return cartas.toString();
    }
}