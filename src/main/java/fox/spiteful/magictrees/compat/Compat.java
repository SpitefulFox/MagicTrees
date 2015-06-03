package fox.spiteful.magictrees.compat;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class Compat {

    public static boolean thaumic = false;
    public static boolean botanical = false;
    public static boolean bloody = false;

    public static Item forestryComb;
    public static Item forestryHoney;
    public static Item forestryHoneydew;
    public static Item forestryPollen;

    public static void census(){
        thaumic = Loader.isModLoaded("Thaumcraft");
        botanical = Loader.isModLoaded("Botania");
        bloody = Loader.isModLoaded("AWWayofTime");
    }

    public static void compatify(){

        forestryComb = getItem("Forestry", "beeCombs");
        forestryHoney = getItem("Forestry", "honeyDrop");
        forestryHoneydew = getItem("Forestry", "honeydew");
        forestryPollen = getItem("Forestry", "pollenFertile");

    }

    public static Item getItem(String mod, String item) {
        Item target = GameRegistry.findItem(mod, item);
        if(target == null)
            throw new ItemNotFoundException(mod, item);
        return target;
    }

    public static class ItemNotFoundException extends RuntimeException {
        public ItemNotFoundException(String mod, String item){
            super("Unable to find item " + item + " in mod " + mod + "! Are you using the correct version of the mod?");
        }
    }
}
