package fr.max2.betterconfig.config.value;

import java.util.ArrayList;
import java.util.List;

import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.spec.ConfigSpec;
import net.minecraft.network.chat.Component;

public final class ConfigTable implements IConfigNode
{
	private final ConfigSpec.Table spec;
	private final List<Entry> entryValues;

	private ConfigTable(ConfigSpec.Table spec, IConfigName identifier)
	{
		this.spec = spec;
		this.entryValues = spec.node().getEntrySpecs().stream().map(entry ->
		{
			return new Entry(new TableChildInfo(identifier, entry.key()), IConfigNode.make(entry.key(), entry.node()));
		}).toList();
	}

	public static ConfigTable make(IConfigName identifier, ConfigSpec.Table spec)
	{
		return new ConfigTable(spec, identifier);
	}

	@Override
	public ConfigSpec.Table getSpec()
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
		IConfigName key,
		IConfigNode node
	)
	{ }

	private static class TableChildInfo implements IConfigName
	{
		private final IConfigName parent;
		private final IConfigName entry;

		private TableChildInfo(IConfigName parent, IConfigName entry)
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
