package com.rubikcube.game.rubik_cube_game;

public class Rotation {
    public int axis; // 0 = X, 1 = Y, 2 = Z
    public int layer; // -1, 0, 1
    public float angle = 0;
    public float speed = 3f;
    public boolean done = false;

    private App app;

    public Rotation(int axis, int layer, App app) {
        this.axis = axis;
        this.layer = layer;
        this.app = app;
    }

    public void update(float deltaTime) {
        angle += speed * deltaTime * 90f;
        if (angle >= 90f) {
            angle = 90f;
            done = true;
            app.updateCubiePositions(); // ✅ Call the update method here
        }
    }
}

