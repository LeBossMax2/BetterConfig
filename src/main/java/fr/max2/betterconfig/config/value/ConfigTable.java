package fr.max2.betterconfig.config.value;

import java.util.ArrayList;
import java.util.List;

import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import net.minecraft.network.chat.Component;

public final class ConfigTable implements IConfigNode
{
	private final IConfigTableSpec spec;
	private final List<Entry> entryValues;
	
	public ConfigTable(IConfigTableSpec spec, IConfigName identifier)
	{
		this.spec = spec;
		this.entryValues = spec.getEntrySpecs().stream().map(entry ->
		{
			return new Entry(new TableChildInfo(identifier, entry.key()), childNode(entry.key(), entry.node()));
		}).toList();
	}
	
	@Override
	public IConfigTableSpec getSpec()
	{
		return spec;
	}
	
	@Override
	public Object getValue()
	{
		return null; // TODO table getValue
	}

	public List<Entry> getEntryValues()
	{
		return entryValues;
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
	
	private IConfigNode childNode(IConfigName identifier, IConfigSpecNode specNode)
	{
		if (specNode instanceof IConfigTableSpec tableSpec)
		{
			return new ConfigTable(tableSpec, identifier);
		}
		else if (specNode instanceof IConfigListSpec listSpec)
		{
			ConfigList node = new ConfigList(listSpec, identifier);
			return node;
		}
		else if (specNode instanceof IConfigPrimitiveSpec<?> primitiveSpec)
		{
			return new ConfigPrimitive<>(primitiveSpec);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
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
