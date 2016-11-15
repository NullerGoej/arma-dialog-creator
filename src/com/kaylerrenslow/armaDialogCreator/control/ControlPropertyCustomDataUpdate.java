/*
 * Copyright (c) 2016 Kayler Renslow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose and noninfringement. in no event shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or the use or other dealings in the software.
 */

package com.kaylerrenslow.armaDialogCreator.control;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 11/14/2016.
 */
public class ControlPropertyCustomDataUpdate implements ControlPropertyUpdate {
	private final ControlProperty controlProperty;
	private final Object customData;
	private final boolean setTo;

	public ControlPropertyCustomDataUpdate(@NotNull ControlProperty controlProperty, @Nullable Object customData, boolean setTo) {
		this.controlProperty = controlProperty;
		this.customData = customData;
		this.setTo = setTo;
	}

	@Override
	public @NotNull ControlProperty getControlProperty() {
		return controlProperty;
	}

	@Nullable
	public Object getCustomData() {
		return customData;
	}

	/**
	 Return true if the {@link #getControlProperty()} was set to custom data via {@link ControlProperty#setCustomDataValue(Object)} or {@link ControlProperty#setHasCustomData(boolean)}, false otherwise
	 */
	public boolean isSetTo() {
		return setTo;
	}
}