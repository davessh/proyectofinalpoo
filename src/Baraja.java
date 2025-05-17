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

    public void barajar() {
        Collections.shuffle(cartas);
    }

    public Carta repartirUna() {
        if (cartas.isEmpty()) {
            System.out.println("No hay cartas");
        }
        return cartas.remove(0);
    }

    public List<Carta> repartirMano(int cantidad) {
        if (cantidad > cartas.size()) {
            System.out.println("No hay cartas sufientes");
        }
        List<Carta> mano = new ArrayList<>(cartas.subList(0, cantidad));
        cartas.subList(0, cantidad).clear();
        return mano;
    }

    public int getCartasRestantes() {
        return cartas.size();
    }

    //Posible futura implementación de método para GUI
    public static String getNombreRuta(Carta carta) {
        String valor = carta.getNombreValor();
        String palo = carta.getPalo().name().toLowerCase();
        return valor + "_" + palo + ".png";
    }

    @Override
    public String toString() {
        return cartas.toString();
    }

    public List<Carta> getBaraja() {
        return cartas;
    }
}
