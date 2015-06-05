package fox.spiteful.magictrees.genetics;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.*;
import forestry.api.world.ITreeGenData;
import fox.spiteful.magictrees.compat.Compat;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.EnumPlantType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public enum MagicTreeSpecies implements IAlleleTreeSpecies, IIconProvider {

    SPIRIT(),
    OCCULT(),
    GREATWOOD(),
    SILVERWOOD();
	
    /**
     * Helps map Tree types to leaf block textures & variants thereof.
     * Don't ask, because we don't know. It's what Forestry does~
     */
	private enum LeafType {
		NORMAL(10, 11, 12),
		CONIFER(15, 16, 17),
		JUNGLE(20, 21, 22),
		WILLOW(25, 26, 27),
		MAPLE(30, 31, 32),
		PALM(35, 36, 37);
		
		public final short fancyID;
		public final short plainID;
		public final short changedID;
		
		private LeafType(int fancy, int plain, int changed) {
			fancyID = (short)fancy;
			plainID = (short)plain;
			changedID = (short)changed;
		}		
	}

    private String name;
    private String binomial;
    private WorldGenerator treeGen;
    private IClassification branch;
    private boolean dom;
    private String authority;
    private EnumTemperature temperature = EnumTemperature.NORMAL;
    private EnumHumidity humidity = EnumHumidity.NORMAL;
    private boolean counted = true;
    private boolean secret = false;
    private boolean fancy = false;
    private int color;
    private Collection<IFruitFamily> fruits;
    private ItemStack log;
    private LeafType leafType = LeafType.NORMAL;

    MagicTreeSpecies(String nombre, String author, IClassification bran, boolean dominate, int color, ItemStack wood){
        name = nombre;
        authority = author;
        branch = bran;
        log = wood;
        dom = dominate;
    }

    @Override
    public String getUID(){
        return "magictrees." + name;
    }

    @Override
    public String getName(){
        return StatCollector.translateToLocal("magictrees.tree." + name);
    }

    @Override
    public String getUnlocalizedName(){
        return "magictrees.tree." + name;
    }

    @Override
    public String getBinomial(){
        return binomial;
    }

    @Override
    public String getAuthority(){
        return authority;
    }

    @Override
    public String getDescription(){
        return "magictrees.description." + name;
    }

    @Override
    public Collection<IFruitFamily> getSuitableFruit(){
        return fruits;
    }

    @Override
    public ItemStack[] getLogStacks(){
        if(log != null)
            return new ItemStack[]{log};
        else
            return new ItemStack[]{};
    }

    @Override
    public float getResearchSuitability(ItemStack itemstack) {
        if (itemstack == null) {
            return 0f;
        }
        if (itemstack.getItem() == Compat.forestryHoney) {
            return 0.5f;
        } else if (itemstack.getItem() == Compat.forestryHoneydew) {
            return 0.7f;
        } else if (itemstack.getItem() == Compat.forestryComb) {
            return 0.4f;
        } else if (getRoot().isMember(itemstack)) {
            return 1.0f;
        }
        for (Map.Entry<ItemStack, Float> entry : getRoot().getResearchCatalysts().entrySet()) {
            if (entry.getKey().isItemEqual(itemstack)) {
                return entry.getValue();
            }
        }
        return 0f;
    }

    @Override
    public ItemStack[] getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
        ItemStack research = null;
        if (world.rand.nextFloat() < ((float) 10 / bountyLevel)) {
            Collection<? extends IMutation> combinations = getRoot().getCombinations(this);
            if (combinations.size() > 0) {
                IMutation[] candidates = combinations.toArray(new IMutation[combinations.size()]);
                research = AlleleManager.alleleRegistry.getMutationNoteStack(researcher, candidates[world.rand.nextInt(candidates.length)]);
            }
        }
        if (research != null) {
            return new ItemStack[]{research};
        } else {
            return new ItemStack[]{};
        }
    }

    @Override
    public int getComplexity() {
        return 1 + getGeneticAdvancement(this, new ArrayList<IAllele>());
    }

    private int getGeneticAdvancement(IAllele species, ArrayList<IAllele> exclude) {
        int own = 1;
        int highest = 0;
        exclude.add(species);
        for (IMutation mutation : getRoot().getPaths(species, EnumBeeChromosome.SPECIES)) {
            if (!exclude.contains(mutation.getAllele0())) {
                int otherAdvance = getGeneticAdvancement(mutation.getAllele0(), exclude);
                if (otherAdvance > highest) {
                    highest = otherAdvance;
                }
            }
            if (!exclude.contains(mutation.getAllele1())) {
                int otherAdvance = getGeneticAdvancement(mutation.getAllele1(), exclude);
                if (otherAdvance > highest) {
                    highest = otherAdvance;
                }
            }
        }
        return own + (highest < 0 ? 0 : highest);
    }

    @Override
    public WorldGenerator getGenerator(ITree tree, World world, int x, int y, int z){
        if(treeGen != null) {
            try {
                return treeGen.getClass().getConstructor(new Class[]{ITreeGenData.class}).newInstance(tree);
            } catch (Exception e) {}
        }
        return treeGen;
    }

    @Override
    public Class<? extends WorldGenerator>[] getGeneratorClasses(){
        return null;
    }

    @Override
    public IClassification getBranch() {
        return this.branch;
    }

    @Override
    public boolean isDominant() {
        return dom;
    }

    @Override
    public boolean isCounted(){
        return counted;
    }

    @Override
    public boolean isSecret(){
        return secret;
    }

    @Override
    public boolean hasEffect(){
        return fancy;
    }

    @Override
    public ITreeRoot getRoot(){
        return (ITreeRoot)(AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees"));
    }

    @Override
    public EnumTemperature getTemperature()
    {
        return temperature;
    }

    @Override
    public EnumHumidity getHumidity()
    {
        return humidity;
    }

    @Override
    public EnumPlantType getPlantType()
    {
        return EnumPlantType.Plains;
    }

    @Override
    public int getIconColour(int renderPass) {
        if (renderPass == 0) {
            return color;
        }
        return 0xffffff;
    }

    @Override
    public short getLeafIconIndex(ITree tree, boolean fancy){
        if (!fancy) {
        	return leafType.plainID;
        }
        else if (tree.getMate() != null) {
        	return leafType.changedID;
        }
        else {
        	return leafType.fancyID;
        }
    }

    @Override
    public int getLeafColour(ITree tree)
    {
        return color;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getGermlingIcon(EnumGermlingType type, int renderPass) {
        if (type == EnumGermlingType.POLLEN) {
            return Compat.forestryPollen.getIconFromDamageForRenderPass(0, renderPass);
        }
        //Return sapling icon!
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getGermlingColour(EnumGermlingType type, int renderPass) {
        if (type == EnumGermlingType.SAPLING) {
            return 0xFFFFFF;
        }
        return getLeafColour(null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIconProvider getIconProvider(){
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(short texUID){
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register){

    }

}
