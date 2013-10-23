package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ForgotPasswordPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


/**
 * Created with IntelliJ IDEA.
 * User: shilpi
 * Date: 10/10/13
 * Time: 12:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class LanguageCheck extends TestCaseHelper {

    String langSelected;

    @BeforeMethod()
    public void setUp() throws Exception {
        super.setup();
    }

    @Test(groups = {"requisition"})
    public void changeLanguageAsEnglishOnLoginPage() throws Exception
    {
        LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginpage.setLangAsEnglish();
        verifyColorOfTextAsGray(loginpage.getEnglishColor());
        verifyPageIdentifierLabelOnLoginPage("Sign In");
    }

    @Test(groups = {"requisition"})
    public void changeLanguageAsPortuguesOnLoginPage() throws Exception
    {
        LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginpage.setLangAsPortugues();
        verifyColorOfTextAsGray(loginpage.getPortuguesColor());
        verifyPageIdentifierLabelOnLoginPage("Entrar");
    }

    @Test(groups = {"requisition"})
    public void changeLanguageAsPortuguesBeforeForgotPasswordPage() throws Exception
    {
        LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginpage.setLangAsPortugues();
        ForgotPasswordPage forgotPassword = loginpage.clickForgotPasswordLink();
        verifyColorOfTextAsGray(loginpage.getPortuguesColor());
        verifyPageIdentifierLabelOnForgotPasswordPage("Submeter");
    }

    @Test(groups = {"requisition"})
    public void changeLanguageAsEnglishBeforeForgotPasswordPage() throws Exception
    {
        LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginpage.setLangAsEnglish();
        ForgotPasswordPage forgotPassword = loginpage.clickForgotPasswordLink();
        verifyColorOfTextAsGray(loginpage.getEnglishColor());
        verifyPageIdentifierLabelOnForgotPasswordPage("Submit");
    }

    @Test(groups = {"requisition"})
    public void changeLanguageAsPortuguesOnForgotPasswordPage() throws Exception
    {
        LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
        ForgotPasswordPage forgotPassword = loginpage.clickForgotPasswordLink();
        loginpage.setLangAsPortugues();
        verifyColorOfTextAsGray(loginpage.getPortuguesColor());
        verifyPageIdentifierLabelOnForgotPasswordPage("Submeter");
    }

    private void verifyColorOfTextAsGray(String color)
    {
        assertEquals(color, "rgba(136, 135, 135, 1)");
    }

   private void verifyPageIdentifierLabelOnLoginPage(String expectedLabel) throws IOException {
       LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
        assertEquals(loginpage.getPageIdentifierOnLoginPageText(),expectedLabel);
   }

    private void verifyPageIdentifierLabelOnForgotPasswordPage(String expectedLabel) throws IOException {
        ForgotPasswordPage forgotPassword = new ForgotPasswordPage(testWebDriver);
        assertEquals(forgotPassword.getPageIdentifierOnForgotPasswordPageAttribute(),expectedLabel);
    }

    @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function")
    public void signInAsAdmin123AndEnglish(String user) throws Exception
    {
        LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginpage.setLangAsEnglish();
        verifyColorOfTextAsGray(loginpage.getEnglishColor());
        HomePage homePage= loginpage.loginAs(user, user);
        verifyColorOfTextAsGray(loginpage.getEnglishColor());
        assertTrue(homePage.getLogoutLink().isDisplayed());
    }

    @DataProvider(name = "Data-Provider-Function")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"Admin123"}
        };

    }

    @AfterMethod(groups = "requisition")
    public void tearDown() throws Exception {
        testWebDriver.sleep(500);
        if (!testWebDriver.getElementById("username").isDisplayed()) {
            HomePage homePage = new HomePage(testWebDriver);
            homePage.logout(baseUrlGlobal);
            dbWrapper.deleteData();
            dbWrapper.closeConnection();
        }
    }
}
