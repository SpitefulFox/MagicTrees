package fox.spiteful.magictrees;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import fox.spiteful.magictrees.compat.Compat;

@Mod(modid = "MagicTrees", name = "Magic Trees", dependencies = "required-after:Forestry;after:Thaumcraft;after:AWWayofTime;after:Botania")
public class MagicTrees {

    @Instance
    public static MagicTrees instance;

    @EventHandler
    public void sapling(FMLPreInitializationEvent event){
        Compat.census();
        Compat.compatify();
    }

    @EventHandler
    public void growth(FMLInitializationEvent event){

    }

    @EventHandler
    public void chop(FMLPostInitializationEvent event){

    }
}
