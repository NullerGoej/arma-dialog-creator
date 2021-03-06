package com.armadialogcreator.application;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 @author K
 @since 01/04/2019 */
public class ProjectDescriptor {
	private final String projectName;
	private final File projectSaveFile;
	private final Workspace workspace;

	public ProjectDescriptor(@NotNull String projectName, @NotNull File projectSaveFile, @NotNull Workspace workspace) {
		this.projectName = projectName;
		this.projectSaveFile = projectSaveFile;
		this.workspace = workspace;
	}

	@NotNull
	public Workspace getWorkspace() {
		return workspace;
	}

	@NotNull
	public File getProjectSaveFile() {
		return projectSaveFile;
	}

	@NotNull
	public String getProjectName() {
		return projectName;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ProjectDescriptor that = (ProjectDescriptor) o;

		if (!projectName.equals(that.projectName)) {
			return false;
		}
		return projectSaveFile.equals(that.projectSaveFile);
	}

}
