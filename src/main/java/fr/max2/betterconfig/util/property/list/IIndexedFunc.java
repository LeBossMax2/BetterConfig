package fr.max2.betterconfig.util.property.list;


public interface IIndexedFunc<T, R>
{
	R apply(int index, T t);
}
