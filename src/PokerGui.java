import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;

public class PokerGui extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;
    Clip musicaFondo;

    int cantidadJugadores = 2;
    String modoSeleccionado = ""; // Nueva variable para guardar el modo elegido

    public PokerGui() {
        setTitle("Juego con Interfaz");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(crearVentanaInicial(), "Inicio");
        mainPanel.add(crearMenuPrincipal(), "Menu");
        mainPanel.add(crearSeleccionModo(), "Modo");
        mainPanel.add(crearSeleccionJugadores(), "SeleccionJugadores");

        setContentPane(mainPanel);
        setVisible(true);

        reproducirMusica("C:\\Users\\V16\\Downloads\\CancionCasino.wav");
    }

    private JPanel crearVentanaInicial() {
        JPanel panel = new JPanel() {
            Image fondo = new ImageIcon("C:\\Users\\V16\\Downloads\\PortadaPoker.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        JButton botonIniciar = crearBotonEstilizado("Iniciar");
        botonIniciar.setBounds(725, 750, 350, 60);
        botonIniciar.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            cardLayout.show(mainPanel, "Menu");
        });

        panel.add(botonIniciar);
        return panel;
    }

    private JPanel crearMenuPrincipal() {
        JPanel panel = new JPanel() {
            Image fondo = new ImageIcon("C:\\Users\\V16\\Downloads\\FondoMenu.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        JButton botonJugar = crearBotonEstilizado("Jugar");
        botonJugar.setBounds(225, 380, 250, 60);
        botonJugar.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            cardLayout.show(mainPanel, "Modo");
        });

        JButton botonCreditos = crearBotonEstilizado("Créditos");
        botonCreditos.setBounds(225, 480, 250, 60);
        botonCreditos.addActionListener(e -> mostrarCreditos());

        JButton botonSalir = crearBotonEstilizado("Salir");
        botonSalir.setBounds(225, 580, 250, 60);
        botonSalir.addActionListener(e -> System.exit(0));

        panel.add(botonJugar);
        panel.add(botonCreditos);
        panel.add(botonSalir);
        return panel;
    }

    private JPanel crearSeleccionModo() {
        JPanel panel = new JPanel() {
            Image fondo = new ImageIcon("C:\\Users\\V16\\Downloads\\FondoModo.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        JLabel titulo = new JLabel("Selecciona modo");
        titulo.setFont(new Font("Serif", Font.BOLD, 40));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(900, 30, 300, 48);
        panel.add(titulo);

        JButton botonAtras = crearBotonEstilizado("Atrás");
        botonAtras.setBounds(30, 30, 100, 40);
        botonAtras.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            cardLayout.show(mainPanel, "Menu");
        });
        panel.add(botonAtras);

        JButton boton1 = crearBotonConImagen("C:\\Users\\V16\\Downloads\\PokerTexas1.jpg");
        boton1.setBounds(750, 340, 200, 200);
        panel.add(boton1);
        boton1.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            modoSeleccionado = "TexasHoldEm";
            cardLayout.show(mainPanel, "SeleccionJugadores");
        });

        JButton boton2 = crearBotonConImagen("C:\\Users\\V16\\Downloads\\PokerFive.jpg");
        boton2.setBounds(1050, 340, 200, 200);
        panel.add(boton2);
        boton2.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            modoSeleccionado = "FiveCardDraw";
            cardLayout.show(mainPanel, "SeleccionJugadores");
        });

        return panel;
    }

    private JPanel crearSeleccionJugadores() {
        JPanel panel = new JPanel() {
            Image fondo = new ImageIcon("C:\\Users\\V16\\Downloads\\FondoJugadores.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        JLabel titulo = new JLabel("Selecciona cantidad de jugadores");
        titulo.setFont(new Font("Serif", Font.BOLD, 36));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(770, 50, 600, 50);
        panel.add(titulo);

        JLabel labelJugadores = new JLabel(cantidadJugadores + " Jugadores", SwingConstants.CENTER);
        labelJugadores.setFont(new Font("Serif", Font.BOLD, 30));
        labelJugadores.setForeground(Color.WHITE);
        labelJugadores.setBounds(900, 370, 200, 60);
        panel.add(labelJugadores);

        JButton botonIzquierda = new JButton("<");
        botonIzquierda.setFont(new Font("Arial", Font.BOLD, 40));
        botonIzquierda.setBounds(825, 370, 80, 60);
        botonIzquierda.addActionListener(e -> {
            if (cantidadJugadores > 2) {
                cantidadJugadores--;
                labelJugadores.setText(cantidadJugadores + " Jugadores");
                reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            }
        });
        panel.add(botonIzquierda);

        JButton botonDerecha = new JButton(">");
        botonDerecha.setFont(new Font("Arial", Font.BOLD, 40));
        botonDerecha.setBounds(1100, 370, 80, 60);
        botonDerecha.addActionListener(e -> {
            if (cantidadJugadores < 8) {
                cantidadJugadores++;
                labelJugadores.setText(cantidadJugadores + " Jugadores");
                reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            }
        });
        panel.add(botonDerecha);

        JButton botonAtras = crearBotonEstilizado("Atrás");
        botonAtras.setBounds(30, 30, 100, 40);
        botonAtras.addActionListener(e -> cardLayout.show(mainPanel, "Modo"));
        panel.add(botonAtras);

        JButton botonContinuar = crearBotonEstilizado("Continuar");
        botonContinuar.setBounds(925, 600, 150, 50);
        botonContinuar.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");

            if (modoSeleccionado.equals("TexasHoldEm")) {
                TableroTexasHoldEm tableroTexas = new TableroTexasHoldEm(cantidadJugadores);
                mainPanel.add(tableroTexas, "JuegoTexas");
                cardLayout.show(mainPanel, "JuegoTexas");
            } else if (modoSeleccionado.equals("FiveCardDraw")) {
                TableroFiveCardDraw tableroFive = new TableroFiveCardDraw(cantidadJugadores);
                mainPanel.add(tableroFive, "JuegoFive");
                cardLayout.show(mainPanel, "JuegoFive");
            }
        });
        panel.add(botonContinuar);

        return panel;
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 18));
        Color colorOriginal = new Color(0, 102, 204);
        Color colorHover = new Color(30, 144, 255);

        boton.setBackground(colorOriginal);
        boton.setForeground(Color.WHITE);
        boton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorHover);
            }

            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorOriginal);
            }
        });

        return boton;
    }

    private JButton crearBotonConImagen(String ruta) {
        ImageIcon icono = new ImageIcon(ruta);
        Image imgOriginal = icono.getImage();

        JButton boton = new JButton();
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        boton.setContentAreaFilled(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int sizeInicial = 200;
        boton.setIcon(new ImageIcon(imgOriginal.getScaledInstance(sizeInicial, sizeInicial, Image.SCALE_SMOOTH)));
        boton.setPreferredSize(new Dimension(sizeInicial, sizeInicial));

        return boton;
    }

    private void mostrarCreditos() {
        JOptionPane.showMessageDialog(this,
                "Desarrollado por:\n- Juan Orduna\n\n\nGracias por jugar :)",
                "Créditos",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void reproducirMusica(String nombreArchivo) {
        try {
            File archivo = new File(nombreArchivo);
            if (archivo.exists()) {
                musicaFondo = AudioSystem.getClip();
                musicaFondo.open(AudioSystem.getAudioInputStream(archivo));
                musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                System.out.println("Archivo de música no encontrado: " + nombreArchivo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reproducirSonido(String nombreArchivo) {
        try {
            File archivo = new File(nombreArchivo);
            if (archivo.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(archivo));
                clip.start();
            } else {
                System.out.println("Archivo de sonido no encontrado: " + nombreArchivo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PokerGui::new);
    }
}
