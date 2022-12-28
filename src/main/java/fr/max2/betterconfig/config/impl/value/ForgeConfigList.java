package fr.max2.betterconfig.config.impl.value;

import java.util.List;

import fr.max2.betterconfig.config.value.ConfigList;
import fr.max2.betterconfig.config.value.ConfigPrimitive;
import fr.max2.betterconfig.config.value.ConfigTable;
import fr.max2.betterconfig.config.value.IConfigNode;

public class ForgeConfigList
{
	public static void newForgeConfigList(ConfigList node, List<?> initialValue)
	{
		if (initialValue != null)
		{
			for (int i = 0; i < initialValue.size(); i++)
			{
				Object val = initialValue.get(i);
				var entry = node.addValue(i);
				childNode(entry.node(), val);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void childNode(IConfigNode node, Object value)
	{
		if (node instanceof ConfigTable tableNode)
		{
			throw new UnsupportedOperationException();
		}
		else if (node instanceof ConfigList listNode)
		{
			newForgeConfigList(listNode, (List<?>)value);
		}
		else if (node instanceof ConfigPrimitive<?> primitiveNode)
		{
			((ConfigPrimitive<Object>)primitiveNode).setValue(value);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
}
