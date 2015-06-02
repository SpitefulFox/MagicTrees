package fox.spiteful.magictrees.genetics;

import forestry.api.genetics.IAllele;
import net.minecraft.util.StatCollector;

public class Allele implements IAllele {

    private String name;
    private String id;
    private boolean dom;

    public Allele(String moniker, boolean dominant){
        name = "magictrees.allele." + moniker;
        id = "magictrees." + moniker;
        dom = dominant;
    }

    @Override
    public String getUID(){
        return id;
    }

    @Override
    public String getName(){
        return StatCollector.translateToLocal(name);
    }

    @Override
    public String getUnlocalizedName(){
        return name;
    }

    @Override
    public boolean isDominant(){
        return dom;
    }
}
