package ftb.utils.mod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import ftb.lib.EventBusHelper;
import ftb.lib.FTBLib;
import ftb.lib.LMMod;
import ftb.utils.api.guide.Top;
import ftb.utils.mod.cmd.CmdBack;
import ftb.utils.mod.cmd.CmdDelHome;
import ftb.utils.mod.cmd.CmdHome;
import ftb.utils.mod.cmd.CmdLMPlayerSettings;
import ftb.utils.mod.cmd.CmdSetHome;
import ftb.utils.mod.cmd.CmdSpawn;
import ftb.utils.mod.cmd.CmdTplast;
import ftb.utils.mod.cmd.CmdWarp;
import ftb.utils.mod.cmd.admin.CmdAdmin;
import ftb.utils.mod.config.FTBUConfig;
import ftb.utils.mod.config.FTBUConfigCmd;
import ftb.utils.mod.handlers.FTBUChatEventHandler;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.mod.handlers.FTBUPlayerEventHandler;
import ftb.utils.mod.handlers.FTBUWorldEventHandler;
import ftb.utils.mod.handlers.ftbl.FTBLIntegration;
import ftb.utils.net.FTBUNetHandler;
import ftb.utils.world.Backups;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(modid = FTBUFinals.MOD_ID, version = FTBUFinals.MOD_VERSION, name = FTBUFinals.MOD_NAME, dependencies = FTBUFinals.MOD_DEP, acceptedMinecraftVersions = "1.7.10")
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU inst;
	
	@SidedProxy(clientSide = "ftb.utils.mod.client.FTBUClient", serverSide = "ftb.utils.mod.FTBUCommon")
	public static FTBUCommon proxy;
	
	@SidedProxy(clientSide = "ftb.utils.mod.handlers.ftbl.FTBLIntegrationClient", serverSide = "ftb.utils.mod.handlers.ftbl.FTBLIntegration")
	public static FTBLIntegration ftbl_int;
	
	public static LMMod mod;
	
	public static Logger logger;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		mod = LMMod.create(FTBUFinals.MOD_ID);
		FTBLib.ftbu = ftbl_int;
		logger = LogManager.getLogger(FTBUFinals.MOD_ID);
		
		FTBUConfig.load();
		
		EventBusHelper.register(new FTBUPlayerEventHandler());
		EventBusHelper.register(new FTBUWorldEventHandler());
		EventBusHelper.register(new FTBUChatEventHandler());
		FTBUChunkEventHandler.instance.init();
		
		FTBUNetHandler.init();
		Backups.init();
		Top.init();
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		FTBLib.addCommand(e, new CmdAdmin());
		FTBLib.addCommand(e, new CmdLMPlayerSettings());
		
		if(FTBUConfigCmd.back.getAsBoolean()) FTBLib.addCommand(e, new CmdBack());
		if(FTBUConfigCmd.spawn.getAsBoolean()) FTBLib.addCommand(e, new CmdSpawn());
		if(FTBUConfigCmd.tplast.getAsBoolean()) FTBLib.addCommand(e, new CmdTplast());
		if(FTBUConfigCmd.warp.getAsBoolean()) FTBLib.addCommand(e, new CmdWarp());
		
		if(FTBUConfigCmd.home.getAsBoolean())
		{
			FTBLib.addCommand(e, new CmdSetHome());
			FTBLib.addCommand(e, new CmdHome());
			FTBLib.addCommand(e, new CmdDelHome());
		}
	}
	
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent e)
	{
		if(FTBLib.hasOnlinePlayers())
		{
			for(EntityPlayerMP ep : FTBLib.getAllOnlinePlayers(null))
			{
				FTBUPlayerEventHandler.playerLoggedOut(ep);
			}
		}
	}
	
	@NetworkCheckHandler
	public boolean checkNetwork(Map<String, String> m, Side side)
	{
		String s = m.get(FTBUFinals.MOD_ID);
		return s == null || s.equals(FTBUFinals.MOD_VERSION);
	}
}