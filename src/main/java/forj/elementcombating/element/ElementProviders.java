package forj.elementcombating.element;

import net.minecraft.entity.EntityType;

import static net.minecraft.entity.EntityType.*;

public class ElementProviders {
    public static final ElementProvider NORMAL = new ElementProvider();
    public static final ElementProvider OVERWORLD_UNDEAD = new ElementProvider(0.85f, 1.2f, 1f, 1.2f, 0.6f, 1.2f, 0.25f, 1.1f, 1f);
    public static final ElementProvider OVERWORLD_CAVE = new ElementProvider(1.2f, 1.1f, 1f, 0.9f, 1.1f, 1f, 0.25f, 1.5f, 0.9f);
    public static final ElementProvider PLAYER = new ElementProvider(1f, 1f, 1f, 1f, 0.7f, 0.8f, 0.65f, 0.95f, 1f);
    public static final ElementProvider WITHER = new ElementProvider(1f, 0.2f, 0.5f, 0.1f, 0.2f, 1.6f, 1.5f, 0.9f, 0.7f);
    public static final ElementProvider FLAME = new ElementProvider(3f, 0f, 0f, 0.1f, 0.5f, 0.8f, 0.0001f, 0.5f, 0.2f);
    public static final ElementProvider NETHER = new ElementProvider(2f, 0f, 0.15f, 0.8f, 0.6f, 1.2f, 0.4f, 1f, 0.9f);
    public static final ElementProvider ENDER = new ElementProvider(0.1f, 0f, 0.1f, 1f, 0.3f, 1f, 2f, 1f, 0.4f);
    public static final ElementProvider FLYING = new ElementProvider(1f, 1f, 1f, 1f, 1f, 1f, 0.5f, 0.5f, 3.5f);

    public static ElementProvider fromEntity(EntityType<?> type) {
        if (type == ZOMBIE || type == SKELETON || type == HUSK || type == STRAY || type == DROWNED || type == ZOMBIE_VILLAGER)
            return OVERWORLD_UNDEAD;
        if (type == CAVE_SPIDER || type == SILVERFISH || type == SLIME)
            return OVERWORLD_CAVE;
        if (type == EntityType.PLAYER)
            return PLAYER;
        if (type == WITHER_SKELETON || type == EntityType.WITHER)
            return WITHER;
        if (type == BLAZE)
            return FLAME;
        if (type == ZOMBIFIED_PIGLIN || type == PIGLIN || type == PIGLIN_BRUTE || type == MAGMA_CUBE || type == GHAST)
            return NETHER;
        if (type == ENDERMAN || type == ENDERMITE || type == SHULKER || type == ENDER_DRAGON)
            return ENDER;
        if (type == PHANTOM || type == BEE)
            return FLYING;
        return NORMAL;
    }
}
