package codyhuh.ambientadditions.registry;

import codyhuh.ambientadditions.AmbientAdditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class AATags {
    public static final TagKey<EntityType<?>> UNCRATEABLE = entityTag("uncrateable");

    private static TagKey<Block> blockTag(String path) {
        return BlockTags.create(new ResourceLocation(AmbientAdditions.MOD_ID, path));
    }

    private static TagKey<EntityType<?>> entityTag(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(AmbientAdditions.MOD_ID, path));
    }
}
