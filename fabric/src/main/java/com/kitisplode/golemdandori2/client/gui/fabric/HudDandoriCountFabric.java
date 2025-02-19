package com.kitisplode.golemdandori2.client.gui.fabric;

import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriLeader;
import com.kitisplode.golemdandori2.util.DataDandoriCount;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.GameType;

public class HudDandoriCountFabric implements HudRenderCallback
{

    @Override
    public void onHudRender(GuiGraphics drawContext, DeltaTracker tickCounter)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return;


        IEntityDandoriLeader player = (IEntityDandoriLeader) mc.player;
        if (player == null) return;

        int total = player.getTotalDandoriCount();
        if (total <= 0) return;

        int draw_x = 12;
        int draw_y = (int)((float) drawContext.guiHeight() * 0.9);
        int color = 0xff_ffffff;

        // Loop through each golem type and then show them.
        for (DataDandoriCount.FOLLOWER_TYPE type : DataDandoriCount.FOLLOWER_TYPE.values())
        {
            int _count = player.getPikCount(type);
            if (_count <= 0) continue;

            drawContext.blit(RenderType::guiTextured, DataDandoriCount.followerIcons.get(type),
                    draw_x, draw_y, 0,0,
                    16,16, 16,16);
            drawContext.drawString(mc.font, "x " + _count, draw_x + 20, draw_y + 8, color, true);
            draw_y -= 24;
        }
    }
}
