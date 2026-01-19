package ru.astemir.simplehunger.hud;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import ru.astemir.simplehunger.util.Traverse;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class HudStacker {

    private static final Traverse<CustomUIHud> traverse = new Traverse<>();
    private static final String[] HUD_GETTER_NAMES = {"getCustomHuds", "getHuds", "getSubHuds"};
    private static final String MHUD_CORE_CLASS = "com.buuz135.mhud.MultipleHUD";
    private static final String MHUD_CONTAINER_CLASS = "com.buuz135.mhud.MultipleCustomUIHud";

    public static void open(Player player, PlayerRef playerRef, CustomUIHud newHud, String hudIdentifier) {
        if (newHud == null) return;
        if (tryOpenViaMhud(player, playerRef, newHud, hudIdentifier)) {
            return;
        }
        HudManager hudManager = player.getHudManager();
        CustomUIHud existingHud = hudManager.getCustomHud();
        Map<String, CustomUIHud> allHuds = new LinkedHashMap<>();
        unpack(existingHud, allHuds);
        allHuds.put(hudIdentifier, newHud);
        CustomUIHud finalHud;
        if (allHuds.size() == 1) {
            finalHud = allHuds.values().iterator().next();
        } else {
            finalHud = tryCreateMhudContainer(playerRef, allHuds);
            if (finalHud == null) {
                finalHud = new HudContainer(playerRef, allHuds);
            }
        }
        hudManager.setCustomHud(playerRef, finalHud);
    }

    private static boolean tryOpenViaMhud(Player player, PlayerRef playerRef, CustomUIHud newHud, String hudIdentifier) {
        try {
            Class<?> mhudClass = Class.forName(MHUD_CORE_CLASS);
            Method getInstance = mhudClass.getMethod("getInstance");
            Object instance = getInstance.invoke(null);
            if (instance != null) {
                Method setHud = mhudClass.getMethod("setCustomHud", Player.class, PlayerRef.class, String.class, CustomUIHud.class);
                setHud.invoke(instance, player, playerRef, hudIdentifier, newHud);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static void unpack(CustomUIHud hud, Map<String, CustomUIHud> collector) {
        if (hud == null) return;
        if (hud instanceof HudContainer || hud.getClass().getName().equals(MHUD_CONTAINER_CLASS)) {
            try {
                Object res = traverse.invoke(hud, "getCustomHuds");
                if (res instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        if (entry.getValue() instanceof CustomUIHud) {
                            collector.put(entry.getKey().toString(), (CustomUIHud) entry.getValue());
                        }
                    }
                    return;
                }
            } catch (Exception ignored) {}
        }
        collector.put(hud.getClass().getName(), hud);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomUIHud> void ifGet(Player player, String hudIdentifier, Class<T> hudClass, Consumer<T> consumer) {
        T found = null;
        try {
            CustomUIHud current = player.getHudManager().getCustomHud();
            if (current != null && current.getClass().getName().equals(MHUD_CONTAINER_CLASS)) {
                Method getCustomHudsMethod = current.getClass().getMethod("getCustomHuds");
                Map<String, CustomUIHud> huds = (Map<String, CustomUIHud>) getCustomHudsMethod.invoke(current);
                CustomUIHud targeted = huds.get(hudIdentifier);
                if (hudClass.isInstance(targeted)) {
                    found = (T) targeted;
                }
            }
        } catch (Exception ignored) {}
        if (found == null) {
            found = findRecursive(player.getHudManager().getCustomHud(), hudClass);
        }
        if (found != null) {
            consumer.accept(found);
        }
    }


    @SuppressWarnings("unchecked")
    private static <T extends CustomUIHud> T findRecursive(CustomUIHud current, Class<T> targetClass) {
        if (current == null) return null;
        if (targetClass.isInstance(current)) return (T) current;
        Object result = tryGetHuds(current);
        if (result instanceof Map<?, ?> map) {
            for (Object value : map.values()) {
                if (value instanceof CustomUIHud) {
                    T found = findRecursive((CustomUIHud) value, targetClass);
                    if (found != null) return found;
                }
            }
        } else if (result instanceof Collection<?> col) {
            for (Object value : col) {
                if (value instanceof CustomUIHud) {
                    T found = findRecursive((CustomUIHud) value, targetClass);
                    if (found != null) return found;
                }
            }
        }
        if (current instanceof HudContainer container) {
            for (CustomUIHud child : container.children.values()) {
                T found = findRecursive(child, targetClass);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static Object tryGetHuds(CustomUIHud hud) {
        for (String methodName : HUD_GETTER_NAMES) {
            try {
                Object result = traverse.invoke(hud, methodName);
                if (result != null) return result;
            } catch (Exception _) {}
        }
        return null;
    }

    private static CustomUIHud tryCreateMhudContainer(PlayerRef playerRef, Map<String, CustomUIHud> huds) {
        try {
            Class<?> clazz = Class.forName(MHUD_CONTAINER_CLASS);
            Constructor<?> constructor = clazz.getConstructor(PlayerRef.class, HashMap.class);
            return (CustomUIHud) constructor.newInstance(playerRef, new HashMap<>(huds));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static class HudContainer extends CustomUIHud {

        private final Map<String, CustomUIHud> children;

        public HudContainer(PlayerRef playerRef, Map<String, CustomUIHud> children) {
            super(playerRef);
            this.children = children;
        }

        @Override
        protected void build(@NonNullDecl UICommandBuilder builder) {
            for (CustomUIHud child : children.values()) {
                if (child != null) {
                    traverse.invoke(child, "build", builder);
                }
            }
        }

        public HashMap<String, CustomUIHud> getCustomHuds() {
            return new HashMap<>(children);
        }
    }
}
