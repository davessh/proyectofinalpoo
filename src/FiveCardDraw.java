import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FiveCardDraw extends JuegoPoker {
    private int etapaActual; // 0=reparto, 1=cambio de cartas, 2=apuestas finales
    private int apuestaMinima;
    private int apuestaInicial;

    public FiveCardDraw(int numeroDeJugadores, int dineroInicial, int anteInicial) {
        super(numeroDeJugadores, dineroInicial, new Baraja(), "Five Card Draw");
        this.etapaActual = 0;
        this.apuestaInicial = anteInicial;
        this.apuestaMinima = anteInicial * 2;
    }

    @Override
    public void iniciarJuego(int numeroDeJugadores) {
        baraja.barajar();

        for (int i = 0; i < numeroDeJugadores; i++) {
            apostar(i, apuestaInicial);
        }

        repartirCartas();

        turnoActual = (turnoInicial + 1) % numeroDeJugadores;
    }

    @Override
    public void repartirCartas() {
        // Repartir 5 cartas a cada jugador
        for (int i = 0; i < 5; i++) {
            for (Jugador jugador : jugadores) {
                Carta carta = baraja.repartirUna();
                if (jugador.getMano() == null) {
                    jugador.setMano(new Mano(List.of(carta)));
                } else {
                    jugador.getMano().agregarCarta(carta);
                }
            }
        }
    }

    @Override
    public void jugarRonda() {
        // Si todos menos uno se han retirado, el que queda gana
        if (rondaTerminada()) {
            determinarGanador();
            return;
        }

        switch (etapaActual) {
            case 0:
                // Etapa de intercambio de cartas
                etapaActual = 1;
                // Resetear el turno para la fase de intercambio
                turnoActual = turnoInicial;
                break;

            case 1:
                // Segunda ronda de apuestas
                etapaActual = 2;
                reiniciarApuestasRonda();
                break;

            case 2:
                // Revelar y determinar ganador
                determinarGanador();
                etapaActual = 3;
                break;
        }
    }


    public void cambiarCartas(int posicionJugador, int[] posicionesCartas) {
        if (etapaActual != 1) {
            return;
        }

        Jugador jugador = jugadores.get(posicionJugador);
        if (!jugador.estaActivo()) {
            return;
        }

        List<Carta> manoActual = jugador.getMano().getCartas();
        List<Carta> nuevaMano = new ArrayList<>(manoActual);

        Arrays.sort(posicionesCartas);
        for (int i = posicionesCartas.length - 1; i >= 0; i--) {
            if (posicionesCartas[i] >= 0 && posicionesCartas[i] < 5) {
                nuevaMano.remove(posicionesCartas[i]);
            }
        }

        // Agregar nuevas cartas
        for (int i = 0; i < posicionesCartas.length; i++) {
            Carta nuevaCarta = baraja.repartirUna();
            nuevaMano.add(nuevaCarta);
        }

        // Asignar la nueva mano
        jugador.setMano(new Mano(nuevaMano));

        siguienteTurno();
    }

    private void reiniciarApuestasRonda() {
        turnoActual = turnoInicial;
    }

    @Override
    public int determinarGanador() {
        // Contar cuántos jugadores activos quedan
        List<Jugador> jugadoresActivos = jugadores.stream()
                .filter(Jugador::estaActivo)
                .collect(Collectors.toList());

        // Si solo queda uno, ese es el ganador
        if (jugadoresActivos.size() == 1) {
            Jugador ganador = jugadoresActivos.get(0);
            ganador.recibir(cantidadApuestaRonda);
            return jugadores.indexOf(ganador);
        }

        // Comparar las manos y determinar el ganador
        Jugador ganador = null;
        Mano mejorMano = null;

        for (Jugador jugador : jugadoresActivos) {
            Mano manoJugador = jugador.getMano();

            if (mejorMano == null || manoJugador.compareTo(mejorMano) > 0) {
                mejorMano = manoJugador;
                ganador = jugador;
            }
        }

        // El ganador recibe el dinero
        if (ganador != null) {
            ganador.recibir(cantidadApuestaRonda);
            return jugadores.indexOf(ganador);
        }

        return -1; // Empate o error
    }

    @Override
    public void mostrarMano() {
        System.out.println("Manos de los jugadores:");
        mostrarManos();
    }

    @Override
    public int determinarTurnoInicial() {
        // En Five Card Draw, el dealer también cambia en cada ronda
        return new Random().nextInt(numeroDeJugadores);
    }


    public int getEtapaActual() {
        return etapaActual;
    }

    public String getNombreEtapa() {
        return switch (etapaActual) {
            case 0 -> "Reparto inicial";
            case 1 -> "Cambio de cartas";
            case 2 -> "Apuestas finales";
            default -> "Fin de juego";
        };
    }
}
