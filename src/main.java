import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
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
        Carta carta;
        Baraja baraja = new Baraja();
        baraja.barajar();
        carta = baraja.repartirUna();
        String ruta = Baraja.getNombreRuta(carta);
        System.out.println(ruta);
    }
}