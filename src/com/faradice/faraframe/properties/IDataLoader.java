package com.faradice.faraframe.properties;

public interface IDataLoader<T extends IPropertyItem>  {
	BasicPropertyModel<T> load() throws Exception;
}
