package forj.elementcombating;

import forj.elementcombating.element.DamageInfoAccessor;
import forj.elementcombating.element.network.PlayerBurstNetworking;
import forj.elementcombating.impl.Elements;
import forj.elementcombating.impl.Utils;
import forj.elementcombating.impl.entity.Entities;
import forj.elementcombating.impl.status_effect.ElementEffects;
import forj.elementcombating.item.ElementGemCraftRecipe;
import forj.elementcombating.item.ElementGemUpgradeRecipe;
import forj.elementcombating.item.Items;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib3.GeckoLib;

import java.util.Random;

public class ElementCombating implements ModInitializer {

    public static final Random RANDOM = new Random();

    public static final Identifier CoolDownSync = new Identifier("element_combating", "cool_down_sync");

    public static final Identifier ChargeSync = new Identifier("element_combating", "charge_sync");

    public static final Identifier ChargeClear = new Identifier("element_combating", "charge_clear");

    public static final Identifier AttributeSync = new Identifier("element_combating", "attribute_sync");

    public static final Identifier ShieldSync = new Identifier("element_combating", "shield_sync");

    public static final Identifier SyncRequest = new Identifier("element_combating", "sync_request");

    public static final Identifier PlayerBurst = new Identifier("element_combating", "player_burst");

    public static final Identifier EffectSync = new Identifier("element_combating", "effect_sync");

    public static final Identifier ArrowElementSync = new Identifier("element_combating", "arrow_element_sync");

    public static final KeyBinding BurstKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key_bind.element_combating.burst",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.key.element_combating.main"
    ));

    public static final KeyBinding SyncKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key_bind.element_combating.sync",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F6,
            "category.key.element_combating.main"
    ));

    @Override
    public void onInitialize() {
        GeckoLib.initialize();
        Utils.init();
        DatapackProcessor.register();
        Elements.init();
        ElementEffects.register();
        initBypassesShieldSources();
        Commands.init();
        PlayerBurstNetworking.initServer();
        Items.init();
        Entities.init();
        ElementGemCraftRecipe.init();
        ElementGemUpgradeRecipe.init();
    }

    public static void initBypassesShieldSources(){
        ((DamageInfoAccessor)DamageSource.IN_FIRE).bypassesShield();
        ((DamageInfoAccessor)DamageSource.ON_FIRE).bypassesShield();
        ((DamageInfoAccessor)DamageSource.LAVA).bypassesShield();
        ((DamageInfoAccessor)DamageSource.HOT_FLOOR).bypassesShield();
        ((DamageInfoAccessor)DamageSource.IN_WALL).bypassesShield();
        ((DamageInfoAccessor)DamageSource.CRAMMING).bypassesShield();
        ((DamageInfoAccessor)DamageSource.DROWN).bypassesShield();
        ((DamageInfoAccessor)DamageSource.STARVE).bypassesShield();
        ((DamageInfoAccessor)DamageSource.FALL).bypassesShield();
        ((DamageInfoAccessor)DamageSource.OUT_OF_WORLD).bypassesShield();
        ((DamageInfoAccessor)DamageSource.GENERIC).bypassesShield();
        ((DamageInfoAccessor)DamageSource.MAGIC).bypassesShield();
        ((DamageInfoAccessor)DamageSource.WITHER).bypassesShield();
        ((DamageInfoAccessor)DamageSource.DRAGON_BREATH).bypassesShield();
        ((DamageInfoAccessor)DamageSource.DRYOUT).bypassesShield();
        ((DamageInfoAccessor)DamageSource.SWEET_BERRY_BUSH).bypassesShield();
        ((DamageInfoAccessor)DamageSource.FREEZE).bypassesShield();
        ((DamageInfoAccessor)DamageSource.STALAGMITE).bypassesShield();
    }
}
