package com.affehund.ohmygoat.core.config;

import com.affehund.ohmygoat.OhMyGoat;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = OhMyGoat.MOD_ID)
public class GoatConfig implements ConfigData {

    @Comment("Whether to add the goat horn to the pillager outpost chest loot.")
    public Boolean ADD_OUTPOST_LOOT = true;

    @Comment("Whether to show tooltips for various items.")
    public Boolean SHOW_TOOLTIPS = true;

    @Comment("How long it takes to make cheese.")
    @ConfigEntry.BoundedDiscrete(min = 10, max = 1200)
    public int CHEESE_MAKING_DURATION = 600;

    @Comment("Minimum amount of goat cheese you will get from cheese making.")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 64)
    public int MIN_GOAT_CHEESE = 1;

    @Comment("Minimum amount of goat cheese you will get from cheese making.")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 64)
    public int MAX_GOAT_CHEESE = 3;

    @Comment("Probability to hurt the attacker when wearing a horned helmet.")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
    public double HURT_ATTACKER_PROBABILITY = 0.15;

    @Comment("Probability to lose your horns when hurt someone with your horns.")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
    public double HORN_LOSING_PROBABILITY = 0.01;

    @Comment("Probability to drop the horns when losing them.")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
    public double HORN_DROPPING_PROBABILITY = 0.85;
}
