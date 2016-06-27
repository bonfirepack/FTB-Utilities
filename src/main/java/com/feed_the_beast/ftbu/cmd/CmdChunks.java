package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandSubBase;
import com.feed_the_beast.ftbl.api.notification.Notification;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.LMAccessToken;
import com.feed_the_beast.ftbu.FTBUGuiHandler;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdChunks extends CommandSubBase
{
    public class CmdClaim extends CommandLM
    {
        public CmdClaim()
        {
            super("claim");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            ForgePlayerMP p = ForgePlayerMP.get(ep);

            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(ep.dimension, parseInt(args[0]), parseInt(args[1]));
            }
            else
            {
                pos = p.getPos().toChunkPos();
            }

            if(!FTBUWorldDataMP.claimChunk(p, pos))
            {
                Notification.error("modify_chunk", new TextComponentString("Can't claim this chunk!")).sendTo(ep); //TODO: Lang
            }
        }
    }

    public class CmdUnclaim extends CommandLM
    {
        public CmdUnclaim()
        {
            super("unclaim");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            ForgePlayerMP p = ForgePlayerMP.get(ep);

            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(ep.dimension, parseInt(args[0]), parseInt(args[1]));
            }
            else
            {
                pos = p.getPos().toChunkPos();
            }

            if(!FTBUWorldDataMP.unclaimChunk(p, pos))
            {
                Notification.error("modify_chunk", new TextComponentString("Can't unclaim this chunk!")).sendTo(ep); //TODO: Lang
            }
        }
    }

    public class CmdLoad extends CommandLM
    {
        public CmdLoad()
        {
            super("load");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            ForgePlayerMP p = ForgePlayerMP.get(ep);

            checkArgs(args, 1, "<loaded> [x] [z]");

            boolean loaded = parseBoolean(args[0]);

            ChunkDimPos pos;

            if(args.length >= 3)
            {
                pos = new ChunkDimPos(ep.dimension, parseInt(args[1]), parseInt(args[2]));
            }
            else
            {
                pos = p.getPos().toChunkPos();
            }

            if(FTBUWorldDataMP.setLoaded(p, pos, loaded))
            {
                new Notification("chunk_modified").addText(new TextComponentString(loaded ? "Chunk Loaded" : "Chunk Unloaded")); //TODO: Lang
            }
        }
    }

    public class CmdUnload extends CommandLM
    {
        public CmdUnload()
        {
            super("unload");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            ForgePlayerMP p = ForgePlayerMP.get(ep);

            checkArgs(args, 1, "<loaded> [x] [z]");

            boolean loaded = parseBoolean(args[0]);

            ChunkDimPos pos;

            if(args.length >= 3)
            {
                pos = new ChunkDimPos(ep.dimension, parseInt(args[1]), parseInt(args[2]));
            }
            else
            {
                pos = p.getPos().toChunkPos();
            }

            if(FTBUWorldDataMP.setLoaded(p, pos, loaded))
            {
                new Notification("chunk_modified").addText(new TextComponentString(loaded ? "Chunk Loaded" : "Chunk Unloaded")); //TODO: Lang
            }
        }
    }

    public class CmdUnclaimAll extends CommandLM
    {
        public CmdUnclaimAll()
        {
            super("unclaim_all");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            ForgePlayerMP p = ForgePlayerMP.get(ep);

            checkArgs(args, 1, "<all_dimensions>");
            FTBUWorldDataMP.unclaimAllChunks(p, parseBoolean(args[0]) ? null : ep.dimension);
            new Notification("unclaimed_all").addText(new TextComponentString("Unclaimed all chunks")).sendTo(ep); //TODO: Lang
        }
    }

    public class CmdUnloadAll extends CommandLM
    {
        public CmdUnloadAll()
        {
            super("admin_unload_all_chunks");
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
        {
            checkArgs(args, 1, "<player>");

            if(args[0].equals("@a"))
            {
                for(ClaimedChunk c : FTBUWorldDataMP.chunks.getAllChunks())
                {
                    c.loaded = false;
                }
                for(ForgePlayer p : ForgeWorldMP.inst.getOnlinePlayers())
                {
                    p.toMP().sendUpdate();
                }
                ics.addChatMessage(new TextComponentString("Unloaded all chunks")); //TODO: Lang
                return;
            }

            ForgePlayerMP p = ForgePlayerMP.get(args[0]);
            for(ClaimedChunk c : FTBUWorldDataMP.chunks.getChunks(p.getProfile().getId()))
            {
                c.loaded = false;
            }
            if(p.isOnline())
            {
                p.sendUpdate();
            }
            ics.addChatMessage(new TextComponentString("Unloaded all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
        }
    }

    public class CmdAdminUnclaim extends CommandLM
    {
        public CmdAdminUnclaim()
        {
            super("admin_unclaim");
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
            NBTTagCompound data = new NBTTagCompound();
            data.setLong("T", LMAccessToken.generate(ep));
            FTBUGuiHandler.instance.openGui(ep, FTBUGuiHandler.ADMIN_CLAIMS, data);
        }
    }

    public class CmdAdminUnclaimAll extends CommandLM
    {
        public CmdAdminUnclaimAll()
        {
            super("admin_unclaim_all_chunks");
        }

        @Nonnull
        @Override
        public String getCommandUsage(@Nonnull ICommandSender ics)
        {
            return '/' + commandName + " <player | @a>";
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
        {
            checkArgs(args, 1, "<player>");
            ForgePlayerMP p = ForgePlayerMP.get(args[0]);
            FTBUWorldDataMP.unclaimAllChunks(p, null);
            ics.addChatMessage(new TextComponentString("Unclaimed all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
        }
    }

    public CmdChunks()
    {
        super("chunks");
        add(new CmdClaim());
        add(new CmdUnclaim());
        add(new CmdLoad());
        add(new CmdUnload());

        add(new CmdUnclaimAll());
        add(new CmdUnloadAll());
        add(new CmdAdminUnclaim());
        add(new CmdAdminUnclaimAll());
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}