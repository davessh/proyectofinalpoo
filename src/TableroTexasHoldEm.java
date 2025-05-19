import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TableroTexasHoldEm extends JPanel {

    private TexasHoldEm juego;
    private int cantidadJugadores;
    private Image backgroundImage;

    private JLabel lblTitulo, lblEtapa, lblTurno, lblDinero;
    private JPanel panelComunitarios, panelMano;

    private JButton btnFold, btnCall, btnBet, btnRaise, btnCheck;

    public TableroTexasHoldEm(int cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;
        juego = new TexasHoldEm(cantidadJugadores, 1000, 50, "Texas Hold'em");
        juego.iniciarJuego(cantidadJugadores);

        // Cargar la imagen de fondo. Verifica que el archivo "mesa.png" exista.
        backgroundImage = new ImageIcon("src/Imagenes/mesa.png").getImage();

        // Se usa layout nulo para poder posicionar cada componente manualmente.
        setLayout(null);
        initUI();
        actualizarPantalla();
    }

    private void initUI() {
        // Título del juego
        lblTitulo = new JLabel("Texas Hold'em", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 36));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(350, 10, 300, 50);
        add(lblTitulo);

        // Etiqueta para la etapa de la partida (Pre-Flop, Flop, Turn, River, etc.)
        lblEtapa = new JLabel("Etapa: " + juego.getNombreEtapa(), SwingConstants.LEFT);
        lblEtapa.setFont(new Font("Serif", Font.BOLD, 28));
        lblEtapa.setForeground(Color.WHITE);
        lblEtapa.setBounds(20, 70, 300, 40);
        add(lblEtapa);

        // Etiqueta para mostrar de quién es el turno (número del jugador)
        lblTurno = new JLabel("Turno: Jugador " + (juego.getTurnoActual() + 1), SwingConstants.RIGHT);
        lblTurno.setFont(new Font("Serif", Font.BOLD, 28));
        lblTurno.setForeground(Color.YELLOW);
        lblTurno.setBounds(680, 70, 300, 40);
        add(lblTurno);

        // Etiqueta para mostrar el dinero actual del jugador a la derecha de los botones
        lblDinero = new JLabel("Dinero: $" + juego.getJugadorActual().getDinero(), SwingConstants.CENTER);
        lblDinero.setFont(new Font("Serif", Font.BOLD, 28));
        lblDinero.setForeground(Color.GREEN);
        lblDinero.setBounds(1100, 860, 250, 35);
        add(lblDinero);

        // Panel para las cartas comunitarias
        panelComunitarios = new JPanel();
        panelComunitarios.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelComunitarios.setOpaque(false);
        panelComunitarios.setBounds(650, 430, 500, 150);
        add(panelComunitarios);

        // Panel para las cartas personales del jugador actual
        panelMano = new JPanel();
        panelMano.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelMano.setOpaque(false);
        panelMano.setBounds(650, 700, 300, 150);
        add(panelMano);

        // Botón "Fold"
        btnFold = new JButton("Fold");
        btnFold.setBounds(650, 860, 100, 35);
        btnFold.addActionListener(e -> ejecutarAccion("fold"));
        add(btnFold);

        // Botón "Call"
        btnCall = new JButton("Call");
        btnCall.setBounds(760, 860, 100, 35);
        btnCall.addActionListener(e -> ejecutarAccion("call"));
        add(btnCall);

        // Botón "Bet"
        btnBet = new JButton("Bet");
        btnBet.setBounds(870, 860, 100, 35);
        btnBet.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingresa cantidad a apostar:");
            try {
                int monto = Integer.parseInt(input);
                ejecutarAccion("bet", monto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida.");
            }
        });
        add(btnBet);

        // Botón "Raise"
        btnRaise = new JButton("Raise");
        btnRaise.setBounds(980, 860, 100, 35);
        btnRaise.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingresa cantidad para subir la apuesta:");
            try {
                int monto = Integer.parseInt(input);
                ejecutarAccion("raise", monto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida.");
            }
        });
        add(btnRaise);

        // Botón "Check" (nuevo)
        btnCheck = new JButton("Check");
        btnCheck.setBounds(490, 860, 150, 35);
        btnCheck.addActionListener(e -> ejecutarAccion("check"));
        add(btnCheck);
    }

    // Ejecuta acciones que no requieren monto: "fold", "call" y ahora "check"
    private void ejecutarAccion(String accion) {
        switch (accion) {
            case "fold":
                JOptionPane.showMessageDialog(this, "Jugador " + (juego.getTurnoActual() + 1) + " se retira.");
                juego.getJugadorActual().retirarse();
                break;
            case "call":
                JOptionPane.showMessageDialog(this, "Jugador " + (juego.getTurnoActual() + 1) + " iguala la apuesta.");
                juego.igualar(juego.getTurnoActual(), juego.cantidadApuestaRonda());
                break;
            case "check":
                // Acción de check: el jugador pasa sin apostar
                JOptionPane.showMessageDialog(this, "Jugador " + (juego.getTurnoActual() + 1) + " hace check.");
                break;
        }
        avanzarTurno();
    }

    // Ejecuta acciones que requieren monto: "bet" y "raise"
    private void ejecutarAccion(String accion, int monto) {
        switch (accion) {
            case "bet":
                JOptionPane.showMessageDialog(this, "Jugador " + (juego.getTurnoActual() + 1) + " apuesta $" + monto + ".");
                juego.apostar(juego.getTurnoActual(), monto);
                break;
            case "raise":
                JOptionPane.showMessageDialog(this, "Jugador " + (juego.getTurnoActual() + 1) + " sube la apuesta a $" + monto + ".");
                juego.subir(juego.getTurnoActual(), monto);
                break;
        }
        avanzarTurno();
    }

    // Avanza el turno: se llama automáticamente a jugarRonda() para avanzar las etapas
    // y se actualiza la interfaz.
    private void avanzarTurno() {
        juego.siguienteTurno();
        // Llamada automática para cambiar de fase si corresponde.
        juego.jugarRonda();
        actualizarPantalla();
    }

    // Actualiza la interfaz gráfica: las etiquetas (etapa, turno, dinero) y los paneles de cartas.
    private void actualizarPantalla() {
        lblEtapa.setText("Etapa: " + juego.getNombreEtapa());
        lblTurno.setText("Turno: Jugador " + (juego.getTurnoActual() + 1));
        lblDinero.setText("Dinero: $" + juego.getJugadorActual().getDinero());

        // Actualiza el panel de cartas comunitarias.
        panelComunitarios.removeAll();
        List<Carta> cartasComunitarias = juego.getCartasComunitarias();
        for (Carta c : cartasComunitarias) {
            panelComunitarios.add(new JLabel(obtenerImagenCarta(c)));
        }
        panelComunitarios.revalidate();
        panelComunitarios.repaint();

        // Actualiza el panel de cartas personales del jugador actual.
        panelMano.removeAll();
        Jugador jugadorActual = juego.getJugadorActual();
        if (jugadorActual != null && jugadorActual.getMano() != null) {
            for (Carta c : jugadorActual.getMano().getCartas()) {
                panelMano.add(new JLabel(obtenerImagenCarta(c)));
            }
        }
        panelMano.revalidate();
        panelMano.repaint();
    }


    private ImageIcon obtenerImagenCarta(Carta carta) {
        String valorStr = String.valueOf(carta.getValor());
        String paloStr = carta.getPalo().name().toLowerCase();
        String nombreCarta = valorStr + "_" + paloStr;

        String ruta = "C:\\Users\\GF76\\IdeaProjects\\proyectofinalpoo2\\Baraja\\" + nombreCarta + ".png";

        ImageIcon icon = new ImageIcon(ruta);
        if (icon.getIconWidth() == -1) {
            System.err.println("No se pudo cargar la imagen: " + ruta);
        }
        Image img = icon.getImage().getScaledInstance(60, 90, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
