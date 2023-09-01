package org.flitter.app;

import org.flitter.app.service.Utils;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;


public class CustomNotification extends JDialog {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 100;

    public CustomNotification(Frame owner, String title, String message) {
        super(owner);
        setTitle(title);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);


        RoundedPanel roundedPanel = new RoundedPanel();
        roundedPanel.setLayout(new BorderLayout());
        roundedPanel.setBackground(Color.WHITE);
        roundedPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        ImageIcon icon = createCustomIcon();
        JLabel avatarLabel = new JLabel(icon);
        roundedPanel.add(avatarLabel, BorderLayout.WEST);


        JLabel messageLabel = new JLabel(message + "  ");

        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        roundedPanel.add(messageLabel, BorderLayout.CENTER);

        add(roundedPanel, BorderLayout.CENTER);


        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - WIDTH - 5;
        int y = screenSize.height - HEIGHT - 5;
        setBounds(x, y, WIDTH, HEIGHT);
        setAlwaysOnTop(true);
        playSound();

        new Thread(() -> {
            try {
                Thread.sleep(7000);
            } catch (Exception e) {

            }
            SwingUtilities.invokeLater(() -> dispose());
        }).start();
    }

    private ImageIcon createCustomIcon() {
        Utils utils = new Utils();
        ImageIcon icon = utils.readImage("flitterRounded.png");
        Image image = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    public void playSound() {

        try (InputStream in = getClass().getResourceAsStream("/icq-message.wav")) {

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in);


            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);


            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {

        }
    }

    class RoundedPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(getBackground());
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        }
    }

}
