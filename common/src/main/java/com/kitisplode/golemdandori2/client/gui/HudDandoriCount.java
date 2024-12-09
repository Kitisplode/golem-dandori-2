package com.kitisplode.golemdandori2.client.gui;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriLeader;
import com.kitisplode.golemdandori2.util.DataDandoriCount;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

public class HudDandoriCount implements LayeredDraw.Layer
{
    public static final HudDandoriCount instance = new HudDandoriCount();

    // Image locations:
    private static final ResourceLocation COBBLE = ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "textures/hud/dandori/golem_cobble.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return;


        IEntityDandoriLeader player = (IEntityDandoriLeader) mc.player;
        if (player == null) return;

        int total = player.getTotalDandoriCount();
        if (total <= 0) return;

        int draw_x = 12;
        int draw_y = (int)((float) guiGraphics.guiHeight() * 0.9);
        int color = 0xff_ffffff;

        // Loop through each golem type and then show them.
        for (DataDandoriCount.FOLLOWER_TYPE type : DataDandoriCount.FOLLOWER_TYPE.values())
        {
            int _count = player.getPikCount(type);
            if (_count <= 0) continue;

            guiGraphics.blit(RenderType::guiTextured, DataDandoriCount.followerIcons.get(type),
                    draw_x, draw_y, 0,0,
                    16,16, 16,16);
            guiGraphics.drawString(mc.font, "x " + _count, draw_x + 20, draw_y + 8, color, true);
            draw_y -= 24;
        }

    }
}
