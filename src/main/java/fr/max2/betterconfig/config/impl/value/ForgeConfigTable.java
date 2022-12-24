package fr.max2.betterconfig.config.impl.value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableList;

import fr.max2.betterconfig.config.IConfigName;
import fr.max2.betterconfig.config.impl.spec.ForgeConfigTableSpec;
import fr.max2.betterconfig.config.spec.ConfigLocation;
import fr.max2.betterconfig.config.spec.IConfigListSpec;
import fr.max2.betterconfig.config.spec.IConfigPrimitiveSpec;
import fr.max2.betterconfig.config.spec.IConfigSpecNode;
import fr.max2.betterconfig.config.spec.IConfigTableSpec;
import fr.max2.betterconfig.config.value.IConfigNode;
import fr.max2.betterconfig.config.value.IConfigTable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ForgeConfigTable extends ForgeConfigNode<IConfigTableSpec> implements IConfigTable
{
	/** The table containing the value of each entry */
	private final UnmodifiableConfig configValues;

	private final List<IConfigTable.Entry> entryValues;
	/** The function to call when the value is changed */
	protected final Consumer<ForgeConfigProperty<?>> changeListener;

	private ForgeConfigTable(IConfigName identifier, IConfigTableSpec spec, Consumer<ForgeConfigProperty<?>> changeListener, UnmodifiableConfig configValues)
	{
		super(spec);
		this.changeListener = changeListener;
		
		this.configValues = configValues;
		ImmutableList.Builder<IConfigTable.Entry> builder = ImmutableList.builder();
		for (IConfigTableSpec.Entry entry : this.getSpec().getEntrySpecs())
		{
			builder.add(new IConfigTable.Entry(new TableChildInfo(identifier, entry.key()), childNode(entry.key(), entry.node())));
		}
		this.entryValues = builder.build();
	}
	
	public static ForgeConfigTable create(ForgeConfigSpec spec, Consumer<ForgeConfigProperty<?>> changeListener)
	{
		return new ForgeConfigTable(RootInfo.INSTANCE, new ForgeConfigTableSpec(spec, getSpecComments(spec)), changeListener, spec.getValues());
	}

	@Override
	protected UnmodifiableConfig getCurrentValue()
	{
		return this.configValues;
	}
	
	@Override
	public List<IConfigTable.Entry> getEntryValues()
	{
		return this.entryValues;
	}
	
	@Override
	public void undoChanges()
	{
		this.entryValues.forEach(entry -> entry.node().undoChanges());
	}
	
	private IConfigNode childNode(IConfigName identifier, IConfigSpecNode specNode)
	{
		var param = this.configValues.get(identifier.getName());
		
		if (specNode instanceof IConfigTableSpec tableSpec)
		{
			return new ForgeConfigTable(identifier, tableSpec, this.changeListener, (UnmodifiableConfig)param);
		}
		else if (specNode instanceof IConfigListSpec listSpec)
		{
			@SuppressWarnings("unchecked")
			var configVal = (ConfigValue<List<?>>)param;
			ForgeConfigList node = new ForgeConfigList(identifier, listSpec, configVal.get());
			ForgeConfigProperty<List<?>> property = new ForgeConfigProperty<>(configVal, this.changeListener, node::getCurrentValue);
			node.addChangeListener(property::onValueChanged);
			return node;
		}
		else if (specNode instanceof IConfigPrimitiveSpec<?> primitiveSpec)
		{
			return this.childPrimitiveNode(primitiveSpec, param);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private <T> IConfigNode childPrimitiveNode(IConfigPrimitiveSpec<T> primitiveSpec, Object param)
	{
		@SuppressWarnings("unchecked")
		ConfigValue<T> configVal = (ConfigValue<T>)param;
		ForgeConfigPrimitive<T> node = new ForgeConfigPrimitive<>(primitiveSpec, configVal.get());
		ForgeConfigProperty<T> property = new ForgeConfigProperty<>(configVal, this.changeListener, node::getValue);
		node.onChanged(newVal -> property.onValueChanged());
		return node;
	}
	
	/** Gets the comments from the spec */
	private static Function<ConfigLocation, String> getSpecComments(ForgeConfigSpec spec)
	{
		return loc -> spec.getLevelComment(loc.getPath());
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("{");
		
		List<IConfigTableSpec.Entry> specs = this.getSpec().getEntrySpecs();
		
		boolean fist = true;
		for (int i = 0; i < specs.size(); i++)
		{
			if (fist)
			{
				builder.append(", ");
				fist = false;
			}
			builder.append(specs.get(i).key().getName());
			builder.append(": ");
			builder.append(this.entryValues.get(i).toString());
		}	
		
		builder.append('}');
		return builder.toString();
	}
	
	private static enum RootInfo implements IConfigName
	{
		INSTANCE;
		
		@Override
		public String getName()
		{
			return "";
		}

		@Override
		public Component getDisplayName()
		{
			return new TextComponent("");
		}
		
		@Override
		public List<String> getPath()
		{
			return List.of();
		}

		@Override
		public String getCommentString()
		{
			return "";
		}

		@Override
		public List<? extends Component> getDisplayComment()
		{
			return List.of(new TextComponent(""));
		}
	}
	
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
