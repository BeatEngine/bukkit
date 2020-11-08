package beatengine;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BukkitServerInfo extends JavaPlugin
{
	
	Thread restServer;
	
	boolean active = false;
	
	@Override
	public void onEnable(){
		//Fired when the server enables the plugin
		active = true;
		getServer().broadcastMessage("ServerInfo Plugin loaded!");
		
		restServer = new Thread(()->{
			ServerSocket socket = null;
			Socket sock = null;
			Server bukkit = getServer();
			try
			{
				socket = new ServerSocket(8088);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			
			
			OutputStream output = null;
			InputStream input = null;
			
			boolean active = true;
			while (active)
			{
				try
				{
					sock = socket.accept();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					continue;
				}
				
				try
				{
					output = sock.getOutputStream();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					continue;
				}
				
				try
				{
					input = sock.getInputStream();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					continue;
				}
				try
				{
					while (input.available()>0)
					{
						input.read();
					}
				}
				catch (Exception exc)
				{
					exc.printStackTrace();
					continue;
				}
				JSONObject info = new JSONObject();
				
				
				Collection<? extends Player> onlinePlayers = bukkit.getOnlinePlayers();
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
				
				String res = info.toString();
				try
				{
					for (int i = 0; i < res.length(); i++)
					{
						output.write(res.charAt(i));
					}
				}
				catch (Exception exc)
				{
					exc.printStackTrace();
					continue;
				}
				try
				{
					sock.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					continue;
				}
				
			}
		});
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
