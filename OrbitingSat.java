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

public class OrbitingSat extends Application {

    private List<Satellite> satellites = new ArrayList<>();
    private Group satelliteGroup = new Group();
    private TextArea log = new TextArea();

    private static final double EARTH_RADIUS = 50;
    private static final double SCALE = 2.0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // Earth sphere
        Sphere earth = new Sphere(EARTH_RADIUS);
        PhongMaterial earthMat = new PhongMaterial(Color.BLUE);
        earth.setMaterial(earthMat);

        Group world = new Group(earth, satelliteGroup);
        world.getTransforms().addAll(new Rotate(-20, Rotate.X_AXIS),
                new Rotate(-30, Rotate.Y_AXIS));

        // Camera
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
        controls.setPrefWidth(220);

        TextField idField = new TextField("Sat-1");
        TextField orbitRadiusField = new TextField("150");  // orbit radius from Earth
        TextField angularVelocityField = new TextField("0.05"); // radians per frame
        TextField radiusField = new TextField("10");        // satellite size

        Button addBtn = new Button("Add Satellite");
        Button predictBtn = new Button("Predict Collisions");

        addBtn.setOnAction(e -> {
            String id = idField.getText();
            double orbitRadius = Double.parseDouble(orbitRadiusField.getText());
            double angularVelocity = Double.parseDouble(angularVelocityField.getText());
            double radius = Double.parseDouble(radiusField.getText());

            Satellite sat = new Satellite(id, orbitRadius, angularVelocity, radius);
            satellites.add(sat);

            Sphere satSphere = new Sphere(radius * SCALE / 5);
            PhongMaterial mat = new PhongMaterial(Color.RED);
            satSphere.setMaterial(mat);
            sat.setSphere(satSphere);
            satelliteGroup.getChildren().add(satSphere);

            log.appendText("Added " + id + " in orbit radius " + orbitRadius + "\n");
        });

        predictBtn.setOnAction(e -> predictFuturePositions(360)); // simulate 360 steps

        log.setPrefHeight(150);
        controls.getChildren().addAll(
                new Label("ID:"), idField,
                new Label("Orbit Radius:"), orbitRadiusField,
                new Label("Angular Velocity (rad/frame):"), angularVelocityField,
                new Label("Radius:"), radiusField,
                addBtn, predictBtn, log
        );

        root.setCenter(subScene);
        root.setRight(controls);

        Scene scene = new Scene(root, 1050, 600);
        stage.setScene(scene);
        stage.setTitle("3D Satellite Orbit Simulator");
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
                            + satellites.get(i).id + " and " + satellites.get(j).id + "\n");
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

    // Satellite class with orbit
    static class Satellite {
        String id;
        double orbitRadius;
        double angularVelocity; // radians per frame
        double theta = 0;       // current angle
        double radius;
        Sphere sphere;

        Satellite(String id, double orbitRadius, double angularVelocity, double radius) {
            this.id = id;
            this.orbitRadius = orbitRadius;
            this.angularVelocity = angularVelocity;
            this.radius = radius;
        }

        void setSphere(Sphere s) { this.sphere = s; }

        void updatePosition() {
            theta += angularVelocity;
            double x = orbitRadius * Math.cos(theta);
            double z = orbitRadius * Math.sin(theta);
            double y = 0; // flat orbit, can add inclination later
            if (sphere != null) {
                sphere.setTranslateX(x * SCALE);
                sphere.setTranslateY(y * SCALE);
                sphere.setTranslateZ(z * SCALE);
            }
        }

        boolean collidesWith(Satellite other) {
            double x1 = orbitRadius * Math.cos(theta);
            double z1 = orbitRadius * Math.sin(theta);
            double x2 = other.orbitRadius * Math.cos(other.theta);
            double z2 = other.orbitRadius * Math.sin(other.theta);
            double distance = Math.sqrt((x1 - x2)*(x1 - x2) + (z1 - z2)*(z1 - z2));
            return distance < this.radius + other.radius;
        }

        boolean futureCollision(Satellite other, int steps) {
            double futureTheta1 = theta + angularVelocity * steps;
            double futureTheta2 = other.theta + other.angularVelocity * steps;
            double x1 = orbitRadius * Math.cos(futureTheta1);
            double z1 = orbitRadius * Math.sin(futureTheta1);
            double x2 = other.orbitRadius * Math.cos(futureTheta2);
            double z2 = other.orbitRadius * Math.sin(futureTheta2);
            double distance = Math.sqrt((x1 - x2)*(x1 - x2) + (z1 - z2)*(z1 - z2));
            return distance < this.radius + other.radius;
        }
    }
}

