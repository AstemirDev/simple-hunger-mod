package ru.astemir.simplehunger;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import ru.astemir.simplehunger.hud.HudStacker;
import ru.astemir.simplehunger.hud.HungerHud;
import ru.astemir.simplehunger.system.HungerComponent;
import ru.astemir.simplehunger.system.HungerSystem;
import ru.astemir.simplehunger.system.SimpleHungerConfig;

public class SimpleHungerMod extends JavaPlugin {

    private final Config<SimpleHungerConfig> cfg;
    private ComponentType<EntityStore, HungerComponent> hungerComponentType;

    public SimpleHungerMod(@NonNullDecl JavaPluginInit init) {
        super(init);
        this.cfg = this.withConfig("SimpleHunger", SimpleHungerConfig.CODEC);
    }

    @Override
    protected void setup() {
        this.hungerComponentType = this.getEntityStoreRegistry().registerComponent(HungerComponent.class, HungerComponent::new);
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event->{
            Player player = event.getPlayer();
            Ref<EntityStore> ref = player.getReference();
            Store<EntityStore> store = ref.getStore();
            store.addComponent(ref, this.hungerComponentType);
            PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
            HudStacker.open(player,playerRef , new HungerHud(playerRef), HungerHud.ID);
        });
        this.cfg.save();
    }

    @Override
    protected void start() {
        this.getEntityStoreRegistry().registerSystem(new HungerSystem(this.hungerComponentType, this.cfg.get()));
    }

}
