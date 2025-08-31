
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class basic extends Application {

    private List<Satellite> satellites = new ArrayList<>();
    private boolean running = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        TextArea log = new TextArea();
        log.setEditable(false);

        Button startBtn = new Button("Start Simulation");
        startBtn.setOnAction(e -> running = true);

        root.setCenter(canvas);
        root.setBottom(log);
        root.setTop(startBtn);

        Scene scene = new Scene(root, 800, 650);
        stage.setScene(scene);
        stage.setTitle("Satellite Collision Detection");
        stage.show();

        // Example: Add satellites manually (later, add user input form)
        satellites.add(new Satellite("Sat-1", 100, 100, 2, 1, 10));
        satellites.add(new Satellite("Sat-2", 700, 500, -2, -1, 10));

        // Animation loop
        new Thread(() -> {
            while (true) {
                if (running) {
                    gc.clearRect(0, 0, 800, 600);

                    for (Satellite s : satellites) {
                        s.updatePosition();
                        gc.setFill(Color.BLUE);
                        gc.fillOval(s.x, s.y, s.radius * 2, s.radius * 2);
                    }

                    // Collision detection
                    for (int i = 0; i < satellites.size(); i++) {
                        for (int j = i + 1; j < satellites.size(); j++) {
                            if (satellites.get(i).collidesWith(satellites.get(j))) {
                                log.appendText("Collision detected between " +
                                        satellites.get(i).id + " and " +
                                        satellites.get(j).id + "\n");
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(50); // control simulation speed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Satellite class
    class Satellite {
        String id;
        double x, y;
        double vx, vy;
        double radius;

        Satellite(String id, double x, double y, double vx, double vy, double radius) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = radius;
        }

        void updatePosition() {
            x += vx;
            y += vy;
        }

        boolean collidesWith(Satellite other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            return distance < this.radius + other.radius;
        }
    }
}
