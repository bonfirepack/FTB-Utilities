package latmod.core.base;
import java.util.*;

import latmod.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;

public abstract class ItemLM extends Item
{
	public final String itemName;
	public final FastList<ItemStack> itemsAdded;
	public final LMMod mod;

	public ItemLM(LMMod m, String s)
	{
		super();
		mod = m;
		itemName = s;
		setUnlocalizedName(mod.getItemName(s));
		itemsAdded = new FastList<ItemStack>();
	}
	
	@SideOnly(Side.CLIENT)
	public abstract CreativeTabs getCreativeTab();
	
	public void onPostLoaded()
	{ itemsAdded.add(new ItemStack(this)); }
	
	@SuppressWarnings("all")
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item j, CreativeTabs c, List l)
	{ l.addAll(itemsAdded); }
	
	public String getUnlocalizedName(ItemStack is)
	{ return mod.getItemName(itemName); }
	
	public void addAllDamages(int until)
	{
		for(int i = 0; i < until; i++)
		itemsAdded.add(new ItemStack(this, 1, i));
	}
	
	public void addAllDamages(int[] dmg)
	{
		for(int i = 0; i < dmg.length; i++)
		itemsAdded.add(new ItemStack(this, 1, dmg[i]));
	}
	
	public final boolean requiresMultipleRenderPasses()
	{ return true; }

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir)
	{ itemIcon = ir.registerIcon(mod.assets + itemName); }

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int i, int r)
	{ return itemIcon; }

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack is, int r)
	{ return getIconFromDamageForRenderPass(is.getItemDamage(), r); }
	
	@SuppressWarnings("all") @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack is, EntityPlayer ep, List l, boolean b)
	{
	}

	public void loadRecipes()
	{
	}
}