import java.util.*;
import java.util.stream.Collectors;

public class TexasHoldEm extends JuegoPoker {
    private List<Carta> cartasComunitarias;
    private int etapaActual; // 0 = Pre-Flop, 1 = Flop, 2 = Turn, 3 = River
    private int apuestaMinima;
    private int ciega;
    private int ciegaGrande;
    private List<Jugador> jugadores;
    private int dealerIndex;
    private int smallBlindIndex;
    private int bigBlindIndex;
    private boolean ciegasColocadas;
    private int pot;
    private boolean apuestaEnRonda;
    private int apuestaMaximaActual;// Para controlar si ya se pusieron las ciegas
    private boolean rondaPreFlopCompletada;
    private int ultimoApostadorIndex;

    public TexasHoldEm(int numeroDeJugadores, int dineroInicial, int ciegaPequena, String[] nombresJugadores) {
        super(numeroDeJugadores, dineroInicial, new Baraja(), nombresJugadores);
        this.cartasComunitarias = new ArrayList<>();
        this.etapaActual = 0;
        this.ciega = ciegaPequena;
        this.ciegaGrande = ciegaPequena * 2;
        this.apuestaMinima = ciegaGrande;
        jugadores = new ArrayList<>();
        this.pot = 0; // Inicializar el pot en 0
        this.apuestaEnRonda = false; // Nadie ha apostado al inicio
        this.dealerIndex = -1; // Inicialmente no hay dealer
        this.ciegasColocadas = false;
        this.apuestaMaximaActual = 0;
        this.jugadores = super.getJugadores();
    }

    @Override
    public void iniciarJuego(int numeroDeJugadores) {
        this.rondaPreFlopCompletada = false;
        this.ultimoApostadorIndex = bigBlindIndex;
        baraja.barajar();
        repartirCartas();
        pot = 0;
        designarDealerYCiegas();
        apuestaEnRonda = true;
        apuestaMinima = ciegaGrande;
        apuestaMaximaActual = ciegaGrande;

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
        if (!rondaPreFlopCompletada) {
            manejarRondaApuestas();
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
        turnoActual = turnoInicial;
        apuestaEnRonda = false;
        apuestaMaximaActual = 0;
        // Resetear las apuestas de cada jugador para la nueva ronda
        jugadores.forEach(Jugador::resetearApuestaRonda);
    }

    @Override
    public int determinarGanador() {
        List<Jugador> jugadoresActivos = jugadores.stream()
                .filter(Jugador::estaActivo)
                .collect(Collectors.toList());

        if (jugadoresActivos.size() == 1) {
            Jugador ganador = jugadoresActivos.get(0);
            ganador.recibir(pot); // Transfiere el pot al ganador
            int indiceGanador = jugadores.indexOf(ganador);
            pot = getPot(); // Reinicia el pot después de transferirlo
            return indiceGanador;
        }

        // Evaluar la mejor mano para cada jugador activo
        Map<Jugador, Mano> mejoresManos = new HashMap<>();
        for (Jugador jugador : jugadoresActivos) {
            List<Carta> todasLasCartas = new ArrayList<>(jugador.getMano().getCartas());
            todasLasCartas.addAll(cartasComunitarias);
            Mano mejorMano = encontrarMejorCombinacion(todasLasCartas);
            mejoresManos.put(jugador, mejorMano);
        }

        // Encontrar el jugador(es) con la mejor mano
        List<Jugador> ganadores = new ArrayList<>();
        Mano mejorManoGlobal = null;

        for (Map.Entry<Jugador, Mano> entry : mejoresManos.entrySet()) {
            if (mejorManoGlobal == null || entry.getValue().compareTo(mejorManoGlobal) > 0) {
                mejorManoGlobal = entry.getValue();
                ganadores.clear();
                ganadores.add(entry.getKey());
            } else if (entry.getValue().compareTo(mejorManoGlobal) == 0) {
                ganadores.add(entry.getKey());
            }
        }

        // Distribuir el pot entre los ganadores
        if (!ganadores.isEmpty()) {
            int montoPorGanador = pot / ganadores.size();
            for (Jugador ganador : ganadores) {
                ganador.recibir(montoPorGanador); // Transfiere el pot dividido
            }
            pot = 0; // Reinicia el pot después de transferirlo
            return jugadores.indexOf(ganadores.get(0));
        }

        return -1;
    }
    private Mano encontrarMejorCombinacion(List<Carta> cartas) {
        // Solo necesitamos evaluar combinaciones si hay más de 5 cartas
        if (cartas.size() == 5) {
            return new Mano(cartas);
        }

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

    public void designarDealerYCiegas() {
        dealerIndex = (dealerIndex + 1) % numeroDeJugadores;
        smallBlindIndex = (dealerIndex + 1) % numeroDeJugadores;
        bigBlindIndex = (dealerIndex + 2) % numeroDeJugadores;

        // Small Blind
        jugadores.get(smallBlindIndex).apostar(ciega);
        pot += ciega;

        // Big Blind
        jugadores.get(bigBlindIndex).apostar(ciegaGrande);
        pot += ciegaGrande;

        turnoActual = (bigBlindIndex + 1) % numeroDeJugadores;
    }

    public Jugador getDealer() {
        return dealerIndex >= 0 ? jugadores.get(dealerIndex) : null;
    }

    public Jugador getSmallBlind() {
        return jugadores.get(smallBlindIndex);
    }

    public Jugador getBigBlind() {
        return jugadores.get(bigBlindIndex);
    }

    public int getDealerIndex() {
        return dealerIndex;
    }

    public int getSmallBlindIndex() {
        return smallBlindIndex;
    }

    public int getBigBlindIndex() {
        return bigBlindIndex;
    }

    public int getCiega() {
        return ciega;
    }

    public int getCiegaGrande() {
        return ciegaGrande;
    }

    public void agregarAlPot(int cantidad) {
        pot += cantidad;
    }

    public int getPot() {
        return pot;
    }

    public boolean isApuestaEnRonda() {
        return apuestaEnRonda;
    }

    public void setApuestaEnRonda(boolean apuestaEnRonda) {
        this.apuestaEnRonda = apuestaEnRonda;
    }

    public int getApuestaMaximaActual() {
        return apuestaMaximaActual;
    }

    public void setApuestaMaximaActual(int cantidad) {
        if (cantidad > apuestaMaximaActual) {
            apuestaMaximaActual = cantidad;
        }
    }

    public void igualarApuesta(int jugadorIndex) {
        Jugador jugador = jugadores.get(jugadorIndex);
        int diferencia = apuestaMaximaActual - jugador.getApuestaRonda();

        if (diferencia > 0) {
            if (jugador.getDinero() >= diferencia) {
                jugador.apostar(diferencia);
                pot += diferencia;
                cantidadApuestaRonda += diferencia;  // Actualizar variable de la clase padre
            } else {
                // Caso All-In
                int puedeApostar = jugador.getDinero();
                jugador.apostar(puedeApostar);
                pot += puedeApostar;
                cantidadApuestaRonda += puedeApostar;  // Actualizar variable de la clase padre
            }
        }
    }

    // Modifica el método manejarRondaApuestas()
    public void manejarRondaApuestas() {
        if (etapaActual != 0) {
            rondaPreFlopCompletada = true;
            return;
        }

        // Verificar si todos han igualado las apuestas o se han retirado
        boolean rondaCompleta = true;
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo() && !jugador.isAllIn() &&
                    jugador.getApuestaRonda() < apuestaMaximaActual) {
                rondaCompleta = false;
                break;
            }
        }

        if (rondaCompleta && turnoActual == ultimoApostadorIndex) {
            rondaPreFlopCompletada = true;
            return;
        }

        siguienteTurno();
    }

    public boolean hayApuestasPendientes() {
        for (Jugador jugador : jugadores) {
            if (jugador.estaActivo() && !jugador.isAllIn() &&
                    jugador.getApuestaRonda() < apuestaMaximaActual) {
                return true;
            }
        }
        return false;
    }

    public void apostar(int jugadorIndex, int cantidad) {
        Jugador jugador = jugadores.get(jugadorIndex);
        int diferencia = cantidad - jugador.getApuestaRonda();

        if (diferencia > 0) {
            jugador.apostar(diferencia);
            pot += diferencia;
            apuestaMaximaActual = cantidad;
            ultimoApostadorIndex = jugadorIndex; // Marca quien hizo raise
        }
    }

    public int getUltimoApostadorIndex() {
        return ultimoApostadorIndex;
    }

    public void siguienteTurno() {
        int jugadoresActivos = 0;

        // Contar jugadores activos (no han hecho fold)
        for (Jugador j : jugadores) {
            if (j.estaActivo()) {
                jugadoresActivos++;
            }
        }

        // Si solo queda un jugador activo, terminar la ronda
        if (jugadoresActivos <= 1) {
            etapaActual = 4; // Fin del juego
            return;
        }

        // Buscar el siguiente jugador válido
        int siguienteTurno = (turnoActual + 1) % jugadores.size();
        while (!jugadores.get(siguienteTurno).estaActivo() || jugadores.get(siguienteTurno).isAllIn()) {
            siguienteTurno = (siguienteTurno + 1) % jugadores.size();

            // Si hemos dado toda la vuelta y volvemos al jugador actual
            if (siguienteTurno == turnoActual) {
                break;
            }
        }

        turnoActual = siguienteTurno;

        // Verificar si todos han igualado la apuesta y si hemos completado la ronda
        boolean todasApuestasIgualadas = true;
        for (Jugador j : jugadores) {
            // Solo considerar jugadores activos que no estén all-in
            if (j.estaActivo() && !j.isAllIn() && j.getApuestaRonda() < apuestaMaximaActual) {
                todasApuestasIgualadas = false;
                break;
            }
        }

        // Si todos han igualado Y hemos vuelto al último apostador o después, pasar a la siguiente etapa
        if (todasApuestasIgualadas) {
            // Si estamos en el ultimoApostadorIndex o lo hemos pasado en esta ronda
            boolean rondaCompleta = false;

            // La ronda está completa si:
            // 1. No hay apuestas en la ronda actual (todos hicieron check)
            // 2. O estamos en/después del último apostador
            if (!apuestaEnRonda || siguienteTurno == ultimoApostadorIndex ||
                    (turnoActual > ultimoApostadorIndex && siguienteTurno < ultimoApostadorIndex)) {
                rondaCompleta = true;
            }

            if (rondaCompleta) {
                avanzarEtapa();
            }
        }
    }

    private void avanzarEtapa() {
        switch (etapaActual) {
            case 0: // Pre-Flop a Flop
                generarFlop();
                etapaActual = 1;
                break;
            case 1: // Flop a Turn
                generarTurn();
                etapaActual = 2;
                break;
            case 2: // Turn a River
                generarRiver();
                etapaActual = 3;
                break;
            case 3: // River a finalizar
                determinarGanador();
                etapaActual = 4;
                break;
        }
        reiniciarApuestasRonda();
        // Establecer el turno inicial después del dealer
        turnoActual = (dealerIndex + 1) % jugadores.size();
        ultimoApostadorIndex = turnoActual; // Reset del último apostador

        // Si el jugador en turnoActual no está activo o está all-in, buscar el siguiente
        while (!jugadores.get(turnoActual).estaActivo() || jugadores.get(turnoActual).isAllIn()) {
            turnoActual = (turnoActual + 1) % jugadores.size();
        }
    }
}