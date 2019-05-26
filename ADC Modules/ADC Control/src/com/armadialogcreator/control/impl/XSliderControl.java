package com.armadialogcreator.control.impl;

import com.armadialogcreator.control.ArmaConfigClassSpec;
import com.armadialogcreator.control.ArmaControl;
import com.armadialogcreator.control.ArmaDisplay;
import com.armadialogcreator.control.ArmaResolution;
import com.armadialogcreator.core.*;
import com.armadialogcreator.expression.Env;
import com.armadialogcreator.util.ArrayUtil;
import com.armadialogcreator.util.ReadOnlyArray;
import com.armadialogcreator.util.ReadOnlyList;
import org.jetbrains.annotations.NotNull;

/**
 @author Kayler
 @since 07/08/2017 */
public class XSliderControl extends ArmaControl {
	public final static ArmaConfigClassSpec SPEC_PROVIDER = new SpecReq();

	public XSliderControl(@NotNull String name, @NotNull ArmaResolution resolution, @NotNull Env env,
						  @NotNull ArmaDisplay display) {
		super(name, ArmaControlLookup.XSlider, resolution, env, display);
		findProperty(ConfigPropertyLookup.STYLE).setValue(ControlStyle.SL_HORZ.getStyleGroup());
	}

	private static class SpecReq implements ArmaConfigClassSpec, AllowedStyleProvider {
		@NotNull
		@Override
		public ReadOnlyList<ConfigPropertyLookupConstant> getRequiredProperties() {
			return new ReadOnlyList<>(
					ArrayUtil.mergeAndSort(ConfigPropertyLookupConstant.class, defaultRequiredProperties,
							ArmaConfigClassSpec.mergeArrays(
									new ConfigPropertyLookup[]{
											ConfigPropertyLookup.COLOR,
											ConfigPropertyLookup.COLOR_ACTIVE,
											ConfigPropertyLookup.ARROW_EMPTY,
											ConfigPropertyLookup.ARROW_FULL,
											ConfigPropertyLookup.BORDER,
											ConfigPropertyLookup.THUMB,
									}
							),
							ConfigPropertyLookupConstant.PRIORITY_SORT
					)
			);
		}

		@NotNull
		@Override
		public ReadOnlyList<ConfigPropertyLookupConstant> getOptionalProperties() {
			return new ReadOnlyList<>(
					ArrayUtil.mergeAndSort(ConfigPropertyLookupConstant.class, defaultOptionalProperties,
							ArmaConfigClassSpec.mergeArrays(
									new ConfigPropertyLookup[]{
											ConfigPropertyLookup.TOOLTIP,
											ConfigPropertyLookup.TOOLTIP_COLOR_SHADE,
											ConfigPropertyLookup.TOOLTIP_COLOR_TEXT,
											ConfigPropertyLookup.TOOLTIP_COLOR_BOX,
											ConfigPropertyLookup.BLINKING_PERIOD
									},
									ConfigPropertyEventLookup.allWithControlScope(),
									ConfigPropertyEventLookup.allWithSliderScope()
							),
							ConfigPropertyLookupConstant.PRIORITY_SORT
					)
			);
		}

		@NotNull
		@Override
		public ReadOnlyArray<ControlStyle> getAllowedStyles() {
			return new ReadOnlyArray<>(new ControlStyle[]{
					ControlStyle.SL_HORZ
			});
		}
	}
}
