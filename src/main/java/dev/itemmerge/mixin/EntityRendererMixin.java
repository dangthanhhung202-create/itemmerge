package dev.itemmerge.mixin;

import dev.itemmerge.ItemMergeClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void itemmerge$hideDuplicates(Entity entity, Frustum frustum, double x, double y, double z,
                                          CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ItemEntity && ItemMergeClient.HIDDEN.contains(entity.getId())) {
            cir.setReturnValue(false);
        }
    }
}
