package ftb.utils.mod.cmd;

import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.mod.FTBULang;
import ftb.utils.world.LMPlayerServer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public class CmdSetHome extends CommandLM
{
	public CmdSetHome()
	{ super("sethome", CommandLevel.ALL); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
	{
		if(args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, LMPlayerServer.get(ics).homes.list());
		}
		
		return super.addTabCompletionOptions(ics, args);
	}
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayerServer p = LMPlayerServer.get(ep);
		checkArgs(args, 1);
		
		int maxHomes = p.getRank().config.max_homes.getAsInt();
		
		if(maxHomes <= 0 || p.homes.size() >= maxHomes)
		{
			if(maxHomes == 0 || p.homes.get(args[0]) == null)
			{
				FTBULang.home_limit.commandError();
			}
		}
		
		p.homes.set(args[0], p.getPos());
		FTBULang.home_set.printChat(ics, args[0]);
	}
}