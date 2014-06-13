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
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.NoSuchElementException;

import static org.openqa.selenium.support.How.ID;

public class RequisitionGroupPage extends FilterSearchPage {

  @FindBy(how = ID, using = "searchOptionButton")
  private static WebElement searchOptionButton = null;

  @FindBy(how = ID, using = "searchRequisitionGroup")
  private static WebElement searchRequisitionGroupParameter = null;

  @FindBy(how = ID, using = "requisitionGroupAddNew")
  private static WebElement requisitionGroupAddNew = null;

  @FindBy(how = ID, using = "searchOption0")
  private static WebElement searchOption1 = null;

  @FindBy(how = ID, using = "searchOption1")
  private static WebElement searchOption2 = null;

  @FindBy(how = ID, using = "searchRequisitionGroupLabel")
  private static WebElement searchRequisitionGroupLabel = null;

  @FindBy(how = ID, using = "noResultMessage")
  private static WebElement noResultMessage = null;

  @FindBy(how = ID, using = "oneResultMessage")
  private static WebElement oneResultMessage = null;

  @FindBy(how = ID, using = "nResultsMessage")
  private static WebElement nResultsMessage = null;

  @FindBy(how = ID, using = "requisitionGroupHeader")
  private static WebElement requisitionGroupHeader = null;

  @FindBy(how = ID, using = "codeHeader")
  private static WebElement codeHeader = null;

  @FindBy(how = ID, using = "supervisoryNodeHeader")
  private static WebElement supervisoryNodeHeader = null;

  @FindBy(how = ID, using = "facilityCount")
  private static WebElement facilityCount = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeSearchResultsButton = null;

  @FindBy(how = ID, using = "searchIcon")
  private static WebElement searchIcon = null;

  @FindBy(how = ID, using = "requisitionGroupSearchResults")
  private static WebElement requisitionGroupSearchResult = null;

  @FindBy(how = ID, using = "requisitionGroupCode")
  private static WebElement requisitionGroupCode = null;

  @FindBy(how = ID, using = "requisitionGroupName")
  private static WebElement requisitionGroupName = null;

  @FindBy(how = ID, using = "searchSupervisoryNode")
  private static WebElement searchSupervisoryNodeField = null;

  @FindBy(how = ID, using = "searchFacility")
  private static WebElement searchFacility = null;

  @FindBy(how = ID, using = "saveButton")
  private static WebElement saveButton = null;

  @FindBy(how = ID, using = "searchFacilityList")
  private static WebElement searchFacilityList = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement saveSuccessMsgDiv = null;

  @FindBy(how = ID, using = "clearNodeSearch")
  private static WebElement clearNodeSearch = null;

  @FindBy(how = ID, using = "cancelButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "closeButton")
  private static WebElement closeButton = null;

  @FindBy(how = ID, using = "noFacilityResultMessage")
  private static WebElement noFacilityResultMessage = null;

  @FindBy(how = ID, using = "tooManyResultsMessage")
  private static WebElement tooManyResultsMessage = null;

  @FindBy(how = ID, using = "duplicateFacilityMessage")
  private static WebElement duplicateFacilityMessage = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsg = null;

  @FindBy(how = ID, using = "addMembers")
  private static WebElement addMembers = null;

  @FindBy(how = ID, using = "addNewRequisitionGroupHeader")
  private static WebElement addNewRequisitionGroupHeader = null;

  @FindBy(how = ID, using = "programSchedules")
  private static WebElement programSchedulesLabel = null;

  @FindBy(how = ID, using = "Members")
  private static WebElement membersLabel = null;

  @FindBy(how = ID, using = "codeLabel")
  private static WebElement codeLabel = null;

  @FindBy(how = ID, using = "nameLabel")
  private static WebElement nameLabel = null;

  @FindBy(how = ID, using = "supervisoryNodeLabel")
  private static WebElement supervisoryNodeLabel = null;

  @FindBy(how = ID, using = "descriptionLabel")
  private static WebElement descriptionLabel = null;

  @FindBy(how = ID, using = "expandAll")
  private static WebElement expandAll = null;

  @FindBy(how = ID, using = "collapseAll")
  private static WebElement collapseAll = null;

  @FindBy(how = ID, using = "facilityAddedMessage")
  private static WebElement facilityAddedMessage = null;

  @FindBy(how = ID, using = "facilityHeader")
  private static WebElement facilityHeader = null;

  @FindBy(how = ID, using = "geoZoneHeader")
  private static WebElement geoZoneHeader = null;

  @FindBy(how = ID, using = "facilityTypeHeader")
  private static WebElement facilityTypeHeader = null;

  @FindBy(how = ID, using = "enabledHeader")
  private static WebElement enabledHeader = null;

  public RequisitionGroupPage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public int getRequisitionGroupSearchResultsTableSize() {
    return testWebDriver.getElementsSizeByXpath("//table[@id='requisitionGroupSearchResults']/tbody/tr");
  }

  public String getSearchRequisitionGroupLabel() {
    testWebDriver.waitForElementToAppear(searchRequisitionGroupLabel);
    return searchRequisitionGroupLabel.getText();
  }

  public boolean isAddNewButtonDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(requisitionGroupAddNew);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    testWebDriver.waitForElementToAppear(requisitionGroupAddNew);
    return requisitionGroupAddNew.isDisplayed();
  }

  public void clickAddNewButton() {
    testWebDriver.waitForElementToAppear(requisitionGroupAddNew);
    requisitionGroupAddNew.click();
  }

  public void clickSearchOptionButton() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    searchOptionButton.click();
  }

  public String getSelectedSearchOption() {
    testWebDriver.waitForElementToAppear(searchOptionButton);
    return searchOptionButton.getText();
  }

  public void selectRequisitionGroupAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption1);
    searchOption1.click();
  }

  public void selectSupervisoryNodeAsSearchOption() {
    testWebDriver.waitForElementToAppear(searchOption2);
    searchOption2.click();
  }

  public void enterSearchParameter(String searchParameter) {
    testWebDriver.waitForElementToAppear(searchRequisitionGroupParameter);
    sendKeys(searchRequisitionGroupParameter, searchParameter);
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

  public boolean isResultDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(requisitionGroupSearchResult);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return requisitionGroupSearchResult.isDisplayed();
  }

  public String getNResultsMessage() {
    testWebDriver.waitForElementToAppear(nResultsMessage);
    return nResultsMessage.getText();
  }

  public String getOneResultsMessage() {
    testWebDriver.waitForElementToAppear(oneResultMessage);
    return oneResultMessage.getText();
  }

  public void closeSearchResults() {
    testWebDriver.waitForElementToAppear(closeSearchResultsButton);
    closeSearchResultsButton.click();
  }

  public boolean isRequisitionGroupHeaderDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(requisitionGroupHeader);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return requisitionGroupHeader.isDisplayed();
  }

  public String getRequisitionGroupHeader() {
    testWebDriver.waitForElementToAppear(requisitionGroupHeader);
    return requisitionGroupHeader.getText();
  }

  public String getCodeHeader() {
    testWebDriver.waitForElementToAppear(codeHeader);
    return codeHeader.getText();
  }

  public String getFacilityCountHeader() {
    testWebDriver.waitForElementToAppear(facilityCount);
    return facilityCount.getText();
  }

  public String getSupervisoryNodeHeader() {
    testWebDriver.waitForElementToAppear(supervisoryNodeHeader);
    return supervisoryNodeHeader.getText();
  }

  public String getRequisitionGroupName(int rowNumber) {
    WebElement name = testWebDriver.getElementById("name" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(name);
    return name.getText();
  }

  public String getRequisitionGroupCode(int rowNumber) {
    WebElement code = testWebDriver.getElementById("code" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(code);
    return code.getText();
  }

  public String getSupervisoryNodeName(int rowNumber) {
    WebElement facility = testWebDriver.getElementById("supervisoryName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facility);
    return facility.getText();
  }

  public String getFacilityCount(int rowNumber) {
    WebElement facilityCount = testWebDriver.getElementById("facilityCount" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facilityCount);
    return facilityCount.getText();
  }

  public void clickSearchIcon() {
    testWebDriver.waitForElementToAppear(searchIcon);
    searchIcon.click();
  }

  public void enterRequisitionGroupCode(String code) {
    testWebDriver.waitForElementToAppear(requisitionGroupCode);
    sendKeys(requisitionGroupCode, code);
  }

  public void enterRequisitionGroupName(String name) {
    testWebDriver.waitForElementToAppear(requisitionGroupName);
    sendKeys(requisitionGroupName, name);
  }

  public void enterParameterToSearchSupervisoryNode(String supervisoryNode) {
    testWebDriver.waitForElementToAppear(searchSupervisoryNodeField);
    sendKeys(searchSupervisoryNodeField, supervisoryNode);
  }

  public void clickMembersAccordionLink() {
    testWebDriver.waitForElementToAppear(membersLabel);
    membersLabel.click();
  }

  public void searchFacilityToBeAssociated(String facilityCode) {
    testWebDriver.waitForElementToAppear(searchFacility);
    sendKeys(searchFacility, facilityCode);
  }

  public boolean isSearchIconDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(searchIcon);
    } catch (TimeoutException e) {
      return false;
    }
    return searchIcon.isDisplayed();
  }

  public void clickSaveButton() {
    testWebDriver.waitForElementToAppear(saveButton);
    saveButton.click();
  }

  public void clickManageRequisitionGroupSearchResult(int rowNumber) {
    WebElement manageButton = testWebDriver.getElementById("manageButton" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(manageButton);
    manageButton.click();
  }

  public void selectSupervisoryNodeSearchResult(int rowNumber) {
    WebElement nodeResult = testWebDriver.getElementById("result" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(nodeResult);
    nodeResult.click();
  }

  public String getSupervisoryNodeSearchResult(int rowNumber) {
    WebElement nodeResult = testWebDriver.getElementById("result" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(nodeResult);
    return nodeResult.getText();
  }

  public String getSuccessMessage() {
    testWebDriver.waitForElementToAppear(saveSuccessMsgDiv);
    return saveSuccessMsgDiv.getText();
  }

  public String getFacilityHeader() {
    testWebDriver.waitForElementToAppear(facilityHeader);
    return facilityHeader.getText();
  }

  public String getGeoZoneHeader() {
    testWebDriver.waitForElementToAppear(geoZoneHeader);
    return geoZoneHeader.getText();
  }

  public String getFacilityTypeHeader() {
    testWebDriver.waitForElementToAppear(facilityTypeHeader);
    return facilityTypeHeader.getText();
  }

  public String getFacilityEnabledHeader() {
    testWebDriver.waitForElementToAppear(enabledHeader);
    return enabledHeader.getText();
  }

  public String getMemberFacilityCode(int rowNumber) {
    WebElement code = testWebDriver.getElementById("code" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(code);
    return code.getText();
  }

  public String getMemberGeoZone(int rowNumber) {
    WebElement type = testWebDriver.getElementById("geoZoneName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(type);
    return type.getText();
  }

  public String getMemberFacilityType(int rowNumber) {
    WebElement type = testWebDriver.getElementById("facilityTypeName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(type);
    return type.getText();
  }

  public boolean isMemberFacilityEnableFlagDisplayed(int rowNumber) {
    WebElement enableFlag = testWebDriver.getElementById("enabledIcon" + (rowNumber - 1));
    try {
      testWebDriver.waitForElementToAppear(enableFlag);
    } catch (TimeoutException e) {
      return false;
    }
    return enableFlag.isDisplayed();
  }

  public void removeRequisitionMember(int rowNumber) {
    WebElement removeButton = testWebDriver.getElementById("removeMemberButton" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(removeButton);
    removeButton.click();
  }

  public void clickClearNodeSearchButton() {
    testWebDriver.waitForElementToAppear(clearNodeSearch);
    clearNodeSearch.click();
  }

  public void clickCancelButton() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();
  }

  public boolean isFacilitySearchListDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(searchFacilityList);
      return searchFacilityList.isDisplayed();
    } catch (TimeoutException e) {
      return false;
    }
  }

  public void clickCloseButton() {
    testWebDriver.waitForElementToAppear(closeButton);
    closeButton.click();
  }

  public String getNoFacilitySearchResultMessage() {
    testWebDriver.waitForElementToAppear(noFacilityResultMessage);
    return noFacilityResultMessage.getText();
  }

  public String getTooManyFacilitySearchResultMessage() {
    testWebDriver.waitForElementToAppear(tooManyResultsMessage);
    return tooManyResultsMessage.getText();
  }

  public String getFacilityResult(int rowNumber) {
    WebElement facilityResult = testWebDriver.getElementById("facilityResult" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facilityResult);
    return facilityResult.getText();
  }

  public String getDuplicateFacilityMessage() {
    testWebDriver.waitForElementToAppear(duplicateFacilityMessage);
    return duplicateFacilityMessage.getText();
  }

  public String getErrorMessage() {
    testWebDriver.waitForElementToAppear(saveErrorMsg);
    return saveErrorMsg.getText();
  }

  public void clickAddMembersButton() {
    testWebDriver.waitForElementToAppear(addMembers);
    addMembers.click();
  }

  public String getAddRequisitionGroupHeader() {
    testWebDriver.waitForElementToAppear(addNewRequisitionGroupHeader);
    return addNewRequisitionGroupHeader.getText();
  }

  public String getCodeLabel() {
    testWebDriver.waitForElementToAppear(codeLabel);
    return codeLabel.getText();
  }

  public String getNameLabel() {
    testWebDriver.waitForElementToAppear(nameLabel);
    return nameLabel.getText();
  }

  public String getSupervisoryNodeLabel() {
    testWebDriver.waitForElementToAppear(supervisoryNodeLabel);
    return supervisoryNodeLabel.getText();
  }

  public String getDescriptionLabel() {
    testWebDriver.waitForElementToAppear(descriptionLabel);
    return descriptionLabel.getText();
  }

  public String getProgramsAndScheduleLabel() {
    testWebDriver.waitForElementToAppear(programSchedulesLabel);
    return programSchedulesLabel.getText();
  }

  public String getMembersLabel() {
    testWebDriver.waitForElementToAppear(membersLabel);
    return membersLabel.getText();
  }

  public void clickExpandAll() {
    testWebDriver.waitForElementToAppear(expandAll);
    expandAll.click();
  }

  public void clickCollapseAll() {
    testWebDriver.waitForElementToAppear(collapseAll);
    collapseAll.click();
  }

  public boolean isAddMembersButtonDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(addMembers);
    } catch (TimeoutException e) {
      return false;
    }
    return addMembers.isDisplayed();
  }

  public String getFacilityAddedMessage() {
    testWebDriver.waitForElementToAppear(facilityAddedMessage);
    return facilityAddedMessage.getText();
  }
}