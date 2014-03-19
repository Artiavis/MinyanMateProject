package org.minyanmate.minyanmate;


/**
 * This interface should be implemented by activities which need to demonstrate sequences
 * hints to users on first install.
 *
 * Created by Jeff on 3/18/14.
 */
public interface Tutorial {

    static final String DASHBOARD_TUTORIAL = "dashboardTutorial";
    static final String SCHEDULE_TUTORIAL = "scheduleTutorial";
    static final String MANAGER_TUTORIAL = "managerTutorial";
    static final String SETTINGS_TUTORIAL = "settingsTutorial";


    void showTutorial();

}
