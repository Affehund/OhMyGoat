package com.affehund.ohmygoat.core.util;

import com.affehund.ohmygoat.OhMyGoat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class GoatTags {
    public static class Blocks {
        public static final TagKey<Block> CAULDRONS = tag("cauldrons");
        public static final TagKey<Block> MINEABLE_PICKAXE = tag("mineable/pickaxe");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> HORNABLE_HELMETS = modTag("hornable_helmets");
        public static final TagKey<Item> HORNED_HELMETS = modTag("horned_helmets");
        public static final TagKey<Item> PIGLIN_LOVED = tag("piglin_loved");

        private static TagKey<Item> modTag(String name) {
            return tag(OhMyGoat.MOD_ID + ":" + name);
        }

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(name));
        }
    }
}
