# Contribute

Structures and dimensions don't have localized names by default. 
If you see something like `structure.<modid>.<name>` in the GUI or HUD, that essentially means there is not localized name for it.
If you would like to add translations, 
create corresponding JSON files under [`resources/assets/<modid>/lang/`](/src/main/resources/assets) and send Pull Requests to us. 
We've put `zh_cn` translations of structures and dimensions in vanilla Minecraft there as a template, 
and your translations should also follow the same style.
```json
{
  "dimension.minecraft.overworld": "Overworld",
  "structure.minecraft.bastion_remnant": "Bastion Remnant"
}
```
