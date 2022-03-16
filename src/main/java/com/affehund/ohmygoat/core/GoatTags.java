package com.affehund.ohmygoat.core;

import com.affehund.ohmygoat.OhMyGoat;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GoatTags {
    public static class Items {
        public static final TagKey<Item> HORNABLE_HELMETS = modTag("hornable_helmets");
        public static final TagKey<Item> HORNED_HELMETS = modTag("horned_helmets");

        private static TagKey<Item> modTag(String name) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier(OhMyGoat.MOD_ID, name));
        }
    }
}
