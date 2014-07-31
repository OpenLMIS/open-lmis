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

import java.util.List;

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

  @FindBy(how = ID, using = "duplicateFacilityMessage")
  private static WebElement duplicateFacilityMessage = null;

  @FindBy(how = ID, using = "saveErrorMsgDiv")
  private static WebElement saveErrorMsg = null;

  @FindBy(how = ID, using = "addMembers")
  private static WebElement addMembers = null;

  @FindBy(how = ID, using = "addNewRequisitionGroupHeader")
  private static WebElement addNewRequisitionGroupHeader = null;

  @FindBy(how = ID, using = "editRequisitionGroupHeader")
  private static WebElement editRequisitionGroupHeader = null;

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

  @FindBy(how = ID, using = "programsHeader")
  private static WebElement programsHeader = null;

  @FindBy(how = ID, using = "schedulesHeader")
  private static WebElement schedulesHeader = null;

  @FindBy(how = ID, using = "directDeliveryHeader")
  private static WebElement directDeliveryHeader = null;

  @FindBy(how = ID, using = "dropOffFacilityHeader")
  private static WebElement dropOffFacilityHeader = null;

  @FindBy(how = ID, using = "programScheduleAddCancel")
  private static WebElement programScheduleAddCancel = null;

  @FindBy(how = ID, using = "programScheduleAdd")
  private static WebElement programScheduleAdd = null;

  @FindBy(how = ID, using = "addNewRow")
  private static WebElement addNewProgramScheduleRow = null;

  @FindBy(how = ID, using = "programs")
  private static WebElement programsDropDown = null;

  @FindBy(how = ID, using = "newSchedule")
  private static WebElement newScheduleDropDown = null;

  @FindBy(how = ID, using = "newDirectDelivery")
  private static WebElement newDirectDelivery = null;

  @FindBy(how = ID, using = "addDropOffFacility")
  private static WebElement addDropOffFacility = null;

  @FindBy(how = ID, using = "clearNewDropOffFacility")
  private static WebElement clearNewDropOffFacility = null;

  @FindBy(how = ID, using = "viewHere")
  private static WebElement viewHereLink = null;

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

  public void enterRequisitionGroupSearchParameter(String searchParameter) {
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
    WebElement supervisoryNode = testWebDriver.getElementById("supervisoryName" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(supervisoryNode);
    return supervisoryNode.getText();
  }

  public String getFacilityCount(int rowNumber) {
    WebElement facilityCount = testWebDriver.getElementById("facilityCount" + (rowNumber - 1));
    testWebDriver.waitForElementToAppear(facilityCount);
    return facilityCount.getText();
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
    WebElement enableFlag;
    try {
      enableFlag = testWebDriver.getElementById("enabledIcon" + (rowNumber - 1));
      testWebDriver.waitForElementToAppear(enableFlag);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
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
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  public void clickCloseButton() {
    testWebDriver.waitForElementToAppear(closeButton);
    closeButton.click();
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
    } catch (NoSuchElementException e) {
      return false;
    }
    return addMembers.isDisplayed();
  }

  public String getFacilityAddedMessage() {
    testWebDriver.waitForElementToAppear(facilityAddedMessage);
    return facilityAddedMessage.getText();
  }

  public void clickSearchIcon() {
    testWebDriver.waitForElementToAppear(searchIcon);
    searchIcon.click();
  }

  public String getEditRequisitionGroupHeader() {
    testWebDriver.waitForElementToAppear(editRequisitionGroupHeader);
    return editRequisitionGroupHeader.getText();
  }

  public String getProgramsHeader() {
    testWebDriver.waitForElementToAppear(programsHeader);
    return programsHeader.getText();
  }

  public String getSchedulesHeader() {
    testWebDriver.waitForElementToAppear(schedulesHeader);
    return schedulesHeader.getText();
  }

  public String getDirectDeliveryHeader() {
    testWebDriver.waitForElementToAppear(directDeliveryHeader);
    return directDeliveryHeader.getText();
  }

  public String getDropOffFacilityHeader() {
    testWebDriver.waitForElementToAppear(dropOffFacilityHeader);
    return dropOffFacilityHeader.getText();
  }

  public void clickRemoveProgramSchedule(String programName) {
    WebElement programScheduleRemove = testWebDriver.getElementById("programScheduleRemove" + programName);
    testWebDriver.waitForElementToAppear(programScheduleRemove);
    programScheduleRemove.click();
  }

  public boolean isRemoveProgramScheduleEnabled(String programName) {
    WebElement programScheduleRemove = testWebDriver.getElementById("programScheduleRemove" + programName);
    testWebDriver.waitForElementToAppear(programScheduleRemove);
    return programScheduleRemove.isEnabled();
  }

  public void clickEditProgramSchedule(String programName) {
    WebElement programScheduleEdit = testWebDriver.getElementById("programScheduleEdit" + programName);
    testWebDriver.waitForElementToAppear(programScheduleEdit);
    programScheduleEdit.click();
  }

  public boolean isEditProgramScheduleEnabled(String programName) {
    WebElement programScheduleEdit = testWebDriver.getElementById("programScheduleEdit" + programName);
    testWebDriver.waitForElementToAppear(programScheduleEdit);
    return programScheduleEdit.isEnabled();
  }

  public void clickCancelEditProgramSchedule(String programName) {
    WebElement programScheduleEditCancel = testWebDriver.getElementById("programScheduleEditCancel" + programName);
    testWebDriver.waitForElementToAppear(programScheduleEditCancel);
    programScheduleEditCancel.click();
  }

  public void clickAddProgramSchedule() {
    testWebDriver.waitForElementToAppear(programScheduleAdd);
    programScheduleAdd.click();
  }

  public boolean isAddProgramScheduleEnabled() {
    testWebDriver.waitForElementToAppear(programScheduleAdd);
    return programScheduleAdd.isEnabled();
  }

  public void clickCancelAddProgramSchedule() {
    testWebDriver.waitForElementToAppear(programScheduleAddCancel);
    programScheduleAddCancel.click();
  }

  public void clickDoneEditProgramSchedule(String programName) {
    WebElement programScheduleEditDone = testWebDriver.getElementById("programScheduleEditDone" + programName);
    testWebDriver.waitForElementToAppear(programScheduleEditDone);
    programScheduleEditDone.click();
  }

  public void clickAddNewProgramScheduleRow() {
    testWebDriver.waitForElementToAppear(addNewProgramScheduleRow);
    addNewProgramScheduleRow.click();
  }

  public void clickProgramsScheduleAccordion() {
    testWebDriver.waitForElementToAppear(programSchedulesLabel);
    programSchedulesLabel.click();
  }

  public void selectProgram(String program) {
    testWebDriver.waitForElementToAppear(programsDropDown);
    testWebDriver.selectByVisibleText(programsDropDown, program);
  }

  public void selectNewSchedule(String schedule) {
    testWebDriver.waitForElementToAppear(newScheduleDropDown);
    testWebDriver.selectByVisibleText(newScheduleDropDown, schedule);
  }

  public String getProgram(String programName) {
    WebElement program = testWebDriver.getElementById("programName" + programName);
    testWebDriver.waitForElementToAppear(program);
    return program.getText();
  }

  public String getSchedule(String programName) {
    WebElement schedule = testWebDriver.getElementById("schedule" + programName);
    testWebDriver.waitForElementToAppear(schedule);
    return schedule.getText();
  }

  public void editSchedules(String programName, String schedule) {
    WebElement schedules = testWebDriver.getElementById("schedules" + programName);
    testWebDriver.waitForElementToAppear(schedules);
    testWebDriver.selectByVisibleText(schedules, schedule);
  }

  public boolean isDirectDeliveryIconDisplay(String programName) {
    WebElement directDeliveryIcon;
    try {
      directDeliveryIcon = testWebDriver.getElementById("directDeliverIcon" + programName);
      testWebDriver.waitForElementToAppear(directDeliveryIcon);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return directDeliveryIcon.isDisplayed();
  }

  public void editDirectDelivery(String programName) {
    WebElement directDelivery = testWebDriver.getElementById("directDelivery" + programName);
    testWebDriver.waitForElementToAppear(directDelivery);
    directDelivery.click();
  }

  public String getDropOffFacility(String programName) {
    WebElement dropOffFacility = testWebDriver.getElementById("dropOffFacility" + programName);
    testWebDriver.waitForElementToAppear(dropOffFacility);
    return dropOffFacility.getText();
  }

  public void editDropOffFacility(String programName) {
    WebElement dropOffFacility = testWebDriver.getElementById("editDropOffFacility" + programName);
    testWebDriver.waitForElementToAppear(dropOffFacility);
    dropOffFacility.click();
  }

  public void clearDropOffFacility(String programName) {
    WebElement clearDropOff = testWebDriver.getElementById("clearDropOffFacility" + programName);
    testWebDriver.waitForElementToAppear(clearDropOff);
    clearDropOff.click();
  }

  public void setNewDirectDelivery() {
    testWebDriver.waitForElementToAppear(newDirectDelivery);
    newDirectDelivery.click();
  }

  public void clickClearNewDropOffFacility() {
    testWebDriver.waitForElementToAppear(clearNewDropOffFacility);
    clearNewDropOffFacility.click();
  }

  public void clickNewDropOffFacility() {
    testWebDriver.waitForElementToAppear(addDropOffFacility);
    addDropOffFacility.click();
  }

  public List<String> getListOfPrograms() {
    testWebDriver.waitForElementToAppear(programsDropDown);
    return testWebDriver.getListOfOptions(programsDropDown);
  }

  public List<String> getListOfSchedules() {
    testWebDriver.waitForElementToAppear(newScheduleDropDown);
    return testWebDriver.getListOfOptions(newScheduleDropDown);
  }

  public String getSelectedDropOffFacility() {
    testWebDriver.waitForElementToAppear(addDropOffFacility);
    return addDropOffFacility.getText();
  }

  public void clickViewHereLink() {
    testWebDriver.waitForElementToAppear(viewHereLink);
    viewHereLink.click();
  }

  public boolean isDropOffFacilityDisplay(String programName) {
    WebElement dropOffFacility;
    try {
      dropOffFacility = testWebDriver.getElementById("dropOffFacility" + programName);
      testWebDriver.waitForElementToAppear(dropOffFacility);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return dropOffFacility.isDisplayed();
  }

  public boolean isProgramDisplayed(String programName) {
    WebElement program;
    try {
      program = testWebDriver.getElementById("programName" + programName);
      testWebDriver.waitForElementToAppear(program);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return program.isDisplayed();
  }
}