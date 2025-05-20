import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TableroFiveCardDraw extends JPanel {

    private FiveCardDraw juego;
    private int cantidadJugadores;
    private Image backgroundImage;

    // Componentes de la interfaz
    private JLabel lblTitulo, lblEtapa, lblTurno, lblDinero;
    private JPanel panelMano;

    // Botones generales de acción
    private JButton btnFold, btnCall, btnBet, btnRaise, btnCheck;

    // Arreglo para rastrear si cada carta ya fue cambiada (cada carta se puede cambiar solo una vez)
    private boolean[] cartasCambiadas;

    public TableroFiveCardDraw(int cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;
        String[] nombreJugadores = new String[cantidadJugadores];
        // Instanciar la lógica del juego (dinero inicial 1000, anteInicial 50)
        juego = new FiveCardDraw(cantidadJugadores, 1000, 50, nombreJugadores);
        juego.iniciarJuego(cantidadJugadores);

        // Cargar imagen de fondo. Verifica que la ruta sea correcta.
        backgroundImage = new ImageIcon("C:\\Users\\V16\\Downloads\\mesa.png").getImage();

        // Se usa layout nulo para posicionar manualmente los componentes.
        setLayout(null);
        initUI();
        actualizarPantalla();
    }

    private void initUI() {
        // Título del juego
        lblTitulo = new JLabel("Five Card Draw", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 36));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(350, 10, 300, 50);
        add(lblTitulo);

        // Etiqueta de etapa (Reparto inicial, Cambio de cartas, Apuestas finales)
        lblEtapa.setFont(new Font("Serif", Font.BOLD, 28));
        lblEtapa.setForeground(Color.WHITE);
        lblEtapa.setBounds(200, 70, 300, 40);
        add(lblEtapa);

        // Etiqueta de turno (jugador actual)
        lblTurno = new JLabel("Turno: Jugador " + (juego.getTurnoActual() + 1), SwingConstants.RIGHT);
        lblTurno.setFont(new Font("Serif", Font.BOLD, 28));
        lblTurno.setForeground(Color.YELLOW);
        lblTurno.setBounds(680, 70, 300, 40);
        add(lblTurno);

        // Etiqueta de dinero del jugador
        lblDinero = new JLabel("Dinero: $" + juego.getJugadorActual().getDinero(), SwingConstants.CENTER);
        lblDinero.setFont(new Font("Serif", Font.BOLD, 28));
        lblDinero.setForeground(Color.GREEN);
        lblDinero.setBounds(1100, 70, 250, 40);
        add(lblDinero);

        // Panel para mostrar la mano del jugador
        panelMano = new JPanel();
        panelMano.setLayout(new GridBagLayout());
        panelMano.setOpaque(false);
        panelMano.setBounds(350, 200, 600, 250);
        add(panelMano);

        // Botón "Fold"
        btnFold = new JButton("Fold");
        btnFold.setBounds(350, 500, 100, 35);
        btnFold.addActionListener(e -> ejecutarAccion("fold"));
        add(btnFold);

        // Botón "Call"
        btnCall = new JButton("Call");
        btnCall.setBounds(460, 500, 100, 35);
        btnCall.addActionListener(e -> ejecutarAccion("call"));
        add(btnCall);

        // Botón "Bet"
        btnBet = new JButton("Bet");
        btnBet.setBounds(570, 500, 100, 35);
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
        btnRaise.setBounds(680, 500, 100, 35);
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

        // Botón "Check"
        btnCheck = new JButton("Check");
        btnCheck.setBounds(790, 500, 100, 35);
        btnCheck.addActionListener(e -> ejecutarAccion("check"));
        add(btnCheck);
    }

    // Actualiza la interfaz: etiquetas y panel de la mano.
    private void actualizarPantalla() {
        lblEtapa.setText("Etapa: " + juego.getNombreEtapa());
        lblTurno.setText("Turno: Jugador " + (juego.getTurnoActual() + 1));
        lblDinero.setText("Dinero: $" + juego.getJugadorActual().getDinero());

        // Actualizamos el panel que muestra la mano.
        panelMano.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;

        Jugador jugadorActual = juego.getJugadorActual();
        if (jugadorActual != null && jugadorActual.getMano() != null) {
            List<Carta> cartas = jugadorActual.getMano().getCartas();
            // Aquí siempre mostramos las cartas como botones.
            // Si estamos en la etapa de cambio de cartas (etapa 1), permitimos cambiar la carta.
            if (juego.getEtapaActual() == 1) {
                // Inicializamos o revalidamos el arreglo para rastrear cambios.
                if (cartasCambiadas == null || cartasCambiadas.length != cartas.size()) {
                    cartasCambiadas = new boolean[cartas.size()];
                }
                for (int i = 0; i < cartas.size(); i++) {
                    gbc.gridx = i;
                    JButton btnCarta = new JButton(obtenerImagenCarta(cartas.get(i)));
                    final int index = i;
                    btnCarta.addActionListener(e -> {
                        if (juego.getEtapaActual() == 1 && !cartasCambiadas[index]) {
                            intercambiarCarta(index);
                        } else {
                            JOptionPane.showMessageDialog(this, "No se puede cambiar esta carta.");
                        }
                    });
                    btnCarta.setEnabled(!cartasCambiadas[i]);
                    panelMano.add(btnCarta, gbc);
                }
            } else {
                // En otros casos, mostramos los botones deshabilitados.
                for (int i = 0; i < cartas.size(); i++) {
                    gbc.gridx = i;
                    JButton btnCarta = new JButton(obtenerImagenCarta(cartas.get(i)));
                    btnCarta.setEnabled(false);
                    panelMano.add(btnCarta, gbc);
                }
                cartasCambiadas = null; // reiniciamos la variable de rastreo.
            }
        }
        panelMano.revalidate();
        panelMano.repaint();
    }

    // Cambia (intercambia) la carta en la posición indicada si aún no se ha cambiado.
    private void intercambiarCarta(int indice) {
        if (cartasCambiadas != null && !cartasCambiadas[indice]) {
            Jugador jugadorActual = juego.getJugadorActual();
            List<Carta> cartas = jugadorActual.getMano().getCartas();
            Carta nuevaCarta = juego.obtenerNuevaCarta();
            cartas.set(indice, nuevaCarta);
            jugadorActual.setMano(new Mano(cartas));
            cartasCambiadas[indice] = true;
            actualizarPantalla();
        }
    }

    // Métodos para ejecutar acciones de juego (Fold, Call, Check) y con monto (Bet, Raise).
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
                JOptionPane.showMessageDialog(this, "Jugador " + (juego.getTurnoActual() + 1) + " hace check.");
                break;
        }
        avanzarTurno();
    }

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

    // Avanza el turno actual y actualiza la interfaz.
    private void avanzarTurno() {
        juego.siguienteTurno();
        actualizarPantalla();
    }


    private ImageIcon obtenerImagenCarta(Carta carta) {
        String nombreCarta = carta.getValor() + "_" + carta.getPalo().name().toLowerCase();
        String ruta = "C:\\Users\\V16\\IdeaProjects\\Pokergui\\src\\BARAJA\\" + nombreCarta + ".png";
        System.out.println("Buscando imagen: " + ruta);
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
