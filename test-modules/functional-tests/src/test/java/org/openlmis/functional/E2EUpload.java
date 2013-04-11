/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EUpload extends TestCaseHelper {

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadCSVFiles(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add("Create Requisition");

    rolesPage.createRole("User", "User", userRoleList, true);

    UploadPage uploadPage = homePage.navigateUploads();
    verifyInValidUserUpload(uploadPage);
    verifyValidUserUpload(uploadPage);

    String userName = "User123";
    String userId = "200";
    dbWrapper.alterUserID(userName, userId);
    dbWrapper.insertRoleAssignment(userId, "User");

//    verifyInvalidProductCategoryUpload(uploadPage);
    verifyValidProductCategoryUpload(uploadPage);

    verifyInValidProductUpload(uploadPage);
    verifyValidProductUpload(uploadPage);

    verifyInvalidProgramProductMappingUpload(uploadPage);
    verifyValidProgramProductMappingUpload(uploadPage);

    verifyInvalidProductPriceUpload(uploadPage);
    verifyValidProductPriceUpload(uploadPage);

    verifyInvalidGeographicZoneUpload(uploadPage);
    verifyValidGeographicZoneUpload(uploadPage);

    verifyInvalidFacilityUpload(uploadPage);
    verifyValidFacilityUpload(uploadPage);

    verifyInvalidFacilityTypeToProductMappingUpload(uploadPage);
    verifyValidFacilityTypeToProductMappingUpload(uploadPage);
    dbWrapper.allocateFacilityToUser(userId, "F10");

    verifyInvalidProgramSupportedByFacilitiesUpload(uploadPage);
    verifyValidProgramSupportedByFacilitiesUpload(uploadPage);

    verifyInValidSupervisoryNodesUpload(uploadPage);
    verifyValidSupervisoryNodesUpload(uploadPage);

    verifyInValidRequisitionGroupUpload(uploadPage);
    verifyValidRequisitionGroupUpload(uploadPage);

    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");

    verifyInvalidRequisitionGroupProgramScheduleUpload(uploadPage);
    verifyValidRequisitionGroupProgramScheduleUpload(uploadPage);

    verifyInvalidRequisitionGroupMembersUpload(uploadPage);
    verifyValidRequisitionGroupMembersUpload(uploadPage);

    verifyInvalidSupplyLinesUpload(uploadPage);
    verifyValidSupplyLinesUpload(uploadPage);
  }

  private void verifyValidSupplyLinesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadSupplyLines("QA_Supply_Lines.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadSupplyLines("QA_Supply_Lines_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidSupplyLinesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadSupplyLines("QA_Supply_Lines_InvalidFacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadSupplyLines("QA_Supply_Lines_InvalidProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadSupplyLines("QA_Supply_Lines_DuplicateCombination_SN_Product_Program.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadSupplyLines("QA_Supply_Lines_ParentNodeNotNull.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyValidRequisitionGroupMembersUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidRequisitionGroupMembersUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_InvalidCombination_RG_FacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
//    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_InvalidFacilityCode.csv");
//    uploadPage.verifyErrorMessageOnUploadScreen();
//    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_InvalidRG.csv");
//    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupMembers("QA_Requisition_Group_Members_FacilityCodeAssignedToRGWithOneProgramInCommon.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();

  }

  private void verifyValidRequisitionGroupProgramScheduleUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidRequisitionGroupProgramScheduleUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_InvalidCombination_RG_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_InvalidProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_InvalidScheduleCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_DDTrue_DropoffFacilityNotNull.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_DDFalse_DropoffFacilityNull.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroupProgramSchedule("QA_Requisition_Group_Program_Schedule_DropoffFacilityCodeNotPresent.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();


  }

  private void verifyValidRequisitionGroupUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidRequisitionGroupUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups_DuplicateRequisitionGroup.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadRequisitionGroup("QA_Requisition_Groups_InvalidSupervisoryNode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyValidSupervisoryNodesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidSupervisoryNodesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes_InvalidFacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes_InvalidParentNode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadSupervisoryNodes("QA_Supervisory_Nodes_DuplicateSupervisoryNode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyValidProgramSupportedByFacilitiesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramSupportedByFacilities("QA_program_supported_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProgramSupportedByFacilitiesUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramSupportedByFacilitiesInvalidScenarios("QA_program_supported_Invalid_FacilityCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadProgramSupportedByFacilitiesInvalidScenarios("QA_program_supported_Invalid_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyValidFacilityTypeToProductMappingUpload(UploadPage uploadPage) throws IOException, SQLException {
    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilityTypeToProductMapping("QA_Facility_Type_To_Product_Mapping_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidFacilityTypeToProductMappingUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_Combination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_FacilityType.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_ProductCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadFacilityTypeToProductMappingInvalidScenarios("QA_Facility_Type_To_Product_Mapping_Invalid_Program_Product_Combination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyValidFacilityUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadFacilities("QA_facilities.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilities("QA_facilities_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidFacilityUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadFacilitiesInvalidScenarios("QA_facilities_Lowest_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadFacilitiesInvalidScenarios("QA_facilities_Duplicate_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyValidGeographicZoneUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadAndVerifyGeographicZone("QA_Geographic_Data.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadAndVerifyGeographicZone("QA_Geographic_Data_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidGeographicZoneUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadGeographicZoneInvalidScenarios("QA_Geographic_Data_Invalid.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadGeographicZoneInvalidScenarios("QA_Geographic_Data_Duplicate.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadGeographicZoneInvalidScenarios("QA_Geographic_Data_Invalid_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyValidProductPriceUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramProductPrice("QA_Product_Price.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramProductPrice("QA_Product_Price_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProductPriceUpload(UploadPage uploadPage) throws FileNotFoundException {
//    uploadPage.uploadProgramProductPrice("QA_Product_Price_DuplicateCombination_ProductCode_ProgramCode.csv");
//    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadProgramProductPrice("QA_Product_Price_Invalid_Program_Product_Combination.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadProgramProductPrice("QA_Product_Price_InvalidPrice.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }



  private void verifyValidProgramProductMappingUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramProductMapping("QA_program_product.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProgramProductMapping("QA_program_product_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProgramProductMappingUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProgramProductMappingInvalidScenarios("QA_program_product_Invalid_ProductCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadProgramProductMappingInvalidScenarios("QA_program_product_Invalid_ProgramCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }


  private void verifyValidUserUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadUsers("QA_Users.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInValidUserUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadInvalidUserScenarios("QA_Users_Duplicate_Email.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadInvalidUserScenarios("QA_Users_Duplicate_EmployeeId.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadInvalidUserScenarios("QA_Users_Duplicate_UserName.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
    uploadPage.uploadInvalidUserScenarios("QA_Users_Invalid_Supervisor.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyValidProductCategoryUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProductCategory("QA_Productcategoryupload.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProductCategory("QA_Productcategoryupload_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  private void verifyInvalidProductCategoryUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProductCategory("QA_ProductCategoryUpload_DuplicateCategoryCode.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyInValidProductUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProductsInvalidScenarios("QA_products_Duplicate_Code.csv");
    uploadPage.verifyErrorMessageOnUploadScreen();
  }

  private void verifyValidProductUpload(UploadPage uploadPage) throws FileNotFoundException {
    uploadPage.uploadProducts("QA_products.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadProducts("QA_products_Subsequent.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
  }

  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
