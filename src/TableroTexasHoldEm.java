import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TableroTexasHoldEm extends JPanel {

    private TexasHoldEm juego;
    private int cantidadJugadores;
    private Image backgroundImage;

    private JLabel lblTitulo, lblEtapa, lblTurno, lblDinero, lblPot, lblApuestaMaxima;
    private JPanel panelComunitarios, panelMano, panelJugadores, panelInfoJugadores;

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
        // Nuevo panel para información de todos los jugadores
        panelInfoJugadores = new JPanel();
        panelInfoJugadores.setLayout(new BoxLayout(panelInfoJugadores, BoxLayout.Y_AXIS));
        panelInfoJugadores.setOpaque(false);
        panelInfoJugadores.setForeground(Color.WHITE);
        panelInfoJugadores.setBounds(1600, 450, 300, 500);
        add(panelInfoJugadores);


        // Botón "Fold"
        btnFold = new JButton("Fold");
        btnFold.setBounds(640, 910, 140, 50);
        btnFold.addActionListener(e -> ejecutarAccion("fold"));
        add(btnFold);

        btnFold = new JButton("All in");
        btnFold.setBounds(1240, 910, 140, 50);
        btnFold.addActionListener(e -> ejecutarAccion("fold"));
        add(btnFold);

        //
        btnCall = new JButton("Call");
        btnCall.setBounds(790, 910, 140, 50);
        btnCall.addActionListener(e -> {
            Jugador jugadorActual = juego.getJugadorActual();
            int diferencia = juego.getApuestaMaximaActual() - jugadorActual.getApuestaRonda();

            if (diferencia > 0) {
                // Lógica para Call
                juego.igualarApuesta(juego.getTurnoActual());
                JOptionPane.showMessageDialog(this,
                        jugadorActual.getNombre() + " iguala la apuesta de $" +
                                juego.getApuestaMaximaActual());
            } else {
                // Lógica para Check
                if (!juego.isApuestaEnRonda()) {
                    JOptionPane.showMessageDialog(this,
                            jugadorActual.getNombre() + " hace check");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No puedes hacer check, hay una apuesta activa");
                    return;
                }
            }
            avanzarTurno();
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
        btnCheck.addActionListener(e -> {
            Jugador jugadorActual = juego.getJugadorActual();

            // Verificar si se puede hacer check (no hay apuesta vigente)
            if (!juego.isApuestaEnRonda()) {
                JOptionPane.showMessageDialog(this, jugadorActual.getNombre() + " hace check");
                avanzarTurno(); // Avanzar inmediatamente al siguiente jugador
            } else {
                JOptionPane.showMessageDialog(this,
                        "No puedes hacer check, hay una apuesta activa. Debes igualar (Call), subir (Raise) o retirarte (Fold).");
            }
        });
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
        panelInfoJugadores.removeAll();

        // Título y información de dealer y ciegas
        JLabel lblTitulo = new JLabel("Jugadores:");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelJugadores.add(lblTitulo);
        panelJugadores.add(Box.createRigidArea(new Dimension(0, 20)));

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

        // Panel de información de todos los jugadores
        JLabel lblInfoJugadores = new JLabel("Estado de los Jugadores:");
        lblInfoJugadores.setFont(new Font("Arial", Font.BOLD, 20));
        lblInfoJugadores.setForeground(Color.WHITE);
        panelInfoJugadores.add(lblInfoJugadores);
        panelInfoJugadores.add(Box.createRigidArea(new Dimension(0, 10)));

        for (Jugador jugador : juego.getJugadores()) {
            JLabel lblJugador = new JLabel(jugador.getNombre() + ": $" + jugador.getDinero());
            lblJugador.setFont(new Font("Arial", Font.PLAIN, 18));

            // Resaltar jugador actual
            if (jugador == juego.getJugadorActual()) {
                lblJugador.setForeground(Color.YELLOW);
                lblJugador.setFont(new Font("Arial", Font.BOLD, 18));
            }
            // Mostrar en rojo si está retirado
            else if (!jugador.estaActivo()) {
                lblJugador.setForeground(Color.RED);
            }
            // Mostrar en blanco si está activo pero no es su turno
            else {
                lblJugador.setForeground(Color.WHITE);
            }

            panelInfoJugadores.add(lblJugador);
            panelInfoJugadores.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panelJugadores.revalidate();
        panelJugadores.repaint();
        panelInfoJugadores.revalidate();
        panelInfoJugadores.repaint();
    }
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
                JOptionPane.showMessageDialog(this,
                        jugadorActual.getNombre() + " hace check");
                avanzarTurno();
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
        // Solo avanzar si el jugador actual ha cumplido con las apuestas
        Jugador jugadorActual = juego.getJugadorActual();
        int diferencia = juego.getApuestaMaximaActual() - jugadorActual.getApuestaRonda();

        if (diferencia <= 0 || jugadorActual.isAllIn() || !jugadorActual.estaActivo()) {
            juego.siguienteTurno();

            // Verificar si el juego ha terminado (etapa 4 o River completado)
            if (juego.getEtapaActual() == 4) {
                // Mostrar ganador
                mostrarGanador();
                return; // Salir para no actualizar la pantalla normalmente
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    jugadorActual.getNombre() + " debe igualar la apuesta primero");
            return;
        }

        actualizarPantalla();
    }

    private void mostrarGanador() {
        int indiceGanador = juego.determinarGanador();
        if (indiceGanador != -1) {
            Jugador ganador = juego.getJugadores().get(indiceGanador);

            // Mostrar el pot actual antes de reiniciar
            int potGanado = juego.getPot();
            int dineroDespues = ganador.getDinero() + juego.getPot();

            StringBuilder mensaje = new StringBuilder();
            mensaje.append("¡").append(ganador.getNombre()).append(" gana con ");

            if (ganador.getMano() != null) {
                mensaje.append(ganador.getMano().mostrar());
            } else {
                mensaje.append("su mano");
            }

            mensaje.append(" y recibe $").append(potGanado).append("!");
            mensaje.append("\nDinero actual: $").append(dineroDespues);

            JOptionPane.showMessageDialog(this, mensaje.toString());

            // Preguntar si desean jugar otra ronda
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Desean jugar otra ronda?",
                    "Fin de juego",
                    JOptionPane.YES_NO_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                // Reiniciar el juego (esto reiniciará el pot a 0)
                juego.iniciarJuego(juego.getJugadores().size());
                actualizarPantalla();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay ganador claro. El pot se divide entre los empates.");
        }
    }
    private void actualizarPantalla() {
        Jugador jugadorActual = juego.getJugadorActual();

        lblEtapa.setText("Etapa: " + juego.getNombreEtapa());
        lblTurno.setText("Turno: " + jugadorActual.getNombre());
        lblDinero.setText("Dinero: $" + jugadorActual.getDinero());
        lblPot.setText("Pot: $" + juego.getPot());
        lblApuestaMaxima.setText("Apuesta Máxima: $" + juego.getApuestaMaximaActual());
        btnCheck.setEnabled(!juego.isApuestaEnRonda());
        actualizarBotonCallCheck();
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

    private void actualizarBotonCallCheck() {
        Jugador jugadorActual = juego.getJugadorActual();
        int diferencia = juego.getApuestaMaximaActual() - jugadorActual.getApuestaRonda();

        // Lógica para Call/Check (sin afectar al botón Check)
        if (diferencia > 0) {
            btnCall.setText("Call (" + diferencia + ")");
        } else {
            btnCall.setText("Check");
        }

        // Mantener el botón Check siempre activo
        btnCheck.setEnabled(true);
    }
    private ImageIcon obtenerImagenCarta(Carta carta) {
        String valorStr = String.valueOf(carta.getValor());
        String paloStr = carta.getPalo().name().toLowerCase();
        String nombreCarta = valorStr + "_" + paloStr;

        String ruta = "C:\\Users\\Usuario\\IdeaProjects\\proyectofinalpoo2\\Baraja\\" + nombreCarta + ".png";

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