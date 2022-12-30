package fr.max2.betterconfig.config.impl;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class ConfigLocation
{
	public static final ConfigLocation ROOT = new ConfigLocation();

	private List<String> path;

	private ConfigLocation()
	{
		this.path = ImmutableList.of();
	}

	public ConfigLocation(ConfigLocation parent, String path)
	{
		this.path = ImmutableList.<String>builderWithExpectedSize(parent.getPath().size() + 1).addAll(parent.getPath()).add(path).build();
	}

	public List<String> getPath()
	{
		return this.path;
	}

	public String getName()
	{
		return this.path.isEmpty() ? "" : this.path.get(this.path.size() - 1);
	}
}
