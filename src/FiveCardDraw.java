import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FiveCardDraw extends JuegoPoker {
    private int etapaActual; // 0 = Reparto inicial, 1 = Cambio de cartas, 2 = Apuestas finales
    private int apuestaMinima;
    private int apuestaInicial;

    public FiveCardDraw(int numeroDeJugadores, int dineroInicial, int anteInicial) {
        // Se asume que JuegoPoker tiene: numeroDeJugadores, dineroInicial, una Baraja y un nombre.
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
                    // Se utiliza List.of() para crear una lista inmutable, por lo que es mejor
                    // iniciar con una lista mutable (usando new ArrayList<>).
                    jugador.setMano(new Mano(new ArrayList<>(Arrays.asList(carta))));
                } else {
                    jugador.getMano().agregarCarta(carta);
                }
            }
        }
    }

    @Override
    public void jugarRonda() {
        // Si todos menos uno se han retirado, el que queda gana.
        if (rondaTerminada()) {
            determinarGanador();
            return;
        }

        switch (etapaActual) {
            case 0:
                // Se pasa a la etapa de cambio de cartas.
                etapaActual = 1;
                // Reiniciar turno para cambio de cartas.
                turnoActual = turnoInicial;
                break;
            case 1:
                // Segunda ronda de apuestas.
                etapaActual = 2;
                reiniciarApuestasRonda();
                break;
            case 2:
                // Revelar cartas y determinar ganador.
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
        // Remover las cartas basándonos en las posiciones.
        for (int i = posicionesCartas.length - 1; i >= 0; i--) {
            if (posicionesCartas[i] >= 0 && posicionesCartas[i] < 5) {
                nuevaMano.remove(posicionesCartas[i]);
            }
        }

        // Agregar nuevas cartas para cada posición removida.
        for (int i = 0; i < posicionesCartas.length; i++) {
            Carta nuevaCarta = baraja.repartirUna();
            nuevaMano.add(nuevaCarta);
        }
        // Asignar la nueva mano.
        jugador.setMano(new Mano(nuevaMano));

        siguienteTurno();
    }

    private void reiniciarApuestasRonda() {
        // Reiniciar turno al turno inicial de la ronda.
        turnoActual = turnoInicial;
    }

    @Override
    public int determinarGanador() {
        // Filtrar jugadores activos.
        List<Jugador> jugadoresActivos = jugadores.stream()
                .filter(Jugador::estaActivo)
                .collect(Collectors.toList());

        if (jugadoresActivos.size() == 1) {
            Jugador ganador = jugadoresActivos.get(0);
            ganador.recibir(cantidadApuestaRonda);
            return jugadores.indexOf(ganador);
        }

        Jugador ganador = null;
        Mano mejorMano = null;

        for (Jugador jugador : jugadoresActivos) {
            Mano manoJugador = jugador.getMano();
            if (mejorMano == null || manoJugador.compareTo(mejorMano) > 0) {
                mejorMano = manoJugador;
                ganador = jugador;
            }
        }

        if (ganador != null) {
            ganador.recibir(cantidadApuestaRonda);
            return jugadores.indexOf(ganador);
        }

        return -1; // Empate o error.
    }

    @Override
    public void mostrarMano() {
        System.out.println("Manos de los jugadores:");
        mostrarManos();
    }

    @Override
    public int determinarTurnoInicial() {
        // El dealer cambia en cada ronda.
        return new Random().nextInt(numeroDeJugadores);
    }

    public int getEtapaActual() {
        return etapaActual;
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public String getNombreEtapa() {
        return switch (etapaActual) {
            case 0 -> "Reparto inicial";
            case 1 -> "Cambio de cartas";
            case 2 -> "Apuestas finales";
            default -> "Fin de juego";
        };
    }

    protected Carta obtenerNuevaCarta() {
        return baraja.repartirUna();
    }
    public int getTurnoInicial() {
        return turnoInicial;
    }
}
