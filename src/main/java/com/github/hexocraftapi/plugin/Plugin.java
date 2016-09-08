package com.github.hexocraftapi.plugin;

/*
 * Copyright 2015 hexosse
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import com.github.hexocraftapi.command.Command;
import com.github.hexocraftapi.command.CommandRegistration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
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
}
