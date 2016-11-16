/*
 * Copyright (c) 2016 Kayler Renslow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose and noninfringement. in no event shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or the use or other dealings in the software.
 */

package com.kaylerrenslow.armaDialogCreator.gui.fx.main.popup.newControl;

import com.kaylerrenslow.armaDialogCreator.control.ControlType;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.BorderedImageView;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.CBMBGroupMenu;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.CBMBMenuItem;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.ComboBoxMenuButton;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.inputfield.IdentifierChecker;
import com.kaylerrenslow.armaDialogCreator.gui.fx.control.inputfield.InputField;
import com.kaylerrenslow.armaDialogCreator.gui.fx.popup.StageDialog;
import com.kaylerrenslow.armaDialogCreator.main.ArmaDialogCreator;
import com.kaylerrenslow.armaDialogCreator.main.HelpUrls;
import com.kaylerrenslow.armaDialogCreator.main.Lang;
import com.kaylerrenslow.armaDialogCreator.util.BrowserUtil;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Popup used for prompting the user to enter a control class name, {@link ControlType}, and if the control is a background control or not.

 @author Kayler
 @since 11/15/2016 */
public class NewControlDialog extends StageDialog<GridPane> {
	private final InputField<IdentifierChecker, String> inClassName = new InputField<>(new IdentifierChecker());
	private final ComboBoxMenuButton<ControlType> menuButtonControlType;
	private final CheckBox checkBoxBackgroundControl = new CheckBox();

	/**
	 Constructs a dialog prompting the user to give control class name, {@link ControlType}, and if the control is a background control or not

	 @param fixedType if null, the user can't specify the control type. If not null, the type will be fixed and the user won't be able to edit it
	 @param fixedIsBackgroundControl if null, the user can't specify the control type. If not null, the user can specify if the control is a background one or not
	 */
	public NewControlDialog(@Nullable ControlType fixedType, @Nullable Boolean fixedIsBackgroundControl) {
		super(ArmaDialogCreator.getPrimaryStage(), new GridPane(), Lang.ApplicationBundle().getString("Popups.NewControl.popup_title"), true, true, true);

		menuButtonControlType = new ComboBoxMenuButton<>(false, "", null);

		for (ControlType.TypeGroup group : ControlType.TypeGroup.values()) {
			CBMBGroupMenu<ControlType> groupMenu = new CBMBGroupMenu<>(group.displayName);
			for (ControlType controlType : ControlType.BETA_SUPPORTED) {
				if (group != controlType.group) {
					continue;
				}
				groupMenu.getCbmbMenuItems().add(new CBMBMenuItem<>(controlType, new BorderedImageView(controlType.icon)));
			}
			if (groupMenu.getItems().size() > 0) {
				menuButtonControlType.addItem(groupMenu);
			}
		}

		myRootElement.addRow(0, new Label(Lang.ApplicationBundle().getString("Popups.NewControl.class_name")), inClassName);
		myRootElement.addRow(1, new Label(Lang.ApplicationBundle().getString("Popups.NewControl.control_type")), menuButtonControlType);
		myRootElement.addRow(2, new Label(Lang.ApplicationBundle().getString("Popups.NewControl.background_control")), checkBoxBackgroundControl);

		myRootElement.setVgap(10);
		myRootElement.setHgap(10);

		myStage.setResizable(false);

		menuButtonControlType.setMinWidth(250);
		if (fixedType == null) {
			menuButtonControlType.chooseItem(ControlType.STATIC);
		} else {
			menuButtonControlType.chooseItem(fixedType);
			menuButtonControlType.setDisable(true);
		}
		if (fixedIsBackgroundControl != null) {
			checkBoxBackgroundControl.setSelected(fixedIsBackgroundControl);
			checkBoxBackgroundControl.setDisable(true);
		}
	}

	@Override
	protected void ok() {
		if (inClassName.getValue() == null) {
			inClassName.requestFocus();
			beep();
			return;
		}
		if (menuButtonControlType.getSelectedItemObserver().getValue() == null) {
			menuButtonControlType.requestFocus();
			beep();
			return;
		}
		super.ok();
	}

	@NotNull
	public String getClassName() {
		return inClassName.getValue();
	}

	@NotNull
	public ControlType getControlType() {
		return menuButtonControlType.getSelectedItemObserver().getValue();
	}

	public boolean isBackgroundControl() {
		return checkBoxBackgroundControl.isSelected();
	}

	@Override
	protected void help() {
		BrowserUtil.browse(HelpUrls.NEW_CONTROL_POPUP);
	}


}