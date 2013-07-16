package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManageRequisitionGroupsPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Henok
 * Date: 7/16/13
 * Time: 4:25 AM
 */

@TransactionConfiguration(defaultRollback = true)
@Transactional
@Listeners
public class E2EManageRequisitionGroups extends TestCaseHelper {

    @BeforeMethod(groups = {"functional2"})
    public void setup() throws Exception {
        super.setup();
    }

    @Test(enabled=true, groups = {"functional2"},dataProvider = "Data-Provider-Function-Positive")
    public void testE2EManageRequisitionGroupsAddNewReqGroup (String [] credentials ) throws IOException{
        LoginPage loginPage = new LoginPage(testWebDriver,baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0],credentials[1]);
        ManageRequisitionGroupsPage manageRequisitionGroupsPage = homePage.navigateToRequisitionGroup();
        manageRequisitionGroupsPage.EnterAndVerifyNewrequisitionGroup("Code123","Test123","1","Test Description");
    }

    @Test(enabled=true, groups = {"functional2"},dataProvider = "Data-Provider-Function-Positive")
    public void testE2EManageRequisitionGroupsAddReqGroupAndAssociateFacility (String [] credentials ) throws IOException{
        LoginPage loginPage = new LoginPage(testWebDriver,baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0],credentials[1]);
        ManageRequisitionGroupsPage manageRequisitionGroupsPage = homePage.navigateToRequisitionGroup();
        manageRequisitionGroupsPage.EnterAndVerifyNewrequisitionGroup("NT","New Test","1","Test Description");
        manageRequisitionGroupsPage.EnterAndVerifyNewAssociatedFacility("1");
    }

    @AfterMethod
    public void tearDown() throws IOException{
        HomePage homePage= new HomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);
    }

    @DataProvider(name="Data-Provider-Function-Positive")
    public Object[] parameterForTheTest(){
        return new Object[]{
                new String[]{"Admin123","Admin123"}
        };
    }



}
