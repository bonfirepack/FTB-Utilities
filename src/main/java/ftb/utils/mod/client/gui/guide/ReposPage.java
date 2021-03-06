package ftb.utils.mod.client.gui.guide;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.api.GuiLang;
import ftb.lib.api.MouseButton;
import ftb.lib.api.gui.GuiIcons;
import ftb.lib.api.gui.widgets.ButtonLM;
import ftb.lib.api.info.InfoPage;
import ftb.lib.mod.client.gui.info.GuiInfo;
import ftb.utils.api.guide.repos.GuideOnlineRepo;
import ftb.utils.api.guide.repos.GuideRepoList;
import latmod.lib.LMColor;
import latmod.lib.LMStringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LatvianModder on 03.04.2016.
 */
@SideOnly(Side.CLIENT)
public class ReposPage extends InfoPage
{
	public static boolean refreshedFirst = false;
	private static Thread thread;
	
	public ReposPage()
	{
		super("Online Guides");
		
		backgroundColor = new LMColor.RGB(30, 30, 30);
		textColor = new LMColor.RGB(20, 200, 255);
		useUnicodeFont = Boolean.FALSE;
		
		if(!refreshedFirst)
		{
			refreshedFirst = true;
			runRefreshThread();
		}
	}
	
	@Override
	public ButtonLM createSpecialButton(final GuiInfo guiInfo)
	{
		ButtonLM button = new ButtonLM(guiInfo, 0, 0, 16, 16)
		{
			@Override
			public void onClicked(MouseButton button)
			{
				clear();
				addSub(new InfoPage("Loading..."));
				gui.refreshWidgets();
				runRefreshThread();
			}
			
			@Override
			public void renderWidget()
			{
				render(GuiIcons.refresh);
			}
		};
		
		button.title = EnumChatFormatting.GREEN + GuiLang.button_refresh.format();
		return button;
	}
	
	@Override
	public void refreshGui(GuiInfo gui)
	{
		clear();
		
		List<GuideOnlineRepo> list = new ArrayList<>();
		list.addAll(GuideRepoList.onlineRepos.values());
		Collections.sort(list, LMStringUtils.ignoreCaseComparator);
		
		for(GuideOnlineRepo r : list)
		{
			addSub(new PageOnlineRepo(r));
		}
		
		thread = null;
	}
	
	public void runRefreshThread()
	{
		if(thread != null) return;
		
		thread = new Thread()
		{
			@Override
			public void run()
			{
				GuideRepoList.refreshOnlineRepos();
				
				if(Minecraft.getMinecraft().currentScreen instanceof GuiInfo)
					((GuiInfo) Minecraft.getMinecraft().currentScreen).refreshWidgets();
			}
		};
		
		thread.setDaemon(true);
		thread.start();
	}
}
