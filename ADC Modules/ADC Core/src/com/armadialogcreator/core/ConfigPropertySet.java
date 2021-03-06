package com.armadialogcreator.core;

import com.armadialogcreator.util.IteratorIterable;
import com.armadialogcreator.util.MapObserver;
import com.armadialogcreator.util.MapObserverListener;
import com.armadialogcreator.util.ReadOnlyMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 @author K
 @since 01/03/2019 */
public class ConfigPropertySet implements Iterable<Map.Entry<String, ConfigProperty>> {
	private final MapObserver<String, ConfigProperty> map = new MapObserver<>(new HashMap<>());
	private final ReadOnlyMap<String, ConfigProperty> propertiesRO = new ReadOnlyMap<>(map);
	private boolean immutable = false;

	public final void makeImmutable() {
		this.immutable = true;
	}

	public final boolean isImmutable() {
		return immutable;
	}

	public final boolean putPropertyIfAbsent(@NotNull ConfigProperty p) {
		if (immutable) {
			noMutateException();
			return false;
		}
		ConfigProperty property = map.putIfAbsent(p.getName(), p);
		return property == p;
	}

	@Nullable
	public final ConfigProperty putProperty(@NotNull ConfigProperty property) {
		if (immutable) {
			noMutateException();
			return null;
		}
		return map.put(property.getName(), property);
	}

	public final void replaceProperty(@NotNull ConfigProperty newProperty) {
		if (immutable) {
			noMutateException();
			return;
		}
		ConfigProperty old = map.replace(newProperty.getName(), newProperty);
		if (old != null) {
			old.invalidate();
		}
	}

	public final boolean removeProperty(@NotNull ConfigProperty property) {
		return removeProperty(property.getName()) != null;
	}

	@Nullable
	public final ConfigProperty removeProperty(@NotNull String name) {
		if (immutable) {
			noMutateException();
		}
		ConfigProperty removed = map.remove(name);
		if (removed != null) {
			removed.invalidate();
		}
		return removed;
	}

	public final void clearProperties() {
		if (immutable) {
			noMutateException();
		}
		for (Map.Entry<String, ConfigProperty> entry : map.entrySet()) {
			entry.getValue().invalidate();
		}
		map.clear();
	}


	/**
	 Get the {@link ConfigProperty} instance for the given property name ({@link ConfigProperty#getName()}).

	 @return the ControlProperty instance
	 @throws MissingConfigPropertyKeyException when the key couldn't be found
	 @see #findPropertyNullable(String)
	 */
	@NotNull
	public final ConfigProperty findProperty(@NotNull String name) throws MissingConfigPropertyKeyException {
		ConfigProperty c = findPropertyNullable(name);
		if (c != null) {
			return c;
		}
		throw new MissingConfigPropertyKeyException(name);
	}

	/**
	 Get the {@link ConfigProperty} instance for the given property name ({@link ConfigProperty#getName()}).

	 @return the ControlProperty instance
	 @throws MissingConfigPropertyKeyException when the key couldn't be found
	 @see #findPropertyNullable(String)
	 */
	@NotNull
	public final ConfigProperty findProperty(@NotNull ConfigPropertyKey key) throws MissingConfigPropertyKeyException {
		ConfigProperty c = findPropertyNullable(key.getPropertyName());
		if (c != null) {
			return c;
		}
		throw new MissingConfigPropertyKeyException(key.getPropertyName());
	}

	/**
	 Get the {@link ConfigProperty} instance for the given property name.

	 @return the ControlProperty instance, or null if couldn't be found
	 @see #findProperty(String)
	 */
	@Nullable
	public final ConfigProperty findPropertyNullable(@NotNull ConfigPropertyKey key) {
		return findPropertyNullable(key.getPropertyName());
	}

	/**
	 Get the {@link ConfigProperty} instance for the given property name.

	 @return the ControlProperty instance, or null if couldn't be found
	 @see #findProperty(String)
	 */
	@Nullable
	public final ConfigProperty findPropertyNullable(@NotNull String name) {
		return map.get(name);
	}


	@NotNull
	@Override
	public final Iterator<Map.Entry<String, ConfigProperty>> iterator() {
		return immutable ? propertiesRO.entrySet().iterator() : map.entrySet().iterator();
	}

	@NotNull
	public final Iterable<ConfigProperty> iterable() {
		return immutable ? new IteratorIterable<>(propertiesRO.values().iterator()) : new IteratorIterable<>(map.values().iterator());
	}

	private void noMutateException() {
		throw new IllegalStateException("can't mutate immutable set");
	}

	public void addAllProperties(@NotNull ConfigPropertySet set) {
		this.map.putAll(set.map);
	}

	public final void addPropertiesSetListener(@NotNull MapObserverListener<String, ConfigProperty> listener) {
		map.addListener(listener);
	}

	public final void removePropertiesSetListener(@NotNull MapObserverListener<String, ConfigProperty> listener) {
		map.removeListener(listener);
	}
}
