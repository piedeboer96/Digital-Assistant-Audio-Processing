package org.audio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VisualizeLevel {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;

    private static JFrame frame;
    private static JPanel panel;

    public static void visualizeLevel(double level) {

        SwingUtilities.invokeLater(() -> {
            if (frame == null) {
                createAndShowGUI();
            }



            int barHeight = 4 * (int) (level * HEIGHT);
            panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            panel.removeAll();
            panel.add(new JLabel(new ImageIcon(createBarImage(barHeight))));
            frame.pack();
            frame.repaint();
        });
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Volume Level Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.getContentPane().add(panel);

        frame.pack();
        frame.setVisible(true);
    }

    private static Image createBarImage(int height) {
        Image image = new BufferedImage(10, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(Color.GREEN);
        g2d.fillRect(0, HEIGHT - height, 10, height);
        g2d.dispose();
        return image;
    }
}
