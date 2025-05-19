import java.util.*;
import java.util.stream.Collectors;

public class TexasHoldEm extends JuegoPoker {
    private List<Carta> cartasComunitarias;
    private int etapaActual; // 0 = Pre-Flop, 1 = Flop, 2 = Turn, 3 = River
    private int apuestaMinima;
    private int ciega;
    private int ciegaGrande;

    public TexasHoldEm(int numeroDeJugadores, int dineroInicial, int ciegaPequena, String nombre) {
        super(numeroDeJugadores, dineroInicial, new Baraja(), nombre);
        this.cartasComunitarias = new ArrayList<>();
        this.etapaActual = 0;
        this.ciega = ciegaPequena;
        this.ciegaGrande = ciegaPequena * 2;
        this.apuestaMinima = ciegaGrande;
    }

    @Override
    public void iniciarJuego(int numeroDeJugadores) {
        baraja.barajar();
        repartirCartas();

        // El turno inicial ya se definió en el constructor, pero se puede redefinir aquí si se requiere.
        turnoInicial = determinarTurnoInicial();
        // La small blind
        int posicionCiegaPequena = turnoInicial;
        apostar(posicionCiegaPequena, ciega);

        // La big blind
        int posicionCiegaGrande = (turnoInicial + 1) % numeroDeJugadores;
        apostar(posicionCiegaGrande, ciegaGrande);

        // Primer turno tras las blinds
        turnoActual = (posicionCiegaGrande + 1) % numeroDeJugadores;
    }

    @Override
    public void repartirCartas() {
        // Repartir 2 cartas a cada jugador (cartas privadas)
        for (int i = 0; i < 2; i++) {
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
        if (rondaTerminada()) {
            determinarGanador();
            return;
        }
        switch (etapaActual) {
            case 0: // Pre-Flop a Flop
                generarFlop();
                break;
            case 1: // Flop a Turn
                generarTurn();
                break;
            case 2: // Turn a River
                generarRiver();
                break;
            case 3: // Determinar ganador
                determinarGanador();
                break;
        }
        etapaActual++;
        if (etapaActual < 4) {
            reiniciarApuestasRonda();
        }
    }

    private void generarFlop() {
        // Quemar la carta superior
        baraja.repartirUna();
        // Repartir 3 cartas comunitarias
        for (int i = 0; i < 3; i++) {
            cartasComunitarias.add(baraja.repartirUna());
        }
    }

    private void generarTurn() {
        // Quemar una carta
        baraja.repartirUna();
        cartasComunitarias.add(baraja.repartirUna());
    }

    private void generarRiver() {
        // Quemar una carta
        baraja.repartirUna();
        // Repartir la quinta carta comunitaria
        cartasComunitarias.add(baraja.repartirUna());
    }

    private void reiniciarApuestasRonda() {
        // Para reiniciar las apuestas después de cada ronda, se resetea el turno al inicial
        turnoActual = turnoInicial;
        // Se puede agregar código para resetear las apuestas de cada jugador sin afectar el bote total
    }

    @Override
    public int determinarGanador() {
        // Se cuenta cuántos jugadores activos quedan
        List<Jugador> jugadoresActivos = jugadores.stream()
                .filter(Jugador::estaActivo)
                .collect(Collectors.toList());

        // Si solo queda uno, ese es el ganador
        if (jugadoresActivos.size() == 1) {
            Jugador ganador = jugadoresActivos.get(0);
            ganador.recibir(cantidadApuestaRonda);
            return jugadores.indexOf(ganador);
        }

        // Se evalúa la mejor mano combinando cartas privadas y comunitarias
        Jugador ganador = null;
        Mano mejorMano = null;

        for (Jugador jugador : jugadoresActivos) {
            List<Carta> todasLasCartas = new ArrayList<>(jugador.getMano().getCartas());
            todasLasCartas.addAll(cartasComunitarias);

            Mano mejorCombinacion = encontrarMejorCombinacion(todasLasCartas);
            if (mejorMano == null || mejorCombinacion.compareTo(mejorMano) > 0) {
                mejorMano = mejorCombinacion;
                ganador = jugador;
            }
        }
        if (ganador != null) {
            ganador.recibir(cantidadApuestaRonda);
            return jugadores.indexOf(ganador);
        }
        return -1;  // Empate o error
    }

    private Mano encontrarMejorCombinacion(List<Carta> cartas) {
        List<List<Carta>> combinaciones = generarCombinaciones(cartas, 5);
        Mano mejorMano = null;
        for (List<Carta> combinacion : combinaciones) {
            Mano manoActual = new Mano(combinacion);
            if (mejorMano == null || manoActual.compareTo(mejorMano) > 0) {
                mejorMano = manoActual;
            }
        }
        return mejorMano;
    }

    private List<List<Carta>> generarCombinaciones(List<Carta> cartas, int k) {
        List<List<Carta>> resultado = new ArrayList<>();
        generarCombinacionesAux(cartas, k, 0, new ArrayList<>(), resultado);
        return resultado;
    }

    private void generarCombinacionesAux(List<Carta> cartas, int k, int inicio,
                                         List<Carta> actual, List<List<Carta>> resultado) {
        if (actual.size() == k) {
            resultado.add(new ArrayList<>(actual));
            return;
        }
        for (int i = inicio; i < cartas.size(); i++) {
            actual.add(cartas.get(i));
            generarCombinacionesAux(cartas, k, i + 1, actual, resultado);
            actual.remove(actual.size() - 1);
        }
    }

    @Override
    public void mostrarMano() {
        System.out.println("Cartas comunitarias: ");
        for (Carta carta : cartasComunitarias) {
            System.out.print(carta + " ");
        }
        System.out.println();
        System.out.println("Manos de los jugadores:");
        mostrarManos();
    }

    @Override
    public int determinarTurnoInicial() {
        // Cambio de dealer aleatorio
        return new Random().nextInt(numeroDeJugadores);
    }

    // Métodos públicos para gestionar y consultar el turno actual
    public int getTurnoActual() {
        return turnoActual;
    }

    public void setTurnoActual(int nuevoTurno) {
        turnoActual = nuevoTurno;
    }


    @Override
    public Jugador getJugadorActual() {
        return jugadores.get(turnoActual);
    }

    public List<Carta> getCartasComunitarias() {
        return cartasComunitarias;
    }

    public int getEtapaActual() {
        return etapaActual;
    }

    public String getNombreEtapa() {
        return switch (etapaActual) {
            case 0 -> "Pre-Flop";
            case 1 -> "Flop";
            case 2 -> "Turn";
            case 3 -> "River";
            default -> "Fin de juego";
        };
    }
}
