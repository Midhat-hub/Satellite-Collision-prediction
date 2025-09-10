import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Satellite> satellites = new ArrayList<>();

        System.out.print("Enter number of satellites: ");
        int n = scanner.nextInt();

        System.out.print("Use 3D simulation? (true/false): ");
        boolean is3D = scanner.nextBoolean();

        // Input satellites
        for (int i = 1; i <= n; i++) {
            System.out.println("Satellite " + i + " details:");
            System.out.print("ID: ");
            String id = scanner.next();
            System.out.print("x y z (space separated): ");
            double x = scanner.nextDouble();
            double y = scanner.nextDouble();
            double z = is3D ? scanner.nextDouble() : 0;
            System.out.print("vx vy vz (space separated): ");
            double vx = scanner.nextDouble();
            double vy = scanner.nextDouble();
            double vz = is3D ? scanner.nextDouble() : 0;
            System.out.print("Radius: ");
            double radius = scanner.nextDouble();

            satellites.add(new Satellite(id, x, y, z, vx, vy, vz, radius));
        }

        System.out.print("Enter number of future time steps to simulate: ");
        int steps = scanner.nextInt();

        boolean collisionDetected = false;

        for (int t = 1; t <= steps; t++) {
            // Move satellites
            for (Satellite s : satellites) s.move();

            // Check for first collision
            outerLoop:
            for (int i = 0; i < satellites.size(); i++) {
                for (int j = i + 1; j < satellites.size(); j++) {
                    if (satellites.get(i).isColliding(satellites.get(j), is3D)) {
                        System.out.println("Collision predicted between " +
                                satellites.get(i).getId() + " and " + satellites.get(j).getId() +
                                " at time step " + t);
                        collisionDetected = true;
                        break outerLoop;
                    }
                }
            }

            if (collisionDetected) break;
        }

        if (!collisionDetected) {
            System.out.println("No collision predicted in " + steps + " time steps.");
        }

        scanner.close();
    }
}
