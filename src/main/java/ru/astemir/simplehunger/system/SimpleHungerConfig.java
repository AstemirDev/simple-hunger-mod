package ru.astemir.simplehunger.system;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class SimpleHungerConfig {

    public static final BuilderCodec<SimpleHungerConfig> CODEC = BuilderCodec.builder(SimpleHungerConfig.class, SimpleHungerConfig::new)
            .append(new KeyedCodec<>("ItemIds", new MapCodec<>(Codec.FLOAT, HashMap::new), true), (cfg, value) -> cfg.itemIds = value, cfg -> cfg.itemIds).add()
            .append(new KeyedCodec<>("DefaultSaturation", Codec.FLOAT), (cfg, value)-> cfg.defaultSaturation = value, cfg -> cfg.defaultSaturation).add()
            .append(new KeyedCodec<>("CreativeRegenSpeed", Codec.FLOAT), (cfg, value)-> cfg.creativeRegenSpeed = value, cfg -> cfg.creativeRegenSpeed).add()
            .append(new KeyedCodec<>("SaturationLossSpeed", Codec.FLOAT), (cfg, value)-> cfg.saturationLossSpeed = value, cfg -> cfg.saturationLossSpeed).add()
            .append(new KeyedCodec<>("SaturationLossTimeSeconds", Codec.FLOAT), (cfg, value)-> cfg.saturationLossTimeSeconds = value, cfg -> cfg.saturationLossTimeSeconds).add()
            .append(new KeyedCodec<>("StarvingDamage", Codec.FLOAT), (cfg, value)-> cfg.starvingDamage = value, cfg -> cfg.starvingDamage).add()
            .append(new KeyedCodec<>("StarvingStaminaDamage", Codec.FLOAT), (cfg, value)-> cfg.starvingStaminaDamage = value, cfg -> cfg.starvingStaminaDamage).add()
            .append(new KeyedCodec<>("StarvingDamageTimeSeconds", Codec.FLOAT), (cfg, value)-> cfg.starvingDamageTimeSeconds = value, cfg -> cfg.starvingDamageTimeSeconds).add()
            .build();

    protected Map<String, Float> itemIds = new HashMap<>();
    protected float defaultSaturation = 10;
    protected float creativeRegenSpeed = 80;
    protected float saturationLossSpeed = 1.5F;
    protected float saturationLossTimeSeconds = 20;
    protected float starvingDamage = 2;
    protected float starvingStaminaDamage=10;
    protected float starvingDamageTimeSeconds = 4;

    public SimpleHungerConfig() {
        itemIds.put("Ingredient_Dough", 2.0F);
        itemIds.put("Ingredient_Flour", 1.0F);
        itemIds.put("Ingredient_Salt", 0.0F);
        itemIds.put("Ingredient_Spices", 0.0F);
        itemIds.put("Plant_Fruit_Windwillow", 10.0F);
        itemIds.put("Plant_Fruit_Spiral", 10.0F);
        itemIds.put("Plant_Fruit_Poison", 8.0F);
        itemIds.put("Plant_Fruit_Pinkberry",10.0F);
        itemIds.put("Plant_Fruit_Mango", 15.0F);
        itemIds.put("Plant_Fruit_Coconut", 15.0F);
        itemIds.put("Plant_Fruit_Berries_Red", 5.0F);
        itemIds.put("Plant_Fruit_Azure", 8.0F);
        itemIds.put("Plant_Fruit_Apple", 15.0F);
        itemIds.put("Plant_Crop_Rice_Item", 5.0F);
        itemIds.put("Plant_Crop_Aubergine_Item", 8.0F);
        itemIds.put("Plant_Crop_Potato_Item", 7.0F);
        itemIds.put("Plant_Crop_Carrot_Item", 8.0F);
        itemIds.put("Plant_Crop_Cauliflower_Item", 8.0F);
        itemIds.put("Plant_Crop_Chilli_Item", 6.0F);
        itemIds.put("Plant_Crop_Corn_Item", 10.0F);
        itemIds.put("Plant_Crop_Lettuce_Item", 5.0F);
        itemIds.put("Plant_Crop_Onion_Item", 4.0F);
        itemIds.put("Plant_Crop_Pumpkin_Item", 12.0F);
        itemIds.put("Plant_Crop_Tomato_Item", 7.0F);
        itemIds.put("Plant_Crop_Turnip_Item", 8.0F);
        itemIds.put("Food_Wildmeat_Raw", 10.0F);
        itemIds.put("Food_Pork_Raw", 10.0F);
        itemIds.put("Food_Chicken_Raw", 8.0F);
        itemIds.put("Food_Beef_Raw", 12.0F);
        itemIds.put("Food_Fish_Raw", 8.0F);
        itemIds.put("Food_Fish_Raw_Uncommon", 10.0F);
        itemIds.put("Food_Fish_Raw_Rare", 15.0F);
        itemIds.put("Food_Fish_Raw_Epic", 25.0F);
        itemIds.put("Food_Fish_Raw_Legendary", 40.0F);
        itemIds.put("Food_Egg", 5.0F);
        itemIds.put("Food_Cheese", 15.0F);
        itemIds.put("Food_Bread", 20.0F);
        itemIds.put("Food_Popcorn", 5.0F);
        itemIds.put("Food_Candy_Cane", 5.0F);
        itemIds.put("Food_Wildmeat_Cooked", 35.0F);
        itemIds.put("Food_Vegetable_Cooked", 25.0F);
        itemIds.put("Food_Fish_Grilled", 30.0F);
        itemIds.put("Food_Salad_Mushroom", 20.0F);
        itemIds.put("Food_Salad_Caesar", 25.0F);
        itemIds.put("Food_Salad_Berry", 18.0F);
        itemIds.put("Food_Kebab_Vegetable", 30.0F);
        itemIds.put("Food_Kebab_Mushroom", 30.0F);
        itemIds.put("Food_Kebab_Fruit", 25.0F);
        itemIds.put("Food_Kebab_Meat", 45.0F);
        itemIds.put("Food_Pie_Pumpkin", 50.0F);
        itemIds.put("Food_Pie_Meat", 65.0F);
        itemIds.put("Food_Pie_Apple", 45.0F);
        itemIds.put("Halloween_Basket_Straw", 40.0F);
        itemIds.put("Halloween_Basket_Pumpkin", 50.0F);
    }
}