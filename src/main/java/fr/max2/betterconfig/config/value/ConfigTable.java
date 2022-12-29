package fr.max2.betterconfig.config.value;

import java.util.ArrayList;
import java.util.List;

import fr.max2.betterconfig.config.ConfigName;
import fr.max2.betterconfig.config.spec.ConfigTableSpec;
import net.minecraft.network.chat.Component;

public final class ConfigTable implements ConfigNode
{
	private final ConfigTableSpec spec;
	private final List<Entry> entryValues;

	private ConfigTable(ConfigTableSpec spec, ConfigName identifier)
	{
		this.spec = spec;
		this.entryValues = spec.entries().stream().map(entry ->
			new Entry(new TableChildInfo(identifier, entry.key()), ConfigNode.make(entry.key(), entry.node()))
		).toList();
	}

	public static ConfigTable make(ConfigName identifier, ConfigTableSpec spec)
	{
		return new ConfigTable(spec, identifier);
	}

	@Override
	public ConfigTableSpec getSpec()
	{
		return this.spec;
	}

	@Override
	public Object getValue()
	{
		return null; // TODO table getValue
	}

	public List<Entry> getEntryValues()
	{
		return this.entryValues;
	}

	@Override
	public void setAsInitialValue()
	{
		this.entryValues.forEach(entry -> entry.node().setAsInitialValue());
	}

	@Override
	public void undoChanges()
	{
		this.entryValues.forEach(entry -> entry.node().undoChanges());
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("{ ");

		boolean fist = true;
		for (var entry : this.entryValues)
		{
			if (!fist)
			{
				builder.append(", ");
			}
			fist = false;
			builder.append(entry.key().getName());
			builder.append(":");
			builder.append(entry.node().toString());
		}

		builder.append(" }");
		return builder.toString();
	}

	public static record Entry
	(
		ConfigName key,
		ConfigNode node
	)
	{ }

	private static class TableChildInfo implements ConfigName
	{
		private final ConfigName parent;
		private final ConfigName entry;

		private TableChildInfo(ConfigName parent, ConfigName entry)
		{
			this.parent = parent;
			this.entry = entry;
		}

		@Override
		public String getName()
		{
			return this.entry.getName();
		}

		@Override
		public Component getDisplayName()
		{
			return this.entry.getDisplayName();
		}

		@Override
		public List<String> getPath()
		{
			var res = new ArrayList<>(this.parent.getPath());
			res.add(this.entry.getName());
			return res;
		}

		@Override
		public String getCommentString()
		{
			return this.entry.getCommentString();
		}

		@Override
		public List<? extends Component> getDisplayComment()
		{
			return this.entry.getDisplayComment();
		}
	}
}
