package com.oitsjustjose.geolosys.common.config;

import com.oitsjustjose.geolosys.Geolosys;
import com.oitsjustjose.geolosys.client.GuiManual;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Geolosys.MODID)
public class ModConfig
{
    @Config.Name("Feature Control")
    @Config.Comment("Enable or disable Geolosys features entirely")
    public static FeatureControl featureControl = new FeatureControl();

    @Config.Name("Prospecting")
    @Config.Comment("Adjust settings specific to prospecting")
    public static Prospecting prospecting = new Prospecting();

    @Config.Name("User Entries")
    @Config.Comment("Custom user entries")
    public static UserEntries userEntries = new UserEntries();

    @Config.Name("Compat")
    @Config.Comment("Inter-mod compatibility configs")
    public static Compat compat = new Compat();

    @Config.Name("Client")
    @Config.Comment("Client-side settings")
    public static Client client = new Client();

    public static class FeatureControl
    {
        @Config.Name("Replace Stone Variant Deposits")
        public boolean modStones = true;

        @Config.Name("Enable Ingots")
        public boolean enableIngots = true;

        @Config.Name("Enable Coals")
        public boolean enableCoals = true;

        @Config.Name("Enable Cluster Smelting")
        public boolean enableSmelting = true;

        @Config.Name("Enable debug print statements for generation")
        public boolean debugGeneration = false;

        @Config.Name("Retroactively replace existing ores in world")
        @Config.Comment("Happens when a player enters a chunk; changes other mod ores into Geolosys's")
        public boolean retroReplace = false;

        @Config.Name("Disable Vanilla Ore Generation")
        public boolean disableVanillaGeneration = true;
    }

    public static class Prospecting
    {
        @Config.Name("Maximum Number of samples per Chunk")
        @Config.RangeInt(min = 1, max = 16)
        public int maxSamples = 10;

        @Config.Name("Allow samples to generate in any water")
        public boolean generateInWater = false;

        @Config.Name("Samples drop nothing (contents revealed in chat)")
        public boolean boringSamples = false;

        @Config.Name("Enable Prospector's Pickaxe")
        public boolean enableProPick = true;

        @Config.Name("Prospector's Pick Takes Damage")
        public boolean enableProPickDamage = false;

        @Config.Name("Prospector's Pick Durability")
        @Config.RangeInt(min = 0)
        public int proPickDurability = 256;

        @Config.Name("Prospector's Pickaxe Range")
        @Config.RangeInt(min = 0, max = 255)
        public int proPickRange = 5;

        @Config.Name("Prospector's Pickaxe Diameter")
        @Config.RangeInt(min = 0, max = 255)
        public int proPickDiameter = 5;

        @Config.Name("Prospector's Pick Sea Levels per Dimension")
        @Config.Comment("The prospector's pick will show what's in a direction only when below this level, per dimension. Add custom dimension ID's and their corresponding Y depth below, format: <dim_id>:<y_level>")
        public String[] proPickDimensionSeaLevels = getDefaultSeaLevels();

        @Config.Name("Surface Prospecting Results")
        @Config.Comment("SAMPLES means prospecting on the surface returns the samples found in that chunk (so if returns 'nothing' there may still be something\n"
                + "OREBLOCKS means prospecting on the surface returns the first Geolosys-registered Ore Block it finds. If it returns something, it's there.")
        public SURFACE_PROSPECTING_TYPE surfaceProspectingResults = SURFACE_PROSPECTING_TYPE.OREBLOCKS;

        @Config.Name("Blocks Samples can Generate On")
        @Config.Comment("Formatted <modid:block:meta>; this list contains blocks that samples should not generate on.")
        public String[] samplePlaceBlacklist = new String[]
        { "minecraft:ice:0", "minecraft:packed_ice:0", "minecraft:frosted_ice" };

        @Config.Name("Extra Ores the Prospector's Pick should search for")
        @Config.Comment("Ores here will be able to be detected by the prospector's pick.\n"
                + "In the form of one of these two:\n" + "    modid:block\n" + "    modid:block:metadata")
        public String[] extraProPickEntries = new String[]
        { "undergroundbiomes:igneous_stone_geolosys.ore.autunite:*",
                "undergroundbiomes:igneous_stone_geolosys.ore.azurite:*",
                "undergroundbiomes:igneous_stone_geolosys.ore.bauxite:*",
                "undergroundbiomes:igneous_stone_geolosys.ore.cassiterite:*",
                "undergroundbiomes:igneous_stone_geolosys.ore.galena:*",
                "undergroundbiomes:igneous_stone_geolosys.ore.limonite:*",
                "undergroundbiomes:igneous_stone_geolosys.ore.malachite:*",
                "undergroundbiomes:igneous_stone_geolosys.ore.platinum:*",
                "undergroundbiomes:igneous_stone_geolosys.ore.sphalerite:*",
                "undergroundbiomes:igneous_stone_geolosys.ore.teallite:*",
                "undergroundbiomes:igneous_stone_geolosys.ore_vanilla.beryl:*",
                "undergroundbiomes:igneous_stone_geolosys.ore_vanilla.cinnabar:*",
                "undergroundbiomes:igneous_stone_geolosys.ore_vanilla.gold:*",
                "undergroundbiomes:igneous_stone_geolosys.ore_vanilla.kimberlite:*",
                "undergroundbiomes:igneous_stone_geolosys.ore_vanilla.lapis:*",
                "undergroundbiomes:igneous_stone_geolosys.ore_vanilla.quartz:*",
                "undergroundbiomes:igneous_stone_geolosys_ore:*",
                "undergroundbiomes:igneous_stone_geolosys_ore_vanilla:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.autunite:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.azurite:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.bauxite:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.cassiterite:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.galena:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.limonite:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.malachite:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.platinum:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.sphalerite:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore.teallite:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore_vanilla.beryl:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore_vanilla.cinnabar:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore_vanilla.gold:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore_vanilla.kimberlite:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore_vanilla.lapis:*",
                "undergroundbiomes:metamorphic_stone_geolosys.ore_vanilla.quartz:*",
                "undergroundbiomes:metamorphic_stone_geolosys_ore:*",
                "undergroundbiomes:metamorphic_stone_geolosys_ore_vanilla:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.autunite:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.azurite:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.bauxite:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.cassiterite:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.galena:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.limonite:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.malachite:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.platinum:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.sphalerite:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore.teallite:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore_vanilla.beryl:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore_vanilla.cinnabar:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore_vanilla.gold:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore_vanilla.kimberlite:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore_vanilla.lapis:*",
                "undergroundbiomes:sedimentary_stone_geolosys.ore_vanilla.quartz:*",
                "undergroundbiomes:sedimentary_stone_geolosys_ore:*",
                "undergroundbiomes:sedimentary_stone_geolosys_ore_vanilla:*" };

        public enum SURFACE_PROSPECTING_TYPE
        {
            SAMPLES, OREBLOCKS;
        }
    }

    public static class UserEntries
    {
        @Config.Name("Blocks mineral deposits can replace")
        @Config.Comment("Format is:\n" + "modid:block OR modid:block:meta")
        public String[] replacementMatsRaw = new String[]
        { "minecraft:stone:0", "minecraft:stone:1", "minecraft:stone:3", "minecraft:stone:5", "minecraft:dirt:0",
                "minecraft:netherrack:0", "undergroundbiomes:igneous_cobble:*",
                "undergroundbiomes:igneous_cobble_mossy:*", "undergroundbiomes:igneous_gravel:*",
                "undergroundbiomes:igneous_monster_stone:*", "undergroundbiomes:igneous_overgrown:*",
                "undergroundbiomes:igneous_overgrown_snowed:*", "undergroundbiomes:igneous_sand:*",
                "undergroundbiomes:igneous_stone:*", "undergroundbiomes:metamorphic_cobble:*",
                "undergroundbiomes:metamorphic_cobble_mossy:*", "undergroundbiomes:metamorphic_gravel:*",
                "undergroundbiomes:metamorphic_monster_stone:*", "undergroundbiomes:metamorphic_overgrown:*",
                "undergroundbiomes:metamorphic_overgrown_snowed:*", "undergroundbiomes:metamorphic_sand:*",
                "undergroundbiomes:metamorphic_stone:*", "undergroundbiomes:sedimentary_gravel:*",
                "undergroundbiomes:sedimentary_monster_stone:*", "undergroundbiomes:sedimentary_overgrown:*",
                "undergroundbiomes:sedimentary_overgrown_snowed:*", "undergroundbiomes:sedimentary_sand:*",
                "undergroundbiomes:sedimentary_stone:*", "undergroundbiomes:sedimentary_stone_mossy:*" };

        @Config.Name("Blocks that the OreConverter feature should ignore")
        @Config.Comment("Format is:\n" + "modid:block OR modid:block:meta")
        public String[] convertBlacklistRaw = getConvertBlacklist();
    }

    public static class Client
    {
        @Config.Name("Field Manual Font Scale")
        @Config.RangeDouble(min = 0.1, max = 3.0)
        public float manualFontScale = 0.75F;

        @Config.Name("Prospector's Pick Depth HUD X")
        @Config.Comment("The X coordinate that the Depth overlay displays at while holding a prospector's pick")
        @Config.RangeInt(min = 0)
        public int hudX = 2;

        @Config.Name("Prospector's Pick Depth HUD Y")
        @Config.Comment("The Y coordinate that the Depth overlay displays at while holding a prospector's pick")
        @Config.RangeInt(min = 0)
        public int hudY = 2;
    }

    public static class Compat
    {
        @Config.Name("Enable Osmium")
        public boolean enableOsmium = true;

        @Config.Name("Enable Osmium Exclusively")
        @Config.Comment("Allows Osmium to be enabled, without enabling Platinum")
        public boolean enableOsmiumExclusively = false;

        @Config.Name("Enable Yellorium")
        public boolean enableYellorium = true;

        @Config.Name("Enable Sulfur")
        public boolean enableSulfur = true;

        @Config.Name("Register Aluminum as oreBauxite")
        @Config.RequiresMcRestart
        public boolean registerAsBauxite = false;

        @Config.Name("Enable BetterWithMods Integration")
        @Config.RequiresMcRestart
        public boolean enableBWMCompat = true;

        @Config.Name("Enable IE Integration")
        @Config.RequiresMcRestart
        public boolean enableIECompat = true;

        @Config.Name("Enable AE2 Integration")
        @Config.RequiresMcRestart
        public boolean enableAE2Compat = true;

        @Config.Name("Underground Biomes Integration")
        @Config.RequiresMcRestart
        public boolean enableUBGCompat = true;

        @Config.Name("Underground Biomes Preloading")
        @Config.Comment("If Underground Biomes is loaded, this will cause UBG to generate stone in a chunk prior to ores being generated")
        @Config.RequiresMcRestart
        public boolean preloadUBGen = false;

        @Config.Name("IE Excavation Recipes to Remove")
        @Config.Comment("If Enable IE Integration is True, then I register my own excavation \"recipes\","
                + " leading to potential redundancy. This config is a list of strings to remove from IE")
        @Config.RequiresMcRestart
        public String[] ieExcavatorRecipesToRemove = new String[]
        { "Iron", "Bauxite", "Cassiterite", "Coal", "Copper", "Galena", "Gold", "Lapis", "Lead", "Magnetite", "Nickel",
                "Platinum", "Pyrite", "Quartzite", "Silver", "Uranium", "Cinnabar" };

        @Config.Name("Vanilla Mode")
        @Config.Comment("When enabled, instead of using Geolosys's replacements for vanilla ores it just uses Vanilla blocks")
        @Config.RequiresMcRestart
        public boolean vanillaMode = false;
    }

    @Mod.EventBusSubscriber(modid = Geolosys.MODID)
    public static class EventHandler
    {
        @SubscribeEvent
        public void onChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equalsIgnoreCase(Geolosys.MODID))
            {
                ConfigManager.sync(Geolosys.MODID, Config.Type.INSTANCE);
                ConfigParser.reinit();
                GuiManual.initPages();
            }
        }
    }

    private static String[] getDefaultSeaLevels()
    {
        return new String[]
        { "-1:128", "0:64", "1:128" };
    }

    private static String[] getConvertBlacklist()
    {
        if (Loader.isModLoaded("nex"))
        {
            if (Loader.isModLoaded("gravelores"))
            {
                return new String[]
                { "gravelores:coal_gravel_ore", "gravelores:iron_gravel_ore", "gravelores:lapis_gravel_ore",
                        "gravelores:gold_gravel_ore", "gravelores:redstone_gravel_ore", "gravelores:diamond_gravel_ore",
                        "gravelores:emerald_gravel_ore", "gravelores:tin_gravel_ore", "gravelores:nickel_gravel_ore",
                        "gravelores:silver_gravel_ore", "gravelores:lead_gravel_ore", "gravelores:copper_gravel_ore",
                        "gravelores:aluminum_gravel_ore", "nex:ore_quartz:0", "nex:ore_quartz:1", "nex:ore_quartz:2",
                        "nex:ore_quartz:3" };
            }
            else
            {
                return new String[]
                { "nex:ore_quartz:0", "nex:ore_quartz:1", "nex:ore_quartz:2", "nex:ore_quartz:3" };
            }
        }
        else if (Loader.isModLoaded("gravelores"))
        {
            return new String[]
            { "gravelores:coal_gravel_ore", "gravelores:iron_gravel_ore", "gravelores:lapis_gravel_ore",
                    "gravelores:gold_gravel_ore", "gravelores:redstone_gravel_ore", "gravelores:diamond_gravel_ore",
                    "gravelores:emerald_gravel_ore", "gravelores:tin_gravel_ore", "gravelores:nickel_gravel_ore",
                    "gravelores:silver_gravel_ore", "gravelores:lead_gravel_ore", "gravelores:copper_gravel_ore",
                    "gravelores:aluminum_gravel_ore", };
        }
        return new String[]
        {};
    }
}
