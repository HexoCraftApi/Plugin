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
import com.github.hexocraftapi.message.predifined.message.ErrorPluginMessage;
import com.github.hexocraftapi.message.predifined.message.PluginMessage;
import com.github.hexocraftapi.message.predifined.message.PluginStraightMessage;
import com.github.hexocraftapi.metrics.Metrics;
import com.github.hexocraftapi.updater.updater.Downloader;
import com.github.hexocraftapi.updater.updater.Response;
import com.github.hexocraftapi.updater.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public abstract class Plugin extends JavaPlugin
{

	/**
	 * Use registerEvents to register any class implementing Listener
	 *
	 * @param listener The Listener to register
	 */
	public void registerEvents(Listener listener)
	{
		if(!this.isEnabled())
			throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");

		this.getServer().getPluginManager().registerEvents(listener, this);
	}

	/**
	 * Use registerCommands to register any class implementing Command
	 *
	 * With this method, no need to declare your command in plugin.yml
	 *
	 * @param command The Command to register
	 */
	public void registerCommands(Command command)
	{
		if(!this.isEnabled())
			throw new IllegalPluginAccessException("Plugin attempted to register " + command + " while not enabled");

		CommandRegistration.registerCommand(this, command);
	}

	/**
	 * With this method, you can automatically run a GitHubUpdater, BukkitUpdater or SpigotUpdater
	 *
	 * @param updater An updater. Can be GitHubUpdater, BukkitUpdater or SpigotUpdater.
	 * @param sender The sender asking to run the updater.
	 * @param delay The delay before launching the updater.
	 */
	public void runUpdater(final Updater updater, final CommandSender sender, int delay)
	{
		final JavaPlugin plugin = this;

		Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Response response = updater.getResult();
					if(response == Response.SUCCESS)
					{
						boolean downloaded = false;
						Downloader downloader = new Downloader(updater.getUpdate());
						if(downloader.download()) downloaded = true;

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

						if(downloaded)
							PluginMessage.toSender(sender, plugin, "Update downloaded into plugins\\Updater ");

						PluginMessage.toSender(sender, plugin, "");
						PluginStraightMessage.toSender(sender, plugin, ChatColor.AQUA);
						EmptyMessage.toSender(sender);
					}
					else if(!(sender instanceof Player))
					{
						if(response == Response.NO_UPDATE)
						{
							PluginMessage.toSender(sender, plugin, "You are running the latest version.", ChatColor.GREEN);
						}
						else if(response == Response.ERROR_TIME_OUT)
						{
							ErrorPluginMessage.toSender(sender, plugin, "Time out will trying to find an update to " + plugin.getDescription().getName());
						}
						else
						{
							ErrorPluginMessage.toSender(sender, plugin, "An error occurred will trying to find an update to " + plugin.getDescription().getName());
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}, delay);
	}

	/**
	 * This method automatically start Metrics after a delay.
	 *
	 * @param delay The delay before launching the updater.
	 */
	public void RunMetrics(int delay)
	{
		final JavaPlugin plugin = this;

		Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable()
		{
			@Override
			public void run()
			{
				CommandSender sender = Bukkit.getServer().getConsoleSender();
				try
				{
					Metrics metrics = new Metrics(plugin);
					if(metrics.start())
					{
						PluginMessage.toSender(sender, plugin, "Successfully started Metrics", ChatColor.GREEN);
						PluginMessage.toSender(sender, plugin, "See http://mcstats.org/" + URLEncoder.encode(plugin.getDescription().getName(), "UTF-8"), ChatColor.GREEN);
					}
					else
						ErrorPluginMessage.toSender(sender, plugin, "An error occurred will trying to start Metrics.");
				}
				catch(IOException e)
				{
					ErrorPluginMessage.toSender(sender, plugin, "An error occurred will trying to submit stats to Metrics.");
				}
			}
		}, delay);
	}
}
