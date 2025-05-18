import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TableroTexasHoldEm extends JFrame {
        Baraja baraja;
        TexasHoldEm texasHoldEm;
        Jugador jugador;
        JuegoPoker juegoPoker;

        public TableroTexasHoldEm() {
                crearVentanaJuego();
        }

        private JPanel crearVentanaJuego() {
                JPanel panel = new JPanel() {
                        Image fondo = new ImageIcon("C:\\Users\\V16\\Downloads\\mesa.png").getImage();
                        @Override
                        protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                        }
                };
                panel.setLayout(null);

                String[] acciones = {"Fold", "Check", "Call", "Raise", "Bet"};
                int yPos = 950;
                for (int i = 0; i < acciones.length; i++) {
                        JButton boton = crearBotonEstilizado(acciones[i]);
                        boton.setBounds(650 + (i * 130), yPos, 120, 40);
                        panel.add(boton);
                }

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
                        }
                });

                boton.setBackground(colorOriginal);
                return boton;
        }
}
