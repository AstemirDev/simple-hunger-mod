package ru.astemir.simplehunger.hud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import ru.astemir.simplehunger.util.Traverse;
import java.util.*;
import java.util.function.Consumer;
public class HudStacker {

    private static final Traverse<CustomUIHud> traverse = new Traverse<>();

    private static final String[] HUD_GETTER_NAMES = {"getCustomHuds", "getHuds", "getSubHuds"};

    public static void open(HudManager hudManager, PlayerRef playerRef, CustomUIHud newHud) {
        if (newHud == null) return;
        CustomUIHud existingHud = hudManager.getCustomHud();
        Map<Class<?>, CustomUIHud> allHuds = new LinkedHashMap<>();
        unpack(existingHud, allHuds);
        unpack(newHud, allHuds);
        if (allHuds.isEmpty()) return;
        List<CustomUIHud> list = new ArrayList<>(allHuds.values());
        CustomUIHud finalHud = (list.size() == 1) ? list.getFirst() : new HudContainer(playerRef, list);
        hudManager.setCustomHud(playerRef, finalHud);
    }

    private static void unpack(CustomUIHud hud, Map<Class<?>, CustomUIHud> collector) {
        if (hud == null) return;
        if (hud instanceof HudContainer container) {
            for (CustomUIHud child : container.children) {
                unpack(child, collector);
            }
            return;
        }
        Object result = tryGetHuds(hud);
        if (result != null) {
            if (result instanceof Map<?, ?> map) {
                for (Object value : map.values()) {
                    if (value instanceof CustomUIHud) unpack((CustomUIHud) value, collector);
                }
                return;
            } else if (result instanceof Collection<?> col) {
                for (Object value : col) {
                    if (value instanceof CustomUIHud) unpack((CustomUIHud) value, collector);
                }
                return;
            }
        }
        collector.put(hud.getClass(), hud);
    }

    public static <T extends CustomUIHud> void ifGet(HudManager hudManager, Class<T> hudClass, Consumer<T> consumer) {
        T found = findRecursive(hudManager.getCustomHud(), hudClass);
        if (found != null) {
            consumer.accept(found);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomUIHud> T findRecursive(CustomUIHud current, Class<T> targetClass) {
        if (current == null) return null;
        if (targetClass.isInstance(current)) return (T) current;
        if (current instanceof HudContainer container) {
            for (CustomUIHud child : container.children) {
                T found = findRecursive(child, targetClass);
                if (found != null) return found;
            }
        }
        Object result = tryGetHuds(current);
        if (result != null) {
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
        }
        return null;
    }

    private static Object tryGetHuds(CustomUIHud hud) {
        for (String methodName : HUD_GETTER_NAMES) {
            try {
                Object result = traverse.invoke(hud, methodName);
                if (result != null) {
                    return result;
                }
            } catch (Exception _) {
            }
        }
        return null;
    }

    private static class HudContainer extends CustomUIHud {

        private final List<CustomUIHud> children;

        public HudContainer(@NonNullDecl PlayerRef playerRef, List<CustomUIHud> children) {
            super(playerRef);
            this.children = children;
        }

        @Override
        protected void build(@NonNullDecl UICommandBuilder builder) {
            for (CustomUIHud child : this.children) {
                if (child != null) {
                    traverse.invoke(child, "build", builder);
                }
            }
        }
    }
}
