package com.oitsjustjose.geolosys.compat;

import java.util.Random;

import com.oitsjustjose.geolosys.common.config.ModConfig;
import com.oitsjustjose.geolosys.common.world.ToDoBlocks;
import exterminatorjeff.undergroundbiomes.api.event.UBForceReProcessEvent;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class UBCompat
{
    public static void forceReprocess(IChunkGenerator chunkGenerator, World world, Random random, int chunkX,
            int chunkZ)
    {
        MinecraftForge.EVENT_BUS.post(new UBForceReProcessEvent(chunkGenerator, world, random, chunkX, chunkZ, true));
    }

    /**
     * Check the chunk after UB has processed it in case we need to handle any post-gen ourselves
     * @param event The reprocess event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUbForceReProcess(UBForceReProcessEvent event) {
        if (!ModConfig.compat.enableUBGCompat) return;

        ToDoBlocks
                .getForWorld(event.getWorld(), "geolosysOreGeneratorPending")
                .processPending(new ChunkPos(event.getChunkX(), event.getChunkZ()), event.getWorld());
    }
}