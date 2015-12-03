/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;

import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static org.openqa.selenium.support.How.CLASS_NAME;
import static org.openqa.selenium.support.How.ID;

public class SupervisoryNodesPage extends FilterSearchPage {

  @FindBy(how = ID, using = "searchOptionButton")
  private static WebElement searchOptionButton = null;

  @FindBy(how = ID, using = "searchSupervisoryNode")
  private static WebElement searchSupervisoryNodeParameter = null;

  @FindBy(how = ID, using = "supervisoryNodeAddNew")
  private static WebElement supervisoryNodeAddNew = null;

  @FindBy(how = ID, using = "searchOption0")
  private static WebElement searchOption1 = null;

  @FindBy(how = ID, using = "searchOption1")
  private static WebElement searchOption2 = null;

  @FindBy(how = ID, using = "searchSupervisoryNodeLabel")
  private static WebElement searchSupervisoryNodeLabel = null;

  @FindBy(how = ID, using = "noResultMessage")
  private static WebElement noResultMessage = null;

  @FindBy(how = ID, using = "oneResultMessage")
  private static WebElement oneResultMessage = null;

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "supervisoryNodeHeader")
  private static WebElement supervisoryNodeHeader = null;

  @FindBy(how = ID, using = "codeHeader")
  private static WebElement codeHeader = null;

  @FindBy(how = ID, using = "facilityHeader")
  private static WebElement facilityHeader = null;

  @FindBy(how = ID, using = "parentHeader")
  private static WebElement parentHeader = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeSearchResultsButton = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = ID, using = "code")
  private static WebElement supervisoryNodeCode = null;

  @FindBy(how = ID, using = "name")
  private static WebElement supervisoryNodeName = null;

  @FindBy(how = ID, using = "description")
  private static WebElement supervisoryNodeDescription = null;

  @FindBy(how = ID, using = "searchParentNode")
  private static WebElement searchParentNode = null;

  @FindBy(how = CLASS_NAME, using = "search-list")
  private static WebElement search_list = null;

  @FindBy(how = ID, using = "clearSearch")
  private static WebElement clearSearch = null;

  @FindBy(how = ID, using = "associatedFacilityField")
  private static WebElement associatedFacilityMemberField = null;

  @FindBy(how = ID, using = "searchAndFilter")
  private static WebElement searchAndFilter = null;

  @FindBy(how = ID, using = "saveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsgDiv = null;

  @FindBy(how = ID, using = "searchSupervisoryNode")
  private static WebElement searchSupervisoryNode = null;

  @FindBy(how = ID, using = "cancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "successMessage")
  private static WebElement successMessage = null;

  @FindBy(how = ID, using = "viewHereLink")
  private static WebElement viewHereLink = null;

  @FindBy(how = ID, using = "editSupervisoryNodeHeader")
  private static WebElement editSupervisoryNodeHeader = null;

  @FindBy(how = ID, using = "addNewSupervisoryNodeHeader")
  private static WebElement addNewSupervisoryNodeHeader = null;

  @FindBy(how = ID, using = "nameLabel")
  private static WebElement nameLabel = null;

  @FindBy(how = ID, using = "codeLabel")
  private static WebElement codeLabel = null;

  @FindBy(how = ID, using = "descriptionLabel")
  private static WebElement descriptionLabel = null;

  @FindBy(how = ID, using = "parentNodeLabel")
  private static WebElement parentNodeLabel = null;

  @FindBy(how = ID, using = "associatedFacilityLabel")
  private static WebElement associatedFacilityLabel = null;

  public SupervisoryNodesPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public String getSearchSupervisoryNodeLabel() {
    testWebDriver.waitForElementToAppear(searchSupervisoryNodeLabel);
    return searchSupervisoryNodeLabel.getText();
  }

  public boolean isAddNewButtonDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(supervisoryNodeAddNew);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return supervisoryNodeAddNew.isDisplayed();
  }

  public void clickAddNewButton() {
    testWebDriver.waitForElementToAppear(supervisoryNodeAddNew);
    supervisoryNodeAddNew.click();
  }

  public void clickSearchOptionButton() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
  }

  public String getSelectedSearchOption() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    return searchOptionButton.getText();
  }

  public void selectSupervisoryNodeAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption1);
    searchOption1.click();
  }

  public void selectSupervisoryNodeParentAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption2);
    searchOption2.click();
  }

  public void enterSearchParameter(String searchParameter) {
    testWebDriver.waitForElementToAppear(searchSupervisoryNodeParameter);
    sendKeys(searchSupervisoryNodeParameter, searchParameter);
  }

  public boolean isNoResultMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(noResultMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return noResultMessage.isDisplayed();
  }

  public boolean isOneResultMessageDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(oneResultMessage);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return oneResultMessage.isDisplayed();
  }

  public String getNResultsMessage() {
    testWebDriver.waitForElementToAppear(nResultsMessage);
    return nResultsMessage.getText();
  }

  public void closeSearchResults() {
    testWebDriver.waitForElementToAppear(closeSearchResultsButton);
    closeSearchResultsButton.click();
  }

  public boolean isSupervisoryNodeHeaderPresent() {
    try {
      testWebDriver.waitForElementToAppear(supervisoryNodeHeader);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return supervisoryNodeHeader.isDisplayed();
  }

  public String getSupervisoryNodeHeader() {
    testWebDriver.waitForElementToAppear(supervisoryNodeHeader);
    return supervisoryNodeHeader.getText();
  }

  public String getCodeHeader() {
    testWebDriver.waitForElementToAppear(codeHeader);
    return codeHeader.getText();
  }

  public String getParentHeader() {
    testWebDriver.waitForElementToAppear(parentHeader);
    return parentHeader.getText();
  }

  public String getFacilityHeader() {
    testWebDriver.waitForElementToAppear(facilityHeader);
    return facilityHeader.getText();
  }

  public String getSupervisoryNodeName(int rowNumber) {
    WebElement name = testWebDriver.getElementById("name" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(name);
    return name.getText();
  }

  public String getSupervisoryNodeCode(int rowNumber) {
    WebElement code = testWebDriver.getElementById("code" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(code);
    return code.getText();
  }

  public String getFacility(int rowNumber) {
    WebElement facility = testWebDriver.getElementById("facility" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facility);
    return facility.getText();
  }

  public String getSearchSupervisoryNodeText() {
    testWebDriver.waitForElementToAppear(searchSupervisoryNode);
    return searchSupervisoryNode.getText();
  }

  public String getParent(int rowNumber) {
    WebElement parent = testWebDriver.getElementById("parent" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(parent);
    return parent.getText();
  }

  public void clickSearchIcon() {
    testWebDriver.waitForElementToAppear(searchIcon);
    searchIcon.click();
  }

  public boolean isSearchIconDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(searchIcon);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return searchIcon.isDisplayed();
  }

  public void selectSupervisoryNodeSearchResult(int rowNumber) {
    WebElement result = testWebDriver.getElementById("result" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(result);
    result.click();
  }

  public void clickAssociatedFacilityMemberField() {
    testWebDriver.waitForElementToAppear(associatedFacilityMemberField);
    associatedFacilityMemberField.click();
    testWebDriver.waitForElementToAppear(searchAndFilter);
  }

  public void clickOnClearSearchResultButton() {
    testWebDriver.waitForElementToAppear(clearSearch);
    clearSearch.click();
  }

  public boolean isClearSearchButtonVisible() {
    testWebDriver.waitForElementToAppear(clearSearch);
    return clearSearch.isDisplayed();
  }

  public boolean isSearchListDisplayed() {
    testWebDriver.waitForElementToAppear(search_list);
    return search_list.isDisplayed();
  }

  public String getParentNodeResult(int rowNumber) {
    WebElement result = testWebDriver.getElementById("result" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(result);
    return result.getText();
  }

  public void enterSupervisoryNodeCode(String code) {
    testWebDriver.waitForElementToAppear(supervisoryNodeCode);
    sendKeys(supervisoryNodeCode, code);
  }

  public void enterSupervisoryNodeName(String name) {
    testWebDriver.waitForElementToAppear(supervisoryNodeName);
    sendKeys(supervisoryNodeName, name);
  }

  public void enterSupervisoryNodeDescription(String description) {
    testWebDriver.waitForElementToAppear(supervisoryNodeDescription);
    sendKeys(supervisoryNodeDescription, description);
  }

  public void enterSearchParentNodeParameter(String parentCode) {
    testWebDriver.waitForElementToAppear(searchParentNode);
    sendKeys(searchParentNode, parentCode);
  }

  public String getSaveMessage() {
    testWebDriver.waitForElementToAppear(saveErrorMsgDiv);
    return saveErrorMsgDiv.getText();
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public void clickOnNode(int rowNumber) {
    WebElement name = testWebDriver.getElementById("name" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(name);
    name.click();
  }

  public String getSuccessMessage() {
    testWebDriver.waitForElementToAppear(successMessage);
    return successMessage.getText();
  }

  public void clickViewHereLink() {
    testWebDriver.waitForElementToAppear(viewHereLink);
    viewHereLink.click();
  }

  public boolean isEditPageHeaderDisplayed() {
    testWebDriver.waitForElementToAppear(editSupervisoryNodeHeader);
    return editSupervisoryNodeHeader.isDisplayed();
  }

  public String getParentOnEditPage() {
    WebElement parent = testWebDriver.getElementByXpath("//*[@id='supervisoryNodeFormGroup']/div[4]/div/div/div[1]/div[2]/span");
    testWebDriver.waitForElementToAppear(parent);
    return parent.getText();
  }

  public String getCodeLabel() {
    testWebDriver.waitForElementToAppear(codeLabel);
    return codeLabel.getText();
  }

  public String getNameLabel() {
    testWebDriver.waitForElementToAppear(nameLabel);
    return nameLabel.getText();
  }

  public String getAssociateFacilityLabel() {
    testWebDriver.waitForElementToAppear(associatedFacilityLabel);
    return associatedFacilityLabel.getText();
  }

  public String getDescriptionLabel() {
    testWebDriver.waitForElementToAppear(descriptionLabel);
    return descriptionLabel.getText();
  }

  public String getParentNodeLabel() {
    testWebDriver.waitForElementToAppear(parentNodeLabel);
    return parentNodeLabel.getText();
  }

  public String getAddSupervisoryNodeHeader() {
    testWebDriver.waitForElementToAppear(addNewSupervisoryNodeHeader);
    return addNewSupervisoryNodeHeader.getText();
  }

  public String getEditSupervisoryNodeHeader() {
    testWebDriver.waitForElementToAppear(editSupervisoryNodeHeader);
    return editSupervisoryNodeHeader.getText();
  }
}