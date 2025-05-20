import java.util.ArrayList;

public class Jugador {
    private final String nombre;
    private int dinero;
    private Mano mano;
    private boolean activo;
    private boolean allIn;
    private int apuestaRonda;

    public Jugador(String nombre, int dineroInicial) {
        this.nombre = nombre;
        this.dinero = dineroInicial;
        this.mano = null;
        this.activo = true;
        this.allIn = false;
    }

    /**
     * Realiza una apuesta, reduciendo el dinero del jugador.
     * Si la cantidad excede el dinero disponible, hace all-in automáticamente.
     */
    public void apostar(int cantidad) {
        if (cantidad > dinero) {
            cantidad = dinero;
            allIn = true;
        }
        this.dinero -= cantidad;
        this.apuestaRonda += cantidad;
        if (dinero == 0) {
            allIn = true;
        }
    }

    /**
     * Añade una cantidad de dinero al jugador (generalmente por ganar una mano)
     */
    public void recibir(int cantidad) {
        this.dinero += cantidad;
    }

    /**
     * Marca al jugador como retirado (no participará en la mano actual)
     */
    public void retirarse() {
        this.activo = false;
    }

    /**
     * Indica si el jugador está activo (no se ha retirado)
     */
    public boolean estaActivo() {
        return activo;
    }

    /**
     * Devuelve el nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la cantidad de dinero disponible del jugador
     */
    public int getDinero() {
        return dinero;
    }

    /**
     * Devuelve la mano actual del jugador (objeto Mano)
     */
    public Mano getMano() {
        return mano;
    }

    /**
     * Establece la mano del jugador
     */
    public void setMano(Mano mano) {
        this.mano = mano;
    }

    /**
     * Indica si el jugador está en estado all-in (sin más dinero para apostar)
     */
    public boolean isAllIn() {
        return allIn;
    }

    /**
     * Establece manualmente el estado all-in del jugador
     */
    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    /**
     * Devuelve la cantidad apostada en la ronda actual
     */
    public int getApuestaRonda() {
        return apuestaRonda;
    }

    /**
     * Reinicia a cero el contador de apuestas de la ronda actual
     */
    public void resetearApuestaRonda() {
        this.apuestaRonda = 0;
    }

    /**
     * Representación textual del jugador: nombre, dinero y estado (all-in/retirado)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombre).append(" (").append(dinero).append(" fichas)");
        if (allIn) {
            sb.append(" [ALL-IN]");
        } else if (!activo) {
            sb.append(" [RETIRADO]");
        }
        return sb.toString();
    }
}