package fyber.endermanage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraftforge.common.Configuration;

@Mod(modid = "Endermanage", name = "Endermanage", version="1.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)

public class Endermanage 
{
	
    int listmode = 0;
    ArrayList<Integer> IDlist = new ArrayList<Integer>();
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {     	
    	String liststring = "";
		
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            cfg.load();
            
            listmode = cfg.get(Configuration.CATEGORY_GENERAL, "mode", 0, "List Mode:\n 0 - All blocks disabled, list enables specific blocks for Endermen to carry\n 1 - Default blocks enabled, list enables additional blocks for Endermen to carry.\n 2 - All blocks enabled, list disables specific blocks from being carried.\n (WARNING: Could have unknown results on certain blocks, especially mod blocks!)").getInt(0);
            liststring = cfg.get(Configuration.CATEGORY_GENERAL, "list", "", "List of block IDs included/excluded for carrying, depending on mode.\nIDs are separated by comma.  Ranges can be specified with -.\ne.g. 0-255, 500, 1000-1100, 1105").getString();
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "Endermanage had a problem loading it's configuration");
        }
        finally
        {
            if (cfg.hasChanged())
                cfg.save();
        }
        
        
        if (liststring.length() > 0)
        {
	        String[] list = liststring.split(",");
	        
	        if (list != null)
	        {
		        for (int n = 0; n < list.length; n++)
		        {	        	
		        	String currentID = list[n].trim();
		        	if (currentID.contains("-"))
		        	{
		        		String[] IDrange = currentID.split("-");
		        		int rangeStart = Integer.parseInt(IDrange[0].trim());
		        		int rangeEnd = Integer.parseInt(IDrange[1].trim());
		        		
		        		for (int i = rangeStart; i <= rangeEnd; i++)
		        		{
		        			IDlist.add(i);
		        		}
		        		
		        	}
		        	else IDlist.add(Integer.parseInt(currentID));
		        }
	        }
        }
        
    }
    
	
	@EventHandler
	public void load(FMLPostInitializationEvent evt)
	{		
		int enabledIDs = 0;		
		
		for (int n = 1; n < EntityEnderman.carriableBlocks.length; n++)
		{
			if (listmode == 0) 
			{
				EntityEnderman.carriableBlocks[n] = false;
				if (IDlist.contains(n)) EntityEnderman.carriableBlocks[n] = true;
			}
			else if (listmode == 1)
			{
				if (IDlist.contains(n)) EntityEnderman.carriableBlocks[n] = true; 
			}
			else if (listmode == 2)
			{
				EntityEnderman.carriableBlocks[n] = true; 
				if (IDlist.contains(n)) EntityEnderman.carriableBlocks[n] = false;
			}
			
			if (EntityEnderman.carriableBlocks[n]) enabledIDs++;
		}
		
		Logger.getLogger("Endermanage").log(Level.INFO, "List mode " + listmode);
		Logger.getLogger("Endermanage").log(Level.INFO, enabledIDs + " block IDs enabled");
		//for (int n = 0; n < IDlist.size(); n++) System.out.println("ID: " + IDlist.get(n));			
	}
}
