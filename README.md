# Satellite-Collision-prediction
🚀 Satellite Collision Detection (Java)

This project is a Java-based simulation tool for detecting potential satellite collisions in space. It uses JavaFX for visualization and allows interactive configuration of satellite parameters such as position, velocity, and orbit radius.

🛰 Theoretical Background
1. Satellite Motion

Satellites in space move according to Newton's laws of motion and gravity. In this simulation, we consider two types of motion:

Linear Motion (Leaving Satellites):
Satellites not in a stable orbit move in straight lines based on their initial speed and direction. Over time, they can drift away from Earth if not in orbit.

Orbiting Motion (Orbiting Satellites):
Satellites in stable orbits revolve around Earth in circular paths. The orbit is defined by its distance from Earth and the speed of the satellite. Faster satellites complete an orbit more quickly than slower ones.

2. Collision Detection

A collision occurs when two satellites come close enough that their physical sizes would overlap. The program continuously checks the distance between satellites to detect possible collisions.

Real-Time Detection:
The simulator constantly monitors satellite positions and reports collisions as they happen.

Future Prediction:
By simulating satellite movement ahead in time, the program can predict potential collisions before they occur.

3. 3D Visualization

Earth is visualized at the center as a blue sphere.

Satellites are displayed as red spheres.

The 3D view provides a perspective to see satellite orbits around Earth.

Rotation and camera movement make the simulation interactive and easier to understand.

4. User Interaction

Users can add satellites with custom orbit distance, speed, and size.

Observe real-time orbits and collision alerts.

Adjust parameters to simulate fast or slow satellites and different orbit distances.

📂 Project Structure

BasicSatelliteSimulation.java → Simple 2D/3D satellite motion with collision detection.

LeavingSatelliteSimulation.java → Satellites moving in straight lines away from Earth.

OrbitingSatelliteSimulation.java → Satellites orbiting Earth with real-time collision detection.

🔧 Requirements

Java 11+

JavaFX SDK

VM Options (IntelliJ/Eclipse):
--module-path "PATH_TO_FX/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics


(Replace PATH_TO_FX with your JavaFX SDK path)

▶️ How to Run

Clone the repository:

git clone https://github.com/your-username/satellite-collision-detection.git
cd satellite-collision-detection/src


Compile and run any simulation:

javac OrbitingSatelliteSimulation.java
java OrbitingSatelliteSimulation


Use the UI to add satellites and predict collisions.

🚀 Future Improvements

Add more realistic orbital physics.

Support for elliptical orbits and inclined planes.

Handle multiple satellites simultaneously.

Export simulation results for analysis.
