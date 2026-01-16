package ru.astemir.simplehunger.system;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class HungerComponent implements Component<EntityStore> {

    private float lerpedHunger;
    private float elapsedTime;
    private float starvingElapsedTime;

    public HungerComponent() {
        this.lerpedHunger = 100.0F;
        this.elapsedTime = 0f;
        this.starvingElapsedTime = 0f;
    }

    public HungerComponent(HungerComponent other) {
        this.lerpedHunger = other.lerpedHunger;
        this.elapsedTime = other.elapsedTime;
        this.starvingElapsedTime = other.starvingElapsedTime;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new HungerComponent(this);
    }

    public float getLerpedHunger() { return lerpedHunger; }

    public void setLerpedHunger(float value) { this.lerpedHunger = value; }

    public float getElapsedTime() { return elapsedTime; }

    public void addElapsedTime(float dt) { this.elapsedTime += dt; }

    public void resetElapsedTime() { this.elapsedTime = 0f; }

    public float getStarvingElapsedTime() { return starvingElapsedTime; }

    public void addStarvingElapsedTime(float dt) { this.starvingElapsedTime += dt; }

    public void resetStarvingElapsedTime() { this.starvingElapsedTime = 0f; }
}