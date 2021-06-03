package fr.max2.betterconfig.client.gui.builder;

import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;

public interface ValueUIBuilder<P>
{
	P buildBoolean(ValueSpec spec, boolean value);
	P buildNumber(ValueSpec spec, Number value);
	P buildString(ValueSpec spec, String value);
	P buildEnum(ValueSpec spec, Enum<?> value);
	P buildList(ValueSpec spec, List<?> value);
	
	P buildUnknown(ValueSpec spec, Object value);
}
