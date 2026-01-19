package ru.astemir.simplehunger.mixin;

import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.ModifyInventoryInteraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModifyInventoryInteraction.class)
public abstract class ModifyInventoryMixin {

    static {
        if (System.getProperty("simplehunger.bridge") == null) {
            java.util.Map<String, Object> bridgeMap = new java.util.concurrent.ConcurrentHashMap<>();
            System.getProperties().put("simplehunger.bridge", bridgeMap);
        }
    }

    @Inject(method = "firstRun", at = @At("RETURN"))
    private void simpleHunger$afterModify(InteractionType type, InteractionContext context, CooldownHandler cooldownHandler, CallbackInfo ci) {
        if (context.getChain() != null) {
            String rootId = context.getChain().getRootInteraction().getId();
            if (rootId != null && (rootId.contains("Food"))) {
                Object item = context.getOriginalItemType();
                Object entity = context.getEntity();
                if (item == null) return;
                try {
                    Object bridgeObj = System.getProperties().get("simplehunger.bridge");
                    if (bridgeObj instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> bridge = (java.util.Map<String, Object>) bridgeObj;
                        Object callback = bridge.get("callback");
                        if (callback != null) {
                            java.lang.reflect.Method acceptMethod = callback.getClass().getMethod("accept", Object.class, Object.class);
                            acceptMethod.setAccessible(true);
                            acceptMethod.invoke(callback, entity, item);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}