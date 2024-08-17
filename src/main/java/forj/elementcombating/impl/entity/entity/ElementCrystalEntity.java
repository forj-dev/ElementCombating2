package forj.elementcombating.impl.entity.entity;

import forj.elementcombating.element.ElementRegistry;
import forj.elementcombating.element.ElementType;
import forj.elementcombating.element.StatAccessor;
import forj.elementcombating.impl.Elements;
import forj.elementcombating.impl.Utils;
import forj.elementcombating.impl.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ElementCrystalEntity extends Entity implements IAnimatable {
    private static final TrackedData<Optional<ElementType>> ELEMENT_TYPE = DataTracker.registerData(ElementCrystalEntity.class, Utils.OptionalElementTypeTrackedDataHandler);
    private int lastTicks = -1;
    private int ownerId = -1;

    private final AnimationFactory factory = new AnimationFactory(this);

    public ElementCrystalEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public ElementCrystalEntity(World world, LivingEntity owner, int lastTicks) {
        super(Entities.ELEMENT_CRYSTAL_ENTITY, world);
        this.ownerId = owner.getId();
        this.lastTicks = lastTicks;
    }

    public ElementType getElementType() {
        return this.dataTracker.get(ELEMENT_TYPE).orElse(Elements.Stone);
    }

    public Entity getOwner() {
        return this.world.getEntityById(this.ownerId);
    }

    public void setElementType(ElementType elementType) {
        this.dataTracker.set(ELEMENT_TYPE, Optional.of(elementType));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().multiply(0.95).add(0.0, -0.025, 0.0));
        }
        this.move(MovementType.SELF, this.getVelocity());
        if (this.world.isClient) return;
        if (getElementType() == null) {
            setElementType(Elements.Stone);
        }
        if (getOwner() == null || --this.lastTicks < 0) {
            this.discard();
            return;
        }
        List<LivingEntity> entities = this.world.getEntitiesByClass(LivingEntity.class, this.getBoundingBox(), e -> true);
        for (LivingEntity entity : entities) {
            if (entity.equals(getOwner())) {
                this.discard();
                ((StatAccessor) entity).getShieldManager().set(false, getElementType(), 140, 15);
                return;
            }
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
            if (entity instanceof LivingEntity) this.ownerId = entity.getId();
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
        if (getOwner() == null) {
            this.discard();
            return;
        }
        nbt.putString("element_type", getElementType().getId());
        nbt.putUuid("owner", getOwner().getUuid());
        nbt.putInt("lastTicks", this.lastTicks);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.element_crystal.loop", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
