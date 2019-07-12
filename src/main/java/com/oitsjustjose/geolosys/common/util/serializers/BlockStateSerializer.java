package com.oitsjustjose.geolosys.common.util.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Type;

@JsonAdapter(IBlockState.class)
public class BlockStateSerializer implements JsonSerializer<IBlockState> {
    @Override
    public JsonElement serialize(IBlockState src, Type typeOfSrc, JsonSerializationContext context) {
        ResourceLocation res = ForgeRegistries.BLOCKS.getKey(src.getBlock());

        return new JsonPrimitive(
                String.format("%s:%s",
                        res.toString(),
                        src.getBlock().getMetaFromState(src)
                ));
    }
}
