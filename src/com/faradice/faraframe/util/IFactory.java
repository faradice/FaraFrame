package com.faradice.faraframe.util;

/**
 * General Instance Factory
 *
 * @author ragnar.valdimarsson
 *
 * @param <T> Type of the instance to create
 */
public interface IFactory<T> {
	T create();
}
