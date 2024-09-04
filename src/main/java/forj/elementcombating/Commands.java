package forj.elementcombating;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import forj.elementcombating.element.*;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("give-attribute")
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .executes(context -> {
                            ServerPlayerEntity sender = context.getSource().getPlayer();
                            ItemStack item = sender.getMainHandStack();
                            if (item == null) return -1;
                            NbtCompound nbt = item.getNbt();
                            if (nbt == null) nbt = new NbtCompound();
                            nbt.put("element_attribute",
                                    new ElementAttribute(AttributeType.ITEM_SKILL, 1, ElementProviders.NORMAL).save());
                            item.setNbt(nbt);
                            return 0;
                        })
                        .then(argument("type", ElementTypeArgumentType.elementType())
                                .then(argument("mode", AttackModeArgumentType.attackMode())
                                        .executes(context -> {
                                            ServerPlayerEntity sender = context.getSource().getPlayer();
                                            ItemStack item = sender.getMainHandStack();
                                            if (item == null) return -1;
                                            NbtCompound nbt = item.getNbt();
                                            if (nbt == null) nbt = new NbtCompound();
                                            nbt.put(
                                                    "element_attribute",
                                                    new ElementAttribute(AttributeType.ITEM_SKILL,
                                                            ElementTypeArgumentType.getElementType(context, "type"),
                                                            AttackModeArgumentType.getAttackMode(context, "mode"),
                                                            1
                                                    ).save()
                                            );
                                            item.setNbt(nbt);
                                            return 0;
                                        })
                                )
                        )
        ));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("element-attack")
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .then(argument("targets", EntityArgumentType.entities())
                                .then(argument("element", ElementTypeArgumentType.elementType())
                                        .executes(context -> {
                                            ServerPlayerEntity sender = context.getSource().getPlayer();
                                            int c = 0;
                                            Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "targets");
                                            ElementType elementType = ElementTypeArgumentType.getElementType(context, "element");
                                            for (Entity entity : entities) {
                                                if (!(entity instanceof LivingEntity target)) continue;
                                                c++;
                                                elementType.attack(target, sender, 1, 100, 1);
                                            }
                                            return c;
                                        })
                                ))));
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("element-charge")
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .then(argument("targets", EntityArgumentType.entities())
                                .then(argument("element", ElementTypeArgumentType.elementType())
                                        .then(argument("amount", FloatArgumentType.floatArg(0, 1))
                                                .executes(context -> {
                                                    ElementType elementType = ElementTypeArgumentType.getElementType(context, "element");
                                                    float amount = FloatArgumentType.getFloat(context, "amount");
                                                    int c = 0;
                                                    for (Entity entity : EntityArgumentType.getEntities(context, "targets")) {
                                                        if (!(entity instanceof LivingEntity target)) continue;
                                                        ((StatAccessor) target).getChargeManager().charge(elementType, amount);
                                                        c++;
                                                    }
                                                    return c;
                                                }))))));
    }

    public static class ElementTypeArgumentType implements ArgumentType<ElementType> {
        @Override
        public ElementType parse(StringReader reader) throws CommandSyntaxException {
            String id = reader.readUnquotedString();
            try {
                return ElementRegistry.getElementTypes().get(id);
            } catch (IllegalArgumentException e) {
                throw new SimpleCommandExceptionType(new TranslatableText("commands.element.not_found").append(id)).create();
            }
        }

        public static ElementType getElementType(CommandContext<ServerCommandSource> context, String name) {
            return context.getArgument(name, ElementType.class);
        }

        public static ElementTypeArgumentType elementType() {
            return new ElementTypeArgumentType();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            for (ElementType elementType : ElementRegistry.getElementTypes()) {
                String id = elementType.getId();
                if (id.startsWith(builder.getRemainingLowerCase())) {
                    builder.suggest(id);
                }
            }
            return builder.buildFuture();
        }
    }

    public static class AttackModeArgumentType implements ArgumentType<AttackMode> {
        @Override
        public AttackMode parse(StringReader reader) throws CommandSyntaxException {
            String id = reader.readUnquotedString();
            try {
                return ElementRegistry.getAttackMode(id);
            } catch (IllegalArgumentException e) {
                throw new SimpleCommandExceptionType(new TranslatableText("commands.attack_mode.not_found").append(id)).create();
            }
        }

        public static AttackMode getAttackMode(CommandContext<ServerCommandSource> context, String name) {
            return context.getArgument(name, AttackMode.class);
        }

        public static AttackModeArgumentType attackMode() {
            return new AttackModeArgumentType();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            for (AttackMode attackMode : ElementRegistry.getAttackModes()) {
                String id = attackMode.getId();
                if (id.startsWith(builder.getRemainingLowerCase())) {
                    builder.suggest(id);
                }
            }
            return builder.buildFuture();
        }
    }
}
