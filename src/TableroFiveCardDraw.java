import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO; // Import para ImageIO
import java.io.File; // Import para File
import java.io.IOException;
// Import para IOException

public class TableroFiveCardDraw extends JFrame {

    // CONSTANTE: Fuente grande para todo (tamaño 32)
    public static final Font FUENTE_GRANDE = new Font("Arial", Font.BOLD, 24);
    // CONVENCIÓN DE CARTAS: Rangos, Palos y sus valores (As = 14)
    public static final String[] RANGOS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    public static final String[] PALOS  = {"Corazones", "Diamantes", "Picas", "Tréboles"};

    public static final Map<String, Integer> valoresRanking = new HashMap<>();
    static {
        valoresRanking.put("2", 2);
        valoresRanking.put("3", 3);
        valoresRanking.put("4", 4);
        valoresRanking.put("5", 5);
        valoresRanking.put("6", 6);
        valoresRanking.put("7", 7);
        valoresRanking.put("8", 8);
        valoresRanking.put("9", 9);
        valoresRanking.put("10", 10);
        valoresRanking.put("J", 11);
        valoresRanking.put("Q", 12);
        valoresRanking.put("K", 13);
        valoresRanking.put("A", 14);
    }

    // Rutas de imágenes
    public static final String RUTA_BASE_IMAGENES = "C:\\Users\\V16\\IdeaProjects\\Pokergui\\src\\BARAJA\\";
    public static final String EXTENSION_IMAGEN = ".png";
    public static final String IMAGEN_DORSO = RUTA_BASE_IMAGENES + "back" + EXTENSION_IMAGEN;
    public static final String RUTA_IMAGEN_FONDO = "C:\\Users\\V16\\Downloads\\mesaPoker.png"; // Ruta de imagen de fondo

    // Fases del juego
    private enum FaseJuego { APUESTA1, DESCARTE, APUESTA2, REVELACION, FIN_RONDA }
    private FaseJuego faseActual;
    // ---------------------------
    // Clases internas
    // ---------------------------

    // Representa una carta.
    public class Carta {
        String rango, palo;
        public Carta(String rango, String palo) {
            this.rango = rango;
            this.palo = palo;
        }
        @Override
        public String toString() {
            return rango + " de " + palo;
        }
    }

    // Representa la baraja.
    public class Baraja {
        private List<Carta> cartas;
        public Baraja() {
            cartas = new ArrayList<>();
            for (String palo : PALOS) {
                for (String rango : RANGOS) {
                    cartas.add(new Carta(rango, palo));
                }
            }
        }
        public void barajar() {
            Collections.shuffle(cartas);
        }
        public Carta repartirUna() {
            // Si la baraja se queda sin cartas, se crea una nueva y se baraja.
            if (cartas.isEmpty()) {
                Baraja nuevaBaraja = new Baraja();
                nuevaBaraja.barajar();
                cartas = nuevaBaraja.cartas;
            }
            // Remueve y retorna la primera carta de la baraja.
            return cartas.remove(0);
        }
    }

    // Representa a un jugador.
    public class Jugador {
        String nombre;
        int fichas;
        List<Carta> mano;
        boolean seRetiro; // Indica si el jugador se ha retirado de la ronda actual
        int apuestaActual; // Fichas apostadas por el jugador en la ronda actual
        public Jugador(String nombre, int fichas) {
            this.nombre = nombre;
            this.fichas = fichas;
            mano = new ArrayList<>();
            seRetiro = false;
            apuestaActual = 0;
        }
    }

    // Panel para mostrar la información de cada jugador.
    public class PanelJugador extends JPanel {
        JLabel etiquetaNombre;
        JLabel etiquetaFichas;
        JLabel etiquetaApuesta;
        JPanel panelMano;
        JLabel[] etiquetasCartas;
        JButton[] botonesDescarte;

        public PanelJugador(Jugador jugador) {
            // Hacemos el panel transparente para mostrar la imagen de fondo
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Borde en negro

            etiquetaNombre = new JLabel(jugador.nombre);
            etiquetaNombre.setFont(FUENTE_GRANDE);
            etiquetaNombre.setForeground(Color.BLACK); // Texto en negro
            etiquetaFichas = new JLabel("Fichas: " + jugador.fichas);
            etiquetaFichas.setFont(FUENTE_GRANDE);
            etiquetaFichas.setForeground(Color.BLACK);
            etiquetaApuesta = new JLabel("Apuesta: " + jugador.apuestaActual);
            etiquetaApuesta.setFont(FUENTE_GRANDE);
            etiquetaApuesta.setForeground(Color.BLACK);

            JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelSuperior.setOpaque(false);
            panelSuperior.add(etiquetaNombre);
            panelSuperior.add(etiquetaFichas);
            panelSuperior.add(etiquetaApuesta);
            add(panelSuperior, BorderLayout.NORTH);

            // Se crea un panel para cada carta con su botón de descarte justo debajo
            panelMano = new JPanel(new GridLayout(1, 5));
            panelMano.setOpaque(false);
            etiquetasCartas = new JLabel[5];
            botonesDescarte = new JButton[5];
            for (int i = 0; i < 5; i++) {
                JPanel slotPanel = new JPanel();
                slotPanel.setLayout(new BoxLayout(slotPanel, BoxLayout.Y_AXIS));
                slotPanel.setOpaque(false);

                etiquetasCartas[i] = new JLabel();
                etiquetasCartas[i].setHorizontalAlignment(SwingConstants.CENTER);
                etiquetasCartas[i].setPreferredSize(new Dimension(80, 120));
                etiquetasCartas[i].setAlignmentX(Component.CENTER_ALIGNMENT);
                etiquetasCartas[i].setFont(FUENTE_GRANDE);
                slotPanel.add(etiquetasCartas[i]);
                // Espacio entre la carta y el botón
                slotPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                botonesDescarte[i] = new JButton("Descartar");
                botonesDescarte[i].setFont(FUENTE_GRANDE);
                // Reducir tamaño del botón y centrarlo
                botonesDescarte[i].setMaximumSize(new Dimension(100, 25));
                botonesDescarte[i].setAlignmentX(Component.CENTER_ALIGNMENT);
                botonesDescarte[i].setBackground(Color.RED);
                botonesDescarte[i].setForeground(Color.BLACK);
                botonesDescarte[i].setFocusPainted(false);
                botonesDescarte[i].setVisible(false);
                final int indice = i;
                botonesDescarte[i].addActionListener(e -> descartarCarta(indice));
                slotPanel.add(botonesDescarte[i]);

                panelMano.add(slotPanel);
            }
            add(panelMano, BorderLayout.CENTER);
        }

        /**
         * Actualiza la información visual del panel del jugador, incluyendo fichas, apuesta y cartas.
         * Muestra las cartas del jugador activo y el dorso para los demás.
         * @param jugador El objeto Jugador cuya información se va a mostrar.
         * @param esActivo Booleano que indica si este panel corresponde al jugador en turno.
         */
        public void actualizar(Jugador jugador, boolean esActivo) {
            etiquetaFichas.setText("Fichas: " + jugador.fichas);
            etiquetaApuesta.setText("Apuesta: " + jugador.apuestaActual);
            for (int i = 0; i < 5; i++) {
                if (i < jugador.mano.size()) {
                    if (esActivo)
                        etiquetasCartas[i].setIcon(obtenerIconoCarta(jugador.mano.get(i))); // Muestra la carta si es el jugador activo
                    else
                        etiquetasCartas[i].setIcon(obtenerIconoDorsoCarta()); // Muestra el dorso si no es el jugador activo
                } else {
                    etiquetasCartas[i].setIcon(null); // No hay carta en esta posición
                }
            }
        }

        /**
         * Habilita o deshabilita los botones de descarte.
         * @param habilitar Booleano para establecer la visibilidad y el estado habilitado de los botones.
         */
        public void establecerModoDescarte(boolean habilitar) {
            for (JButton btn : botonesDescarte) {
                btn.setVisible(habilitar);
                btn.setEnabled(habilitar);
                btn.setText("Descartar");
            }
        }

        /**
         * Oculta todos los botones de descarte.
         */
        public void ocultarBotonesDescarte() {
            for (JButton btn : botonesDescarte) {
                btn.setVisible(false);
            }
        }

        /**
         * Deshabilita un botón de descarte específico después de que una carta ha sido descartada.
         * @param indice El índice del botón de descarte a deshabilitar.
         */
        public void deshabilitarBotonDescarte(int indice) {
            botonesDescarte[indice].setEnabled(false);
            botonesDescarte[indice].setText("Descartado");
        }
    }

    // Panel personalizado para la imagen de fondo
    public class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar la imagen de fondo: " + imagePath, "Error de Imagen", JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        }
    }

    // -----------------------------
    // Variables de estado y componentes de la GUI
    // -----------------------------
    private List<Jugador> jugadores;
    private List<PanelJugador> panelesJugadores;
    private Baraja baraja;
    private int bote; // Total de fichas en el bote
    private int indiceJugadorActual; // Índice del jugador cuyo turno es
    private int apuestaMasAltaActual; // La apuesta más alta realizada en la ronda actual
    private boolean[] turnoDescarteHecho; // Rastrea si cada jugador ha completado su descarte
    private JPanel contenedorJugadorActivo; // Panel que muestra la interfaz del jugador activo
    private JLabel etiquetaTurno, etiquetaEstado, etiquetaBote;
    private JButton btnPasar, btnApostar, btnIgualar, btnSubir, btnRetirarse;
    private JButton btnTerminarDescarte;
    private JButton btnRevelacion, btnNuevaRonda;

    // -----------------------------
    // Constructor: Configuración de la GUI
    // -----------------------------
    public TableroFiveCardDraw(int numJugadores) {
        setTitle("Poker Five Card Draw");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Se usa el BackgroundPanel personalizado
        BackgroundPanel backgroundPanel = new BackgroundPanel(RUTA_IMAGEN_FONDO);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Se maximiza la ventana
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Valida el número de jugadores (2 a 8)
        if (numJugadores < 2) numJugadores = 2;
        if (numJugadores > 8) numJugadores = 8;

        // Crear jugadores y sus paneles.
        jugadores = new ArrayList<>();
        panelesJugadores = new ArrayList<>();
        for (int i = 0; i < numJugadores; i++) {
            String nombre = JOptionPane.showInputDialog(null, "Ingrese el nombre del Jugador " + (i + 1) + ":", "Jugador " + (i + 1));
            if (nombre == null || nombre.trim().isEmpty()) {
                nombre = "Jugador " + (i + 1);
            }

            Object[] opcionesFichas = {1000, 2000, 3000};
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione la cantidad de fichas para " + nombre,
                    "Fichas iniciales",
                    JOptionPane.DEFAULT_OPTION,

                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcionesFichas,
                    opcionesFichas[0]
            );
            int cantidadFichas = 1000;
            if (seleccion >= 0 && seleccion < opcionesFichas.length) {
                cantidadFichas = (int) opcionesFichas[seleccion];
            }

            Jugador p = new Jugador(nombre, cantidadFichas);
            jugadores.add(p);
            panelesJugadores.add(new PanelJugador(p));
        }

        // Panel superior: Estado, Turno y Bote.
        JPanel panelSuperior = new JPanel(new GridLayout(1, 3));
        panelSuperior.setOpaque(false);
        etiquetaEstado = new JLabel("Bienvenido a Poker Five Card Draw - 3 Fases", SwingConstants.CENTER);
        etiquetaEstado.setFont(FUENTE_GRANDE);
        etiquetaEstado.setForeground(Color.BLACK);
        etiquetaBote = new JLabel("Bote: 0", SwingConstants.CENTER);
        etiquetaBote.setFont(FUENTE_GRANDE);
        etiquetaBote.setForeground(Color.BLACK);
        etiquetaTurno = new JLabel("Turno: ", SwingConstants.CENTER);
        etiquetaTurno.setFont(FUENTE_GRANDE);
        etiquetaTurno.setForeground(Color.BLACK);
        panelSuperior.add(etiquetaEstado);
        panelSuperior.add(etiquetaTurno);
        panelSuperior.add(etiquetaBote);
        backgroundPanel.add(panelSuperior, BorderLayout.NORTH);

        // Panel central: Se mostrará solo el panel del jugador activo.
        contenedorJugadorActivo = new JPanel(new BorderLayout());
        contenedorJugadorActivo.setOpaque(false);
        backgroundPanel.add(contenedorJugadorActivo, BorderLayout.CENTER);

        // Panel inferior: Controles de acción.
        JPanel panelControl = new JPanel(new FlowLayout());
        panelControl.setOpaque(false);
        btnPasar = new JButton("Pasar (Check)");
        btnPasar.setFont(FUENTE_GRANDE);
        btnPasar.setBackground(Color.RED);
        btnPasar.setForeground(Color.BLACK);
        btnApostar = new JButton("Apostar (Bet)");
        btnApostar.setFont(FUENTE_GRANDE);
        btnApostar.setBackground(Color.RED);
        btnApostar.setForeground(Color.BLACK);
        btnIgualar = new JButton("Igualar (Call)");
        btnIgualar.setFont(FUENTE_GRANDE);
        btnIgualar.setBackground(Color.RED);
        btnIgualar.setForeground(Color.BLACK);
        btnSubir = new JButton("Subir (Raise)");
        btnSubir.setFont(FUENTE_GRANDE);
        btnSubir.setBackground(Color.RED);
        btnSubir.setForeground(Color.BLACK);
        btnRetirarse = new JButton("Retirarse (Fold)");
        btnRetirarse.setFont(FUENTE_GRANDE);
        btnRetirarse.setBackground(Color.RED);
        btnRetirarse.setForeground(Color.BLACK);
        panelControl.add(btnPasar);
        panelControl.add(btnApostar);
        panelControl.add(btnIgualar);
        panelControl.add(btnSubir);
        panelControl.add(btnRetirarse);
        btnTerminarDescarte = new JButton("Terminar Descarte");
        btnTerminarDescarte.setFont(FUENTE_GRANDE);
        btnTerminarDescarte.setBackground(Color.RED);
        btnTerminarDescarte.setForeground(Color.BLACK);
        btnTerminarDescarte.setEnabled(false);
        panelControl.add(btnTerminarDescarte);
        btnRevelacion = new JButton("Revelación");
        btnRevelacion.setFont(FUENTE_GRANDE);
        btnRevelacion.setBackground(Color.RED);
        btnRevelacion.setForeground(Color.BLACK);
        btnRevelacion.setEnabled(false);
        panelControl.add(btnRevelacion);
        btnNuevaRonda = new JButton("Nueva Ronda");
        btnNuevaRonda.setFont(FUENTE_GRANDE);
        btnNuevaRonda.setBackground(Color.RED);
        btnNuevaRonda.setForeground(Color.BLACK);
        btnNuevaRonda.setEnabled(false);
        panelControl.add(btnNuevaRonda);
        backgroundPanel.add(panelControl, BorderLayout.SOUTH);

        // Listeners para los botones.
        btnPasar.addActionListener(e -> procesarPasar());
        btnApostar.addActionListener(e -> procesarApostar());
        btnIgualar.addActionListener(e -> procesarIgualar());
        btnSubir.addActionListener(e -> procesarSubir());
        btnRetirarse.addActionListener(e -> procesarRetirarse());
        btnTerminarDescarte.addActionListener(e -> terminarDescarte());
        btnRevelacion.addActionListener(e -> hacerRevelacion());
        btnNuevaRonda.addActionListener(e -> iniciarNuevaRonda());

        // Iniciar la primera ronda.
        iniciarNuevaRonda();
        setVisible(true);
    }

    // -----------------------------
    // Métodos de juego y transiciones entre fases
    // -----------------------------

    /**
     * Verifica las condiciones de fin de partida.
     * Si queda 1 jugador o menos con fichas suficientes, muestra un mensaje de fin de partida
     * y ofrece la opción de iniciar una nueva partida o volver al menú principal.
     */
    private void checkGameOver() {
        // Se crea una lista de jugadores que aun tienen fichas suficientes.
        List<Jugador> jugadoresActivos = new ArrayList<>();
        for (Jugador p : jugadores) {
            if (p.fichas >= 10) { // Un mínimo de 10 fichas para poder jugar el ante.
                jugadoresActivos.add(p);
            }
        }
        if (jugadoresActivos.size() <= 1) { // Si queda 1 jugador o menos, el juego termina.
            String mensaje;
            if (jugadoresActivos.size() == 1) {
                mensaje = jugadoresActivos.get(0).nombre + " es el ganador de la partida!";
            } else {
                mensaje = "Todos se han quedado sin fichas. Fin de la partida.";
            }
            int opcion = JOptionPane.showOptionDialog(
                    this,
                    mensaje + "\n¿Deseas jugar otra partida o volver al menú?",
                    "Fin de Partida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[] { "Nueva Partida", "Menú Principal" },
                    "Nueva Partida"
            );
            if (opcion == JOptionPane.YES_OPTION) {
                reiniciarJuego();
            } else {
                mostrarMenuPrincipal();
            }
        }
    }

    /**
     * Reinicia la partida, comenzando una nueva ronda y manteniendo a los jugadores con fichas.
     */
    private void reiniciarJuego() {
        // Llama a iniciarNuevaRonda para configurar el estado de la nueva partida.
        iniciarNuevaRonda();
    }

    /**
     * Muestra el menú principal en el JFrame.
     */
    private void mostrarMenuPrincipal() {
        JPanel menu = crearMenuPrincipal();
        setContentPane(menu); // Establece el panel del menú como el contenido principal
        revalidate(); // Vuelve a validar la jerarquía de componentes
        repaint(); // Vuelve a dibujar el componente
    }

    // -----------------------------
    // Método para iniciar una nueva ronda con validación de fichas
    // -----------------------------
    /**
     * Inicia una nueva ronda de póker.
     * Realiza las siguientes acciones:
     * 1. Elimina a los jugadores que no tienen suficientes fichas para el ante.
     * 2. Verifica si quedan suficientes jugadores para continuar.
     * 3. Reinicia las variables del juego (baraja, bote, apuestas, etc.).
     * 4. Reparte 5 cartas a cada jugador activo.
     * 5. Cobra el ante de 10 fichas a cada jugador activo y lo añade al bote.
     * 6. Establece el estado inicial de la primera fase de apuestas.
     */
    private void iniciarNuevaRonda() {
        // Elimina de la lista a los jugadores que se hayan quedado sin fichas suficientes para cubrir el ante (10 fichas)
        Iterator<Jugador> it = jugadores.iterator();
        Iterator<PanelJugador> itPanel = panelesJugadores.iterator();
        while (it.hasNext() && itPanel.hasNext()) {
            Jugador p = it.next();
            itPanel.next();
            if (p.fichas < 10) { // Si el jugador tiene menos de 10 fichas, no puede pagar el ante.
                JOptionPane.showMessageDialog(this, p.nombre + " se ha quedado sin fichas y saldrá de la partida.");
                it.remove(); // Elimina al jugador de la lista de jugadores
            }
        }

        // Verifica si quedan al menos 2 jugadores para poder jugar
        if (jugadores.size() < 2) {
            checkGameOver(); // Si no hay suficientes jugadores, se llama a checkGameOver.
            return; // Se detiene la ronda ya que no hay suficientes jugadores
        }

        // Reiniciar variables y crear una nueva baraja.
        baraja = new Baraja();
        baraja.barajar(); // Baraja la nueva baraja.
        bote = 0; // Reinicia el bote a 0.
        apuestaMasAltaActual = 0; // Reinicia la apuesta más alta a 0.
        turnoDescarteHecho = new boolean[jugadores.size()]; // Reinicia el seguimiento del descarte.
        // Repartir cartas y cobrar el ante de 10 fichas a cada jugador.
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador p = jugadores.get(i);
            p.mano.clear(); // Limpia la mano del jugador.
            p.seRetiro = false; // El jugador no está retirado al inicio de la ronda.
            p.apuestaActual = 0; // Reinicia la apuesta actual del jugador.
            for (int j = 0; j < 5; j++) {
                p.mano.add(baraja.repartirUna()); // Reparte 5 cartas a cada jugador.
            }
            if (p.fichas >= 10) {
                p.fichas -= 10; // Cobra el ante de 10 fichas.
                p.apuestaActual = 10; // La apuesta inicial es el ante.
                bote += 10; // Añade el ante al bote.
            } else {
                p.seRetiro = true; // Si no tiene fichas para el ante, se retira automáticamente.
            }
            turnoDescarteHecho[i] = false; // Marca que el descarte no ha sido hecho para este jugador.
        }

        apuestaMasAltaActual = 10; // La apuesta más alta inicial es el ante.
        indiceJugadorActual = 0; // Comienza el turno con el primer jugador.
        // Encontrar el primer jugador activo.
        while (jugadores.get(indiceJugadorActual).seRetiro) {
            indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size(); // Salta a jugadores retirados.
        }

        faseActual = FaseJuego.APUESTA1; // Establece la fase inicial del juego.
        etiquetaEstado.setText("Fase 1 de Apuestas iniciada"); // Actualiza el estado del juego.
        actualizarEtiquetaTurno(); // Actualiza la etiqueta del turno.
        actualizarPanelActivo(); // Actualiza el panel del jugador activo.
        habilitarBotonesApuesta(true); // Habilita los botones de apuesta.
        btnTerminarDescarte.setEnabled(false); // Deshabilita el botón de terminar descarte.
        btnRevelacion.setEnabled(false); // Deshabilita el botón de revelación.
        btnNuevaRonda.setEnabled(false); // Deshabilita el botón de nueva ronda.
        actualizarEtiquetaBote(); // Actualiza la etiqueta del bote.
    }

    /**
     * Actualiza la etiqueta que muestra el turno del jugador actual.
     * Asegura que el jugador mostrado no esté retirado.
     */
    private void actualizarEtiquetaTurno() {
        int idx = indiceJugadorActual;
        // Busca el siguiente jugador activo si el actual se retiró.
        while (jugadores.get(idx).seRetiro) {
            idx = (idx + 1) % jugadores.size();
        }
        indiceJugadorActual = idx; // Actualiza el índice del jugador actual.
        etiquetaTurno.setText("Turno: " + jugadores.get(indiceJugadorActual).nombre); // Muestra el nombre del jugador en turno.
    }

    /**
     * Actualiza el panel que muestra la información del jugador activo.
     * Habilita o deshabilita los botones de descarte según la fase actual.
     */
    private void actualizarPanelActivo() {
        contenedorJugadorActivo.removeAll(); // Limpia el panel del jugador activo.
        PanelJugador pp = panelesJugadores.get(indiceJugadorActual); // Obtiene el panel del jugador actual.
        if (faseActual == FaseJuego.DESCARTE) {
            pp.establecerModoDescarte(true); // Habilita los botones de descarte si es la fase de descarte.
        } else {
            pp.ocultarBotonesDescarte(); // Oculta los botones de descarte en otras fases.
        }
        pp.actualizar(jugadores.get(indiceJugadorActual), true); // Actualiza el panel del jugador con sus cartas visibles.
        contenedorJugadorActivo.add(pp, BorderLayout.CENTER); // Añade el panel actualizado al contenedor.
        contenedorJugadorActivo.revalidate(); // Vuelve a validar el contenedor.
        contenedorJugadorActivo.repaint(); // Vuelve a dibujar el contenedor.
    }

    /**
     * Actualiza la etiqueta que muestra el monto actual del bote.
     */
    private void actualizarEtiquetaBote() {
        etiquetaBote.setText("Bote: " + bote); // Muestra el valor actual del bote.
    }

    /**
     * Habilita o deshabilita los botones de acción de apuesta.
     * Ajusta la disponibilidad de los botones "Pasar", "Apostar", "Igualar" y "Subir"
     * según el estado de la apuesta actual en la mesa.
     * @param habilitar Booleano para activar o desactivar los botones.
     */
    private void habilitarBotonesApuesta(boolean habilitar) {
        btnPasar.setEnabled(habilitar);
        btnApostar.setEnabled(habilitar);
        btnIgualar.setEnabled(habilitar);
        btnSubir.setEnabled(habilitar);
        btnRetirarse.setEnabled(habilitar); // El botón de retirarse siempre está disponible si habilitar es true.

        Jugador actual = jugadores.get(indiceJugadorActual);
        // Lógica específica para habilitar/deshabilitar botones en fases de apuesta
        if (faseActual == FaseJuego.APUESTA1 || faseActual == FaseJuego.APUESTA2) {
            // "Pasar" solo si la apuesta actual del jugador es igual a la apuesta más alta (es decir, ya igualó o es el primero en apostar).
            btnPasar.setEnabled(actual.apuestaActual == apuestaMasAltaActual && habilitar);
            // "Apostar" solo si no hay una apuesta previa (apuestaMasAltaActual es 0).
            btnApostar.setEnabled(apuestaMasAltaActual == 0 && habilitar);
            // "Igualar" solo si hay una apuesta más alta que la apuesta actual del jugador.
            btnIgualar.setEnabled(apuestaMasAltaActual > 0 && actual.apuestaActual < apuestaMasAltaActual && habilitar);
            // "Subir" solo si hay una apuesta previa.
            btnSubir.setEnabled(apuestaMasAltaActual > 0 && habilitar);
        }
    }

    /**
     * Comprueba si la ronda de apuestas actual ha sido completada.
     * Una ronda se considera completa si todos los jugadores activos han igualado la apuesta más alta,
     * o si solo queda un jugador activo.
     * @return true si la ronda de apuestas está completa, false en caso contrario.
     */
    private boolean rondaApuestasCompleta() {
        int jugadoresNoRetirados = 0;
        for (Jugador p : jugadores) {
            if (!p.seRetiro) {
                jugadoresNoRetirados++;
                // Si algún jugador no retirado no ha igualado la apuesta más alta, la ronda no está completa.
                if (p.apuestaActual != apuestaMasAltaActual) {
                    return false;
                }
            }
        }
        // La ronda está completa si solo queda 1 jugador (ganador por default) o si todos igualaron.
        return jugadoresNoRetirados <= 1 || true; // El `true` aquí es redundante si la condición de arriba ya evalúa.
    }

    /**
     * Pasa el turno al siguiente jugador activo en la secuencia.
     */
    private void siguienteTurnoApuesta() {
        // Cicla a través de los jugadores hasta encontrar el siguiente activo.
        do {
            indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
        } while (jugadores.get(indiceJugadorActual).seRetiro); // Salta a los jugadores que se han retirado.
        actualizarEtiquetaTurno(); // Actualiza la etiqueta del turno.
        actualizarPanelActivo(); // Actualiza el panel del jugador activo.
        habilitarBotonesApuesta(true); // Habilita los botones de apuesta para el nuevo turno.
    }

    /**
     * Determina la siguiente acción o fase después de una acción de apuesta.
     * - Si solo queda un jugador activo, pasa a la siguiente fase (Descarte o Revelación).
     * - Si todos los jugadores activos han igualado la apuesta, pasa a la siguiente fase.
     * - De lo contrario, pasa el turno al siguiente jugador.
     */
    private void siguienteAccionDespuesApuesta() {
        actualizarEtiquetaBote(); // Actualiza el bote después de cada acción de apuesta.
        long jugadoresActivosCount = jugadores.stream().filter(p -> !p.seRetiro).count(); // Cuenta los jugadores no retirados.

        // Si solo queda un jugador activo, se pasa a la siguiente fase directamente.
        if (jugadoresActivosCount <= 1) {
            if (faseActual == FaseJuego.APUESTA1) {
                faseActual = FaseJuego.DESCARTE; // Si estamos en APUESTA1, pasamos a DESCARTE.
                etiquetaEstado.setText("Fase de Descarte: Turno de " + jugadores.get(indiceJugadorActual).nombre);
                btnTerminarDescarte.setEnabled(true); // Habilita el botón para terminar el descarte.
                habilitarBotonesApuesta(false); // Deshabilita los botones de apuesta.
                actualizarPanelActivo(); // Actualiza el panel para mostrar el modo de descarte.
            } else if (faseActual == FaseJuego.APUESTA2) {
                etiquetaEstado.setText("Ronda final de Apuestas terminada. Presiona 'Revelación'.");
                habilitarBotonesApuesta(false); // Deshabilita los botones de apuesta.
                btnRevelacion.setEnabled(true); // Habilita el botón de revelación.
            }
            return;
        }

        // Verifica si todos los jugadores activos han igualado la apuesta más alta.
        boolean allMatched = jugadores.stream()
                .filter(p -> !p.seRetiro)
                .allMatch(p -> p.apuestaActual == apuestaMasAltaActual);
        if (allMatched) {
            // Si todos igualaron, se avanza de fase.
            if (faseActual == FaseJuego.APUESTA1) {
                faseActual = FaseJuego.DESCARTE;
                etiquetaEstado.setText("Fase de Descarte: Turno de " + jugadores.get(indiceJugadorActual).nombre);
                btnTerminarDescarte.setEnabled(true);
                habilitarBotonesApuesta(false);
                actualizarPanelActivo();
            } else if (faseActual == FaseJuego.APUESTA2) {
                etiquetaEstado.setText("Ronda final de Apuestas terminada. Presiona 'Revelación'.");
                habilitarBotonesApuesta(false);
                btnRevelacion.setEnabled(true);
            }
        } else {
            // Si no todos han igualado, el turno pasa al siguiente jugador.
            siguienteTurnoApuesta();
        }
    }

    // -----------------------------
    // Acciones de apuesta (APUESTA1 y APUESTA2)
    // -----------------------------
    /**
     * Procesa la acción de "Pasar" (Check) del jugador actual.
     * Un jugador solo puede pasar si su apuesta actual es igual a la apuesta más alta en la mesa.
     */
    private void procesarPasar() {
        Jugador actual = jugadores.get(indiceJugadorActual);
        if (actual.apuestaActual == apuestaMasAltaActual) { // Solo puede pasar si ha igualado la apuesta o no hay apuesta.
            etiquetaEstado.setText(actual.nombre + " pasa (Check).");
            siguienteAccionDespuesApuesta(); // Pasa al siguiente turno o fase.
        } else {
            JOptionPane.showMessageDialog(this, "No puedes pasar hasta igualar la apuesta.");
        }
    }

    /**
     * Procesa la acción de "Apostar" (Bet) del jugador actual.
     * Permite al jugador realizar una nueva apuesta si no hay una apuesta previa en la mesa.
     */
    private void procesarApostar() {
        Jugador actual = jugadores.get(indiceJugadorActual);
        if (apuestaMasAltaActual != 0) { // Si ya hay una apuesta, el jugador no puede "Apostar", debe "Igualar" o "Subir".
            JOptionPane.showMessageDialog(this, "Ya hay una apuesta. Usa Igualar o Subir.");
            return;
        }
        String entrada = JOptionPane.showInputDialog(this, actual.nombre + ", ingresa tu apuesta (mínimo 10):");
        if (entrada == null) return; // Si el usuario cancela la entrada.
        int montoApuesta = analizarMonto(entrada, 10); // Parsea el monto y asegura que sea al menos 10.
        if (montoApuesta == 0) { // Si el monto es inválido o menor que el mínimo.
            JOptionPane.showMessageDialog(this, "Monto de apuesta inválido. Intenta de nuevo.");
            return;
        }

        if (montoApuesta > actual.fichas) { // Si el jugador no tiene suficientes fichas, hace "ALL-IN".
            JOptionPane.showMessageDialog(this, "No tienes suficientes fichas. Tu apuesta es ALL-IN con " + actual.fichas + " fichas.");
            montoApuesta = actual.fichas;
        }

        actual.fichas -= montoApuesta; // Resta las fichas apostadas del jugador.
        actual.apuestaActual += montoApuesta; // Añade el monto a la apuesta actual del jugador.
        bote += montoApuesta; // Añade el monto al bote.
        apuestaMasAltaActual = actual.apuestaActual; // Actualiza la apuesta más alta.
        etiquetaEstado.setText(actual.nombre + " apuesta " + montoApuesta + ".");
        siguienteAccionDespuesApuesta(); // Pasa al siguiente turno o fase.
    }

    /**
     * Procesa la acción de "Igualar" (Call) del jugador actual.
     * El jugador paga la diferencia para igualar la apuesta más alta en la mesa.
     */
    private void procesarIgualar() {
        Jugador actual = jugadores.get(indiceJugadorActual);
        int diferencia = apuestaMasAltaActual - actual.apuestaActual; // Cantidad necesaria para igualar.
        if (diferencia <= 0) { // Si no hay una apuesta que igualar o el jugador ya igualó.
            JOptionPane.showMessageDialog(this, "No hay apuesta que igualar. Puedes pasar.");
            return;
        }
        if (diferencia > actual.fichas) { // Si el jugador no tiene suficientes fichas para igualar, hace "ALL-IN".
            JOptionPane.showMessageDialog(this, "No tienes suficientes fichas. Igualas con ALL-IN " + actual.fichas + ".");
            diferencia = actual.fichas; // Iguala con todas sus fichas restantes.
        }
        actual.fichas -= diferencia; // Resta las fichas pagadas del jugador.
        actual.apuestaActual += diferencia; // Añade la diferencia a la apuesta actual del jugador.
        bote += diferencia; // Añade la diferencia al bote.
        etiquetaEstado.setText(actual.nombre + " iguala con " + diferencia + ".");
        siguienteAccionDespuesApuesta(); // Pasa al siguiente turno o fase.
    }

    /**
     * Procesa la acción de "Subir" (Raise) del jugador actual.
     * Permite al jugador igualar la apuesta actual y luego añadir un monto adicional.
     */
    private void procesarSubir() {
        Jugador actual = jugadores.get(indiceJugadorActual);
        int diferencia = apuestaMasAltaActual - actual.apuestaActual; // Cantidad necesaria para igualar la apuesta actual.
        String entrada = JOptionPane.showInputDialog(this, actual.nombre + ", ingresa el monto adicional para subir (mínimo 10) además de pagar " + diferencia + ":");
        if (entrada == null) return; // Si el usuario cancela la entrada.
        int montoSubida = analizarMonto(entrada, 10); // Parsea el monto adicional para subir.
        if (montoSubida == 0 && diferencia > 0) { // Si el monto de subida es 0 pero hay una apuesta que igualar.
            JOptionPane.showMessageDialog(this, "Monto de subida inválido. Si solo quieres igualar, usa 'Igualar'.");
            return;
        }
        if (montoSubida == 0 && diferencia == 0) { // Si no hay apuesta y el monto de subida es 0.
            JOptionPane.showMessageDialog(this, "Monto de subida inválido. No hay apuesta para subir.");
            return;
        }

        int total = diferencia + montoSubida; // Monto total que el jugador debe pagar.
        if (total > actual.fichas) { // Si el jugador no tiene suficientes fichas para el total.
            JOptionPane.showMessageDialog(this, "No tienes suficientes fichas. Subes con ALL-IN " + actual.fichas + ".");
            total = actual.fichas; // El jugador hace "ALL-IN".
            montoSubida = total - diferencia; // Ajusta el monto de subida si hizo ALL-IN.
        }
        if (montoSubida < 0) { // Asegura que el monto de subida no sea negativo.
            montoSubida = 0;
        }

        actual.fichas -= total; // Resta el total de fichas del jugador.
        actual.apuestaActual += total; // Añade el total a la apuesta actual del jugador.
        bote += total; // Añade el total al bote.
        apuestaMasAltaActual = actual.apuestaActual; // La apuesta actual del jugador se convierte en la más alta.
        etiquetaEstado.setText(actual.nombre + " sube la apuesta en " + montoSubida + " (total aportado " + total + ").");
        siguienteAccionDespuesApuesta(); // Pasa al siguiente turno o fase.
    }

    /**
     * Procesa la acción de "Retirarse" (Fold) del jugador actual.
     * El jugador se retira de la ronda actual y no participa más en las apuestas ni en la revelación.
     */
    private void procesarRetirarse() {
        Jugador actual = jugadores.get(indiceJugadorActual);
        actual.seRetiro = true; // Marca al jugador como retirado.
        etiquetaEstado.setText(actual.nombre + " se retira.");

        long jugadoresActivos = jugadores.stream().filter(p -> !p.seRetiro).count(); // Cuenta los jugadores que no se han retirado.
        if (jugadoresActivos <= 1) { // Si solo queda un jugador activo después de un retiro.
            if (faseActual == FaseJuego.APUESTA1 || faseActual == FaseJuego.APUESTA2) {
                faseActual = FaseJuego.FIN_RONDA; // Se establece la fase como FIN_RONDA.
                etiquetaEstado.setText("¡Sólo queda un jugador activo! La ronda ha terminado.");
                Jugador ganador = jugadores.stream().filter(p -> !p.seRetiro).findFirst().orElse(null); // Encuentra al ganador.
                if (ganador != null) {
                    JOptionPane.showMessageDialog(this, ganador.nombre + " es el único jugador activo y gana el bote de " + bote + " fichas.");
                    ganador.fichas += bote; // El ganador se lleva el bote.
                    bote = 0; // El bote se reinicia.
                } else {
                    JOptionPane.showMessageDialog(this, "Todos se retiraron. Nadie gana el bote.");
                    bote = 0; // El bote se reinicia si todos se retiraron antes de que alguien ganara.
                }
                habilitarBotonesApuesta(false); // Deshabilita los botones de apuesta.
                btnRevelacion.setEnabled(false); // Deshabilita el botón de revelación.
                btnNuevaRonda.setEnabled(true); // Habilita el botón para iniciar una nueva ronda.
            }
        } else {
            siguienteTurnoApuesta(); // Si hay más de un jugador activo, pasa al siguiente turno.
        }
    }

    /**
     * Analiza una cadena de entrada para obtener un monto numérico,
     * asegurándose de que sea al menos un valor mínimo.
     * @param entrada La cadena de texto a analizar.
     * @param min El valor mínimo permitido para el monto.
     * @return El monto analizado, o 0 si la entrada es inválida o menor que el mínimo.
     */
    private int analizarMonto(String entrada, int min) {
        int monto = 0;
        try {
            monto = Integer.parseInt(entrada.trim()); // Intenta convertir la entrada a un entero.
            if (monto < min) {
                monto = min; // Si es menor que el mínimo, se ajusta al mínimo.
            }
        } catch (NumberFormatException e) {
            // monto permanece 0 para entrada inválida
        }
        return monto;
    }

    // -----------------------------
    // Fase de Descarte
    // -----------------------------
    /**
     * Procesa el descarte de una carta por parte del jugador actual.
     * Reemplaza la carta descartada con una nueva carta de la baraja y actualiza la visualización.
     * @param indiceCarta El índice de la carta a descartar en la mano del jugador.
     */
    private void descartarCarta(int indiceCarta) {
        if (faseActual != FaseJuego.DESCARTE) // Solo permite descartar durante la fase de descarte.
            return;
        Jugador actual = jugadores.get(indiceJugadorActual);
        if (indiceCarta >= 0 && indiceCarta < actual.mano.size()) {
            actual.mano.set(indiceCarta, baraja.repartirUna()); // Reemplaza la carta en la mano.
            panelesJugadores.get(indiceJugadorActual).deshabilitarBotonDescarte(indiceCarta); // Deshabilita el botón para esa carta.
            panelesJugadores.get(indiceJugadorActual).actualizar(actual, true); // Actualiza la mano visible del jugador.
        }
    }

    /**
     * Marca el descarte del jugador actual como completado y pasa al siguiente turno de descarte,
     * o avanza a la siguiente fase (APUESTA2) si todos han descartado.
     */
    private void terminarDescarte() {
        turnoDescarteHecho[indiceJugadorActual] = true; // Marca que el jugador actual ha terminado su descarte.
        panelesJugadores.get(indiceJugadorActual).ocultarBotonesDescarte(); // Oculta los botones de descarte para el jugador actual.
        etiquetaEstado.setText(jugadores.get(indiceJugadorActual).nombre + " terminó su descarte.");

        int siguiente = encontrarSiguienteTurnoDescarte(); // Busca el siguiente jugador que necesita descartar.
        if (siguiente == -1) { // Si no hay más jugadores para descartar.
            faseActual = FaseJuego.APUESTA2; // Cambia a la fase de la segunda ronda de apuestas.
            etiquetaEstado.setText("Fase final de Apuestas iniciada");
            // Reinicia las apuestas actuales de los jugadores para la nueva ronda de apuestas.
            for (Jugador p : jugadores) {
                if (!p.seRetiro) {
                    p.apuestaActual = 0;
                }
            }
            apuestaMasAltaActual = 0; // Reinicia la apuesta más alta.
            indiceJugadorActual = 0; // Vuelve al primer jugador para iniciar la nueva fase de apuestas.
            // Asegura que el primer jugador en turno no esté retirado.
            while (jugadores.get(indiceJugadorActual).seRetiro) {
                indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.size();
            }

            actualizarPanelActivo(); // Actualiza el panel del jugador activo.
            habilitarBotonesApuesta(true); // Habilita los botones de apuesta para la fase APUESTA2.
            btnTerminarDescarte.setEnabled(false); // Deshabilita el botón de terminar descarte.
            actualizarEtiquetaTurno(); // Actualiza la etiqueta del turno.
        } else {
            indiceJugadorActual = siguiente; // Pasa al siguiente jugador para el descarte.
            actualizarEtiquetaTurno(); // Actualiza la etiqueta del turno.
            actualizarPanelActivo(); // Actualiza el panel del jugador activo.
            panelesJugadores.get(indiceJugadorActual).establecerModoDescarte(true); // Habilita los botones de descarte para el nuevo jugador.
        }
    }

    /**
     * Encuentra el índice del siguiente jugador que aún no ha realizado su descarte.
     * @return El índice del siguiente jugador para descartar, o -1 si todos han descartado.
     */
    private int encontrarSiguienteTurnoDescarte() {
        for (int i = 0; i < jugadores.size(); i++) {
            int idx = (indiceJugadorActual + 1 + i) % jugadores.size(); // Recorre los jugadores circularmente.
            if (!jugadores.get(idx).seRetiro && !turnoDescarteHecho[idx]) // Si no está retirado y no ha descartado.
                return idx; // Retorna el índice del jugador.
        }
        return -1; // Retorna -1 si todos los jugadores han descartado.
    }

    // -----------------------------
    // Fase de REVELACION
    // -----------------------------
    /**
     * Realiza la fase de revelación de cartas y determina al ganador de la ronda.
     * Muestra las manos de todos los jugadores (revelando las cartas) y anuncia al ganador,
     * o si hay un empate, divide el bote.
     */
    private void hacerRevelacion() {
        JPanel panelRevelacion = new JPanel(new GridLayout(0, 1, 5, 5));
        panelRevelacion.setOpaque(false);
        List<Jugador> jugadoresActivos = new ArrayList<>();

        // Filtra y ordena a los jugadores activos por la fuerza de su mano para la exhibición.
        for (Jugador p : jugadores) {
            if (!p.seRetiro) {
                jugadoresActivos.add(p);
            }
        }

        // Ordena los jugadores activos de mayor a menor puntuación de mano.
        jugadoresActivos.sort((p1, p2) -> Integer.compare(obtenerPuntuacionCompuestaMano(p2.mano), obtenerPuntuacionCompuestaMano(p1.mano)));

        // Muestra las manos de todos los jugadores en un JOptionPane.
        for (Jugador p : jugadores) {
            JPanel panelP = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelP.setOpaque(false);
            panelP.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    p.nombre + (p.seRetiro ? " (Retirado)" : " - " + evaluarNombreMano(p.mano)), // Muestra si está retirado o la evaluación de su mano.
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,

                    FUENTE_GRANDE,
                    Color.BLACK
            ));
            if (!p.seRetiro) {
                for (Carta c : p.mano) {
                    JLabel etiquetaCarta = new JLabel(obtenerIconoCarta(c)); // Muestra la imagen de la carta.
                    etiquetaCarta.setFont(FUENTE_GRANDE);
                    panelP.add(etiquetaCarta);
                }
            } else {
                JLabel retiredLabel = new JLabel("Se Retiró"); // Etiqueta para jugadores retirados.
                retiredLabel.setFont(FUENTE_GRANDE);
                retiredLabel.setForeground(Color.GRAY);
                panelP.add(retiredLabel);
            }
            panelRevelacion.add(panelP);
        }

        // Muestra el diálogo con todas las manos.
        JOptionPane.showMessageDialog(this, new JScrollPane(panelRevelacion), "Revelación: Todas las Manos", JOptionPane.PLAIN_MESSAGE);

        Jugador ganador = null;
        int mejorPuntuacion = -1;
        List<Jugador> posiblesGanadores = new ArrayList<>();

        // Determina al ganador (o ganadores en caso de empate).
        for (Jugador p : jugadores) {
            if (!p.seRetiro) {
                int puntuacion = obtenerPuntuacionCompuestaMano(p.mano); // Obtiene la puntuación de la mano.
                if (puntuacion > mejorPuntuacion) {
                    mejorPuntuacion = puntuacion; // Actualiza la mejor puntuación.
                    ganador = p; // Establece al jugador como el ganador provisional.
                    posiblesGanadores.clear(); // Limpia la lista de posibles ganadores.
                    posiblesGanadores.add(p); // Añade al nuevo ganador.
                } else if (puntuacion == mejorPuntuacion) {
                    posiblesGanadores.add(p); // Si hay empate, añade al jugador a la lista de posibles ganadores.
                }
            }
        }

        // Anuncia al ganador y distribuye el bote.
        if (ganador == null) {
            etiquetaEstado.setText("Todos se retiraron. Fin de la ronda.");
            bote = 0;
        } else if (posiblesGanadores.size() == 1) {
            JOptionPane.showMessageDialog(this, ganador.nombre + " es el ganador con " + evaluarNombreMano(ganador.mano) + " y gana el bote de " + bote + " fichas.");
            ganador.fichas += bote; // El único ganador se lleva todo el bote.
            bote = 0; // El bote se vacía.
        } else {
            StringBuilder sb = new StringBuilder("¡Empate!\n"); // Mensaje para el empate.
            for (Jugador p : posiblesGanadores) {
                sb.append(p.nombre).append(" con ").append(evaluarNombreMano(p.mano)).append("\n");
            }
            int share = bote / posiblesGanadores.size(); // Divide el bote equitativamente.
            sb.append("El bote de ").append(bote).append(" fichas se divide entre los ganadores. Cada uno recibe ").append(share).append(" fichas.");
            JOptionPane.showMessageDialog(this, sb.toString());
            for (Jugador p : posiblesGanadores) {
                p.fichas += share; // Cada ganador recibe su parte del bote.
            }
            bote = 0; // El bote se vacía.
        }

        actualizarEtiquetaBote(); // Actualiza la etiqueta del bote.
        btnRevelacion.setEnabled(false); // Deshabilita el botón de revelación.
        btnNuevaRonda.setEnabled(true); // Habilita el botón para iniciar una nueva ronda.
        etiquetaEstado.setText("Ronda finalizada. Presiona 'Nueva Ronda' para jugar otra.");
    }

    // -----------------------------
    // Métodos para evaluar la mano
    // -----------------------------
    /**
     * Evalúa una mano de 5 cartas y retorna el nombre de la combinación de póker.
     * Ejemplos: "Escalera Real", "Póker", "Full House", etc.
     * @param mano La lista de objetos Carta que representan la mano del jugador.
     * @return Una cadena de texto con el nombre de la combinación de póker.
     */
    private String evaluarNombreMano(List<Carta> mano) {
        List<Integer> valores = new ArrayList<>();
        List<String> palos = new ArrayList<>();
        for (Carta c : mano) {
            valores.add(valoresRanking.get(c.rango)); // Obtiene el valor numérico de cada carta.
            palos.add(c.palo); // Obtiene el palo de cada carta.
        }
        Collections.sort(valores); // Ordena los valores de las cartas para facilitar la evaluación.

        // Determina si es color (todos los palos son iguales).
        boolean esColor = (new HashSet<>(palos)).size() == 1;

        // Determina si es escalera.
        boolean esEscalera = true;
        // Caso especial para escalera de A-5 (A se considera como 1 para esta escalera).
        if (valores.get(0) == 2 && valores.get(1) == 3 && valores.get(2) == 4 &&
                valores.get(3) == 5 && valores.get(4) == 14) { // 14 es el valor del As.
            esEscalera = true;
        } else {
            // Verifica si los valores son consecutivos.
            for (int i = 0; i < valores.size() - 1; i++) {
                if (valores.get(i + 1) - valores.get(i) != 1) {
                    esEscalera = false;
                    break;
                }
            }
        }

        // Cuenta la frecuencia de cada valor de carta para identificar parejas, tríos, póker.
        Map<Integer, Integer> frecuencia = new HashMap<>();
        for (int v : valores)
            frecuencia.put(v, frecuencia.getOrDefault(v, 0) + 1);
        List<Integer> conteos = new ArrayList<>(frecuencia.values());
        Collections.sort(conteos, Collections.reverseOrder()); // Ordena los conteos de frecuencia de forma descendente.

        // Evalúa la mano según las reglas del póker, de la más alta a la más baja.
        if (esEscalera && esColor) {
            if (valores.get(0) == 10 && valores.get(4) == 14) // Si la escalera de color es de 10 a As.
                return "Escalera Real (Straight Flush)";
            return "Escalera de Color (Straight Flush)";
        } else if (conteos.get(0) == 4) // Cuatro cartas del mismo rango.
            return "Póker (Four of a Kind)";
        else if (conteos.get(0) == 3 && conteos.size() > 1 && conteos.get(1) == 2) // Tres cartas del mismo rango y una pareja.
            return "Full House";
        else if (esColor) // Todas las cartas del mismo palo, pero no una escalera.
            return "Color (Flush)";
        else if (esEscalera) // Cartas consecutivas, pero no del mismo palo.
            return "Escalera (Straight)";
        else if (conteos.get(0) == 3) // Tres cartas del mismo rango.
            return "Trío (Three of a Kind)";
        else if (conteos.get(0) == 2 && conteos.size() > 1 && conteos.get(1) == 2) // Dos parejas.
            return "Doble Pareja (Two Pair)";
        else if (conteos.get(0) == 2) // Una sola pareja.
            return "Pareja (One Pair)";
        else // Ninguna de las combinaciones anteriores.
            return "Carta Alta (High Card)";
    }


    /**
     * Obtiene una categoría numérica para la mano de póker, permitiendo una comparación rápida.
     * Categorías:
     * 10: Escalera Real
     * 9: Escalera de Color
     * 8: Póker
     * 7: Full House
     * 6: Color
     * 5: Escalera
     * 4: Trío
     * 3: Doble Pareja
     * 2: Pareja
     * 1: Carta Alta
     * @param mano La lista de objetos Carta que representan la mano del jugador.
     * @return Un entero que representa la categoría de la mano.
     */
    private int obtenerCategoriaMano(List<Carta> mano) {
        List<Integer> valores = new ArrayList<>();
        List<String> palos  = new ArrayList<>();
        for (Carta c : mano) {
            valores.add(valoresRanking.get(c.rango));
            palos.add(c.palo);
        }
        Collections.sort(valores);
        boolean esColor = (new HashSet<>(palos)).size() == 1;
        boolean esEscalera = true;
        // Manejo de la escalera A-5
        if (valores.get(0)==2 && valores.get(1)==3 && valores.get(2)==4 &&
                valores.get(3)==5 && valores.get(4)==14) {
            esEscalera = true;
        } else {
            for (int i = 0; i < valores.size() - 1; i++) {
                if (valores.get(i + 1) - valores.get(i) != 1) {
                    esEscalera = false;
                    break;
                }
            }
        }
        Map<Integer, Integer> frecuencia = new HashMap<>();
        for (int v : valores)
            frecuencia.put(v, frecuencia.getOrDefault(v, 0) + 1);
        List<Integer> conteos = new ArrayList<>(frecuencia.values());
        Collections.sort(conteos, Collections.reverseOrder());

        if (esEscalera && esColor) {
            if (valores.get(0)==10 && valores.get(4)==14)
                return 10; // Escalera Real
            return 9; // Escalera de Color
        } else if (conteos.get(0)==4)
            return 8; // Póker
        else if (conteos.get(0)==3 && conteos.size()>1 && conteos.get(1)==2)
            return 7; // Full House
        else if (esColor)
            return 6; // Color
        else if (esEscalera)
            return 5; // Escalera
        else if (conteos.get(0)==3)
            return 4; // Trío
        else if (conteos.get(0)==2 && conteos.size()>1 && conteos.get(1)==2)
            return 3; // Doble Pareja
        else if (conteos.get(0)==2)
            return 2; // Pareja
        else
            return 1; // Carta Alta
    }

    /**
     * Calcula una puntuación compuesta para la mano de un jugador.
     * Esta puntuación se usa para comparar manos y resolver empates.
     * Combina la categoría de la mano (Escalera Real, Póker, etc.) con los valores de las cartas.
     * Las categorías tienen un peso mayor, y luego las cartas individuales se ponderan
     * para desempates (ej. Doble Pareja de Ases y Reyes vs. Doble Pareja de Reinas y Jotas).
     * @param mano La lista de objetos Carta que representan la mano del jugador.
     * @return Un entero que representa la puntuación compuesta de la mano.
     */
    private int obtenerPuntuacionCompuestaMano(List<Carta> mano) {
        int categoria = obtenerCategoriaMano(mano); // Primero obtiene la categoría general de la mano.
        List<Integer> valores = new ArrayList<>();
        for (Carta c : mano) {
            valores.add(valoresRanking.get(c.rango)); // Obtiene los valores numéricos de las cartas.
        }
        Collections.sort(valores, Collections.reverseOrder()); // Ordena los valores de las cartas en orden descendente.

        int puntuacionDetallada = 0;
        // Calcula una puntuación secundaria basada en los valores de las cartas individuales.
        // Cada valor de carta se multiplica por una potencia de 15 (un número mayor que el máximo valor de carta, 14)
        // para dar más peso a las cartas de mayor valor en caso de empate dentro de la misma categoría.
        for (int i = 0; i < valores.size(); i++) {
            puntuacionDetallada += valores.get(i) * Math.pow(15, (valores.size() - 1 - i));
        }
        // Combina la categoría (multiplicada por un gran número para que tenga prioridad)
        // con la puntuación detallada de las cartas.
        return categoria * 10000000 + puntuacionDetallada;
    }

    // -----------------------------
    // Métodos para cargar imágenes de cartas
    // -----------------------------
    /**
     * Carga y escala la imagen de una carta específica.
     * @param carta El objeto Carta cuya imagen se desea obtener.
     * @return Un ImageIcon que contiene la imagen escalada de la carta.
     */
    private ImageIcon obtenerIconoCarta(Carta carta) {
        String ruta = obtenerRutaImagenCarta(carta); // Obtiene la ruta del archivo de la imagen.
        ImageIcon icono = new ImageIcon(ruta);
        Image img = icono.getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH); // Escala la imagen.
        return new ImageIcon(img); // Retorna el ImageIcon escalado.
    }

    /**
     * Carga y escala la imagen del dorso de una carta.
     * @return Un ImageIcon que contiene la imagen escalada del dorso de la carta.
     */
    private ImageIcon obtenerIconoDorsoCarta() {
        ImageIcon icono = new ImageIcon(IMAGEN_DORSO); // Carga la imagen del dorso.
        Image img = icono.getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH); // Escala la imagen.
        return new ImageIcon(img);
    }

    /**
     * Construye la ruta del archivo de imagen para una carta dada.
     * Convierte el rango y el palo de la carta a un formato que coincide con los nombres de archivo de imagen.
     * @param carta El objeto Carta para el cual se quiere la ruta de la imagen.
     * @return La ruta completa del archivo de imagen de la carta.
     */
    private String obtenerRutaImagenCarta(Carta carta) {
        String rangoStr = "";
        // Mapea los rangos de cartas a sus representaciones numéricas o de texto para el nombre del archivo.
        switch (carta.rango) {
            case "A": rangoStr = "14"; break; // As es 14 en este sistema de valores.
            case "J": rangoStr = "11"; break;
            case "Q": rangoStr = "12"; break;
            case "K": rangoStr = "13"; break;
            default:  rangoStr = carta.rango; break; // Para números, el rango es el mismo.
        }
        // Formatea el palo (minúsculas, sin espacios ni tildes).
        String paloStr = carta.palo.toLowerCase().replace("é", "e").replace(" ", "");
        // Combina la ruta base, el rango, el palo y la extensión para formar la ruta completa.
        return RUTA_BASE_IMAGENES + rangoStr + "_" + paloStr + EXTENSION_IMAGEN;
    }

    // -----------------------------
    // Menú Principal
    // -----------------------------
    /**
     * Crea y retorna el panel del menú principal.
     * Incluye botones para "Nueva Partida" y "Salir".
     * @return Un JPanel configurado como el menú principal.
     */
    private JPanel crearMenuPrincipal() {
        JPanel panelMenu = new JPanel(new BorderLayout());
        panelMenu.setBackground(Color.DARK_GRAY);

        JLabel titulo = new JLabel("Menú Principal", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);
        panelMenu.add(titulo, BorderLayout.NORTH);
        // Panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.DARK_GRAY);
        JButton btnNuevaPartida = new JButton("Nueva Partida");
        JButton btnSalir = new JButton("Salir");
        // Configurar acciones de los botones
        btnNuevaPartida.addActionListener(e -> {
            reiniciarJuego(); // Inicia una nueva partida al hacer clic.
        });
        btnSalir.addActionListener(e -> {
            System.exit(0); // Cierra la aplicación al hacer clic.
        });
        panelBotones.add(btnNuevaPartida);
        panelBotones.add(btnSalir);
        panelMenu.add(panelBotones, BorderLayout.CENTER);

        return panelMenu;
    }
}