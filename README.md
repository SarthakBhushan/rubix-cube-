# rubix-cube-
# 🧊 3D Rubik's Cube in Java using OpenGL

A fully interactive **3D Rubik's Cube simulator** built using **Java** and **OpenGL (LWJGL)**. The project demonstrates real-time 3D rendering, camera movement, and animated cube rotations using a combination of graphics programming and logic.

---

## 🧩 Features

- 🧱 Realistic 3D Rubik’s Cube visualization
- 🎮 Mouse and keyboard controls for rotating layers and the entire cube
- 🔄 Smooth animation for each 90° rotation
- 🧠 Internal logic to track cube state
- 🔧 Built using OpenGL through **LWJGL (Lightweight Java Game Library)**

---

## 🧰 Technologies Used

- Java (JDK 8+)
- LWJGL (Lightweight Java Game Library)
- OpenGL (via LWJGL bindings)
- Matrix & Quaternion math for 3D transformations
- Custom shaders (optional for advanced lighting)

---

## 🎮 Controls

- 🖱️ Mouse drag — Rotate the entire cube
- ⌨️ Keys (e.g. `U`, `R`, `L`, `D`, `F`, `B`) — Rotate cube layers
- ⌨️ `S` — Shuffle
- ⌨️ `R` — Reset

You can customize controls in the `InputHandler` class.

---

## 📁 Project Structure

```
RubiksCube3D/
├── Main.java
├── Cube.java
├── Cubie.java
├── Rotation.java
```

---

## ⚙️ Setup Instructions

### 1. Install Prerequisites
- Java (JDK 8+)
- [LWJGL](https://www.lwjgl.org/)
- OpenGL-compatible graphics card

### 2. Clone and Configure
```bash
git clone https://github.com/yourusername/RubiksCube3D.git
cd RubiksCube3D
```

### 3. Configure LWJGL libraries in your IDE (Eclipse/IntelliJ)

- Link LWJGL JARs and native libraries (DLLs/SOs) in the project settings

### 4. Run the Main class
```bash
javac Main.java
java Main
```

---

## 🧠 How It Works

- The cube is composed of 27 `Cubie` objects.
- Each rotation is managed by a `Rotation` object that interpolates between angles.
- The cube maintains logical and visual consistency after each turn.
- `Camera.java` handles 3D perspective and transformations.
- `Renderer.java` handles drawing and OpenGL state.

---

## 🛠 Future Improvements

- 🎨 Add rotation and camera movements
- 💡 Implement lighting using shaders
- 🧩 Cube solving algorithm (visual or step-by-step)
- 📷 Save screenshots or record animations
- 🌐 Online multiplayer cube scrambling

---

## 🙏 Acknowledgments

- LWJGL team for the powerful OpenGL bindings
- Math libraries and tutorials from the OpenGL community
- Rubik’s Cube enthusiasts who inspired this project
