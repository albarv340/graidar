package red.bread.graidar.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.bread.graidar.client.GraidarClient;

@Mixin(BossBarHud.class)
public class BossBarMixin {
    @Inject(method = "renderBossBar(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/entity/boss/BossBar;)V",
            at = @At("HEAD"))
    private void onRender(DrawContext context, int x, int y, BossBar bossBar, CallbackInfo ci) {
        GraidarClient.onRenderBossBar(bossBar);
    }
}