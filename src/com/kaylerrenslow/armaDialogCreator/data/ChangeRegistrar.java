package com.kaylerrenslow.armaDialogCreator.data;

/**
 Created by Kayler on 08/02/2016.
 */
public interface ChangeRegistrar {
	boolean undo(Change c) throws ChangeUpdateFailedException;
	boolean redo(Change c) throws ChangeUpdateFailedException;
}
