package ru.astemir.simplehunger.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import ru.astemir.simplehunger.hud.HudStacker;
import ru.astemir.simplehunger.hud.HungerHud;
import javax.annotation.Nonnull;

public class HungerSystem extends EntityTickingSystem<EntityStore>{

    private static HungerSystem instance;
    private final ComponentType<EntityStore, HungerComponent> hungerComponentType;
    private final SimpleHungerConfig config;

    private DamageCause cachedDamageCause;
    private int cachedHungerIndex = -1;

    @SuppressWarnings("unchecked")
    public HungerSystem(ComponentType<EntityStore, HungerComponent> hungerComponentType, SimpleHungerConfig config) {
        instance = this;
        Object bridgeObj = System.getProperties().get("simplehunger.bridge");
        if (bridgeObj instanceof java.util.Map) {
            java.util.Map<String, Object> bridge = (java.util.Map<String, Object>) bridgeObj;
            java.util.function.BiConsumer<Object, Object> callback = (entity, item) -> {
                try {
                    this.eatFood((Ref<EntityStore>) entity, (Item) item);
                } catch (ClassCastException e) {
                    e.printStackTrace(System.err);
                }
            };
            bridge.put("callback", callback);
        }
        this.hungerComponentType = hungerComponentType;
        this.config = config;
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        HungerComponent hungerData = archetypeChunk.getComponent(index, hungerComponentType);
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        EntityStatMap statMap = store.getComponent(ref, EntityStatMap.getComponentType());
        int hIdx = getHungerIndex();
        EntityStatValue hungerStat = statMap.get(hIdx);
        float currentHunger = hungerStat.get();
        float nextHunger = currentHunger;
        if (player.getGameMode() == GameMode.Adventure) {
            hungerData.addElapsedTime(dt);
            if (hungerData.getElapsedTime() >= config.saturationLossTimeSeconds) {
                hungerData.resetElapsedTime();
                nextHunger = clampHunger(hungerStat, currentHunger - config.saturationLossSpeed);
            }

            if (nextHunger <= hungerStat.getMin()) {
                hungerData.addStarvingElapsedTime(dt);

                if (hungerData.getStarvingElapsedTime() >= config.starvingDamageTimeSeconds) {
                    hungerData.resetStarvingElapsedTime();
                    if (this.cachedDamageCause == null){
                        this.cachedDamageCause = DamageCause.getAssetMap().getAsset("Starvation");
                    }
                    Damage damage = new Damage(Damage.NULL_SOURCE, this.cachedDamageCause, config.starvingDamage);
                    DamageSystems.executeDamage(ref, commandBuffer, damage);

                    int staminaIdx = DefaultEntityStatTypes.getStamina();
                    statMap.subtractStatValue(staminaIdx, config.starvingStaminaDamage);
                }
            } else {
                hungerData.resetStarvingElapsedTime();
            }
        } else if (currentHunger < hungerStat.getMax()) {
            nextHunger = clampHunger(hungerStat, currentHunger + (config.creativeRegenSpeed * dt));
        }
        if (nextHunger != currentHunger) {
            statMap.setStatValue(hIdx, nextHunger);
        }
        float diff = nextHunger - hungerData.getLerpedHunger();
        if (Math.abs(diff) < 0.1f) {
            hungerData.setLerpedHunger(nextHunger);
        } else {
            hungerData.setLerpedHunger(hungerData.getLerpedHunger() + diff * Math.min(dt * 5.0f, 1.0f));
        }
        HudStacker.ifGet(player, HungerHud.ID, HungerHud.class, hud -> {
            hud.updateValues(player.getGameMode(), hungerData.getLerpedHunger() / hungerStat.getMax());
        });
    }

    public void eatFood(Ref<EntityStore> ref, Item item) {
        float saturation = this.config.itemIds.getOrDefault(item.getId(), this.config.defaultSaturation);
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null){
            int hungerIdx = this.getHungerIndex();
            EntityStatMap statMap = store.getComponent(player.getReference(), EntityStatMap.getComponentType());
            EntityStatValue hungerStat = statMap.get(hungerIdx);
            statMap.setStatValue(hungerIdx, clampHunger(hungerStat, hungerStat.get()+saturation));
        }
    }

    private float clampHunger(EntityStatValue hungerStat, float value) {
       return Math.min(hungerStat.getMax(), Math.max(hungerStat.getMin(), value));
    }

    private int getHungerIndex() {
        if (this.cachedHungerIndex == -1){
            this.cachedHungerIndex = EntityStatType.getAssetMap().getIndex("Hunger");
        }
        return this.cachedHungerIndex;
    }


    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), hungerComponentType);
    }

    public static HungerSystem getInstance() {
        return instance;
    }
}
