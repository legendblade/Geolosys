package com.oitsjustjose.geolosys.common.world;

import com.oitsjustjose.geolosys.Geolosys;
import com.oitsjustjose.geolosys.common.api.GeolosysAPI;
import com.oitsjustjose.geolosys.common.api.world.IBiomeRestrictedOreGen;
import com.oitsjustjose.geolosys.common.api.world.IOre;
import com.oitsjustjose.geolosys.common.api.world.IOreWithState;
import com.oitsjustjose.geolosys.common.config.ModConfig;
import com.oitsjustjose.geolosys.common.util.GeolosysSaveData;
import com.oitsjustjose.geolosys.compat.UBCompat;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkGeneratorEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Random;

/**
 * A modified version of:
 * https://github.com/BluSunrize/ImmersiveEngineering/blob/master/src/main/java/blusunrize/immersiveengineering/common/world/IEWorldGen.java
 * Original Source & Credit: BluSunrize
 **/

@Mod.EventBusSubscriber
public class OreGenerator extends BaseGenerator implements IWorldGenerator
{
    private static final String dataID = "geolosysOreGeneratorPending";
    private static HashMap<Integer, OreGen> oreSpawnWeights = new HashMap<>();
    private static int last = 0;

    public static void addOreGen(IOre ore)
    {
        if(!registerOre(ore)) return;

        OreGenerator.OreGen gen = new OreGenerator.OreGen(ore);

        for (int i = last; i < last + ore.getChance(); i++)
        {
            oreSpawnWeights.put(i, gen);
        }
        last = last + ore.getChance();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
            IChunkProvider chunkProvider)
    {
        ToDoBlocks.getForWorld(world, dataID).processPending(new ChunkPos(chunkX, chunkZ), world);

        if (oreSpawnWeights.keySet().size() > 0)
        {
            int rng = random.nextInt(oreSpawnWeights.keySet().size());
            // Check the biome
            OreGen oreGen = oreSpawnWeights.get(rng);

            if (oreGen.ore instanceof IBiomeRestrictedOreGen)
            {
                IBiomeRestrictedOreGen deposit = (IBiomeRestrictedOreGen) oreGen.ore;

                if (deposit.getBiomeList().size() <= 0) {
                    oreGen.generate(world, random, (chunkX * 16), (chunkZ * 16));
                } else {
                    for (Biome b : deposit.getBiomeList()) {
                        if (world.getBiome(new BlockPos((chunkX * 16), 256, (chunkZ * 16))) != b) continue;

                        oreGen.generate(world, random, (chunkX * 16), (chunkZ * 16));
                        break;
                    }
                }
            }
            else {
                oreGen.generate(world, random, (chunkX * 16), (chunkZ * 16));
            }
        }
        // Call UBG's event to make sure those are correctly processed
        if (Loader.isModLoaded("undergroundbiomes") && ModConfig.compat.enableUBGCompat)
        {
            UBCompat.forceReprocess(chunkGenerator, world, random, chunkX, chunkZ);
        }
    }

    /**
     * Handles generating ore after chunk population when UB compat is disabled
     * @param event The event to handle
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onChunkGenned(PopulateChunkEvent.Post event) {
        if (ModConfig.compat.enableUBGCompat) return; // This is handled after UB reprocessing instead
        ToDoBlocks
                .getForWorld(event.getWorld(), dataID)
                .processPending(new ChunkPos(event.getChunkX(), event.getChunkZ()), event.getWorld());
    }

    public static class OreGen
    {
        WorldGenMinableSafe pluton;
        IOre ore;

        public OreGen(IOre ore)
        {
            this.pluton = new WorldGenMinableSafe(ore, dataID);
            this.ore = ore;
        }

        public void generate(World world, Random rand, int x, int z)
        {
            if (!Geolosys.getInstance().chunkOreGen.canGenerateInChunk(world, new ChunkPos(x / 16, z / 16),
                    world.provider.getDimension()))
            {
                return;
            }
            boolean lastState = ForgeModContainer.logCascadingWorldGeneration;
            ForgeModContainer.logCascadingWorldGeneration = false;
            for (int d : this.ore.getDimensionBlacklist())
            {
                if (d == world.provider.getDimension())
                {
                    return;
                }
            }
            if (rand.nextInt(100) < this.ore.getChance())
            {
                int y = this.ore.getYMin() != this.ore.getYMax()
                        ? this.ore.getYMin() + rand.nextInt(this.ore.getYMax() - this.ore.getYMin())
                        : this.ore.getYMin();
                if (Loader.isModLoaded("twilightforest") && world.provider.getDimension() == 7)
                {
                    y /= 2;
                    y /= 2;
                }
                // If the pluton placed any ores at all
                if (pluton.generate(world, rand, new BlockPos(x, y, z)))
                {
                    IBlockState tmp = this.ore.getOre();
                    GeolosysAPI.putWorldDeposit(new ChunkPos(x / 16, z / 16), world.provider.getDimension(),
                            tmp.getBlock().getRegistryName() + ":" + tmp.getBlock().getMetaFromState(tmp));
                    GeolosysSaveData.get(world).markDirty();
                    Geolosys.getInstance().chunkOreGen.addChunk(new ChunkPos(x / 16, z / 16), world, y, this.ore);
                }
            }
            ForgeModContainer.logCascadingWorldGeneration = lastState;
        }
    }
}
