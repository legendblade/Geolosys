package com.oitsjustjose.geolosys.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oitsjustjose.geolosys.Geolosys;
import com.oitsjustjose.geolosys.common.api.GeolosysAPI;
import com.oitsjustjose.geolosys.common.util.Utils;
import net.minecraft.block.state.IBlockState;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ConfigOres
{
    private File jsonFile;

    private final GsonBuilder builder = new GsonBuilder();
    private Gson gson;

    public ConfigOres(File configRoot)
    {
        this.jsonFile = new File(configRoot.getAbsolutePath() + "/geolosys.json");
    }

    public void init()
    {
        builder.excludeFieldsWithoutExposeAnnotation();
        ConfigRoot config = null;

        try
        {
            config = GetGson().fromJson(new FileReader(jsonFile), ConfigRoot.class);
        }
        catch (IOException e)
        {
            // Download the file from GitHub if it can't be found
            try
            {
                Geolosys.getInstance().LOGGER.info("Could not find geolosys.json. Downloading it from GitHub...");
                BufferedInputStream in = new BufferedInputStream(
                        new URL("https://raw.githubusercontent.com/oitsjustjose/Geolosys/master/geolosys_ores.json")
                                .openStream());
                Files.copy(in, Paths.get(jsonFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                Geolosys.getInstance().LOGGER.info("Done downloading geolosys.json from GitHub!");
                config = GetGson().fromJson(new FileReader(jsonFile), ConfigRoot.class);
            }
            catch (IOException f)
            {
                Geolosys.getInstance().LOGGER.error("File " + jsonFile.getAbsolutePath()
                        + "could neither be found nor downloaded. Unable to load any ores unless they are from CraftTweaker.");
            }
        }

        if (config == null) return;

        config.ores.forEach(GeolosysAPI::registerMineralDeposit);

        config.stones.forEach((stone) -> {
            register(
                Utils.getBlockStateFromString(stone.block),
                stone.yMin,
                stone.yMax,
                stone.chance,
                stone.size,
                // This is hacky and should be fixed later, but config reading happens once
                stone.dimBlacklist.stream().mapToInt(i->i).toArray()
            );
        });
    }

    private void register(IBlockState stone, int yMin, int yMax, int chance, int size, int[] dimBlacklist)
    {
        GeolosysAPI.registerStoneDeposit(stone, yMin, yMax, chance, size, dimBlacklist);
    }

    private Gson GetGson() {
        if (gson == null) gson = builder.create();
        return gson;
    }
}
