import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class main {
    public main(String[] args) {
//        List<Carta> Baraja = new ArrayList();
//        Carta unaCarta;
//        int cantidad;
//        Baraja baraja = new Baraja();
//        Baraja = baraja.repartirMano(5);
//        for (Carta c : Baraja) {
//            System.out.println(c);
//        }
//        System.out.println(Baraja.size());
//        unaCarta = baraja.repartirUna();
//        System.out.println(unaCarta);
//        cantidad = baraja.getCartasRestantes();
//        System.out.println(cantidad);
//        Baraja baraja = new Baraja();
//        int tamaño;
//        List<Carta> barajaMain = new ArrayList<>();
//        List<Carta> mano = new ArrayList<>();
//        barajaMain = baraja.getBaraja();
//        for (Carta c : barajaMain) {
//            System.out.println(c);
//        }
//        System.out.println("----");
//        mano = baraja.repartirMano(5);
//        for (Carta c : mano) {
//            System.out.println(c);
//        }
//        Baraja baraja = new Baraja();
//        for (Carta c : baraja.getBaraja()){
//            System.out.println(c);
//        }
//        Carta carta;
//        Baraja baraja = new Baraja();
//        baraja.barajar();
//        carta = baraja.repartirUna();
//        String ruta = Baraja.getNombreRuta(carta);
//        System.out.println(ruta);
//        PokerGui interfaz = new PokerGui();
        int valor;
        Baraja baraja = new Baraja();
        baraja.barajar();

        List<Carta> manoJugador1 = baraja.repartirMano(5);
        List<Carta> manoJugador2 = baraja.repartirMano(5);
        ArrayList<Mano> manos = new ArrayList<>();

        Mano mano1 = new Mano(manoJugador1);
        Mano mano2 = new Mano(manoJugador2);

        manos.add(mano1);
        manos.add(mano2);
        Mano mano_max = Collections.max(manos);
        System.out.println(mano_max);

        System.out.println("Jugador 1: " + mano1.mostrar() + " | Valor: " + mano1.getValorMano());
        System.out.println("Jugador 2: " + mano2.mostrar() + " | Valor: " + mano2.getValorMano());

        if (mano1.compareTo(mano2) > 0) {
            System.out.println("Gana Jugador 1");
        } else if (mano1.compareTo(mano2) < 0) {
            System.out.println("Gana Jugador 2");
        } else {
            System.out.println("Empate");
        }
    }

    public static void main(String[] args) {
        main main = new main(args);
    }
    }
