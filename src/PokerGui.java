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
        mainPanel.add(crearSeleccionModo(), "Modo");

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
        titulo.setBounds(570, 30, 300, 48);
        panel.add(titulo);

        // Botón Atrás con estilo hover
        JButton botonAtras = new JButton("Atrás");
        botonAtras.setFont(new Font("Arial", Font.BOLD, 16));
        botonAtras.setBounds(30, 30, 100, 40);
        botonAtras.setBackground(new Color(0, 102, 204));
        botonAtras.setForeground(Color.WHITE);
        botonAtras.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        botonAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color colorOriginal = new Color(0, 102, 204);
        Color colorHover = new Color(30, 144, 255);

        botonAtras.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                botonAtras.setBackground(colorHover);
            }

            public void mouseExited(MouseEvent e) {
                botonAtras.setBackground(colorOriginal);
            }
        });

        botonAtras.addActionListener(e -> {
            reproducirSonido("C:\\Users\\V16\\Downloads\\SonidoBotton.wav");
            cardLayout.show(mainPanel, "Menu");
        });

        panel.add(botonAtras);

        JButton boton1 = crearBotonConImagen("C:\\Users\\V16\\Downloads\\PokerTexas1.jpg");
        boton1.setBounds(400, 240, 200, 200);
        panel.add(boton1);

        JButton boton2 = crearBotonConImagen("C:\\Users\\V16\\Downloads\\PokerFive.jpg");
        boton2.setBounds(650, 240, 200, 200);
        panel.add(boton2);

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

        final Timer[] timerAmpliar = new Timer[1];
        final Timer[] timerReducir = new Timer[1];
        final int[] currentSize = {sizeInicial};

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (timerReducir[0] != null && timerReducir[0].isRunning()) timerReducir[0].stop();

                timerAmpliar[0] = new Timer(15, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (currentSize[0] < 220) {
                            currentSize[0]++;
                            boton.setIcon(new ImageIcon(imgOriginal.getScaledInstance(currentSize[0], currentSize[0], Image.SCALE_SMOOTH)));
                        } else ((Timer) e.getSource()).stop();
                    }
                });
                timerAmpliar[0].start();
                boton.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            }

            public void mouseExited(MouseEvent e) {
                if (timerAmpliar[0] != null && timerAmpliar[0].isRunning()) timerAmpliar[0].stop();

                timerReducir[0] = new Timer(15, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (currentSize[0] > sizeInicial) {
                            currentSize[0]--;
                            boton.setIcon(new ImageIcon(imgOriginal.getScaledInstance(currentSize[0], currentSize[0], Image.SCALE_SMOOTH)));
                        } else ((Timer) e.getSource()).stop();
                    }
                });
                timerReducir[0].start();
                boton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }
        });

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
