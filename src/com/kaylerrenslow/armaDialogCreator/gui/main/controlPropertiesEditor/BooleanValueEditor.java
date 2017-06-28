package com.kaylerrenslow.armaDialogCreator.gui.main.controlPropertiesEditor;

import com.kaylerrenslow.armaDialogCreator.control.sv.SVBoolean;
import com.kaylerrenslow.armaDialogCreator.gui.fxcontrol.BooleanChoiceBox;
import com.kaylerrenslow.armaDialogCreator.util.ReadOnlyValueObserver;
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
	private final ValueObserver<SVBoolean> svBooleanValueObserver = new ValueObserver<>(null);

	public BooleanValueEditor() {
		choiceBox.getValueObserver().addListener(new ValueListener<Boolean>() {
			@Override
			public void valueUpdated(@NotNull ValueObserver<Boolean> observer, Boolean oldValue, Boolean newValue) {
				svBooleanValueObserver.updateValue(SVBoolean.get(newValue));
			}
		});
	}

	@Override
	public void submitCurrentData() {

	}

	@Override
	public SVBoolean getValue() {
		return choiceBox.getValue() == null ? null : SVBoolean.get(choiceBox.getValue());
	}

	@Override
	public void setValue(SVBoolean val) {
		choiceBox.setValue(val == null ? null : val.isTrue());
	}

	@Override
	public @NotNull Node getRootNode() {
		return masterPane;
	}

	@Override
	public boolean displayFullWidth() {
		return false;
	}
	
	@Override
	public void focusToEditor() {
		choiceBox.requestFocus();
	}

	@NotNull
	@Override
	public ReadOnlyValueObserver<SVBoolean> getReadOnlyObserver() {
		return svBooleanValueObserver.getReadOnlyValueObserver();
	}

}
