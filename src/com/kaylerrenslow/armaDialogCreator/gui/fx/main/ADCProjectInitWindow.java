package com.kaylerrenslow.armaDialogCreator.gui.fx.main;

import com.kaylerrenslow.armaDialogCreator.data.Project;
import com.kaylerrenslow.armaDialogCreator.data.io.xml.ProjectXmlLoader;
import com.kaylerrenslow.armaDialogCreator.data.io.xml.XmlParseException;
import com.kaylerrenslow.armaDialogCreator.gui.fx.popup.StagePopup;
import com.kaylerrenslow.armaDialogCreator.main.ArmaDialogCreator;
import com.kaylerrenslow.armaDialogCreator.main.Lang;
import com.kaylerrenslow.armaDialogCreator.util.ValueListener;
import com.kaylerrenslow.armaDialogCreator.util.ValueObserver;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

/**
 @author Kayler
 Used for setting up the initial {@link Project}. This window is shown before {@link ADCWindow}.
 Created on 07/19/2016. */
public class ADCProjectInitWindow extends StagePopup<VBox> {
	private final LinkedList<ProjectInitTab> initTabs = new LinkedList<>();
	private final TabPane tabPane = new TabPane();
	
	public ADCProjectInitWindow() {
		super(ArmaDialogCreator.getPrimaryStage(), new VBox(5), Lang.ProjectInitWindow.WINDOW_TITLE);
		myRootElement.setPadding(new Insets(10));
		
		//header
		final Label lblProjectSetup = new Label(Lang.ProjectInitWindow.PROJECT_SETUP);
		lblProjectSetup.setFont(Font.font(18d));
		
		initTabPane();
		
		myRootElement.getChildren().addAll(lblProjectSetup, new Separator(Orientation.HORIZONTAL), tabPane);
		myRootElement.getChildren().addAll(new Separator(Orientation.HORIZONTAL), getResponseFooter(false, true, false));
		
		myStage.initModality(Modality.APPLICATION_MODAL);
		myStage.setWidth(720d);
		myStage.setHeight(360d);
		myStage.setResizable(false);
		
		this.btnOk.setPrefWidth(130d);
	}
	
	private void initTabPane() {
		initTabs.add(new NewProjectTab());
		initTabs.add(new TabOpen());
		//		initTabs.add(new ImportTab());
		
		final ValueListener<Boolean> enabledListener = new ValueListener<Boolean>() {
			@Override
			public void valueUpdated(@NotNull ValueObserver<Boolean> observer, Boolean oldValue, Boolean enabled) {
				getOkButton().setDisable(!enabled);
			}
		};
		for (ProjectInitTab initTab : initTabs) {
			tabPane.getTabs().add(initTab.getTab());
			initTab.getTab().setClosable(false);
			initTab.btnOkEnabledObserver.addValueListener(enabledListener);
			
			tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
				@Override
				public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab selectedTab) {
					for (ProjectInitTab initTab : initTabs) {
						if (initTab.getTab() == selectedTab) {
							getOkButton().setText(initTab.getOkBtnLabel());
							getOkButton().setDisable(!initTab.btnOkEnabledObserver.getValue());
							return;
						}
					}
					throw new IllegalStateException("tab should have been matched");
				}
			});
			
		}
	}
	
	private Button getOkButton() {
		return this.btnOk;
	}
	
	private static VBox getTabVbox(double spacing) {
		VBox vBox = new VBox(spacing);
		vBox.setPadding(new Insets(10));
		vBox.setMinHeight(200d);
		return vBox;
	}
	
	@Override
	protected void onCloseRequest(WindowEvent event) {
		System.exit(0);
	}
	
	public ProjectInit getProjectInit() {
		Tab selected = tabPane.getSelectionModel().getSelectedItem();
		for (ProjectInitTab initTab : initTabs) {
			if (initTab.getTab() == selected) {
				return initTab.getResult();
			}
		}
		throw new IllegalStateException("Should provide implementation for selected tab");
	}
	
	private abstract class ProjectInitTab {
		protected final ValueObserver<Boolean> btnOkEnabledObserver = new ValueObserver<>(true);
		
		abstract ProjectInit getResult();
		
		abstract String getOkBtnLabel();
		
		abstract Tab getTab();
	}
	
	public class NewProjectTab extends ProjectInitTab {
		
		private final Tab tabNew = new Tab(Lang.ProjectInitWindow.TAB_NEW);
		
		/** TextField used for getting project name in new tab */
		private final TextField tfProjectName;
		private final TextArea taProjectDescription = new TextArea();
		
		public NewProjectTab() {
			final VBox root = getTabVbox(10);
			final Label lblCreateNewProject = new Label(Lang.ProjectInitWindow.NEW_PROJECT_TITLE);
			VBox.setMargin(lblCreateNewProject, new Insets(0, 0, 10, 0));
			tfProjectName = new TextField();
			tfProjectName.setPrefWidth(200d);
			final Label lblProjectName = new Label(Lang.ProjectInitWindow.PROJECT_NAME, tfProjectName);
			lblProjectName.setContentDisplay(ContentDisplay.RIGHT);
			final Label lblProjectDescription = new Label(Lang.ProjectInitWindow.NEW_PROJECT_DESCRIPTION, taProjectDescription);
			lblProjectDescription.setContentDisplay(ContentDisplay.RIGHT);
			taProjectDescription.setPrefRowCount(2);
			
			root.getChildren().addAll(lblCreateNewProject, lblProjectName, lblProjectDescription);
			
			tabNew.setContent(root);
		}
		
		@Override
		public ProjectInit getResult() {
			return new ProjectInit.NewProject(tfProjectName.getText(), taProjectDescription.getText());
		}
		
		@Override
		public String getOkBtnLabel() {
			return Lang.ProjectInitWindow.NEW_PROJECT_OK;
		}
		
		@Override
		public Tab getTab() {
			return tabNew;
		}
	}
	
	public class TabOpen extends ProjectInitTab {
		
		private final Tab tabOpen = new Tab(Lang.ProjectInitWindow.TAB_OPEN);
		private final ListView<Project> lvKnownProjects = new ListView<>();
		private Project selectedProject;
		
		public TabOpen() {
			btnOkEnabledObserver.updateValue(false);
			final VBox root = getTabVbox(10d);
			tabOpen.setContent(root);
			initKnownProjects();
			final Label lblOpenProject = new Label(Lang.ProjectInitWindow.OPEN_PROJECT_TITLE);
			VBox.setMargin(lblOpenProject, new Insets(0d, 0d, 10d, 0d));
			
			
			root.getChildren().addAll(lblOpenProject);
			
			lvKnownProjects.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Project>() {
				@Override
				public void changed(ObservableValue<? extends Project> observable, Project oldValue, Project selected) {
					selectedProject = selected;
					btnOkEnabledObserver.updateValue(selected != null);
				}
			});
		}
		
		private void initKnownProjects() {
			LinkedList<ProjectXmlLoader.ProjectParseResult> knownProjects = new LinkedList<>();
			fetchProjects(knownProjects);
			if (knownProjects.size() == 0) {
				
			} else {
				for (ProjectXmlLoader.ProjectParseResult result : knownProjects) {
					
				}
			}
		}
		
		private void fetchProjects(LinkedList<ProjectXmlLoader.ProjectParseResult> knownProjects) {
			File[] files = ArmaDialogCreator.getApplicationDataManager().getAppSaveDataDirectory().listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						File[] projectFiles = f.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return dir.getName().equals("project.xml");
							}
						});
						if (projectFiles == null) {
							continue;
						}
						for (File projectFile : projectFiles) {
							try {
								ProjectXmlLoader.ProjectParseResult result = ProjectXmlLoader.parse(ArmaDialogCreator.getApplicationData(), projectFile);
								knownProjects.add(result);
							} catch (XmlParseException e) {
								continue;
							}
						}
					}
				}
			}
		}
		
		@Override
		public ProjectInit getResult() {
			return null;
		}
		
		@Override
		public String getOkBtnLabel() {
			return Lang.ProjectInitWindow.OPEN_PROJECT_OK;
		}
		
		@Override
		public Tab getTab() {
			return tabOpen;
		}
		
	}
	
	public class ImportTab extends ProjectInitTab {
		
		private final Tab tabImport = new Tab(Lang.ProjectInitWindow.TAB_IMPORT);
		
		public ImportTab() {
			tabImport.setUserData(Lang.ProjectInitWindow.IMPORT_PROJECT_OK);
			final VBox root = getTabVbox(20);
			final Label lblOpenProject = new Label(Lang.ProjectInitWindow.IMPORT_PROJECT_TITLE);
			
			root.getChildren().addAll(lblOpenProject);
			
			tabImport.setContent(root);
		}
		
		@Override
		public ProjectInit getResult() {
			return null;
		}
		
		@Override
		public String getOkBtnLabel() {
			return Lang.ProjectInitWindow.IMPORT_PROJECT_OK;
		}
		
		@Override
		public Tab getTab() {
			return tabImport;
		}
	}
	
	public interface ProjectInit {
		
		class NewProject implements ProjectInit {
			private final String projectName;
			private final String projectDescription;
			
			public NewProject(String projectName, String projectDescription) {
				this.projectName = projectName;
				this.projectDescription = projectDescription;
			}
			
			public String getProjectDescription() {
				return projectDescription;
			}
			
			public String getProjectName() {
				return projectName;
			}
		}
		
		class OpenProject implements ProjectInit {
			private final Project project;
			
			public OpenProject(Project project) {
				this.project = project;
			}
			
			public Project getProject() {
				return project;
			}
		}
		
		class ImportProject implements ProjectInit {
			private final File descriptionExt;
			
			public ImportProject(File descriptionExt) {
				this.descriptionExt = descriptionExt;
			}
			
			public File getDescriptionExt() {
				return descriptionExt;
			}
		}
	}
}
