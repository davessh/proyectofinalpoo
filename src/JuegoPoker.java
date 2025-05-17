import java.util.ArrayList;

public abstract class JuegoPoker {
    protected int numeroDeJugadores;
    protected int dineroInicial;
    protected ArrayList<Jugador> jugadores;
    protected Baraja baraja;
    protected int turnoActual;
    protected int turnoInicial;
    protected int cantidadApuestaRonda;

    public JuegoPoker(int numeroDeJugadores, int dineroInicial, Baraja baraja, String Nombre) {
        this.numeroDeJugadores = numeroDeJugadores;
        this.dineroInicial = dineroInicial;
        this.baraja = baraja;
        this.turnoInicial = determinarTurnoInicial();
        this.turnoActual = turnoInicial;
        this.jugadores = new ArrayList<>();
        this.cantidadApuestaRonda = 0;
        for (int i = 0; i < numeroDeJugadores; i++) {
            jugadores.add(new Jugador(Nombre, dineroInicial));
        }
    }

    public abstract void repartirCartas();
    public abstract int determinarGanador();
    public abstract void mostrarMano();
    public abstract void jugarRonda();
    public abstract int determinarTurnoInicial();
    public abstract void iniciarJuego(int numeroDeJugadores);


    public void pasar() {
        siguienteTurno();
    }

    public void apostar(int posicionJugador, int cantidad) {
        Jugador jugador = jugadores.get(posicionJugador);
        if (jugador.getDinero() >= cantidad) {
            jugador.apostar(cantidad);
            cantidadApuestaRonda += cantidad;
        } else {
            //
        }
        siguienteTurno();
    }

    public void igualar(int posicionActual, int cantidadAigualar) {
        Jugador jugador = jugadores.get(posicionActual);
        int cantidad = Math.min(jugador.getDinero(), cantidadAigualar);
        jugador.apostar(cantidad);
        cantidadApuestaRonda += cantidad;
        siguienteTurno();
    }

    public void subir(int posicionActual, int cantidad) {
        apostar(posicionActual, cantidad);
    }

    public void retirarse(int posicionaActual) {
        jugadores.get(posicionaActual).retirarse();
        siguienteTurno();
    }

    public void mostrarManos() {
        for (Jugador jugador : jugadores) {
            System.out.println(jugador.getNombre() + ": " + jugador.getMano());
        }
    }

    public void siguienteTurno() {
        do {
            turnoActual = (turnoActual + 1) % numeroDeJugadores;
        } while (!jugadores.get(turnoActual).estaActivo());
    }

    public boolean rondaTerminada() {
        int activos = 0;
        for (Jugador j : jugadores) {
            if (j.estaActivo()) activos++;
        }
        return activos <= 1;
    }

    public int cantidadApuestaRonda() {
        return cantidadApuestaRonda;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public Jugador getJugadorActual() {
        return jugadores.get(turnoActual);
    }
}
