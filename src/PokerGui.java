import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;

public class PokerGui extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;
    Clip musicaFondo;

    public PokerGui() {
        setTitle("Juego con Interfaz");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(crearVentanaInicial(), "Inicio");
        mainPanel.add(crearMenuPrincipal(), "Menu");
        mainPanel.add(crearSeleccionModo(), "ModoJuego");

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
        botonIniciar.setBounds(275, 450, 250, 60);
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
        botonJugar.setBounds(275, 300, 250, 60);
        botonJugar.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            cardLayout.show(mainPanel, "ModoJuego");
        });

        JButton botonCreditos = crearBotonEstilizado("Créditos");
        botonCreditos.setBounds(275, 400, 250, 60);
        botonCreditos.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            mostrarCreditos();
        });

        JButton botonSalir = crearBotonEstilizado("Salir");
        botonSalir.setBounds(275, 500, 250, 60);
        botonSalir.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            if (musicaFondo != null && musicaFondo.isRunning()) {
                musicaFondo.stop();
            }
            System.exit(0);
        });

        panel.add(botonJugar);
        panel.add(botonCreditos);
        panel.add(botonSalir);
        return panel;
    }

    private JPanel crearSeleccionModo() {
        JPanel panel = new JPanel() {
            Image fondo = new ImageIcon("C:\\Users\\V16\\Downloads\\FondoModoJuego.jpg").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        JLabel titulo = new JLabel("Selecciona Modo");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(270, 20, 400, 40);
        panel.add(titulo);

        // Botón 1: Texas Hold'em
        JLabel labelTexas = new JLabel("Poker Texas Hold'em");
        labelTexas.setFont(new Font("Arial", Font.BOLD, 18));
        labelTexas.setForeground(Color.WHITE);
        labelTexas.setBounds(150, 100, 200, 30);
        panel.add(labelTexas);

        JButton botonTexas = crearBotonConImagen("C:\\Users\\V16\\Downloads\\texas.png");
        botonTexas.setBounds(130, 140, 200, 200);
        botonTexas.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Seleccionaste Texas Hold'em");
        });
        panel.add(botonTexas);

        // Botón 2: Five Cards
        JLabel labelFive = new JLabel("Poker Five Cards");
        labelFive.setFont(new Font("Arial", Font.BOLD, 18));
        labelFive.setForeground(Color.WHITE);
        labelFive.setBounds(480, 100, 200, 30);
        panel.add(labelFive);

        JButton botonFive = crearBotonConImagen("C:\\Users\\V16\\Downloads\\fivecards.png");
        botonFive.setBounds(460, 140, 200, 200);
        botonFive.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Seleccionaste Five Cards");
        });
        panel.add(botonFive);

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
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorOriginal);
            }
        });

        return boton;
    }

    private JButton crearBotonConImagen(String ruta) {
        ImageIcon icono = new ImageIcon(ruta);
        Image img = icono.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JButton boton = new JButton(new ImageIcon(img));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        boton.setContentAreaFilled(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                boton.setSize(220, 220);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                boton.setSize(200, 200);
            }
        });

        return boton;
    }

    private void mostrarCreditos() {
        JOptionPane.showMessageDialog(this,
                "Desarrollado por:\n- Tu Nombre Aquí\n- Otro Colaborador\n\nGracias por jugar :)",
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
