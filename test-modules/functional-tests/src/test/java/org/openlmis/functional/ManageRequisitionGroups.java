package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class ManageRequisitionGroups extends TestCaseHelper {

  LoginPage loginPage;
  RequisitionGroupPage requisitionGroupPage;

  public static final String ADMIN = "admin";
  public static final String PASSWORD = "password";

  public Map<String, String> testData = new HashMap<String, String>() {{
    put(PASSWORD, "Admin123");
    put(ADMIN, "Admin123");
  }};

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.insertFacilities("F10", "F11");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    requisitionGroupPage = PageObjectFactory.getRequisitionGroupPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testRightsNotPresent() throws SQLException {
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToUser();

    assertFalse(homePage.isRequisitionGroupTabDisplayed());
    homePage.logout();

    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToUser();
    assertTrue(homePage.isRequisitionGroupTabDisplayed());
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    assertEquals("Search Requisition Group", requisitionGroupPage.getSearchRequisitionGroupLabel());
    assertTrue(requisitionGroupPage.isAddNewButtonDisplayed());
    assertEquals("Requisition group", requisitionGroupPage.getSelectedSearchOption());
    assertTrue(requisitionGroupPage.isSearchIconDisplayed());
  }

  @Test(groups = {"admin"})
  public void testRequisitionGroupSearch() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    assertTrue(homePage.isRequisitionGroupTabDisplayed());
    assertEquals("Search Requisition Group", requisitionGroupPage.getSearchRequisitionGroupLabel());
    assertTrue(requisitionGroupPage.isAddNewButtonDisplayed());
    assertEquals("Requisition group", requisitionGroupPage.getSelectedSearchOption());
    assertFalse(requisitionGroupPage.isResultDisplayed());

    search("re");
    assertEquals("2 matches found for 're'", requisitionGroupPage.getNResultsMessage());
    assertEquals("Requisition Group Name", requisitionGroupPage.getRequisitionGroupHeader());
    assertEquals("Code", requisitionGroupPage.getCodeHeader());
    assertEquals("Supervisory Node Name", requisitionGroupPage.getSupervisoryNodeHeader());
    assertEquals("Facilities Count", requisitionGroupPage.getFacilityCountHeader());

    assertEquals("Requisition Group 2", requisitionGroupPage.getRequisitionGroupName(1));
    assertEquals("RG2", requisitionGroupPage.getRequisitionGroupCode(1));
    assertEquals("Super1", requisitionGroupPage.getSupervisoryNodeName(1));
    assertEquals("", requisitionGroupPage.getFacilityCount(1));

    dbWrapper.updateFieldValue("requisition_groups", "name", "rg", "code", "RG2");
    requisitionGroupPage.clickSearchIcon();
    assertEquals("1 match found for 're'", requisitionGroupPage.getOneResultsMessage());
    assertEquals("Requisition Group 1", requisitionGroupPage.getRequisitionGroupName(1));
    assertEquals("RG1", requisitionGroupPage.getRequisitionGroupCode(1));
  }

  @Test(groups = {"admin"})
  public void testRequisitionGroupSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadRequisitionGroup("QA_RequisitionGroups21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    dbWrapper.insertRequisitionGroupMembersTestData();

    homePage.navigateToRequisitionGroupPage();
    assertEquals("Search Requisition Group", requisitionGroupPage.getSearchRequisitionGroupLabel());
    search("Requisition Group 1");
    assertEquals("11 matches found for 'Requisition Group 1'", requisitionGroupPage.getNResultsMessage());
    search("Requisition Group ");
    assertEquals("22 matches found for 'Requisition Group'", requisitionGroupPage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(22, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1",
      "Super1", "Super1"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 11", "Requisition Group 12", "Requisition Group 13",
      "Requisition Group 15", "Requisition Group 16", "Requisition Group 18", "Requisition Group 20", "Requisition Group 4",
      "Requisition Group 5", "Requisition Group 7"});
    verifyFacilityCountOnPage(new String[]{"2", "2", "1", "1", "1", "2", "", "1", "1", "2"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1", "Super2", "Super2", "Super2", "Super2", "Super2", "Super2", "Super2",
      "Super2", "Super2"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 9", "Requisition Group 1", "Requisition Group 10",
      "Requisition Group 14", "Requisition Group 17", "Requisition Group 19", "Requisition Group 2", "Requisition Group 20",
      "Requisition Group 20", "Requisition Group 3"});

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(2);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super2", "Super2"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 6", "Requisition Group 8"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1",
      "Super1", "Super1"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 11", "Requisition Group 12", "Requisition Group 13",
      "Requisition Group 15", "Requisition Group 16", "Requisition Group 18", "Requisition Group 20", "Requisition Group 4",
      "Requisition Group 5", "Requisition Group 7"});

    navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(2);

    navigateToPreviousPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);

    requisitionGroupPage.closeSearchResults();
    assertFalse(requisitionGroupPage.isRequisitionGroupHeaderDisplayed());
  }

  @Test(groups = {"admin"})
  public void testRequisitionGroupSupervisoryNodeSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadRequisitionGroup("QA_RequisitionGroups21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    dbWrapper.insertRequisitionGroupMembersTestData();

    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    assertEquals("Search Requisition Group", requisitionGroupPage.getSearchRequisitionGroupLabel());
    assertEquals("Requisition group", requisitionGroupPage.getSelectedSearchOption());
    requisitionGroupPage.clickSearchOptionButton();
    requisitionGroupPage.selectSupervisoryNodeAsSearchOption();
    search("Super2");
    assertEquals("11 matches found for 'Super2'", requisitionGroupPage.getNResultsMessage());
    search("Super1");
    assertEquals("11 matches found for 'Super1'", requisitionGroupPage.getNResultsMessage());
    assertEquals("Supervisory node", requisitionGroupPage.getSelectedSearchOption());

    verifyNumberOFPageLinksDisplayed(11, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1",
      "Super1", "Super1"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 11", "Requisition Group 12", "Requisition Group 13",
      "Requisition Group 15", "Requisition Group 16", "Requisition Group 18", "Requisition Group 20", "Requisition Group 4",
      "Requisition Group 5", "Requisition Group 7"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 9"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 11", "Requisition Group 12", "Requisition Group 13",
      "Requisition Group 15", "Requisition Group 16", "Requisition Group 18", "Requisition Group 20", "Requisition Group 4",
      "Requisition Group 5", "Requisition Group 7"});

    navigateToLastPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);

    navigateToPreviousPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
  }

  @Test(groups = {"admin"})
  public void testRequisitionGroupSearchWhenNoResults() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    assertEquals("Requisition group", requisitionGroupPage.getSelectedSearchOption());
    requisitionGroupPage.clickSearchOptionButton();
    requisitionGroupPage.selectSupervisoryNodeAsSearchOption();
    assertEquals("Supervisory node", requisitionGroupPage.getSelectedSearchOption());
    search("RE");
    assertTrue(requisitionGroupPage.isNoResultMessageDisplayed());

    requisitionGroupPage.clickSearchOptionButton();
    requisitionGroupPage.selectRequisitionGroupAsSearchOption();
    assertTrue(requisitionGroupPage.isNoResultMessageDisplayed());

    dbWrapper.insertRequisitionGroup("RG1", "Req Group", "N1");
    testWebDriver.refresh();
    search("RE");
    assertTrue(requisitionGroupPage.isOneResultMessageDisplayed());
    assertEquals("Req Group", requisitionGroupPage.getRequisitionGroupName(1));
    assertEquals("RG1", requisitionGroupPage.getRequisitionGroupCode(1));
    assertEquals("Super1", requisitionGroupPage.getSupervisoryNodeName(1));
    assertEquals("", requisitionGroupPage.getFacilityCount(1));

    requisitionGroupPage.clickSearchOptionButton();
    requisitionGroupPage.selectSupervisoryNodeAsSearchOption();
    requisitionGroupPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertTrue(requisitionGroupPage.isNoResultMessageDisplayed());
  }

  public void search(String searchParameter) {
    requisitionGroupPage.enterSearchParameter(searchParameter);
    requisitionGroupPage.clickSearchIcon();
    testWebDriver.waitForAjax();
  }

  private void verifyRequisitionGroupNameOrderOnPage(String[] requisitionGroupNames) {
    for (int i = 1; i < requisitionGroupNames.length; i++) {
      assertEquals(requisitionGroupNames[i - 1], requisitionGroupPage.getRequisitionGroupName(i));
    }
  }

  private void verifyFacilityCountOnPage(String[] counts) {
    for (int i = 1; i < counts.length; i++) {
      assertEquals(counts[i - 1], requisitionGroupPage.getFacilityCount(i));
    }
  }

  private void verifySupervisoryNodeNameOrderOnPage(String[] supervisoryNodeNames) {
    for (int i = 1; i < supervisoryNodeNames.length; i++) {
      assertEquals(supervisoryNodeNames[i - 1], requisitionGroupPage.getSupervisoryNodeName(i));
    }
  }

  private void verifyNumberOfLineItemsVisibleOnPage(int numberOfLineItems) {
    assertEquals(numberOfLineItems, testWebDriver.getElementsSizeByXpath("//table[@id='requisitionGroupSearchResults']/tbody/tr"));
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }
}
