public class Jugador {
    private String nombre;
    private Mano mano;
    private int dinero;
    private boolean activo;


    public Jugador(String nombre, int dineroInicial) {
        this.nombre = nombre;
        this.dinero = dineroInicial;
        this.activo = true;
    }

    public void setMano(Mano mano) {
        this.mano = mano;
    }

    public Mano getMano() {
        return mano;
    }
    public int getDinero() {
        return dinero;
    }
    public void apostar(int cantidad) {
        dinero -= cantidad;
    }
    public void recibir(int cantidad) {
        dinero += cantidad;
    }
    public void retirarse() {
        activo = false;
    }
    public boolean estaActivo() {
        return activo;
    }
    public String getNombre() {
        return nombre;
    }
}
