/*
 * Copyright (c) 2016 Kayler Renslow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose and noninfringement. in no event shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or the use or other dealings in the software.
 */

package com.kaylerrenslow.armaDialogCreator.control.sv;

import com.kaylerrenslow.armaDialogCreator.util.DataContext;
import com.kaylerrenslow.armaDialogCreator.util.ValueConverter;
import org.jetbrains.annotations.NotNull;

/** A generic wrapper implementation for a String. */
public final class SVString extends SerializableValue {

	public static final ValueConverter<SVString> CONVERTER = new ValueConverter<SVString>() {
		@Override
		public SVString convert(DataContext context, @NotNull String... values) throws Exception {
			return new SVString(values[0]);
		}
	};
	
	public SVString(String s) {
		super(s);
	}

	public String getString() {
		return valuesAsArray[0];
	}

	public void setString(String s) {
		this.valuesAsArray[0] = s;
	}

	@Override
	public SerializableValue deepCopy() {
		return new SVString(valuesAsArray[0]);
	}

	@Override
	public String toString() {
		return valuesAsArray[0];
	}
	
	@Override
	public boolean equals(Object o){
		if(o == this){
			return true;
		}
		if(o instanceof SVString){
			SVString other = (SVString) o;
			return this.valuesAsArray[0].equals(other.valuesAsArray[0]);
		}
		return false;
	}
}