package com.armadialogcreator.control.impl;

import com.armadialogcreator.control.ArmaControlSpecRequirement;
import com.armadialogcreator.core.ConfigPropertyLookup;
import com.armadialogcreator.core.ConfigPropertyLookupConstant;
import com.armadialogcreator.util.ArrayUtil;
import com.armadialogcreator.util.ReadOnlyList;
import org.jetbrains.annotations.NotNull;

/**
 @author K
 @since 5/14/19 */
public class ListScrollbarControlSpec implements ArmaControlSpecRequirement {
	public static final String CLASS_NAME = "ListScrollBar";

	public static final ListScrollbarControlSpec instance = new ListScrollbarControlSpec();

	@Override
	@NotNull
	public ReadOnlyList<ConfigPropertyLookupConstant> getRequiredProperties() {
		return new ReadOnlyList<>(
				ArrayUtil.sort(new ConfigPropertyLookup[]{
								ConfigPropertyLookup.COLOR,
								ConfigPropertyLookup.THUMB,
								ConfigPropertyLookup.ARROW_FULL,
								ConfigPropertyLookup.ARROW_EMPTY,
								ConfigPropertyLookup.BORDER
						},
						ConfigPropertyLookupConstant.PRIORITY_SORT
				)
		);
	}

}