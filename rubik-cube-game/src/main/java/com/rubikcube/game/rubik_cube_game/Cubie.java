package com.rubikcube.game.rubik_cube_game;

import static org.lwjgl.opengl.GL11.*;

import java.util.HashMap;
import java.util.Map;

public class Cubie {

    public int x, y, z;
    public float animAngle = 0;
    public boolean rotating = false;
    public float[] rotationAxis = {0, 0, 0};

    // Face colors: up, down, front, back, left, right
    public Map<String, float[]> faces = new HashMap<>();

    // Predefined colors
    private static final float[] white  = {1, 1, 1};
    private static final float[] yellow = {1, 1, 0};
    private static final float[] red    = {1, 0, 0};
    private static final float[] orange = {1, 0.5f, 0};
    private static final float[] blue   = {0, 0, 1};
    private static final float[] green  = {0, 1, 0};
    private static final float[] black  = {0.1f, 0.1f, 0.1f}; // internal/default

    public Cubie(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;

        // Set face colors based on position
        faces.put("up",    y == 1 ? white  : black);
        faces.put("down",  y == -1 ? yellow : black);
        faces.put("front", z == 1 ? red    : black);
        faces.put("back",  z == -1 ? orange : black);
        faces.put("left",  x == -1 ? blue   : black);
        faces.put("right", x == 1 ? green  : black);
    }

    public void draw() {
        glPushMatrix();

        // Position and scale
        glTranslatef(x * 1.02f, y * 1.02f, z * 1.02f);
        glScalef(0.48f, 0.48f, 0.48f);

        drawCubie(); // Draw faces

        glPopMatrix();
    }

    private void drawCubie() {
        glBegin(GL_QUADS);

        // Front face (Z+)
        glColor3fv(faces.get("front"));
        glVertex3f(-1, -1, 1); glVertex3f(1, -1, 1);
        glVertex3f(1,  1, 1); glVertex3f(-1, 1, 1);

        // Back face (Z-)
        glColor3fv(faces.get("back"));
        glVertex3f(-1, -1, -1); glVertex3f(-1, 1, -1);
        glVertex3f(1,  1, -1); glVertex3f(1, -1, -1);

        // Top face (Y+)
        glColor3fv(faces.get("up"));
        glVertex3f(-1, 1, -1); glVertex3f(-1, 1, 1);
        glVertex3f(1,  1, 1); glVertex3f(1, 1, -1);

        // Bottom face (Y-)
        glColor3fv(faces.get("down"));
        glVertex3f(-1, -1, -1); glVertex3f(1, -1, -1);
        glVertex3f(1, -1, 1); glVertex3f(-1, -1, 1);

        // Right face (X+)
        glColor3fv(faces.get("right"));
        glVertex3f(1, -1, -1); glVertex3f(1, 1, -1);
        glVertex3f(1,  1, 1); glVertex3f(1, -1, 1);

        // Left face (X-)
        glColor3fv(faces.get("left"));
        glVertex3f(-1, -1, -1); glVertex3f(-1, -1, 1);
        glVertex3f(-1, 1, 1); glVertex3f(-1, 1, -1);

        glEnd();
    }

    public void rotate(int axis) {
        // Copy old face colors
        float[] up    = faces.get("up");
        float[] down  = faces.get("down");
        float[] front = faces.get("front");
        float[] back  = faces.get("back");
        float[] left  = faces.get("left");
        float[] right = faces.get("right");

        switch (axis) {
            case 0: // X-axis rotation (up/down/front/back)
                faces.put("up",    back);
                faces.put("back",  down);
                faces.put("down",  front);
                faces.put("front", up);
                break;

            case 1: // Y-axis rotation (front/back/left/right)
                faces.put("front", right);
                faces.put("right", back);
                faces.put("back",  left);
                faces.put("left",  front);
                break;

            case 2: // Z-axis rotation (up/down/left/right)
                faces.put("up",    left);
                faces.put("left",  down);
                faces.put("down",  right);
                faces.put("right", up);
                break;
        }
    }


    private void glColor3fv(float[] color) {
        glColor3f(color[0], color[1], color[2]);
    }
}

