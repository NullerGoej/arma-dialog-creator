/*
 * Copyright (c) 2016 Kayler Renslow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose and noninfringement. in no event shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or the use or other dealings in the software.
 */

package com.kaylerrenslow.armaDialogCreator.gui.fx.control.inputfield;

import com.kaylerrenslow.armaDialogCreator.control.sv.Expression;
import com.kaylerrenslow.armaDialogCreator.expression.Env;
import com.kaylerrenslow.armaDialogCreator.expression.ExpressionEvaluationException;
import com.kaylerrenslow.armaDialogCreator.expression.ExpressionInterpreter;
import com.kaylerrenslow.armaDialogCreator.main.lang.FXControlLang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 @author Kayler
 An {@link InputFieldDataChecker} implementation for expressions to be evaluated with {@link ExpressionInterpreter}
 Created on 07/15/2016. */
public class ExpressionChecker implements InputFieldDataChecker<Expression> {
	private final Env env;

	public ExpressionChecker(Env env) {
		this.env = env;
	}

	@Override
	public String validData(@NotNull String data) {
		try {
			ExpressionInterpreter.getInstance().evaluate(data, env);
			return null;
		} catch (ExpressionEvaluationException ex) {
			return (ex.getMessage() == null || ex.getMessage().length() == 0) ? FXControlLang.InputField.DataCheckers.Expression.UNKNOWN_ERROR : ex.getMessage();
		}
	}

	@Nullable
	@Override
	public Expression parse(@NotNull String data) {
		return new Expression(data, env);
	}

	@Override
	public String getTypeName() {
		return FXControlLang.InputField.DataCheckers.Expression.TYPE_NAME;
	}

	@Override
	public boolean allowEmptyData() {
		return false;
	}
	
}