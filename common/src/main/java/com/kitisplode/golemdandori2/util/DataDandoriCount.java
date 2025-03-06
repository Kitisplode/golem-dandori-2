package com.kitisplode.golemdandori2.util;

import com.kitisplode.golemdandori2.ExampleModCommon;
import com.kitisplode.golemdandori2.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemdandori2.entity.golem.legends.EntityGolemGrindstone;
import com.kitisplode.golemdandori2.entity.golem.legends.EntityGolemPlank;
import com.kitisplode.golemdandori2.entity.interfaces.IEntityDandoriPik;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDandoriCount
{
    public enum FOLLOWER_TYPE {
        COBBLE,
        PLANK,
        GRINDSTONE,
        IRON,
        SNOW
    };

    private static final double dandoriCountRange = 64.0;
    public static Map<FOLLOWER_TYPE, Class> followerClasses = new HashMap<>();
    public static Map<FOLLOWER_TYPE, ResourceLocation> followerIcons = new HashMap<>();
    private Map<FOLLOWER_TYPE, Integer> followerCounts = new HashMap<>();
    private int totalCount = 0;

    // Fill the class and resource location maps.
    public static void init()
    {
        followerClasses.put(FOLLOWER_TYPE.COBBLE, EntityGolemCobble.class);
        followerClasses.put(FOLLOWER_TYPE.PLANK, EntityGolemPlank.class);
        followerClasses.put(FOLLOWER_TYPE.GRINDSTONE, EntityGolemGrindstone.class);
        followerClasses.put(FOLLOWER_TYPE.IRON, IronGolem.class);
        followerClasses.put(FOLLOWER_TYPE.SNOW, SnowGolem.class);

        followerIcons.put(FOLLOWER_TYPE.COBBLE, ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "textures/item/spawn/golem_cobble_spawn_egg.png"));
        followerIcons.put(FOLLOWER_TYPE.PLANK, ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "textures/item/spawn/golem_plank_spawn_egg.png"));
        followerIcons.put(FOLLOWER_TYPE.GRINDSTONE, ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "textures/item/spawn/golem_grindstone_spawn_egg.png"));
        followerIcons.put(FOLLOWER_TYPE.IRON, ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "textures/hud/dandori/golem_iron.png"));
        followerIcons.put(FOLLOWER_TYPE.SNOW, ResourceLocation.fromNamespaceAndPath(ExampleModCommon.MODID, "textures/hud/dandori/golem_snow.png"));
    }

    public static boolean entityIsOfType(FOLLOWER_TYPE type, LivingEntity entity)
    {
        if (type == null) return true;
        Class _class = followerClasses.getOrDefault(type, null);
        if (_class == null) return false;
        return entity.getClass() == _class;
    }

    public void updateCounts(LivingEntity player)
    {
        List<LivingEntity> _followerList = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(dandoriCountRange), entity ->
        {
            if (!(entity instanceof IEntityDandoriPik)) return false;
            if (((IEntityDandoriPik) entity).isDandoriOff()) return false;
            return ((IEntityDandoriPik) entity).getOwner() == player;
        });

        followerCounts.clear();
        totalCount = 0;
        for (LivingEntity _entity : _followerList)
        {
            for (FOLLOWER_TYPE _key : followerClasses.keySet())
            {
                if (followerClasses.get(_key) == _entity.getClass())
                {
                    totalCount++;
                    followerCounts.merge(_key, 1, Integer::sum);
                    break;
                }
            }
        }
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public int getFollowerCount(FOLLOWER_TYPE type)
    {
        Integer _count = followerCounts.get(type);
        if (_count == null) return 0;
        return _count;
    }

    private FOLLOWER_TYPE getNextTypeWithCount(FOLLOWER_TYPE type, boolean nextValue)
    {
        final FOLLOWER_TYPE[] _FOLLOWER_TYPES_VALUES = FOLLOWER_TYPE.values();
        int _typeInt = type.ordinal();
        if (getTotalCount() <= 0) return null;
        if (_typeInt >= _FOLLOWER_TYPES_VALUES.length) return null;
        int _i = _typeInt;
        int _addend = 1;
        if (!nextValue) _addend = -1;
        for (; _i >= 0 && _i < _FOLLOWER_TYPES_VALUES.length; _i += _addend)
        {
            if (getFollowerCount(_FOLLOWER_TYPES_VALUES[_i]) > 0) break;
        }
        if (_i < 0 || _i >= _FOLLOWER_TYPES_VALUES.length) return null;
        return _FOLLOWER_TYPES_VALUES[_i];
    }
    public FOLLOWER_TYPE getNextTypeWithCount(FOLLOWER_TYPE type)
    {
        return getNextTypeWithCount(type, true);
    }
    public FOLLOWER_TYPE getPreviousTypeWithCount(FOLLOWER_TYPE type)
    {
        return getNextTypeWithCount(type, false);
    }
}
