package com.kaylerrenslow.armaDialogCreator.main;

import com.kaylerrenslow.armaDialogCreator.gui.fx.popup.StagePopup;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 @author Kayler
 Handles all exceptions in the program
 Created on 07/06/2016. */
public final class ExceptionHandler implements Thread.UncaughtExceptionHandler {
	private static final ExceptionHandler INSTANCE = new ExceptionHandler();

	private ExceptionHandler() {
	}

	/** Make an error window popup with the stack trace printed. Only use this for when the error is recoverable. If the error is non-recoverable, use ArmaDialogCreator.fatal() */
	public static void error(Throwable t) {
		error(Thread.currentThread(), t);
	}

	/** Make an error window popup with the stack trace printed. Only use this for when the error is recoverable. If the error is non-recoverable, use ArmaDialogCreator.fatal() */
	public static void error(Thread threadWhereErrorOccurred, Throwable t) {
		new StagePopup<>(ArmaDialogCreator.getPrimaryStage(), getExceptionTextArea(threadWhereErrorOccurred, t), "An internal error occurred.").show();
	}

	/** Makes an error window popup with the stack trace printed. This method should be used when a <b>non-recoverable</b> error occurred. After the error window is closed, the application will also close. */
	public static void fatal(Throwable t) {
		fatal(Thread.currentThread(), t);
	}

	/** Makes an error window popup with the stack trace printed. This method should be used when a <b>non-recoverable</b> error occurred. After the error window is closed, the application will also close. */
	public static void fatal(Thread threadWereErrorOccurred, Throwable t) {
		if (ArmaDialogCreator.getSaveDataManager() == null || !ArmaDialogCreator.getPrimaryStage().isShowing()) { //can be null if this method is called when ApplicationDataManager had an error before constructor finished
			JOptionPane.showMessageDialog(null, getExceptionString(t), "FATAL ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		StagePopup sp = new StagePopup<TextArea>(ArmaDialogCreator.getPrimaryStage(), getExceptionTextArea(threadWereErrorOccurred, t), "A FATAL error occurred.") {
			@Override
			protected void onCloseRequest(WindowEvent event) {
				boolean good = ArmaDialogCreator.getSaveDataManager().forceSave();
				new StagePopup<TextArea>(ArmaDialogCreator.getPrimaryStage(), new TextArea(good ? "Your entry was successfully saved regardless of the error." : "Your entry couldn't be saved."), "Save notification") {
					@Override
					protected void onCloseRequest(WindowEvent event) {
						ArmaDialogCreator.getPrimaryStage().close();
						super.onCloseRequest(event);
					}

					@Override
					public void show() {
						myStage.initModality(Modality.APPLICATION_MODAL);
						super.show();
					}
				};

			}
		};
		sp.setStageSize(300,300);
		sp.show();
	}

	@NotNull
	private static TextArea getExceptionTextArea(Thread thread, Throwable t) {
		String err = getExceptionString(t);
		TextArea ta = new TextArea();
		ta.setText("THREAD:" + thread.getName() + "\n" + err);
		return ta;
	}

	private static String getExceptionString(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return "An error occurred. Please report this message to the developer(s).\n" + sw.toString();
	}

	/** Initializes the exception handling (should be called when application is launched) */
	static void init() {
		Thread.currentThread().setUncaughtExceptionHandler(INSTANCE);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		error(t, e);
	}
}