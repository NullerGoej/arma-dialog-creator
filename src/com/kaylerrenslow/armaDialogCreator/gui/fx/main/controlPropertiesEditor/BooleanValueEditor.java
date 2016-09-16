/*
 * Copyright (c) 2016 Kayler Renslow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose and noninfringement. in no event shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or the use or other dealings in the software.
 */

package com.kaylerrenslow.armaDialogCreator.gui.fx.main.controlPropertiesEditor;

import com.kaylerrenslow.armaDialogCreator.control.sv.SVBoolean;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.BooleanChoiceBox;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.inputfield.InputField;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.inputfield.StringChecker;
import com.kaylerrenslow.armaDialogCreator.util.ValueListener;
import com.kaylerrenslow.armaDialogCreator.util.ValueObserver;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

/**
 Created by Kayler on 07/13/2016.
 */
public class BooleanValueEditor implements ValueEditor<SVBoolean> {
	protected final BooleanChoiceBox choiceBox = new BooleanChoiceBox();
	private final StackPane masterPane = new StackPane(choiceBox);
	private final InputField<StringChecker, String> overrideField = new InputField<>(new StringChecker());


	@Override
	public void submitCurrentData() {

	}

	@Override
	public SVBoolean getValue() {
		return choiceBox.getValue() == null ? null : SVBoolean.get(choiceBox.getValue());
	}

	@Override
	public void setValue(SVBoolean val) {
		choiceBox.setValue(val.isTrue());
	}

	@Override
	public @NotNull Node getRootNode() {
		return masterPane;
	}

	@Override
	public void setToOverride(boolean override) {
		masterPane.getChildren().clear();
		if (override) {
			masterPane.getChildren().add(overrideField);
		} else {
			masterPane.getChildren().add(choiceBox);
		}
	}

	@Override
	public InputField<StringChecker, String> getOverrideTextField() {
		return overrideField;
	}
	
	@Override
	public void focusToEditor() {
		choiceBox.requestFocus();
	}

	@Override
	public void addValueListener(@NotNull ValueListener<SVBoolean> listener) {
		choiceBox.getValueObserver().addValueListener(new WrapperBooleanValueListener(listener, choiceBox.getValueObserver()));
	}

	@Override
	public boolean removeValueListener(@NotNull ValueListener<SVBoolean> listener) {
		return choiceBox.getValueObserver().removeListener(new WrapperBooleanValueListener(listener, choiceBox.getValueObserver()));
	}

	private static class WrapperBooleanValueListener implements ValueListener<Boolean>{

		private final ValueListener<SVBoolean> l;
		private final ValueObserver<SVBoolean> observer = new ValueObserver<>(null);

		public WrapperBooleanValueListener(ValueListener<SVBoolean> l, ValueObserver<Boolean> syncObserver) {
			this.l = l;
			observer.addValueListener(new ValueListener<SVBoolean>() {
				@Override
				public void valueUpdated(@NotNull ValueObserver<SVBoolean> observer, SVBoolean oldValue, SVBoolean newValue) {
					syncObserver.updateValue(newValue == null ? null : newValue.isTrue());
				}
			});
		}

		@Override
		public void valueUpdated(@NotNull ValueObserver<Boolean> observer, Boolean oldValue, Boolean newValue) {
			observer.updateValue(newValue);
			l.valueUpdated(this.observer, SVBoolean.get(oldValue), SVBoolean.get(newValue));
		}

		public boolean equals(Object o){
			if(o == this){
				return true;
			}
			if(o instanceof WrapperBooleanValueListener){
				WrapperBooleanValueListener other = (WrapperBooleanValueListener) o;
				return other.l == this.l;
			}
			return false;
		}
	}
}
