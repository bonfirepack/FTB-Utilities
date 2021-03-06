package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.FTBLib;
import ftb.lib.api.friends.ILMPlayer;
import ftb.utils.mod.FTBU;
import ftb.utils.world.claims.LMWorldSettings;
import latmod.lib.LMUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class LMWorld // FTBWorld
{
	public static LMWorld getWorld(Side s)
	{
		if(s.isServer()) return LMWorldServer.inst;
		return FTBU.proxy.getClientWorldLM();
	}
	
	public static LMWorld getWorld()
	{ return getWorld(FTBLib.getEffectiveSide()); }
	
	public final Side side;
	public final LMWorldSettings settings;
	
	public LMWorld(Side s)
	{
		side = s;
		settings = new LMWorldSettings(this);
	}
	
	public abstract Map<UUID, ? extends LMPlayer> playerMap();
	
	public abstract World getMCWorld();
	
	public LMWorldServer getServerWorld()
	{ return null; }
	
	@SideOnly(Side.CLIENT)
	public LMWorldClient getClientWorld()
	{ return null; }
	
	public LMPlayer getPlayer(Object o)
	{
		if(o == null || o instanceof FakePlayer) return null;
		
		if(o.getClass() == UUID.class)
		{
			return playerMap().get(o);
		}
		else if(o instanceof GameProfile)
		{
			return playerMap().get(((GameProfile) o).getId());
		}
		else if(o instanceof ILMPlayer)
		{
			return playerMap().get(((ILMPlayer) o).getProfile().getId());
		}
		else if(o instanceof EntityPlayer)
		{
			return getPlayer(((EntityPlayer) o).getGameProfile().getId());
		}
		else if(o instanceof CharSequence)
		{
			String s = o.toString();
			
			if(s == null || s.isEmpty()) return null;
			
			for(LMPlayer p : playerMap().values())
			{
				if(p.getProfile().getName().equalsIgnoreCase(s))
				{
					return p;
				}
			}
			
			return getPlayer(LMUtils.fromString(s));
		}
		
		return null;
	}
	
	public List<LMPlayer> getAllOnlinePlayers()
	{
		List<LMPlayer> l = new ArrayList<>();
		
		for(LMPlayer p : playerMap().values())
		{
			if(p.isOnline())
			{
				l.add(p);
			}
		}
		
		return l;
	}
}