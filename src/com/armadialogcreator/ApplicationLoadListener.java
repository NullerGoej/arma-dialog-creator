package com.armadialogcreator;

import com.armadialogcreator.data.olddata.ApplicationLoader;

/**
 Created by Kayler on 08/07/2016.
 */
public interface ApplicationLoadListener {
	void loaded(ApplicationLoader.ApplicationLoadConfig config);
}
