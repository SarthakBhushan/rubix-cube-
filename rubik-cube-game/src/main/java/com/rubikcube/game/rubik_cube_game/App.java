package com.rubikcube.game.rubik_cube_game;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class App {
	
	private long window;
	private List<Cubie> cubies = new ArrayList<>();
	private Rotation currentRotation = null;
	private static final String[] ROTATION_KEYS = {"U", "D", "L", "R", "F", "B"};
    private static final int[] AXES = {0, 1, 2}; // X, Y, Z
    private static final int[] DIRECTIONS = {-1, 1}; // -1 (counterclockwise), 1 (clockwise)
    private Random random = new Random();


    public void run() {
        System.out.println("Starting Rubik's Cube...");
        init();
        shuffle();
        loop();

        // Free resources
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(800, 600, "Rubik's Cube", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        


        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    cubies.add(new Cubie(x, y, z));
                }
            }
        }
    }
    
 // This is not part of LWJGL, so we define it ourselves
    private void gluPerspective(float fov, float aspect, float zNear, float zFar) {
        float y_scale = (float)(1f / Math.tan(Math.toRadians(fov / 2f)));
        float x_scale = y_scale / aspect;
        float frustum_length = zFar - zNear;

        float[] matrix = new float[16];

        matrix[0] = x_scale;
        matrix[5] = y_scale;
        matrix[10] = -((zFar + zNear) / frustum_length);
        matrix[11] = -1f;
        matrix[14] = -((2 * zNear * zFar) / frustum_length);
        matrix[15] = 0f;

        glLoadMatrixf(matrix);
    }

    
    private void loop() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);

        // Setup projection
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = 800f / 600f;
        gluPerspective(45.0f, aspect, 0.1f, 100.0f);

        glMatrixMode(GL_MODELVIEW);

        long lastTime = System.nanoTime();

        while (!glfwWindowShouldClose(window)) {
            long now = System.nanoTime();
            float deltaTime = (now - lastTime) / 1_000_000_000f;
            lastTime = now;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();
            glTranslatef(0.0f, 0.0f, -10.0f);

            // 🔑 Check for input
            if (currentRotation == null) {
                if (glfwGetKey(window, GLFW_KEY_U) == GLFW_PRESS) {
                    currentRotation = new Rotation(1, 1,this); // Top layer
                } else if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
                    currentRotation = new Rotation(1, -1,this); // Bottom layer
                } else if (glfwGetKey(window, GLFW_KEY_L) == GLFW_PRESS) {
                    currentRotation = new Rotation(0, -1,this); // Left
                } else if (glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS) {
                    currentRotation = new Rotation(0, 1,this); // Right
                } else if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS) {
                    currentRotation = new Rotation(2, 1,this); // Front
                } else if (glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS) {
                    currentRotation = new Rotation(2, -1,this); // Back
                }
            }

            // 🔁 Update animation angle
            if (currentRotation != null && !currentRotation.done) {
                currentRotation.update(deltaTime);
            }

            // 🌀 Draw ROTATING LAYER
            if (currentRotation != null && !currentRotation.done) {
                glPushMatrix();

                float[] axisVec = new float[]{0f, 0f, 0f};
                axisVec[currentRotation.axis] = 1f;

                float layerOffset = currentRotation.layer * 1.02f;

                // move to layer center
                switch (currentRotation.axis) {
                    case 0 -> glTranslatef(layerOffset, 0f, 0f); // X
                    case 1 -> glTranslatef(0f, layerOffset, 0f); // Y
                    case 2 -> glTranslatef(0f, 0f, layerOffset); // Z
                }

                glRotatef(currentRotation.angle, axisVec[0], axisVec[1], axisVec[2]);

                // move back
                switch (currentRotation.axis) {
                    case 0 -> glTranslatef(-layerOffset, 0f, 0f);
                    case 1 -> glTranslatef(0f, -layerOffset, 0f);
                    case 2 -> glTranslatef(0f, 0f, -layerOffset);
                }

                for (Cubie c : cubies) {
                    if (getAxisValue(c, currentRotation.axis) == currentRotation.layer) {
                        c.draw();
                    }
                }

                glPopMatrix();
            }

            // 🧱 Draw STATIC layers
            for (Cubie c : cubies) {
                if (currentRotation == null || getAxisValue(c, currentRotation.axis) != currentRotation.layer) {
                    c.draw();
                }
            }

            // ✅ Finish rotation
            if (currentRotation != null && currentRotation.done) {
                updateCubiePositions(); // <- critical
            }


            glfwSwapBuffers(window);
            glfwPollEvents();
            
        }
    }
    
    private int getAxisValue(Cubie c, int axis) {
        return switch (axis) {
            case 0 -> c.x;
            case 1 -> c.y;
            case 2 -> c.z;
            default -> 0;
        };
    }
    
    void updateCubiePositions() {
        List<Cubie> rotated = new ArrayList<>();

        for (Cubie c : cubies) {
            if (getAxisValue(c, currentRotation.axis) == currentRotation.layer) {
                rotated.add(c);
            }
        }

        for (Cubie c : rotated) {
            int x = c.x, y = c.y, z = c.z;
            switch (currentRotation.axis) {
                case 0 -> { // X
                    c.y = -z;
                    c.z = y;
                }
                case 1 -> { // Y
                    c.x = z;
                    c.z = -x;
                }
                case 2 -> { // Z
                    c.x = -y;
                    c.y = x;
                }
            }

            // ✅ Rotate face colors ONLY after full 90-degree turn
            c.rotate(currentRotation.axis);
        }

        currentRotation = null;
    }

    
    void rotateCubieFaces(Cubie c, Map<String, float[]> oldFaces, int axis) {
        switch (axis) {
            case 0 -> { // X-axis
                c.faces.put("up",    oldFaces.get("back"));
                c.faces.put("back",  oldFaces.get("down"));
                c.faces.put("down",  oldFaces.get("front"));
                c.faces.put("front", oldFaces.get("up"));
            }
            case 1 -> { // Y-axis
                c.faces.put("front", oldFaces.get("right"));
                c.faces.put("right", oldFaces.get("back"));
                c.faces.put("back",  oldFaces.get("left"));
                c.faces.put("left",  oldFaces.get("front"));
            }
            case 2 -> { // Z-axis
                c.faces.put("up",    oldFaces.get("left"));
                c.faces.put("left",  oldFaces.get("down"));
                c.faces.put("down",  oldFaces.get("right"));
                c.faces.put("right", oldFaces.get("up"));
            }
        }     
    }
    public void shuffle() {
        int numRotations = 20; // Number of random rotations for shuffling
        for (int i = 0; i < numRotations; i++) {
            String randomMove = getRandomRotation();
            applyRotation(randomMove);
        }
    }

    private String getRandomRotation() {
        // Randomly select a rotation axis and direction (clockwise or counterclockwise)
        String rotationKey = ROTATION_KEYS[random.nextInt(6)];  // Choose from "U", "D", "L", "R", "F", "B"
        int axis = AXES[random.nextInt(3)]; // Randomly pick X, Y, or Z
        int direction = DIRECTIONS[random.nextInt(2)]; // Randomly choose clockwise (1) or counterclockwise (-1)

        return rotationKey + direction; // Combine rotation and direction (e.g., "U1" or "L-1")
    }

    private void applyRotation(String rotation) {
        switch (rotation.charAt(0)) {
            case 'U': currentRotation = new Rotation(1, 1, this); break; // Up
            case 'D': currentRotation = new Rotation(1, -1, this); break; // Down
            case 'L': currentRotation = new Rotation(0, -1, this); break; // Left
            case 'R': currentRotation = new Rotation(0, 1, this); break;  // Right
            case 'F': currentRotation = new Rotation(2, 1, this); break;  // Front
            case 'B': currentRotation = new Rotation(2, -1, this); break; // Back
        }
    }

    public static void main( String[] args ){
    	
    	new App().run();
    }
}
