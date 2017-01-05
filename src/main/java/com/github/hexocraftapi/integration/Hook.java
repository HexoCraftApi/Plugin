package com.github.hexocraftapi.integration;

/*
 * Copyright 2017 hexosse
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

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Hook
{
	private String pluginName;
	private Hooker hooker;


	public Hook(Class<? extends Hooker> hookerClass, String pluginName, String pluginPackage)
	{
		this.pluginName = pluginName;

		try
		{
			if(packagesExists(pluginPackage))
			{
				PluginManager pm = Bukkit.getServer().getPluginManager();
				JavaPlugin pl = (JavaPlugin)pm.getPlugin(pluginName);
				if(pl != null && pm.isPluginEnabled(pl))
					this.hooker = hookerClass.newInstance().capture(pl);
			}
		}
		catch(Exception e) {}
	}

	/**
	 * @return The plugin hooker
	 */
	public Hooker get()
	{
		return this.hooker;
	}

	/**
	 * Indicate if the plugin has been found
	 *
	 * @return true or false
	 */
	public boolean enable()
	{
		return  this.hooker != null;
	}

	/**
	 * Determines if all packages in a String array are within the Classpath
	 *
	 * @param packages String Array of package names to check
	 * @return Success or Failure
	 */
	private boolean packagesExists(String... packages)
	{
		try {
			for(String pkg : packages)
				Class.forName(pkg);

			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
}
