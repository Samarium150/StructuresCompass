package com.github.samarium150.structurescompass.item;

import com.github.samarium150.structurescompass.util.StructureUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class for setting the property of the compass' model
 */
@OnlyIn(Dist.CLIENT)
public final class StructuresCompassItemPropertyGetter implements IItemPropertyGetter {
    
    private static class Angle {
        
        private double rotation = 0D;
        private double delta = 0D;
        private long lastUpdateTick;
        
        private Angle() { }
        
        private boolean isOutdated(long lastUpdateTick) {
            return this.lastUpdateTick != lastUpdateTick;
        }
        
        private double wobble(World world, double amount) {
            if (world != null && isOutdated(world.getGameTime())) {
                lastUpdateTick = world.getGameTime();
                double d0 = amount - rotation;
                d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                delta += d0 * 0.1D;
                delta *= 0.8D;
                rotation = MathHelper.positiveModulo(rotation + delta, 1.0D);
            }
            return rotation;
        }
    }
    
    private final Angle r1 = new Angle();
    private final Angle r2 = new Angle();
    
    private double getFrameRotation(@Nonnull ItemFrameEntity frameEntity) {
        Direction direction = frameEntity.getHorizontalFacing();
        int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getOffset() : 0;
        return MathHelper.wrapDegrees(180 + direction.getHorizontalIndex() * 90 + frameEntity.getRotation() * 45 + i);
    }
    
    private static boolean closeEnough(@Nonnull Entity entity, @Nonnull BlockPos pos) {
        return entity.getPositionVec().squareDistanceTo(
            (double)pos.getX() + 0.5D,
            entity.getPositionVec().getY(),
            (double)pos.getZ() + 0.5D
        ) < (double)1.0E-5F;
    }
    
    private static double getAngle(@Nonnull BlockPos blockPos, @Nonnull Entity entity) {
        return Math.atan2(blockPos.getZ() - entity.getPosZ(), blockPos.getX() - entity.getPosX());
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
    public float call(@Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        Entity entity = livingEntity != null ? livingEntity : stack.getItemFrame();
        if (entity == null) return 0F;
        if (world == null && entity.world instanceof ClientWorld)
            world = entity.world;
        if (world == null) return 0F;
        BlockPos pos = StructuresCompassItem.getPos(stack);
        if (pos != null
                && StructureUtils.getDimensionName(world.getDimension().getType()).equals(StructuresCompassItem.getDimension(stack))
                && !closeEnough(entity, pos)) {
            boolean flag = entity instanceof PlayerEntity && ((PlayerEntity)entity).isUser();
            double d1;
            if (flag)
                d1 = entity.rotationYaw;
            else if (entity instanceof ItemFrameEntity)
                d1 = this.getFrameRotation((ItemFrameEntity)entity);
            else
                d1 = livingEntity.renderYawOffset;
            
            d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
            double d2 = getAngle(pos, entity) / (double)((float)Math.PI * 2F);
            double d3 = (flag) ? d2 + r1.wobble(world, 0.5D - (d1 - 0.25D)) : 0.5D - (d1 - 0.25D - d2);
            return MathHelper.positiveModulo((float)d3, 1.0F);
        }
        return MathHelper.positiveModulo(
            (float)(r2.wobble(world, Math.random()) + (double)((float)stack.hashCode() / 2.14748365E9F)),
            1.0F
        );
    }
}
