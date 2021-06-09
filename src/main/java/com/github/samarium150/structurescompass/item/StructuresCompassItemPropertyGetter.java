package com.github.samarium150.structurescompass.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class for setting the property of the compass' model
 * @see net.minecraft.item.CompassItem
 */
@OnlyIn(Dist.CLIENT)
public final class StructuresCompassItemPropertyGetter implements IItemPropertyGetter {
    
    private double rotation;
    private double rota;
    private long lastUpdateTick;
    
    private double wobble(@Nonnull World worldIn, double p_185093_2_) {
        if (worldIn.getGameTime() != this.lastUpdateTick) {
            this.lastUpdateTick = worldIn.getGameTime();
            double d0 = p_185093_2_ - this.rotation;
            d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
            this.rota += d0 * 0.1D;
            this.rota *= 0.8D;
            this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
        }
        return this.rotation;
    }
    
    private double getFrameRotation(@Nonnull ItemFrameEntity frameEntity) {
        return MathHelper.wrapDegrees(180 + frameEntity.getHorizontalFacing().getHorizontalIndex() * 90);
    }
    
    @OnlyIn(Dist.CLIENT)
    private double getSpawnToAngle(@Nonnull IWorld p_185092_1_, @Nonnull Entity p_185092_2_) {
        BlockPos blockpos = p_185092_1_.getSpawnPoint();
        return Math.atan2(
            (double)blockpos.getZ() - p_185092_2_.func_226281_cx_(),
            (double)blockpos.getX() - p_185092_2_.func_226277_ct_()
        );
    }
    
    private static boolean closeEnough(@Nonnull Entity entity, @Nonnull BlockPos pos) {
        return entity.getPositionVec().squareDistanceTo(
            (double)pos.getX() + 0.5D,
            entity.getPositionVec().getY(),
            (double)pos.getZ() + 0.5D
        ) < (double)1.0E-5F;
    }
    
    private static double getAngle(@Nonnull Vec3d vector, @Nonnull Entity entity) {
        Vec3d positionVector = entity.getPositionVector();
        return Math.atan2(vector.getZ() - positionVector.getZ(), vector.getX() - positionVector.getX());
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
        // TODO: FIX ITEM PROPERTY
//        Entity entity = livingEntity != null ? livingEntity : stack.getAttachedEntity();
//        if (entity == null) return 0F;
//        if (world == null && entity.world instanceof ClientWorld)
//            world = entity.world;
//        if (world == null) return 0F;
//        BlockPos pos = StructuresCompassItem.getPos(stack);
//        if (pos != null
//                && world.getDimensionKey().getLocation().toString().equals(StructuresCompassItem.getDimension(stack))
//                && !closeEnough(entity, pos)) {
//            boolean flag = entity instanceof PlayerEntity && ((PlayerEntity)entity).isUser();
//            double d1 = 0.0D;
//            if (flag) {
//                d1 = entity.rotationYaw;
//            } else if (entity instanceof ItemFrameEntity) {
//                d1 = this.getFrameRotation((ItemFrameEntity)entity);
//            } else if (entity instanceof ItemEntity) {
//                d1 = (180.0F - ((ItemEntity)entity).getItemHover(0.5F) / ((float)Math.PI * 2F) * 360.0F);
//            } else if (livingEntity != null) {
//                d1 = livingEntity.renderYawOffset;
//            }
//
//            d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
//            double d2 = getAngle(Vec3d.copyCentered(pos), entity) / (double)((float)Math.PI * 2F);
//            double d3 = (flag) ? d2 + r1.wobble(world, 0.5D - (d1 - 0.25D)) : 0.5D - (d1 - 0.25D - d2);
//            return MathHelper.positiveModulo((float)d3, 1.0F);
//        }
//        return MathHelper.positiveModulo(
//            (float)(r2.wobble(world, Math.random()) + (double)((float)stack.hashCode() / 2.14748365E9F)),
//            1.0F
//        );
        if (livingEntity == null && !stack.isOnItemFrame())
            return 0.0F;
        else {
            boolean flag = livingEntity != null;
            Entity entity = flag ? livingEntity : stack.getItemFrame();
            assert entity != null;
            if (world == null)
                world = entity.world;
            double d0;
            if (world.dimension.isSurfaceWorld()) {
                double d1 = flag ? (double)entity.rotationYaw : this.getFrameRotation((ItemFrameEntity) entity);
                d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                double d2 = this.getSpawnToAngle(world, entity) / 6.2831854820251465D;
                d0 = 0.5D - (d1 - 0.25D - d2);
            } else d0 = Math.random();
        
            if (flag) d0 = this.wobble(world, d0);
            return MathHelper.positiveModulo((float)d0, 1.0F);
        }
    }
}
