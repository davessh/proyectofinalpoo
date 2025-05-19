import java.util.ArrayList;

/**
 * Clase que representa un jugador de poker
 */
public class Jugador {
    private String nombre;
    private int dinero;
    private Mano mano;
    private boolean activo;
    private boolean allIn;

    public Jugador(String nombre, int dineroInicial) {
        this.nombre = nombre;
        this.dinero = dineroInicial;
        this.mano = null;
        this.activo = true;
        this.allIn = false;
    }

    public void apostar(int cantidad) {
        if (cantidad > dinero) {
            cantidad = dinero;
            allIn = true;
        }
        this.dinero -= cantidad;
        if (dinero == 0) {
            allIn = true;
        }
    }

    public void recibir(int cantidad) {
        this.dinero += cantidad;
    }

    public void retirarse() {
        this.activo = false;
    }

    public boolean estaActivo() {
        return activo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDinero() {
        return dinero;
    }

    public Mano getMano() {
        return mano;
    }

    public void setMano(Mano mano) {
        this.mano = mano;
    }

    public boolean isAllIn() {
        return allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

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