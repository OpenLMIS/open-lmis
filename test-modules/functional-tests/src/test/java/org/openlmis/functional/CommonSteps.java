package org.openlmis.functional;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CommonSteps extends TestCaseHelper {
    @Before
    public void setUp() throws Exception {
        super.setup();

    }
    @And("^I logout$")
    public void logout() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);
    }

    @And("^I am logged in as \"([^\"]*)\"$")
    public void login(String username) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginPage.loginAs(username, "Admin123");
    }

    @Given("^I am logged in as Admin$")
    public void adminLogin() throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginPage.loginAs("Admin123", "Admin123");
    }

    @Given("^I have \"([^\"]*)\" user with \"([^\"]*)\" rights$")
    public void setupUserWithRights(String user, String rights) throws IOException, SQLException {
        String[] rightList=rights.split(",");
        List<String> rightsList = new ArrayList<String>();
        for(int i=0;i<rightList.length;i++)
            rightsList.add(rightList[i]);
        setupTestUserRoleRightsData("200", user, "openLmis", rightsList);
    }

}
