package ru.astemir.simplehunger.hud;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class HungerHud extends CustomUIHud {

    public static final String ID = "HungerHud";

    public HungerHud(@NonNullDecl PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder builder) {
        builder.append("Hud/HungerHud.ui");
    }

    public void updateValues(GameMode gameMode, float hungerValue){
        UICommandBuilder builder = new UICommandBuilder()
                .set("#Icon.Background", gameMode == GameMode.Creative
                        ? "Hud/Textures/CreativeHungerIcon.png"
                        : "Hud/Textures/HungerIcon.png")
                .set("#ProgressBarAdventure.Value", hungerValue)
                .set("#ProgressBarCreative.Value", hungerValue)
                .set("#ProgressBarAdventure.Visible", gameMode == GameMode.Adventure)
                .set("#ProgressBarCreative.Visible", gameMode == GameMode.Creative);
        this.update(false, builder);
    }
}