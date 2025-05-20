import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TableroTexasHoldEm extends JPanel {

    private TexasHoldEm juego;
    private int cantidadJugadores;
    private Image backgroundImage;

    private JLabel lblTitulo, lblEtapa, lblTurno, lblDinero, lblPot, lblApuestaMaxima;
    private JPanel panelComunitarios, panelMano, panelJugadores;

    private JButton btnFold, btnCall, btnBet, btnRaise, btnCheck;

    public TableroTexasHoldEm(int cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;

        // Pedir nombres de los jugadores al inicio
        String[] nombresJugadores = new String[cantidadJugadores];
        for (int i = 0; i < cantidadJugadores; i++) {
            nombresJugadores[i] = JOptionPane.showInputDialog(this,
                    "Ingrese el nombre del Jugador " + (i + 1) + ":");
            if (nombresJugadores[i] == null || nombresJugadores[i].trim().isEmpty()) {
                nombresJugadores[i] = "Jugador " + (i + 1);
            }
        }

        juego = new TexasHoldEm(cantidadJugadores, 1000, 50, nombresJugadores);
        juego.iniciarJuego(cantidadJugadores);


        backgroundImage = new ImageIcon("src/Imagenes/mesa.png").getImage();

        setLayout(null);
        initUI();
        actualizarPantalla();
    }

    private void initUI() {
        lblEtapa = new JLabel("Etapa: " + juego.getNombreEtapa(), SwingConstants.LEFT);
        lblEtapa.setFont(new Font("Serif", Font.BOLD, 40));
        lblEtapa.setForeground(Color.WHITE);
        lblEtapa.setBounds(1600, 150, 500, 40);
        add(lblEtapa);

        // Etiqueta para mostrar de quién es el turno (nombre del jugador)
        lblTurno = new JLabel("Turno: " + juego.getJugadorActual().getNombre(), SwingConstants.RIGHT);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 35));
        lblTurno.setForeground(Color.WHITE);
        lblTurno.setBounds(120, 960, 300, 40);
        add(lblTurno);

        lblDinero = new JLabel("DINERO: $" + juego.getJugadorActual().getDinero(), SwingConstants.CENTER);
        lblDinero.setFont(new Font("Arial", Font.BOLD, 35));
        lblDinero.setForeground(Color.WHITE);
        lblDinero.setBounds(1480, 960, 300, 35);
        add(lblDinero);

        // Panel para las cartas comunitarias
        panelComunitarios = new JPanel();
        panelComunitarios.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelComunitarios.setOpaque(false);
        panelComunitarios.setBounds(320, 280, 900, 250);
        add(panelComunitarios);

        // Panel para las cartas personales del jugador actual
        panelMano = new JPanel();
        panelMano.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelMano.setOpaque(false);
        panelMano.setBounds(810, 620, 300, 250);
        add(panelMano);

        // Panel para mostrar información de los jugadores
        panelJugadores = new JPanel();
        panelJugadores.setLayout(new BoxLayout(panelJugadores, BoxLayout.Y_AXIS));
        panelJugadores.setOpaque(false);
        panelJugadores.setForeground(Color.WHITE);
        panelJugadores.setBounds(1600, 220, 300, 700);
        add(panelJugadores);

        // Botón "Fold"
        btnFold = new JButton("Fold");
        btnFold.setBounds(640, 910, 140, 50);
        btnFold.addActionListener(e -> ejecutarAccion("fold"));
        add(btnFold);

        btnFold = new JButton("All in");
        btnFold.setBounds(1240, 910, 140, 50);
        btnFold.addActionListener(e -> ejecutarAccion("fold"));
        add(btnFold);

        // Botón "Call"
        btnCall = new JButton("Call");
        btnCall.setBounds(790, 910, 140, 50);
        btnCall.addActionListener(e -> {
            int diferencia = juego.getApuestaMaximaActual() - juego.getJugadorActual().getApuestaRonda();
            if (diferencia > 0) {
                ejecutarAccion("call");
            } else {
                ejecutarAccion("check");
            }
        });
        add(btnCall);

        // Botón "Bet"
        btnBet = new JButton("Bet");
        btnBet.setBounds(940, 910, 140, 50);
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
        btnRaise.setBounds(1090, 910, 140, 50);
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
        btnCheck.setBounds(490, 910, 140, 50);
        btnCheck.addActionListener(e -> ejecutarAccion("check"));
        add(btnCheck);

        lblPot = new JLabel("Pot: $0", SwingConstants.CENTER);
        lblPot.setFont(new Font("Arial", Font.BOLD, 30));
        lblPot.setForeground(Color.WHITE);
        lblPot.setBounds(700, 200, 500, 40);
        add(lblPot);

        lblApuestaMaxima = new JLabel("Apuesta Máxima: $0", SwingConstants.CENTER);
        lblApuestaMaxima.setFont(new Font("Arial", Font.BOLD, 20));
        lblApuestaMaxima.setForeground(Color.WHITE);
        lblApuestaMaxima.setBounds(700, 250, 500, 30); // Debajo del pot
        add(lblApuestaMaxima);
    }

    private void actualizarPanelJugadores() {
        panelJugadores.removeAll();

        // Título
        JLabel lblTitulo = new JLabel("Jugadores:");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelJugadores.add(lblTitulo);
        panelJugadores.add(Box.createRigidArea(new Dimension(0, 20)));

        // Información de dealer y ciegas
        if(juego.getJugadores().size() > 0) {
            JLabel lblDealer = new JLabel("Dealer: " + juego.getDealer().getNombre());
            lblDealer.setFont(new Font("Arial", Font.BOLD, 20));
            lblDealer.setForeground(Color.CYAN);
            panelJugadores.add(lblDealer);

            JLabel lblSmallBlind = new JLabel("SB: " + juego.getSmallBlind().getNombre() + " ($" + juego.getCiega() + ")");
            lblSmallBlind.setFont(new Font("Arial", Font.PLAIN, 18));
            lblSmallBlind.setForeground(Color.WHITE);
            panelJugadores.add(lblSmallBlind);

            JLabel lblBigBlind = new JLabel("BB: " + juego.getBigBlind().getNombre() + " ($" + juego.getCiegaGrande() + ")");
            lblBigBlind.setFont(new Font("Arial", Font.PLAIN, 18));
            lblBigBlind.setForeground(Color.WHITE);
            panelJugadores.add(lblBigBlind);
        }

        panelJugadores.add(Box.createRigidArea(new Dimension(0, 20)));

        // Lista de jugadores con su información actualizada
        for (Jugador jugador : juego.getJugadores()) {
            String estado = jugador.estaActivo() ? (jugador.isAllIn() ? " [ALL-IN]" : "") : " [FOLD]";
            String info = String.format("%s: $%d (Apuesta: $%d)%s",
                    jugador.getNombre(),
                    jugador.getDinero(),
                    jugador.getApuestaRonda(),
                    estado);

            JLabel lblJugador = new JLabel(info);
            lblJugador.setFont(new Font("Arial", Font.BOLD, 20));

            // Resaltar jugador actual
            if (jugador == juego.getJugadorActual()) {
                lblJugador.setForeground(Color.YELLOW);
                lblJugador.setFont(lblJugador.getFont().deriveFont(Font.BOLD, 22));
            } else {
                lblJugador.setForeground(Color.WHITE);
            }

            panelJugadores.add(lblJugador);
            panelJugadores.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panelJugadores.revalidate();
        panelJugadores.repaint();
    }
    // Ejecuta acciones que no requieren monto: "fold", "call" y ahora "check"
    private void ejecutarAccion(String accion) {
        Jugador jugadorActual = juego.getJugadorActual();
        switch (accion) {
            case "fold":
                jugadorActual.retirarse();
                break;
            case "call":
                juego.igualarApuesta(juego.getTurnoActual());
                JOptionPane.showMessageDialog(this,
                        jugadorActual.getNombre() + " iguala la apuesta de $" +
                                juego.getApuestaMaximaActual());
                break;
            case "check":
                if (juego.isApuestaEnRonda()) {
                    JOptionPane.showMessageDialog(this,
                            "No puedes hacer check, hay una apuesta activa");
                    return;
                }
                JOptionPane.showMessageDialog(this,
                        jugadorActual.getNombre() + " hace check");
                break;
        }
        avanzarTurno();
    }

    private void ejecutarAccion(String accion, int monto) {
        Jugador jugadorActual = juego.getJugadorActual();
        int apuestaActualJugador = jugadorActual.getApuestaRonda();

        switch (accion) {
            case "bet":
                // Establece nueva apuesta máxima
                juego.setApuestaMaximaActual(monto);
                // Calcula diferencia real a apostar (resta lo ya apostado)
                int diferenciaBet = monto - apuestaActualJugador;
                jugadorActual.apostar(diferenciaBet);
                juego.agregarAlPot(diferenciaBet);
                juego.setApuestaEnRonda(true);
                break;

            case "raise":
                // Calcula el nuevo total (apuesta anterior + raise)
                int nuevoTotal = apuestaActualJugador + monto;
                juego.setApuestaMaximaActual(nuevoTotal);
                // El monto ya es la diferencia a subir
                jugadorActual.apostar(monto);
                juego.agregarAlPot(monto);
                juego.setApuestaEnRonda(true);
                break;
        }
        avanzarTurno();
    }

    private void avanzarTurno() {
        juego.siguienteTurno();
        juego.jugarRonda();
        actualizarPantalla();
    }

    private void actualizarPantalla() {
        Jugador jugadorActual = juego.getJugadorActual();

        lblEtapa.setText("Etapa: " + juego.getNombreEtapa());
        lblTurno.setText("Turno: " + jugadorActual.getNombre());
        lblDinero.setText("Dinero: $" + jugadorActual.getDinero());
        lblPot.setText("Pot: $" + juego.getPot());
        lblApuestaMaxima.setText("Apuesta Máxima: $" + juego.getApuestaMaximaActual());
        btnCheck.setEnabled(!juego.isApuestaEnRonda());

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
        if (jugadorActual != null && jugadorActual.getMano() != null) {
            for (Carta c : jugadorActual.getMano().getCartas()) {
                panelMano.add(new JLabel(obtenerImagenCarta(c)));
            }
        }
        panelMano.revalidate();
        panelMano.repaint();

        // Actualiza el panel de jugadores
        actualizarPanelJugadores();
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
        Image img = icon.getImage().getScaledInstance(140, 200, Image.SCALE_SMOOTH);
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