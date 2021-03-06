package ftb.utils.mod.client.gui.friends;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.client.GlStateManager;
import ftb.lib.api.info.InfoPage;
import ftb.lib.api.info.lines.InfoTextLine;
import ftb.lib.mod.client.gui.info.ButtonInfoTextLine;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.world.LMPlayerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

/**
 * Created by LatvianModder on 23.03.2016.
 */
@SideOnly(Side.CLIENT)
public class InfoPlayerViewLine extends InfoTextLine
{
	public final LMPlayerClient playerLM;
	
	public InfoPlayerViewLine(InfoPage c, LMPlayerClient p)
	{
		super(c, null);
		playerLM = p;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ButtonInfoTextLine createWidget(GuiInfo gui)
	{ return new ButtonInfoPlayerView(gui, this); }
	
	public class ButtonInfoPlayerView extends ButtonInfoTextLine
	{
		private Player player;
		
		public ButtonInfoPlayerView(GuiInfo g, InfoPlayerViewLine w)
		{
			super(g, null);
			height = 1;
		}
		
		@Override
		public void renderWidget()
		{
			int ay = getAY();
			int ax = getAX();
			
			if(player == null) player = new Player(playerLM);
			
			if(mouseOver() && Mouse.isButtonDown(1))
			{
				for(int i = 0; i < player.inventory.armorInventory.length; i++)
				{
					player.inventory.armorInventory[i] = null;
				}
			}
			else
			{
				EntityPlayer ep1 = playerLM.getPlayer();
				
				if(ep1 != null)
				{
					player.inventory.mainInventory[0] = ep1.inventory.mainInventory[ep1.inventory.currentItem];
					System.arraycopy(ep1.inventory.armorInventory, 0, player.inventory.armorInventory, 0, 4);
					player.inventory.currentItem = 0;
				}
				else
				{
					System.arraycopy(playerLM.lastArmor, 0, player.inventory.armorInventory, 0, 4);
					player.inventory.mainInventory[0] = playerLM.lastArmor[4];
					player.inventory.currentItem = 0;
				}
			}
			
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			
			int pheight = 120;
			int pwidth = (int) (pheight / 1.625F);
			
			int playerX = guiInfo.mainPanel.width - pwidth / 2 - 30;
			int playerY = ay + pheight + 10;
			
			pheight = pheight / 2;
			
			FTBLibClient.setTexture(player.getLocationSkin());
			GlStateManager.translate(0F, 0F, 100F);
			GuiInventory.func_147046_a(playerX, playerY, pheight, playerX - gui.mouse().x, playerY - (pheight + (pheight / 1.625F)) - gui.mouse().y, player);
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}
		
		public class Player extends AbstractClientPlayer
		{
			public Player(LMPlayerClient p)
			{
				super(Minecraft.getMinecraft().theWorld, p.getProfile());
			}
			
			@Override
			public boolean equals(Object o)
			{ return playerLM.equals(o); }
			
			@Override
			public void addChatMessage(IChatComponent i) { }
			
			@Override
			public boolean canCommandSenderUseCommand(int i, String s)
			{ return false; }
			
			@Override
			public ChunkCoordinates getPlayerCoordinates()
			{ return new ChunkCoordinates(0, 0, 0); }
			
			@Override
			public boolean isInvisibleToPlayer(EntityPlayer ep)
			{ return true; }
			
			@Override
			public ResourceLocation getLocationSkin()
			{ return playerLM.getSkin(); }
			
			@Override
			public boolean func_152122_n()
			{ return false; }
			
			@Override
			public ResourceLocation getLocationCape()
			{ return null; }
		}
	}
}
