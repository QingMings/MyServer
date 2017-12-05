import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class AwtCanvasWrapper extends Application {
    private static final int W = 200;
    private static final int H = 100;

    @Override public void start(final Stage stage) throws Exception {

        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {

            JPanel jPanel = new JPanel();
            jPanel.add(new CustomAwtCanvas(W, H));
            swingNode.setContent(jPanel);
        });




        stage.setScene(new Scene(new Group(swingNode), W, H));
//        stage.setResizable(false);
        stage.show();
    }



    private class CustomAwtCanvas extends Canvas {
        public CustomAwtCanvas(int width, int height) {
            setSize(width, height);
        }

        public void paint(Graphics g) {
            Graphics2D g2;
            g2 = (Graphics2D) g;
            g2.setColor(Color.GRAY);
            g2.fillRect(
                    0, 0,
                    (int) getSize().getWidth(), (int) getSize().getHeight()
            );
            g2.setColor(Color.BLACK);
            g2.drawString("It is a custom canvas area", 25, 50);
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}