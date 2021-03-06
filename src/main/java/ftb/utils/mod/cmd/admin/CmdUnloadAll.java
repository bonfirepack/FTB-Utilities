package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.world.LMPlayer;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import ftb.utils.world.claims.ClaimedChunk;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CmdUnloadAll extends CommandLM
{
	public CmdUnloadAll()
	{ super("unload_all", CommandLevel.OP); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player | @a>"; }
	
	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(args[0].equals("@a"))
		{
			for(ClaimedChunk c : LMWorldServer.inst.claimedChunks.getAllChunks())
				c.isChunkloaded = false;
			for(LMPlayer p : LMWorldServer.inst.getAllOnlinePlayers())
				p.toPlayerMP().sendUpdate();
			ics.addChatMessage(new ChatComponentText("Unloaded all chunks"));
		}
		else
		{
			LMPlayerServer p = LMPlayerServer.get(args[0]);
			for(ClaimedChunk c : LMWorldServer.inst.claimedChunks.getChunks(p, null))
				c.isChunkloaded = false;
			if(p.isOnline()) p.sendUpdate();
			ics.addChatMessage(new ChatComponentText("Unloaded all " + p.getProfile().getName() + "'s chunks"));
		}
	}
}