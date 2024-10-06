package forj.elementcombating.impl.entity.entity;

import forj.elementcombating.DatapackProcessor;
import forj.elementcombating.element.ElementRegistry;
import forj.elementcombating.element.ElementType;
import forj.elementcombating.impl.Elements;
import forj.elementcombating.impl.Utils;
import forj.elementcombating.impl.entity.Entities;
import forj.elementcombating.utils.attribute_creator.AttributeCreator;
import forj.elementcombating.utils.attribute_creator.AttributeCreatorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TornadoEntity extends Entity implements IAnimatable {
    private static final TrackedData<Optional<ElementType>> ELEMENT_TYPE = DataTracker.registerData(TornadoEntity.class, Utils.OptionalElementTypeTrackedDataHandler);
    private static AttributeCreator velocityProvider;

    public static void reloadVelocityProvider() {
        velocityProvider = AttributeCreatorProvider.Instance.getCreator("tornado_mode_attract_velocity");
    }

    public static void init() {
        DatapackProcessor.registerOnReload(TornadoEntity::reloadVelocityProvider);
    }

    private final AnimationFactory factory = new AnimationFactory(this);


    private int damageInterval, lastTicks, duration, level, damageCooldown = 0;
    private float damage, attractRange;
    private LivingEntity owner;


    public TornadoEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public TornadoEntity(World world, LivingEntity owner, int lastTicks, int damageInterval, int duration, int level, float damage, float attractRange) {
        super(Entities.TORNADO_ENTITY, world);
        this.owner = owner;
        this.lastTicks = lastTicks;
        this.attractRange = attractRange;
        this.damageInterval = damageInterval;
        this.duration = duration;
        this.level = level;
        this.damage = damage;
    }

    public ElementType getElementType() {
        return this.dataTracker.get(ELEMENT_TYPE).orElse(Elements.Wind);
    }

    public LivingEntity getOwner() {
        return this.owner;
    }

    public void setElementType(ElementType elementType) {
        this.dataTracker.set(ELEMENT_TYPE, Optional.of(elementType));
    }

    private void attract(LivingEntity target, double strength) {
        strength *= 1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        if (strength <= 0.0) return;
        Vec3d direction = this.getPos().subtract(target.getPos()).normalize();
        target.setVelocity(target.getVelocity().add(direction.multiply(strength)));
        target.velocityModified = true;
        target.velocityDirty = true;
    }

    @Override
    public void tick() {
        super.tick();
        Vec3d velocity = this.getVelocity();
        this.setVelocity(new Vec3d(velocity.x, 0, velocity.z));
        this.move(MovementType.SELF, this.getVelocity());
        if (this.world.isClient || this.getWorld().isClient()) return;
        if (getElementType() == null) {
            setElementType(Elements.Wind);
        }
        if (getOwner() == null || --this.lastTicks < 0) {
            this.discard();
            return;
        }
        //attract entities
        List<LivingEntity> entities = this.world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(this.attractRange), e -> true);
        for (LivingEntity entity : entities) {
            if (entity == getOwner()) continue;
            double strength = velocityProvider.create(
                    Util.make(new HashMap<>(), map -> {
                        map.put("distance", new AttributeCreator.Num(entity.getPos().distanceTo(this.getPos())));
                        map.put("attractRange", new AttributeCreator.Num(this.attractRange));
                    })
            ).get("strength").getDoubleValue();
            attract(entity, strength);
        }
        if (--damageCooldown > 0) return;
        damageCooldown = damageInterval;
        List<LivingEntity> targets = this.world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox(), e -> true);
        for (LivingEntity target : targets) {
            if (target == this.owner) continue;
            getElementType().attack(target, this.owner, this.level, this.duration, this.damage);
        }
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(ELEMENT_TYPE, Optional.empty());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        try {
            String id = nbt.getString("element_type");
            UUID uuid = nbt.getUuid("owner");
            this.lastTicks = nbt.getInt("lastTicks");
            Entity entity = ((ServerWorld) this.world).getEntity(uuid);
            this.damage = nbt.getFloat("damage");
            this.duration = nbt.getInt("duration");
            this.level = nbt.getInt("level");
            this.damageInterval = nbt.getInt("damageInterval");
            this.attractRange = nbt.getFloat("attractRange");
            if (entity instanceof LivingEntity) this.owner = (LivingEntity) entity;
            try {
                this.dataTracker.set(ELEMENT_TYPE, Optional.of(ElementRegistry.getElementTypes().get(id)));
            } catch (IllegalArgumentException e) {
                this.dataTracker.set(ELEMENT_TYPE, Optional.empty());
            }
        } catch (NullPointerException e) {
            if (this.world.isClient) return;
            this.discard();
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.world.isClient) return;
        if (getOwner() == null) {
            this.discard();
            return;
        }
        nbt.putString("element_type", getElementType().getId());
        nbt.putUuid("owner", this.owner.getUuid());
        nbt.putInt("lastTicks", this.lastTicks);
        nbt.putFloat("damage", this.damage);
        nbt.putInt("duration", this.duration);
        nbt.putInt("level", this.level);
        nbt.putInt("damageInterval", this.damageInterval);
        nbt.putFloat("attractRange", this.attractRange);
    }


    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.tornado.loop", true));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
