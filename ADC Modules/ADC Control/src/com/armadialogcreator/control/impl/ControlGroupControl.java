package com.armadialogcreator.control.impl;

import com.armadialogcreator.control.ArmaControlGroup;
import com.armadialogcreator.control.ArmaResolution;
import com.armadialogcreator.core.ControlPropertyLookup;
import com.armadialogcreator.core.old.SpecificationRegistry;
import com.armadialogcreator.expression.Env;
import org.jetbrains.annotations.NotNull;

/**
 Created by Kayler on 07/04/2016.
 */
public class ControlGroupControl extends ArmaControlGroup {

	public ControlGroupControl(@NotNull String name, int idc, @NotNull ArmaResolution resolution, @NotNull Env env,
							   @NotNull SpecificationRegistry registry) {
		super(name, ArmaControlLookup.ControlsGroup, resolution, env);
		findProperty(ControlPropertyLookup.IDC).setValue(idc);
	}

}
