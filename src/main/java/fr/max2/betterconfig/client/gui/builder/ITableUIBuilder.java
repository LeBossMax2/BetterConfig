package fr.max2.betterconfig.client.gui.builder;

import java.util.List;

public interface ITableUIBuilder<P>
{
	ITableUIBuilder<P> subTableBuilder(String path, String comment);
	IValueUIBuilder<P> tableEntryBuilder(String path, String comment);
	P buildTable(List<P> tableContent);
}
