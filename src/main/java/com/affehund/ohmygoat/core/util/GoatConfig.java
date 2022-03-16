package com.affehund.ohmygoat.core.util;

import net.minecraftforge.common.ForgeConfigSpec;

public class GoatConfig {

    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupCommonConfig(configBuilder);
        COMMON_SPEC = configBuilder.build();
    }

    public static ForgeConfigSpec.BooleanValue ADD_OUTPOST_LOOT;
    public static ForgeConfigSpec.BooleanValue SHOW_TOOLTIPS;
    public static ForgeConfigSpec.IntValue CHEESE_MAKING_DURATION;
    public static ForgeConfigSpec.IntValue MIN_GOAT_CHEESE;
    public static ForgeConfigSpec.IntValue MAX_GOAT_CHEESE;
    public static ForgeConfigSpec.DoubleValue HURT_ATTACKER_PROBABILITY;
    public static ForgeConfigSpec.DoubleValue HORN_LOSING_PROBABILITY;
    public static ForgeConfigSpec.DoubleValue HORN_DROPPING_PROBABILITY;

    private static void setupCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Oh My Goat Common Config");

        builder.push("General");
        ADD_OUTPOST_LOOT = builder.comment("Whether to add the goat horn to the pillager outpost chest loot.").define("add_outpost_loot", true);
        SHOW_TOOLTIPS = builder.comment("Whether to show tooltips for various items.").define("show_tooltips", true);
        builder.pop();

        builder.push("Goat Cheese Making");
        CHEESE_MAKING_DURATION = builder.comment("How long it takes to make cheese.").defineInRange("cheese_making_duration", 600, 10, 1200);
        MIN_GOAT_CHEESE = builder.comment("Minimum amount of goat cheese you will get from cheese making.").defineInRange("min_goat_cheese", 1, 0, 64);
        MAX_GOAT_CHEESE = builder.comment("Maximum amount of goat cheese you will get from cheese making.").defineInRange("max_goat_cheese", 1, 0, 64);
        builder.pop();

        builder.push("Horned Helmets");
        HURT_ATTACKER_PROBABILITY = builder.comment("Probability to hurt the attacker when wearing a horned helmet.").defineInRange("hurt_attacker_probability", 0.15, 0, 1);
        HORN_LOSING_PROBABILITY = builder.comment("Probability to lose your horns when hurt someone with your horns.").defineInRange("horn_losing_probability", 0.01, 0, 1);
        HORN_DROPPING_PROBABILITY = builder.comment("Probability to drop the horns when losing them.").defineInRange("drop_horns_probability", 0.85, 0, 1);
        builder.pop();
    }
}
