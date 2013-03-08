package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolesPage extends Page {

  Map<String, WebElement> webElementMap = new HashMap();

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Add new')]")
  private static WebElement createNewRoleButton;

  @FindBy(how = How.ID, using = "name")
  private static WebElement roleNameField;

  @FindBy(how = How.ID, using = "description")
  private static WebElement roleDescription;

  @FindBy(how = How.ID, using = "CONFIGURE_RNR")
  private static WebElement rightConfigureTemplate;

  @FindBy(how = How.ID, using = "MANAGE_FACILITY")
  private static WebElement rightManageFacilities;

  @FindBy(how = How.ID, using = "MANAGE_ROLE")
  private static WebElement rightManageRoles;

  @FindBy(how = How.ID, using = "MANAGE_SCHEDULE")
  private static WebElement rightManageSchedules;

  @FindBy(how = How.ID, using = "UPLOADS")
  private static WebElement rightUploads;

  @FindBy(how = How.ID, using = "CREATE_REQUISITION")
  private static WebElement rightCreateRequisition;

  @FindBy(how = How.ID, using = "AUTHORIZE_REQUISITION")
  private static WebElement rightAuthorizeRequisition;

  @FindBy(how = How.ID, using = "APPROVE_REQUISITION")
  private static WebElement rightApproveRequisition;

  @FindBy(how = How.ID, using = "CONVERT_TO_ORDER")
  private static WebElement rightConvertToOrderRequisition;

  @FindBy(how = How.XPATH, using = "//input[@value='Save']")
  private static WebElement saveButton;

  @FindBy(how = How.ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv;

  @FindBy(how = How.ID, using = "saveFailMessage")
  private static WebElement saveErrorMsgDiv;

  public RolesPage(TestWebDriver driver) throws IOException {
    super(driver);

    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(createNewRoleButton);
    SeleneseTestNgHelper.assertTrue(createNewRoleButton.isDisplayed());
  }


  public void createRole(String roleName, String roleDesc, List<String> rights) {
    webElementMap.put("Configure Template", rightConfigureTemplate);
    webElementMap.put("Manage Facilities", rightManageFacilities);
    webElementMap.put("Manage Roles", rightManageRoles);
    webElementMap.put("Manage Schedules", rightManageSchedules);
    webElementMap.put("Uploads", rightUploads);
    webElementMap.put("Create Requisition", rightCreateRequisition);
    webElementMap.put("Authorize Requisition", rightAuthorizeRequisition);
    webElementMap.put("Approve Requisition", rightApproveRequisition);
    webElementMap.put("Convert To Order Requisition", rightConvertToOrderRequisition);

    testWebDriver.waitForElementToAppear(createNewRoleButton);
    createNewRoleButton.click();
    for (String right : rights) {
          testWebDriver.sleep(1000);
          webElementMap.get(right).click();
      }

      for (String right : rights) {
          if(!webElementMap.get(right).isSelected())
          testWebDriver.click(webElementMap.get(right));
      }
    roleNameField.sendKeys(roleName);
    roleDescription.sendKeys(roleDesc);
    saveButton.click();
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    SeleneseTestNgHelper.assertEquals(saveSuccessMsgDiv.getText().trim(), "'" + roleName + "' created successfully");

  }
}
