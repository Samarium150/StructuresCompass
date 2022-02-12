package io.github.samarium150.minecraft.mod.structures_compass.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class for setting the property of the compass' model
 * @see net.minecraft.client.renderer.item.ItemProperties
 */
@OnlyIn(Dist.CLIENT)
public final class StructuresCompassItemProperty implements ClampedItemPropertyFunction {
    
    private static class Angle {
        
        private double rotation = 0D;
        private double delta = 0D;
        private long lastUpdateTick;
        
        private Angle() { }
        
        private boolean isOutdated(long lastUpdateTick) {
            return this.lastUpdateTick != lastUpdateTick;
        }
        
        private double wobble(ClientLevel world, double amount) {
            if (world != null && isOutdated(world.getGameTime())) {
                lastUpdateTick = world.getGameTime();
                double d0 = amount - rotation;
                d0 = Mth.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                delta += d0 * 0.1D;
                delta *= 0.8D;
                rotation = Mth.positiveModulo(rotation + delta, 1.0D);
            }
            return rotation;
        }
    }
    
    private final Angle r1 = new Angle();
    private final Angle r2 = new Angle();
    
    private double getFrameRotation(@Nonnull ItemFrame frameEntity) {
        Direction direction = frameEntity.getDirection();
        int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
        return Mth.wrapDegrees(180 + direction.get2DDataValue() * 90 + frameEntity.getRotation() * 45 + i);
    }
    
    private static boolean closeEnough(@Nonnull Entity entity, @Nonnull BlockPos pos) {
        return entity.position().distanceToSqr(
            (double)pos.getX() + 0.5D,
            entity.position().y(),
            (double)pos.getZ() + 0.5D
        ) < (double)1.0E-5F;
    }
    
    private static double getAngle(@Nonnull Vec3 vector, @Nonnull Entity entity) {
        return Math.atan2(vector.z() - entity.getZ(), vector.x() - entity.getX());
    }
    
    /**
     * Determine the angle of the needle on compass
     * <p>
     * It should point to the block pos of the structure found by the compass,
     * otherwise to a random position
     * @param stack ItemStack
     * @param world ClientWorld
     * @param livingEntity player
     * @return the angle of the needle
     */
    @Override
    public float unclampedCall(@Nonnull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int p) {
        Entity entity = livingEntity != null ? livingEntity : stack.getEntityRepresentation();
        if (entity == null) return 0F;
        if (world == null && entity.level instanceof ClientLevel)
            world = (ClientLevel) entity.level;
        if (world == null) return 0F;
        BlockPos pos = StructuresCompassItem.getPos(stack);
        if (pos != null
                && world.dimension().location().toString().equals(StructuresCompassItem.getDimension(stack))
                && !closeEnough(entity, pos)) {
            boolean flag = entity instanceof Player && ((Player)entity).isLocalPlayer();
            double d1 = 0.0D;
            if (flag) {
                d1 = entity.getYRot();
            } else if (entity instanceof ItemFrame) {
                d1 = this.getFrameRotation((ItemFrame)entity);
            } else if (entity instanceof ItemEntity) {
                d1 = (180.0F - ((ItemEntity)entity).getSpin(0.5F) / ((float)Math.PI * 2F) * 360.0F);
            } else if (livingEntity != null) {
                d1 = livingEntity.yBodyRot;
            }
    
            d1 = Mth.positiveModulo(d1 / 360.0D, 1.0D);
            double d2 = getAngle(Vec3.atCenterOf(pos), entity) / (double)((float)Math.PI * 2F);
            double d3 = (flag) ? d2 + r1.wobble(world, 0.5D - (d1 - 0.25D)) : 0.5D - (d1 - 0.25D - d2);
            return Mth.positiveModulo((float)d3, 1.0F);
        }
        return Mth.positiveModulo(
            (float)(r2.wobble(world, Math.random()) + (double)((float)stack.hashCode() / 2.14748365E9F)),
            1.0F
        );
    }
}
