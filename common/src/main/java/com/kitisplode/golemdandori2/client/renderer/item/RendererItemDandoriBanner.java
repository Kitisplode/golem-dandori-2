package com.kitisplode.golemdandori2.client.renderer.item;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.item.ItemDandoriCall;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RendererItemDandoriBanner extends GeoItemRenderer<ItemDandoriCall>
{
    public RendererItemDandoriBanner()
    {
        super (new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "banner_courage")));
    }
}
