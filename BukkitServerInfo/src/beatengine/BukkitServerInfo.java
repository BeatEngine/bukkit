package beatengine;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BukkitServerInfo extends JavaPlugin
{
	
	Thread restServer;
	
	boolean active;
	
	@Override
	public void onEnable(){
		//Fired when the server enables the plugin
		
		restServer = new Thread("Rest API Server"){
			public void run(){
				Socket socket = null;
				Server bksrv = getServer();
				try
				{
					socket = new Socket("localhost", 8088);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				OutputStream output = null;
				try
				{
					output = socket.getOutputStream();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				InputStream input = null;
				try
				{
					input = socket.getInputStream();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
				while (active)
				{
					
					BufferedReader buff = new BufferedReader(new InputStreamReader(input));
					while (true)
					{
						try
						{
							if (!buff.ready())
								break;
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					PrintStream ps = new PrintStream(output, true);
					JSONObject info = new JSONObject();
					
					Collection<? extends Player> onlinePlayers = bksrv.getOnlinePlayers();
					List<Player> players = new ArrayList<>();
					while (onlinePlayers.iterator().hasNext())
					{
						players.add(onlinePlayers.iterator().next());
					}
					info.append("players-online", players.size());
					JSONArray jplayers = new JSONArray();
					for (int i = 0; i < players.size(); i++)
					{
						jplayers.put(players.get(i).getDisplayName());
					}
					info.append("players", jplayers);
					ps.println(info.toString());
				}
			}
		};
		restServer.start();
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equals("serverinfo"))
		{
			sender.sendMessage("BukkitServerInfo plugin by BeatEngine is running\n Usage:\n /serverinfo ...\n");
			return true;
		}
		else
		{
			return super.onCommand(sender, command, label, args);
		}
	}
	
	@Override
	public void onDisable(){
		active = false;
		//Fired when the server stops and disables all plugins
	}


}
