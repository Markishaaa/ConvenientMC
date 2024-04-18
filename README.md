# ConvenientMC Paper Plugin

ConvenientMC is a Paper plugin designed to enhance the convenience of gameplay in Minecraft. It introduces several features that modify existing mechanics or introduce new crafting recipes to make the player's experience more enjoyable.

## Compatibility

ConvenientMC is designed for Minecraft version 1.20.4.

## Features

### 1. Stripped Log Crafting
Craft stripped logs effortlessly by placing your axe and unstripped logs in a crafting area. The plugin calculates durability, considering enchantments like Unbreaking, and subtracts the appropriate amount of durability from the axe.

### 2. More Yield in Crafting Recipes
Increase the yield for various crafting recipes, such as:
- Wooden stairs (now gives 6 instead of 4)
- Wooden trapdoors (now gives 4 instead of 2)
- All buttons (now gives 4 instead of 1)
- Banners (now gives 4 instead of 1)

### 3. New Crafting Recipes
Discover new crafting possibilities, including:
- Turning clay and snow blocks into clay and snowballs
- Converting wool into 4 strings
- Combining 4 mangrove roots to obtain 2 mangrove logs

### 4. Modified Enchantments
Enable the addition of "looting" to a trident exclusively through an anvil.

### 5. Combat Update
Shields now have no delay when used (default was a 5 tick delay).

### 6. Villager Transportation
Transport villagers using camels:
- Right-click a villager while on a camel to make them climb onto it if you're close to them.
- Right-click the villager again to make them get off the camel when you're done transporting them.

### 7. Dispenser Enhancements
Dispensers now have additional functionality:
- When facing down with a cauldron below, they can dispense water, lava, and powdered snow into the cauldrons (using buckets), as well as retrieve them.
- You can add and retrieve layers of water to cauldrons using water bottles.

### 8. Recoloring Items
All items that can be colored (like concrete, glass, candles) can now be recolored. 
- Recipes are the same as when coloring them the first time, but now you can use any colored item with a specific dye to recolor it.
- You can drop any colored stained clay into a cauldron with water to wash the color off (turns it into "minecraft:terracotta"). Washing away color also consumes water from the cauldron (same as washing the color of a leather armor piece would). A third of a stack lowers one level of water in a cauldron (1 full stack will consume all of the water).

## Installation

1. Download the plugin JAR file from the [releases](https://github.com/Markishaaa/ConvenientMC/releases) section.
2. Place the JAR file in the `plugins` folder of your Spigot server.
3. Restart your server to apply the changes.

## Configuration

The plugin comes with a customizable configuration file that generates itself after the first run. In this file, you can selectively disable specific features if you prefer not to have them on your server.

You can find the configuration file in your server's directory under: `/plugins/ConvenientMC/config.yml`.

## Issues and Feedback

If you encounter any issues or have suggestions for improvement, feel free to open an [issue](https://github.com/Markishaaa/ConvenientMC/issues) on the GitHub repository.

## License

This project is licensed under the [MIT License](LICENSE).
