package dev.miles.sounddetector.mixin;
import dev.miles.sounddetector.Projection;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import org.joml.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
 @Inject(method="render",at=@At("HEAD"))
 private void resonance$matrix(ObjectAllocator allocator,RenderTickCounter ticks,boolean outline,Camera camera,Matrix4f view,Matrix4f basic,Matrix4f projection,GpuBufferSlice fog,Vector4f color,boolean sky,CallbackInfo ci){Projection.capture(view,projection,camera.getCameraPos());}
}
