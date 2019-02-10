package com.armadialogcreator.data;

import com.armadialogcreator.application.ApplicationManager;
import com.armadialogcreator.application.Configurable;
import com.armadialogcreator.application.DataLevel;
import com.armadialogcreator.core.RequirementsConfigClass;
import com.armadialogcreator.util.ApplicationSingleton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 @author K
 @since 01/04/2019 */
@ApplicationSingleton
public class ControlClassRegistry extends ConfigClassRegistryBase<RequirementsConfigClass> {

	public static final ControlClassRegistry instance = new ControlClassRegistry(new ConfigClassConfigurableHandler<>() {
		@Override
		public void loadFromConfigurable(@NotNull Configurable config, @NotNull List<RequirementsConfigClass> classes) {

		}

		@Override
		public void exportToConfigurable(@NotNull Configurable config, @NotNull List<RequirementsConfigClass> classes, @NotNull DataLevel level) {

		}
	});

	static {
		ApplicationManager.getInstance().addStateSubscriber(instance);
	}

	protected ControlClassRegistry(@NotNull ConfigClassConfigurableHandler<RequirementsConfigClass> configurableHandler) {
		super(configurableHandler);
	}
}