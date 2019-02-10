package com.armadialogcreator.core.old;

import com.armadialogcreator.util.UpdateGroupListener;
import org.jetbrains.annotations.NotNull;

/**
 A {@link UpdateGroupListener} useful for preventing memory leaks while wanting to listen to {@link ControlClassOld#getControlClassUpdateGroup()}

 @author Kayler
 @see ControlPropertyUpdateListener
 @since 11/20/2016 */
public abstract class ControlClassUpdateListener implements UpdateGroupListener<ControlClassUpdate> {
	private final ControlClassOld controlClass;

	public ControlClassUpdateListener(@NotNull ControlClassOld controlClass) {
		this.controlClass = controlClass;
	}

	public void unlink() {
		controlClass.getControlClassUpdateGroup().removeListener(this);
	}

	public void relink() {
		controlClass.getControlClassUpdateGroup().addListener(this);
	}
}