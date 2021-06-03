package fr.max2.betterconfig.client.gui.builder;

import java.util.List;

public interface TableUIBuilder<P>
{
	TableUIBuilder<P> subTableBuilder(String path, String comment);
	ValueUIBuilder<P> tableEntryBuilder(String path, String comment);
	P buildTable(List<P> tableContent);
}
