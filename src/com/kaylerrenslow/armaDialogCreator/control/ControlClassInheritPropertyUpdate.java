package com.kaylerrenslow.armaDialogCreator.control;

import org.jetbrains.annotations.NotNull;

/**
 Update triggered when a property was inherited or overridden in a {@link ControlClass}

 @author Kayler
 @since 11/16/2016 */
public class ControlClassInheritPropertyUpdate implements ControlClassUpdate {
	private final ControlClass controlClass;
	private final ControlProperty overidden;
	private final boolean inherited;
	private final boolean updatingExtendClass;

	/**
	 @param controlClass {@link ControlClass} that was updated
	 @param overridden the {@link ControlProperty} that was overridden
	 @param inherited true if it was inherited, false if was overridden
	 @param updatingExtendClass true if the inherit update was caused by
	 {@link ControlClass#extendControlClass(ControlClass)}, false otherwise
	 */
	public ControlClassInheritPropertyUpdate(@NotNull ControlClass controlClass, @NotNull ControlProperty overridden,
											 boolean inherited, boolean updatingExtendClass) {
		this.controlClass = controlClass;
		this.overidden = overridden;
		this.inherited = inherited;
		this.updatingExtendClass = updatingExtendClass;
	}

	/** @return the {@link ControlProperty} affected with this update */
	@NotNull
	public ControlProperty getControlProperty() {
		return overidden;
	}

	/**
	 @return true if {@link #getControlProperty()} was overridden, false if it was inherited
	 */
	public boolean wasOverridden() {
		return !inherited;
	}

	/**
	 @return true if {@link #getControlProperty()} was inherited, false if it was overridden
	 */
	public boolean wasInherited() {
		return inherited;
	}

	@Override
	public @NotNull ControlClass getOwnerControlClass() {
		return controlClass;
	}

	/**
	 @return true if the inherit update was caused by {@link ControlClass#extendControlClass(ControlClass)},
	 false otherwise
	 */
	public boolean isUpdatingExtendClass() {
		return updatingExtendClass;
	}
}
