package com.kaylerrenslow.armaDialogCreator.gui.fx.popup;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 05/30/2016.
 */
public class StagePopupUndecorated extends StagePopup {
	private double xOffset = 0;
	private double yOffset = 0;


	/**
	 Creates a new JavaFX Stage based popup window that has not "decoration" (not minimize button, maximize button, or close button). The window is move-able by dragging the root element, however.

	 @see StagePopup
	 */
	public StagePopupUndecorated(@Nullable Stage primaryStage, Parent rootElement, String title) {
		super(primaryStage, rootElement, title);
		myStage.initStyle(StageStyle.UNDECORATED);
		rootElement.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		rootElement.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				myStage.setX(event.getScreenX() - xOffset);
				myStage.setY(event.getScreenY() - yOffset);
			}
		});

	}

}
