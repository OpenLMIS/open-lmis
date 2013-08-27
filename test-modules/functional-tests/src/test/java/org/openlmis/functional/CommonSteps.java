package org.openlmis.functional;

import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    @When("^I have \"([^\"]*)\" role having \"([^\"]*)\" based \"([^\"]*)\" rights$")
    public void createRoleWithRights(String roleName, String roleType, String right) throws Exception {
        setupTestRoleRightsData(roleName,roleType,right);
    }

    @And("^I have users:$")
    public void createUser(DataTable userTable) throws Exception {
        String password="TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
        List<Map<String, String>> data = userTable.asMaps();
        for (Map map : data){
            dbWrapper.insertUser(map.get("UserId").toString(), map.get("UserName").toString(),password, map.get("FacilityCode").toString(), map.get("Email").toString(),"openLmis");
            dbWrapper.insertRoleAssignment(map.get("UserId").toString(),map.get("Role").toString());
        }
    }
    @And("^I have approved quantity \"([^\"]*)\"$")
    public void insertApprovedQuantity(int approvedQuantity) throws Exception {
        dbWrapper.insertApprovedQuantity(approvedQuantity);
    }
}
