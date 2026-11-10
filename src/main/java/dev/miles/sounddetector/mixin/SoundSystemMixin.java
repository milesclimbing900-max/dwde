package dev.miles.sounddetector.mixin;
import dev.miles.sounddetector.Resonance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
 @Inject(method="play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;",at=@At("HEAD"))
 private void resonance$capture(SoundInstance sound,CallbackInfoReturnable<SoundSystem.PlayResult> cir){if(Resonance.INSTANCE!=null)Resonance.INSTANCE.capture(sound);}
}
