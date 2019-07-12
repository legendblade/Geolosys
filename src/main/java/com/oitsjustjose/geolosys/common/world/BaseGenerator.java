package com.oitsjustjose.geolosys.common.world;

import com.oitsjustjose.geolosys.Geolosys;
import com.oitsjustjose.geolosys.common.api.world.IOre;
import com.oitsjustjose.geolosys.common.util.Utils;

import java.util.HashMap;

public class BaseGenerator {
    protected static HashMap<String, IOre> genMap = new HashMap<>();

    public static boolean registerOre(IOre ore) {
        String id = ore.getId();
        if (id == null || id.isEmpty()) id = Utils.getDefaultIdForOre(ore);

        if (genMap.containsKey(id)) {
            Geolosys.getInstance().LOGGER.error("There is already an ore deposit with ID " + id + "; this " +
                    "deposit will be skipped.");
            return false;
        }

        genMap.put(id, ore);
        return true;
    }

    public static IOre getGeneratorById(String id) {
        return genMap.get(id);
    }
}
