package fr.max2.betterconfig.client.gui.builder;

import java.util.List;

/**
 * A builder for creating a user interface for a table config
 * @param <P> the type of user interface primitives
 */
public interface ITableUIBuilder<P>
{
	/**
	 * Starts the creation of a new sub-table
	 * @param path the path of the sub-table config
	 * @param comment the comment associated with the sub-table
	 * @return a builder to create the sub-table user interface
	 */
	ITableUIBuilder<P> subTableBuilder(String path, String comment);
	
	/**
	 * Starts the creation of a table entry
	 * @param path the path of the config property in the table
	 * @param comment the comment associated with the property
	 * @return a builder to create the table entry user interface
	 */
	IValueUIBuilder<P> tableEntryBuilder(String path, String comment);
	
	/**
	 * Builds the table user interface,
	 * ends the creation of the table config
	 * @param tableContent the primitives forming the table
	 * @return the primitive corresponding to the table user interface
	 */
	P buildTable(List<P> tableContent);
}
