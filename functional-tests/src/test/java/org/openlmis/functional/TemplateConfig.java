package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.TemplateConfigPage;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TemplateConfig extends TestCaseHelper {

    @Test
    public void testTemplateConfig() throws FileNotFoundException, IOException {
        LoginPage loginpage=new LoginPage(testWebDriver);
        loginpage.login("Admin123", "Admin123");
        TemplateConfigPage config=new TemplateConfigPage(testWebDriver);
        config.selectProgramToConfigTemplate("HIV");
        config.configureTemplate();
        loginpage.logout();
    }
}
