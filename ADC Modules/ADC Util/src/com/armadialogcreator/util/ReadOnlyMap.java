package com.armadialogcreator.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 @author Kayler
 @since 07/03/2017 */
public class ReadOnlyMap<K, V> implements Map<K, V> {
	private final Map<K, V> map;

	public ReadOnlyMap(@NotNull Map<K, V> map) {
		this.map = map;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public V put(K key, V value) {
		noMutateException();
		return null;
	}

	@Override
	public V remove(Object key) {
		noMutateException();
		return null;
	}

	@Override
	public void putAll(@NotNull Map<? extends K, ? extends V> m) {
		noMutateException();
	}

	@Override
	public void clear() {
		noMutateException();
	}

	@NotNull
	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@NotNull
	@Override
	public Collection<V> values() {
		return map.values();
	}

	@NotNull
	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	private void noMutateException() {
		throw new IllegalStateException("can't mutate read only list");
	}
}
