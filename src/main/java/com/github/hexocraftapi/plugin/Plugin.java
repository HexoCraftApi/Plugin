package com.github.hexocraftapi.plugin;

/*
 * Copyright 2016 hexosse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.hexocraftapi.command.Command;
import com.github.hexocraftapi.command.CommandRegistration;
import com.github.hexocraftapi.message.predifined.message.EmptyMessage;
import com.github.hexocraftapi.message.predifined.message.ErrorMessage;
import com.github.hexocraftapi.message.predifined.message.PluginMessage;
import com.github.hexocraftapi.message.predifined.message.PluginStraightMessage;
import com.github.hexocraftapi.updater.updater.Downloader;
import com.github.hexocraftapi.updater.updater.Response;
import com.github.hexocraftapi.updater.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public abstract class Plugin extends JavaPlugin
{


	public void registerEvents(Listener listener)
	{
		if(!this.isEnabled())
			throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");

		this.getServer().getPluginManager().registerEvents(listener, this);
	}

	public void registerCommands(Command command)
	{
		if(!this.isEnabled())
			throw new IllegalPluginAccessException("Plugin attempted to register " + command + " while not enabled");

		CommandRegistration.registerCommand(this, command);
	}

	public void runUpdater(final Updater updater, final CommandSender sender, int delay)
	{
		final JavaPlugin plugin = this;

		Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				try {
					Response response = updater.getResult();
					if(response == Response.SUCCESS)
					{
						EmptyMessage.toSender(sender);
						PluginStraightMessage.toSender(sender, plugin, ChatColor.AQUA);
						PluginMessage.toSender(sender, plugin, "");
						PluginMessage.toSender(sender, plugin, "New update is available: " + ChatColor.YELLOW + plugin.getDescription().getName() + " " + updater.getLatestVersion());

						if(updater.getUpdate().getChanges() != null && updater.getUpdate().getChanges().isEmpty() == false)
						{
							PluginMessage.toSender(sender, plugin, "");
							PluginMessage.toSender(sender, plugin, updater.getUpdate().getChanges(), ChatColor.GREEN);
							PluginMessage.toSender(sender, plugin, "");
						}

						Downloader downloader = new Downloader(updater.getUpdate());
						if(downloader.download())
							PluginMessage.toSender(sender, plugin, "Update downloaded into plugins\\Updater ");

						PluginMessage.toSender(sender, plugin, "");
						PluginStraightMessage.toSender(sender, plugin, ChatColor.AQUA);
						EmptyMessage.toSender(sender);
					}
					else if(response == Response.NO_UPDATE)
					{
						PluginMessage.toSender(sender, plugin, "You are running the latest version;", ChatColor.GREEN);
					}
					else if(response == Response.ERROR_TIME_OUT)
					{
						ErrorMessage.toSender(sender, "Time out will trying to find an update to " + plugin.getDescription().getName());
					}
					else
					{
						ErrorMessage.toSender(sender, "an error occured will trying to find an update to " + plugin.getDescription().getName());
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}, delay);
	}
}
