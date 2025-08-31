import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

public class LeavingSat extends Application {

    private List<Satellite> satellites = new ArrayList<>();
    private Group satelliteGroup = new Group();
    private TextArea log = new TextArea();

    private static final double EARTH_RADIUS = 50;

    private static final double SCALE = 2.0; // scale for positions

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // 3D Scene setup
        Sphere earth = new Sphere(EARTH_RADIUS);
        PhongMaterial earthMat = new PhongMaterial(Color.BLUE);
        earth.setMaterial(earthMat);

        Group world = new Group(earth, satelliteGroup);

        // Rotate world slightly for better view
        world.getTransforms().addAll(new Rotate(-20, Rotate.X_AXIS),
                new Rotate(-30, Rotate.Y_AXIS));

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-600);
        camera.setTranslateY(-50);
        camera.setNearClip(0.1);
        camera.setFarClip(2000);

        SubScene subScene = new SubScene(world, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);

        // UI Controls
        VBox controls = new VBox(5);
        controls.setPrefWidth(200);
        TextField idField = new TextField("Sat-1");
        TextField posField = new TextField("200,0,0"); // x,y,z
        TextField velField = new TextField("0,1,0");   // vx,vy,vz
        TextField radiusField = new TextField("10");
        Button addBtn = new Button("Add Satellite");
        Button predictBtn = new Button("Predict Collisions");

        addBtn.setOnAction(e -> {
            String id = idField.getText();
            double[] pos = parseTriple(posField.getText());
            double[] vel = parseTriple(velField.getText());
            double radius = Double.parseDouble(radiusField.getText());

            Satellite sat = new Satellite(id, pos[0], pos[1], pos[2],
                    vel[0], vel[1], vel[2], radius);
            satellites.add(sat);

            Sphere satSphere = new Sphere(radius * SCALE / 5); // scaled for visibility
            PhongMaterial mat = new PhongMaterial(Color.RED);
            satSphere.setMaterial(mat);
            sat.setSphere(satSphere);
            satelliteGroup.getChildren().add(satSphere);

            log.appendText("Added " + id + "\n");
        });

        predictBtn.setOnAction(e -> predictFuturePositions(100));

        log.setPrefHeight(150);
        controls.getChildren().addAll(new Label("ID:"), idField,
                new Label("Position (x,y,z):"), posField,
                new Label("Velocity (vx,vy,vz):"), velField,
                new Label("Radius:"), radiusField,
                addBtn, predictBtn, log);

        root.setCenter(subScene);
        root.setRight(controls);

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("3D Satellite Collision Simulator");
        stage.show();

        // Animation loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (Satellite s : satellites) {
                    s.updatePosition();
                }
                checkCollisions();
            }
        };
        timer.start();
    }

    private void checkCollisions() {
        for (int i = 0; i < satellites.size(); i++) {
            for (int j = i + 1; j < satellites.size(); j++) {
                if (satellites.get(i).collidesWith(satellites.get(j))) {
                    log.appendText("⚠ Collision detected between "
                            + satellites.get(i).id + " and "
                            + satellites.get(j).id + "\n");
                }
            }
        }
    }

    private void predictFuturePositions(int steps) {
        log.appendText("Prediction (" + steps + " steps ahead):\n");
        for (int step = 1; step <= steps; step++) {
            for (int i = 0; i < satellites.size(); i++) {
                for (int j = i + 1; j < satellites.size(); j++) {
                    if (satellites.get(i).futureCollision(satellites.get(j), step)) {
                        log.appendText("   Possible collision between "
                                + satellites.get(i).id + " and "
                                + satellites.get(j).id + " at step " + step + "\n");
                    }
                }
            }
        }
    }

    private double[] parseTriple(String text) {
        String[] parts = text.split(",");
        return new double[]{Double.parseDouble(parts[0].trim()),
                Double.parseDouble(parts[1].trim()),
                Double.parseDouble(parts[2].trim())};
    }

    // Satellite Class
    static class Satellite {
        String id;
        double x, y, z;
        double vx, vy, vz;
        double radius;
        Sphere sphere;

        Satellite(String id, double x, double y, double z,
                  double vx, double vy, double vz, double radius) {
            this.id = id;
            this.x = x; this.y = y; this.z = z;
            this.vx = vx; this.vy = vy; this.vz = vz;
            this.radius = radius;
        }

        void setSphere(Sphere s) { this.sphere = s; }

        void updatePosition() {
            x += vx * 0.1; // scale down speed
            y += vy * 0.1;
            z += vz * 0.1;
            if (sphere != null) {
                sphere.setTranslateX(x * SCALE);
                sphere.setTranslateY(y * SCALE);
                sphere.setTranslateZ(z * SCALE);
            }
        }

        boolean collidesWith(Satellite other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double dz = this.z - other.z;
            double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
            return distance < this.radius + other.radius;
        }

        boolean futureCollision(Satellite other, int steps) {
            double fx = this.x + this.vx * 0.1 * steps;
            double fy = this.y + this.vy * 0.1 * steps;
            double fz = this.z + this.vz * 0.1 * steps;

            double ox = other.x + other.vx * 0.1 * steps;
            double oy = other.y + other.vy * 0.1 * steps;
            double oz = other.z + other.vz * 0.1 * steps;

            double dx = fx - ox;
            double dy = fy - oy;
            double dz = fz - oz;
            double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
            return distance < this.radius + other.radius;
        }
    }
}
