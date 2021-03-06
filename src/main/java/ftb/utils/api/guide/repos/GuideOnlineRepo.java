package ftb.utils.api.guide.repos;

import ftb.lib.api.client.FTBLibClient;
import ftb.utils.api.guide.ClientGuideFile;
import ftb.utils.mod.config.FTBUConfigGeneral;
import latmod.lib.LMFileUtils;
import latmod.lib.net.LMURLConnection;
import latmod.lib.net.RequestMethod;
import latmod.lib.net.Response;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by LatvianModder on 03.04.2016.
 */
public class GuideOnlineRepo extends GuideRepo
{
	public final String homePath;
	private final GuideInfo info;
	private Map<String, GuideMode> modes;
	private ResourceLocation icon;
	
	public GuideOnlineRepo(String id, String path) throws Exception
	{
		super(id);
		homePath = path;
		info = new GuideInfo(getFile("guide.json").asJson().getAsJsonObject());
	}
	
	@Override
	public GuideInfo getInfo()
	{ return info; }
	
	@Override
	public Map<String, GuideMode> getModes()
	{
		if(modes == null)
		{
			Map<String, GuideMode> map = new HashMap<>();
			
			for(String s : info.modes)
			{
				try
				{
					map.put(s, new GuideMode(this, s));
				}
				catch(Exception ex)
				{
					//ex.printStackTrace();
				}
			}
			
			modes = Collections.unmodifiableMap(map);
		}
		
		return modes;
	}
	
	@Override
	public Response getFile(String path) throws Exception
	{ return new LMURLConnection(RequestMethod.SIMPLE_GET, homePath + path).connect(); }
	
	@Override
	public ResourceLocation getIcon()
	{
		if(icon == null)
		{
			icon = new ResourceLocation("ftbu_guidepacks", getID());
			FTBLibClient.getDownloadImage(icon, homePath + "icon.png", new ResourceLocation("textures/misc/unknown_pack.png"), null);
		}
		
		return icon;
	}
	
	public GuideLocalRepo getLocalRepo()
	{ return ClientGuideFile.instance.localRepos.get(getID()); }
	
	@Override
	public boolean isLocal()
	{ return getLocalRepo() != null; }
	
	public boolean needsUpdate()
	{
		GuideLocalRepo r = getLocalRepo();
		return r == null || !r.getInfo().version.equals(getInfo().version);
	}
	
	public GuideLocalRepo download() throws Exception
	{
		File file = new File(FTBUConfigGeneral.guidepacksFolderFile, getID());
		if(file.exists()) LMFileUtils.delete(file);
		file.mkdirs();
		
		Set<String> filesToDownload = new HashSet<>();
		
		for(GuideMode m : getModes().values())
		{
			
		}
		
		ReadableByteChannel rbc;
		FileOutputStream fos;
		
		for(String s : filesToDownload)
		{
			System.out.println("Downloading " + s);
			
			rbc = Channels.newChannel(new URL(s).openStream());
			fos = new FileOutputStream(LMFileUtils.newFile(new File(file, s)));
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}
		
		GuideLocalRepo r = new GuideLocalRepo(file);
		ClientGuideFile.instance.localRepos.put(r.getID(), r);
		return r;
	}
}