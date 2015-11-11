/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
services.factory('MailingLabels', function ($resource) {
    return $resource('/reports/reportdata/mailingLabels.json', {}, {});
});

services.factory('ConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/consumption.json', {}, {});
});

services.factory('AverageConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/averageConsumption.json', {}, {});
});


services.factory('ReportRegimenCategories', function($resource){
   return $resource('/reports/regimenCategories.json', {}, {}) ;
} );

services.factory('ProductsByCategory', function($resource){
    return $resource('/reports/products_by_category.json', {}, {});
});

services.factory('ProductCategoriesByProgram', function($resource){
    return $resource('/reports/programs/:programId/productCategories.json', {}, {});
}) ;

services.factory('SummaryReport', function($resource){
    return $resource('/reports/reportdata/summary.json', {}, {});
}) ;
services.factory('SupplyStatusReport', function($resource){
    return $resource('/reports/reportdata/supply_status.json', {}, {});
}) ;

services.factory('NonReportingFacilities', function($resource){
    return $resource('/reports/reportdata/non_reporting.json', {}, {});
});

services.factory('RequisitionGroupsByProgramSchedule', function($resource){
    return $resource('/reports/reporting_groups_by_program_schedule.json', {}, {});
});

services.factory('RequisitionGroupsByProgram', function($resource){
    return $resource('/reports/reporting_groups_by_program.json', {}, {});
});

services.factory('AdjustmentSummaryReport', function ($resource) {
    return $resource('/reports/reportdata/adjustmentSummary.json', {}, {});
});

services.factory('OrderReport', function ($resource) {
    return $resource('/reports/reportdata/viewOrders.json', {}, {});
});

services.factory('StockImbalanceReport', function ($resource) {
    return $resource('/reports/reportdata/stockImbalance.json', {}, {});
});

services.factory('AdjustmentTypes', function($resource){
    return $resource('/reports/adjustmentTypes.json', {}, {});
});

services.factory('StockedOutReport',function($resource){
   return $resource('/reports/reportdata/stockedOut.json',{},{});
});

services.factory('DistrictConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/districtConsumption.json', {}, {});
});

services.factory('AggregateConsumptionReport', function ($resource) {
  return $resource('/reports/reportdata/aggregateConsumption.json', {}, {});
});

services.factory('RnRFeedbackReport', function($resource){
    return $resource('/reports/reportdata/rnr_feedback.json', {}, {});
}) ;

services.factory('OperationYears',function($resource){
    return $resource('/reports/operationYears.json',{},{});
});
services.factory('Months',function($resource){
    return $resource('/reports/months.json',{},{});
});

services.factory('ReportPrograms', function ($resource) {
  return $resource('/reports/programs.json', {}, {});
});

services.factory('ReportUserPrograms', function ($resource) {
  return $resource('/reports/user-programs.json', {}, {});
});


services.factory('ReportSchedules', function ($resource) {
    return $resource('/reports/schedules.json', {}, {});
});

services.factory('ReportProgramSchedules', function ($resource) {
    return $resource('/reports/schedules-by-program.json', {}, {});
});

services.factory('ReportFacilityTypes', function ($resource) {
    return $resource('/reports/facilityTypes.json', {}, {});
});

services.factory('ReportFacilityTypesByProgram', function ($resource) {
  return $resource('/reports/facilityTypesForProgram.json', {}, {});
});

services.factory('ReportFacilityLevels', function ($resource) {
    return $resource('/reports/facility-levels.json', {}, {});
});

services.factory('ReportRegimenCategories', function ($resource) {
    return $resource('/reports/regimenCategories.json', {}, {});
});

services.factory('ReportRegimensByCategory',function ($resource){
    return $resource('/reports/regimenCategories/:regimenCategoryId/regimens.json', {}, {});
});

services.factory('ReportGeographicZonesByLevel',function ($resource){
    return $resource('/reports/geographicLevels/:geographicLevelId/zones.json', {}, {});
});

services.factory('FlatGeographicZoneList',function ($resource){
  return $resource('/reports//geographic-zones/flat.json', {}, {});
});

services.factory('TreeGeographicZoneList',function ($resource){
  return $resource('/reports//geographic-zones/tree.json', {}, {});
});

services.factory('TreeGeographicZoneListByProgram',function ($resource){
  return $resource('/reports//geographic-zones/tree-program.json', {}, {});
});

services.factory('ReportRegimens',function ($resource){
    return $resource('/reports/regiments.json', {}, {}) ;
});

services.factory('ReportGeographicLevels',function ($resource){
    return $resource('/reports/geographicLevels.json', {}, {}) ;
});

//deprecated: use "AllFacilities" service instead
services.factory('GetFacilityCompleteList',function($resource){
  return $resource('/reports/allFacilities.json',{},{});
});


services.factory('ColdChainEquipmentService',function($resource){
    return $resource('/reports/reportdata/coldChainEquipment.json',{},{});
});

services.factory('FacilityList', function ($resource) {
    return $resource('/reports/reportdata/facilitylist.json', {}, {});
});

services.factory('ReportPeriods', function ($resource) {
    return $resource('/reports/schedules/:scheduleId/periods.json', {}, {});
});

services.factory('ReportPeriodsByScheduleAndYear', function ($resource) {
    return $resource('/reports/schedules/:scheduleId/year/:year/periods.json', {}, {});
});
services.factory('AllReportPeriods', function ($resource) {
    return $resource('/reports/allPeriods.json', {}, {});
});

services.factory('ReportFilteredPeriods', function ($resource) {
    return $resource('/reports/periods.json', {}, {});
});

services.factory('ReportGeographicZones', function ($resource) {
    return $resource('/reports/geographicZones.json', {}, {});
});

services.factory('GeographicZone', function ($resource) {
    return $resource('/geographicZone/:id.json', {}, {update:{method:'PUT'}});
});

services.factory('CreateGeographicZone', function ($resource) {
    return $resource('/geographicZone/insert.json', {}, {update:{method:'POST'}});
});

services.factory('GeographicLevels', function($resource) {
    return $resource('/geographicLevels.json',{},{});
});

services.factory('GetGeographicZone',function($resource){
    return $resource('/geographicZone/getDetails/:id.json',{},{});
});

services.factory('SetGeographicZone',function($resource){
    return $resource('/geographicZone/setDetails.json',{},{update:{method:'POST'}});
});

services.factory('Supplylinelist', function ($resource) {
    return $resource('/supplylineslist.json', {}, {});
});



services.factory('Supplyline', function ($resource) {
    return $resource('/supplylines/:id.json', {}, {update:{method:'PUT'}});
});

services.factory('SupplylineDelete', function ($resource) {
    return $resource('/supplylineDelete/:id.json', {}, {update:{method:'PUT'}});
});

//Parameters are passed for searching geographic zones.
services.factory('GeographicZoneList', function ($resource) {
    return $resource('/geographicZones.json', {}, {});
});

services.factory('ProductList', function ($resource) {
    return $resource('/productslist.json', {}, {});
});

services.factory('GeographicZoneCompleteList', function ($resource) {
    return $resource('/geographicZone/getList.json', {}, {});
});

services.factory('RequisitionGroupCompleteList',function($resource){
    return $resource('/requisitionGroup/getList.json',{},{});
});

services.factory('SaveRequisitionGroup',function($resource){
    return $resource('/requisitionGroup/insert.json',{},{});
});

services.factory('GetRequisitionGroup',function($resource){
    return $resource('/requisitionGroup/getDetails/:id.json',{},{});
});

services.factory('RemoveRequisitionGroup',function($resource){
    return $resource('/requisitionGroup/remove/:id.json',{},{});
});

services.factory('FacilityCompleteListInRequisitionGroup',function($resource){
    return $resource('/facilities/getListInRequisitionGroup/:id.json',{},{});
});



services.factory('GetFacilityByFacilityType',function($resource){
    return $resource('/facilities/facilityType/:facilityTypeId.json',{},{});
});
services.factory('FacilityByFacilityType',function($resource){
    return $resource('/reports/facilitiesByType/:facilityTypeId.json',{},{});
});
services.factory('FacilityByProgramByFacilityType',function($resource){
    return $resource('/reports/facilitiesByType.json',{},{});
});
services.factory('SaveRequisitionGroupMember',function($resource){
    return $resource('/requisitionGroupMember/insert.json',{},{});
});

services.factory('RemoveRequisitionGroupMember',function($resource){
    return $resource('/requisitionGroupMember/remove/:rgId/:facId.json',{},{});
});

services.factory("AllFacilites",function($resource)  {
    return   $resource('/reports/allFacilities.json', {}, {});
});

services.factory("FacilitiesByProgramParams",function($resource)  {
  //return   $resource('/reports/facilities/program/:program/schedule/:schedule/type/:type/requisitionGroup/:requisitionGroup.json', {}, {});
  return   $resource('/reports/facilities.json', {}, {});
});

services.factory('SupervisoryNodeCompleteList',function($resource){
    return $resource('/supervisoryNode/getList.json',{},{});
});

services.factory('SaveSupervisoryNode',function($resource){
    return $resource('/supervisoryNode/insert.json',{},{});
});

services.factory('GetSupervisoryNode',function($resource){
    return $resource('/supervisoryNode/getDetails/:id.json',{},{});
});

services.factory('RemoveSupervisoryNode',function($resource){
    return $resource('/supervisoryNode/remove/:id.json',{},{});
});

services.factory('Settings',function($resource){
    return $resource('/settings.json',{},{});
});

services.factory('SettingsByKey',function($resource){
    return $resource('/settings/:key.json',{},{});
});

services.factory('SettingUpdator', function($resource){
    return $resource('/saveSettings.json', {} , { post: {method:'POST'} } );
});

services.factory('ProductDetail', function($resource){
    return $resource('/productDetail/:id.json', {} , { post: {method:'GET'} } );
});

services.factory('PriceHistory', function($resource){
    return $resource('/priceHistory/:productId.json', {} , {} );
});

services.factory('ProgramCompleteList',function($resource){
    return $resource('/programs.json',{},{});
});

services.factory('ScheduleCompleteList',function($resource){
    return $resource('/schedules.json',{},{});
});

services.factory('LoadSchedulesForRequisitionGroupProgram',function($resource){
    return $resource('/requisitionGroupProgramSchedule/getDetails/:rgId/:pgId.json',{},{});
});

services.factory('SaveRequisitionGroupProgramSchedule',function($resource){
    return $resource('/requisitionGroupProgramSchedule/insert.json',{},{});
});

services.factory('RemoveRequisitionGroupProgramSchedule',function($resource){
    return $resource('/requisitionGroupProgramSchedule/remove/:id',{},{});
});

services.factory('GetProgramsForAFacilityCompleteList',function($resource){
    return $resource('/facilities/:facilityId/programsList.json',{},{});
});

services.factory('GetFacilityApprovedProductsCompleteList', function ($resource) {
    return $resource('/facilityApprovedProducts/facility/:facilityId/program/:programId/all.json', {}, {});
});

services.factory('GetFacilityProgramProductAlreadyAllowedList',function($resource){
    return $resource('/facility/:facilityId/program/:programId/programProductList.json',{},{});
});

services.factory('GetFacilityTypeApprovedProductsCompleteList', function ($resource) {
    return $resource('/facilityApprovedProducts/facilityType/:facilityTypeId/program/:programId/all.json', {}, {});
});

services.factory('GetFacilityTypeProgramProductAlreadyAllowedList',function($resource){
    return $resource('/facilityApprovedProducts/:facilityTypeId/program/:programId/programProductList.json',{},{});
});

services.factory('GetProductsCompleteListForAProgram',function($resource){
    return $resource('/programProducts/program/:programId/all.json',{},{});
});

services.factory('ReportProductsByProgram',function($resource){
    return $resource('/reports/program-products/:programId.json',{},{});
});

services.factory('GetApprovedProductForFacilityTypeDetail', function($resource){
    return $resource('/facilityApprovedProducts/facilityType/:facilityTypeId/program/:programId/product/:productId',{},{});
});

services.factory('SaveApprovedProductForFacilityType',function($resource){
    return $resource('/facilityApprovedProducts/insert.json',{},{});
});

services.factory('RemoveApprovedProductForFacilityType', function($resource){
    return $resource('/facilityApprovedProducts/remove/facilityType/:facilityTypeId/program/:programId/product/:productId',{},{});
});

services.factory("SupplyingFacilities",function($resource)  {
    return $resource('/facility/supplyingFacilities.json', {}, {});
});

services.factory("GetRequisitionGroupsForSupervisoryNode", function($resource) {
    return $resource('/requisitionGroup/getForSupervisoryNode/:supervisoryNodeId.json',{},{});
});

services.factory('OrderFillRateReport', function ($resource) {
    return $resource('/reports/reportdata/orderFillRate.json', {}, {});
});

services.factory('RegimenSummaryReport', function ($resource) {
    return $resource('/reports/reportdata/regimenSummary.json', {}, {});
});

services.factory('AggregateRegimenSummaryReport', function($resource){
   return $resource('/reports/reportdata/aggregateRegimenSummary.json',{},{});
});

//It populate all programs with regimens
services.factory('ReportRegimenPrograms', function ($resource) {
    return $resource('/reports/programs-supporting-regimen.json', {}, {});
});

services.factory('DistrictFinancialSummaryReport', function ($resource) {
    return $resource('/reports/reportdata/districtFinancialSummary.json', {}, {});
});

services.factory('SaveGeographicInfo', function($resource){
   return $resource('/geographic-zone/save-gis.json',{}, {post:{method:'POST'}});
});


services.factory('ReportingFacilityList', function($resource){
  return $resource('/gis/reporting-facilities.json',{}, {});
});

services.factory('NonReportingFacilityList', function($resource){
  return $resource('/gis/non-reporting-facilities.json',{}, {});
});

services.factory('ContactList', function($resource){
   return $resource('/facility-contacts',{},{});
});

/* Dashboard data factories */
services.factory('UserSupervisoryNodes', function($resource){
    return $resource('/reports/user/supervisory-nodes.json',{},{});
});
services.factory('UserGeographicZoneTree',function ($resource){
    return $resource('/reports/geographic-zones/tree.json', {}, {});
});
/*services.factory('UserDefaultSupervisoryNode', function($resource){
   return $resource('/reports/user/default-supervisory-node.json',{},{});
});*/
/*services.factory('ProgramListBySupervisoryNodes', function ($resource) {
    return $resource('/reports/supervisory-nodes/programs.json', {}, {});
});*/
services.factory('UserSupervisedActivePrograms', function ($resource) {
    return $resource('/reports/user/programs.json', {}, {});
});
services.factory("FacilitiesByGeographicZoneTree",function($resource)  {
    return   $resource('/reports/geographic-zone/facilities.json', {}, {});
});

services.factory("FacilitiesByGeographicZone",function($resource)  {
    return   $resource('/reports/geographic-zone/:geoId/facilities.json', {}, {});
});

services.factory("FacilitiesForNotifications",function($resource)  {
    return   $resource('/reports/notification/facilities.json', {}, {});
});

services.factory('OrderFillRate', function($resource){
    return $resource('/dashboard/orderFillRate.json',{}, {});
});

services.factory('ItemFillRate', function($resource){
    return $resource('/dashboard/itemFillRate.json',{}, {});
});

services.factory('ShipmentLeadTime', function($resource){
    return $resource('/dashboard/shipmentLeadTime.json',{}, {});
});

services.factory('StockEfficiency', function($resource){
    return $resource('/dashboard/stockEfficiency.json',{}, {});
});

services.factory('StockEfficiencyDetail', function($resource){
    return $resource('/dashboard/stockEfficiencyDetail.json',{}, {});
});

services.factory('StockedOutFacilities', function($resource){
    return $resource('/dashboard/stockedOutFacilities.json',{}, {});
});

services.factory('ReportProgramsBySupervisoryNode', function ($resource) {
    return $resource('/reports/supervisory-node/:supervisoryNodeId/programs.json', {}, {});
});

services.factory('StockedOutFacilitiesByDistrict', function($resource){
    return $resource('/dashboard/geographic-zone/:zoneId/program/:programId/period/:periodId/product/:productId/stockedOutFacilities.json',{},{});

});
services.factory('Alerts', function($resource){
    return $resource('/dashboard/alerts.json',{},{});
});

services.factory('StockedOutAlerts', function($resource){
    return $resource('/dashboard/stocked-out/alerts.json',{},{});
});

services.factory('NotificationAlerts', function($resource) {
    return $resource('/dashboard/notification/alerts.json', {}, {});
});
/*services.factory('DashboardNotificationsDetail', function($resource){
   return $resource('/dashboard/notifications/:alertId/:detailTable.json',{},{});
});*/
services.factory('DashboardNotificationsDetail', function($resource){
    return $resource('/dashboard/notifications/:programId/:periodId/:zoneId/:productId/:detailTable.json',{},{});
});
services.factory('SendNotification', function($resource){
   return $resource('/dashboard/notification/send.json',{},{});
});
services.factory('GetPeriod', function($resource){
   return $resource('/dashboard/period/:id.json',{},{});
});
services.factory('GetProduct', function($resource){
    return $resource('/dashboard/productDetail/:id/:periodId.json',{},{});
});
services.factory('ReportingPerformance',function($resource){
    return $resource('/dashboard/reportingPerformance.json',{},{});
});
services.factory('ReportingPerformanceDetail',function($resource){
    return $resource('/dashboard/reportingPerformance-detail.json',{},{});
});

services.factory('ReportingPerformanceDetail',function($resource){
    return $resource('/dashboard/reportingPerformance-detail.json',{},{});
});

/* End Dashboard data factories */

services.factory('SMSCompleteList',function($resource){
    return $resource('/sms/MessageList.json',{},{});
});

services.factory('GetSMSInfo', function($resource){
    return $resource('/sms/setDetails',{}, {get:{method:'GET'}});
});

services.factory('GetMessagesForMobile', function($resource){
    return $resource('/sms/MessagesForMobile',{}, {get:{method:'GET'}});
});

services.factory('GetReplyMessages', function($resource){
    return $resource('/sms/getSMS',{}, {get:{method:'GET'}});
});

/*End SMS data Factories*/
services.factory('UserSummaryReport', function($resource){
    return $resource('/reports/reportdata/userSummary.json', {}, {});
}) ;
services.factory('GetAllRoles', function ($resource) {
    return $resource('/roles/getList.json', {},{});
});

services.factory('UserRoleAssignmentsSummary', function($resource){
    return $resource('/reports/UserRoleAssignments/getUserRoleAssignments',{},{});
});

services.factory("UserRoleAssignmentsSummary1", function($resource){
    return $resource('reports/reportdata/userRoleAssignmentSummary');

});

/*services.factory("RnRStatusSummary",function($resource){
    return $resource('/dashboard/rnrstatusSummary/requisitionGroup/:requisitionGroupId.json',{},{});
});*/

services.factory("totalRnRCreatedByRequisitionGroup",function($resource){
    return $resource('/dashboard//RnRCreateForRequisitionGroup',{},{});
});
services.factory('RnRStatusSummary',function($resource){
    return $resource('/dashboard/RnRStatus/:zoneId/:periodId/:programId/rnrStatus.json',{},{});
  });

services.factory("EmergencyRnRStatusSummary", function($resource){
    return $resource('/dashboard/EmergencyRnRStatus/:zoneId/:periodId/:programId/rnrStatus.json',{},{});
});

services.factory("SendMessages",function($resource){
  return $resource('/messages/send.json',{}, {post: {method:'POST'}});
});


/*services.factory("RnRStatusByRequisitionGroupAndPeriodDetails ",function($resource){
    $resource('/dashboard/RnRStatusByRequisitionGroupDetails.json',{},{});
});*/

services.factory('RnRStatusDetail', function($resource){
  return $resource('/dashboard/rnrStatus-detail.json',{},{});
});


services.factory('GetLabEquipmentList', function ($resource) {
    return $resource('/dashboard/notification/alerts.json',{}, {}); // just for mock
});

services.factory('ReportEquipmentTypes', function ($resource) {
    return $resource('/reports/equipmentTypes.json',{}, {}); // just for mock
});

services.factory('LabEquipmentListReport', function ($resource) {
    return $resource('/reports/reportdata/labEquipmentList.json', {}, {});
});

services.factory('CCEStorageCapacityReport', function ($resource) {
    return $resource('/reports/reportdata/cceStorageCapacity.json', {}, {});
});

services.factory("PipelineExportReport", function($resource){
    return $resource('/reports/reportdata/pipelineExport.json', {}, {});

});

services.factory('ReportProgramsWithBudgeting',function($resource){
    return $resource('/reports/programs-supporting-budget.json',{},{});

});
services.factory('RegimenDistributionReport',function($resource){
    return $resource('/reports/reportdata/getRegimenDistribution.json',{},{});
});

services.factory('LabEquipmentListByDonorReport', function ($resource) {
    return $resource('/reports/reportdata/labEquipmentsByFundingSource.json', {}, {});
});

services.factory('GetAllUsers',function($resource){
    return $resource('/user/getAll.json',{},{});
});

services.factory('GetPushedProductList', function($resource){
    return $resource('/reports/reportdata/pushedProductList.json',{},{});
});

services.factory('GetUserUnassignedSupervisoryNode',function ($resource){
    return $resource('/reports/supervisory-node/user-unassigned-node.json', {}, {});
});
services.factory('GetProductCategoryProductByProgramTree',function ($resource){
    return $resource('/reports/productProgramCategoryTree/:programId', {}, {});
});

services.factory('GetYearSchedulePeriodTree',function ($resource){
    return $resource('/reports/yearSchedulePeriod', {}, {});
});

services.factory('OrderFillRateSummaryReport', function ($resource) {
    return $resource('/reports/reportdata/orderFillRateReportSummary.json', {}, {});
});

services.factory('GetOrderFillRateSummary', function($resource){
    return $resource('/reports/OrderFillRateSummary/program/:programId/period/:periodId/schedule/:scheduleId/facilityTypeId/:facilityTypeId/zone/:zoneId/status/:status/orderFillRateSummary.json',{},{});
});

services.factory('StockedOutFacilityList', function($resource){
    return $resource('/gis/stocked-out-facilities.json',{}, {});
});

services.factory('OverStockedFacilityList', function($resource){
    return $resource('/gis/over-stocked-facilities.json',{}, {});
});

services.factory('UnderStockedFacilityList', function($resource){
    return $resource('/gis/under-stocked-facilities.json',{}, {});
});

services.factory('AdequatelyStockedFacilityList', function($resource){
    return $resource('/gis/adequately-stocked-facilities.json',{}, {});
});

services.factory('StockStatusProductList', function($resource){
    return $resource('/gis/stock-status-products.json',{}, {});
});

services.factory('StockedOutFacilityByProductList', function($resource){
    return $resource('/gis/stocked-out-products.json',{}, {});
});

services.factory('OverStockedFacilityByProductList', function($resource){
    return $resource('/gis/over-stocked-products.json',{}, {});
});

services.factory('UnderStockedFacilityByProductList', function($resource){
    return $resource('/gis/under-stocked-products.json',{}, {});
});

services.factory('AdequatelyStockedFacilityByProductList', function($resource){
    return $resource('/gis/adequately-stocked-products.json',{}, {});
});

services.factory('GetFacilitiesByEquipmentStatus', function($resource){
    return $resource('/gis/facilitiesByEquipmentOperationalStatus.json',{}, {});
});

services.factory('GetFacilitiesEquipmentStatusSummary', function($resource){
    return $resource('/gis/facilitiesEquipmentStatusSummary.json',{}, {});
});

services.factory('ReportEquipments', function ($resource) {
    return $resource('/reports/equipmentsByType/:equipmentType',{}, {});
});

services.factory('NonFunctioningLabEquipment', function ($resource) {
    return $resource('/reports/reportdata/nonFunctioningLabEquipment.json', {}, {});
});

services.factory('FunctioningLabEquipment', function ($resource) {
    return $resource('/reports/reportdata/functioningLabEquipment.json', {}, {});
});

services.factory('StockStatusProductConsumptionGraph', function($resource){
    return $resource('/gis/stock-status-product-consumption.json',{}, {});
});

services.factory('GetDonors', function($resource) {
    return $resource('/reports/donors', {}, {});
});

services.factory('GetFacilitySupervisors', function($resource){
    return $resource('/facility-supervisors.json',{}, {});
});

services.factory("SendMessagesReportAttachment",function($resource){
    return $resource('/messages/send/report.json',{}, {post: {method:'POST'}});

});services.factory('UserPrograms', function ($resource) {
    return $resource('/reports/users/:userId/programs.json', {}, {});
});
services.factory('UserFacilitiesForProgram', function ($resource) {
    return $resource('/users/:userId/supervised/:programId/facilities.json', {}, {});
});

services.factory('UserPreferences', function ($resource) {
    return $resource('/users/:userId/preferences.json', {}, {});
});
services.factory('UpdateUserPreference', function ($resource) {
    return $resource('/users/:userId/preferences.json', {}, update);
});
services.factory('UserProfile', function ($resource) {
   return $resource('/preference/users/:id.json', {}, {});
});

services.factory('SyncDashboard', function($resource){
   return $resource('/dashboard/sync.json', {},update);
});

services.factory('SupervisoryNodesList', function ($resource) {
    return $resource('/supervisory-nodes/list.json', {}, {});
});

services.factory('RolesList', function ($resource) {
    return $resource('/roles/list.json', {}, {});
});
/* help modudule services as updated on october 16

 */
//CreateHelp topic
//load helptopic intialize
services.factory('IntializeHelpTopic', function ($resource) {
    return $resource('/helpTopicForCreate.json', {}, {post:{method:'GET'}});
});

services.factory('CreateHelpTopic', function ($resource) {
    return $resource('/createHelpTopic.json', {}, {post:{method:'POST'}});
});
//load helptopic detail
services.factory('HelpTopicDetail', function ($resource) {
    return $resource('/helpTopicDetail/:id.json', {}, {post:{method:'GET'}});
});
//update help topic
services.factory('UpdateHelpTopic', function ($resource) {
    return $resource('/updateHelpTopic.json', {}, {post:{method:'POST'}});
});
//load help topic list
services.factory('HelpTopicList', function ($resource) {
    return $resource('/helpTopicList.json', {}, {});
});

services.factory('CreateHelpContent', function ($resource) {
    return $resource('/createHelpContent.json', {}, {post:{method:'POST'}});
});

services.factory('UpdateHelpContent', function ($resource) {
    return $resource('/updateHelpContent.json', {}, {post:{method:'POST'}});
});

services.factory('HelpContentList', function ($resource) {
    return $resource('/helpContentList.json', {}, {});
});

services.factory('HelpContentDetail', function ($resource) {
    return $resource('/helpContentDetail/:id.json', {}, {post:{method:'GET'}});
});

services.factory('HelpUsertopicList', function ($resource) {
    return $resource('/userHelpTopicList.json', {}, {});
});
//load helptopic detail
services.factory('SiteContent', function ($resource) {
    return $resource('/site_content/:content_name.json', {}, {post:{method:'GET'}});
});
services.factory('VaccineTargetUpdate', function ($resource) {
    return $resource('/vaccine/target/create.json', {}, {post:{method:'POST'}});
});

services.factory('VaccineTargetList', function ($resource) {
    return $resource('/vaccine/target/list.json', {}, {});
});

services.factory('GetVaccineTarget', function ($resource) {
    return $resource('/vaccine/target/get/:id.json', {}, {});
});

services.factory('DeleteVaccineTarget', function ($resource) {
    return $resource('/vaccine/target/delete/:id.json', {}, {});
});

//vaccine storage service
services.factory('CreateVaccineStorage', function ($resource) {

    return $resource('/createVaccineStorage.json', {}, {post:{method:'POST'}});
});

services.factory('UpdateVaccineStorage', function ($resource) {
    return $resource('/updateVaccineStorage.json', {}, {post:{method:'POST'}});
});

services.factory('VaccineStorageList', function ($resource) {
    return $resource('/vaccineStorageList.json', {}, {});
});

services.factory('VaccineStorageDetail', function ($resource) {
    return $resource('/vaccineStorageDetail/:id.json', {}, {post:{method:'GET'}});
});
services.factory('DeleteVaccineStorage', function ($resource) {
    return $resource('/deleteVaccineStorage.json', {}, {post:{method:'POST'}});
});
services.factory('StorageTypeList', function ($resource) {
    return $resource('/storageTypeList.json', {}, {});
});

services.factory('TempratureList', function ($resource) {
    return $resource('/tempratureList.json', {}, {});
});

services.factory('VaccineQuantificationUpdate', function ($resource) {
    return $resource('/vaccine/quantification/create.json', {}, {post:{method:'POST'}});
});

services.factory('VaccineQuantificationList', function ($resource) {
    return $resource('/vaccine/quantification/list.json', {}, {});
});

services.factory('GetVaccineQuantification', function ($resource) {
    return $resource('/vaccine/quantification/get/:id.json', {}, {});
});

services.factory('DeleteVaccineQuantification', function ($resource) {
    return $resource('/vaccine/quantification/delete/:id.json', {}, {});
});

services.factory('VaccineQuantificationFormLookUps', function ($resource) {
    return $resource('/vaccine/quantification/formLookups.json', {}, {});
});

//storage type
services.factory('CreateStorageType', function ($resource) {

    return $resource('/createStorageType.json', {}, {post:{method:'POST'}});
});

services.factory('UpdateStorageType', function ($resource) {
    return $resource('/updateStorageType.json', {}, {post:{method:'POST'}});
});

services.factory('StorageTypeDetail', function ($resource) {
    return $resource('/storageTypeDetail/:id.json', {}, {post:{method:'GET'}});
});
services.factory('DeleteStorageType', function ($resource) {
    return $resource('/deleteStorageType.json', {}, {post:{method:'POST'}});
});
services.factory('StorageTypes', function ($resource) {
    var resource = $resource('/storageTypes/:id.json', {id: '@id'}, update);

    resource.disable = function (pathParams, success, error) {
        $resource('/storageTypes/:id.json', {}, {update: {method: 'DELETE'}}).update(pathParams, {}, success, error);
    };

    return resource;
});
//temprature
services.factory('CreateTemprature', function ($resource) {

    return $resource('/createTemprature.json', {}, {post:{method:'POST'}});
});

services.factory('UpdateTemprature', function ($resource) {
    return $resource('/updateTemprature.json', {}, {post:{method:'POST'}});
});
services.factory('TempratureDetail', function ($resource) {
    return $resource('/tempratureDetail/:id.json', {}, {post:{method:'GET'}});
});
services.factory('DeleteTemprature', function ($resource) {
    return $resource('/deleteTemprature.json', {}, {post:{method:'POST'}});
});
services.factory('Tempratures', function ($resource) {
    var resource = $resource('/tempratures/:id.json', {id: '@id'}, update);

    resource.disable = function (pathParams, success, error) {
        $resource('/tempratures/:id.json', {}, {update: {method: 'DELETE'}}).update(pathParams, {}, success, error);
    };

    return resource;
});
services.factory('Countries', function ($resource) {
    var resource = $resource('/countries/:id.json', {},  {
        update: {
            method: 'PUT',params: {id: '@id'}
        },
        remove: {method:'DELETE'}
    });

//    resource.disable = function (pathParams, success, error) {
//        $resource('/countries/:id.json', {}, {update: {method: 'DELETE'}}).update(pathParams, {}, success, error);
//    };

    return resource;
});
services.factory('DeleteCountries', function ($resource) {
    return $resource('/countries_remove.json', {}, {post:{method:'POST'}});
});
services.factory('StorageFacilityList', function ($resource) {
    return $resource('/facilityList.json', {}, {});
});

/* Begin: Vaccine Supply Line */

services.factory('Manufacturers', function($resource){
    return $resource('/vaccine/manufacturers.json',{},{});
});

services.factory('VaccineDistributionStatus', function($resource){
    return $resource('/vaccine/status.json',{},{});
});

services.factory('VaccineStorageByFacility', function($resource){
    return $resource('/vaccine-storage/facility/:facilityId.json',{},{});
});
services.factory('UserSupervisedFacilities', function($resource){
   return $resource('/reports/user/supervised/facilities.json',{},{});
});

services.factory('ReceiveVaccines', function ($resource) {
    return $resource('/vaccine/receive-vaccine/:id.json', {id: '@id'}, update);
});

services.factory('UsableBatches', function ($resource) {
    return $resource('/vaccine/usable-batches/product/:productId.json', {}, {});
});

services.factory('DistributeVaccines', function ($resource) {
    return $resource('/vaccine/distribute-vaccine.json', {}, {});
});

services.factory('GeoZoneFacilityTrees', function($resource){
   return $resource('/vaccine/geographic-zone-facility/tree.json',{},{});
});

services.factory('PushProgramProducts', function($resource){
   return $resource('/reports/push-program/products.json',{},{});
});


/* End: Vaccine Supply Line */


services.factory('VaccineManufacturerUpdate', function ($resource) {
    return $resource('/vaccine/manufacturer/create.json', {}, {post:{method:'POST'}});
});

services.factory('VaccineManufacturerList', function ($resource) {
    return $resource('/vaccine/manufacturer/list.json', {}, {});
});

services.factory('GetVaccineManufacturer', function ($resource) {
    return $resource('/vaccine/manufacturer/get/:id.json', {}, {});
});

services.factory('DeleteVaccineManufacturer', function ($resource) {
    return $resource('/vaccine/manufacturer/delete/:id.json', {}, {});
});

services.factory('GetVaccineManufacturerProductMapping', function ($resource) {
    return $resource('/vaccine/manufacturer/getManufacturerProducts/:id.json', {}, {});
});

services.factory('VaccineManufacturerProductUpdate', function ($resource) {
    return $resource('/vaccine/manufacturer/createProduct.json', {}, {post:{method:'POST'}});
});

services.factory('GetManufacturerProduct', function ($resource) {
    return $resource('/vaccine/manufacturer/getProduct/:id.json', {}, {});
});

services.factory('DeleteManufacturerProductMapping', function ($resource) {
    return $resource('/vaccine/manufacturer/deleteProduct/:id.json', {}, {});
});

services.factory('VaccineTransactionTypeList', function ($resource) {
    return $resource('/vaccine/transaction-type/list.json', {}, {});
});

services.factory('GetVaccineTransactionType', function ($resource) {
    return $resource('/vaccine/transaction-type/get/:id.json', {}, {});
});

services.factory('VaccineTransactionTypeSave', function ($resource) {
    return $resource('/vaccine/transaction-type/save.json', {}, {post:{method:'POST'}});
});

services.factory('DeleteVaccineTransactionType', function ($resource) {
    return $resource('/vaccine/transaction-type/delete/:id.json', {}, {});
});

services.factory('SearchVaccineTransactionType', function ($resource) {
    return $resource('/vaccine/transaction-type/search.json', {}, {});
});


services.factory('VaccineReceivedStatusList', function ($resource) {
    return $resource('/vaccine/received-status/list.json', {}, {});
});

services.factory('GetVaccineReceivedStatus', function ($resource) {
    return $resource('/vaccine/received-status/get/:id.json', {}, {});
});

services.factory('VaccineReceivedStatusSave', function ($resource) {
    return $resource('/vaccine/received-status/save.json', {}, {post:{method:'POST'}});
});

services.factory('DeleteVaccineReceivedStatus', function ($resource) {
    return $resource('/vaccine/received-status/delete/:id.json', {}, {});
});

services.factory('SearchVaccineReceivedStatus', function ($resource) {
    return $resource('/vaccine/received-status/search.json', {}, {});
});
services.factory('HelpDocumentList', function ($resource) {
    return $resource('/loadDocumentList.json', {}, {});
});


services.factory('ExtraAnalyticDataForRnRStatus',function($resource){
   return $resource('/dashboard/extraAnalyticsRnRStatus/:zoneId/:periodId/:programId/statusData.json',{},{});
});


services.factory('SeasonalityRationingReport',function($resource){
    return $resource('/reports/reportdata/seasonalityRationing.json',{},{});
});
services.factory('SeasonalityRationingTypes', function ($resource) {

    var resource = $resource('/season-rationing/seasonalityRationingTypes/:id.json', {},  {

        update: {
            method: 'PUT',params: {id: '@id'}
        },
        delete: { method: 'DELETE',
            params: {id: '@id'} }
    });

    return resource;
});
services.factory('SeasonalityRationingTypeList', function ($resource) {
   return $resource('/season-rationing/seasonalityRationingTypeList.json', {},{});
});

services.factory('AdjustmentFactors', function ($resource) {

    var resource = $resource('/season-rationing/adjustmentFactors/:id.json', {},  {

        update: {
            method: 'PUT',params: {id: '@id'}
        },
        delete: { method: 'DELETE',
                       params: {id: '@id'} }
    });
    return resource;
});
services.factory('AdjustmentFactorList', function ($resource) {
    return $resource('/season-rationing/adjustmentFactorList.json', {}, {});
});

services.factory('FacilityByTypeAndRequisition', function ($resource) {
    return $resource('/facilityType/:facilityTypeId/requisitionGroup/:requisitionGroupId/facilities.json', {},{});
});

services.factory('AdjustmentProducts', function ($resource) {
   return $resource('/season-rationing/adjustmentProducts.json', {id: '@id'}, update);
});

services.factory('AdjustmentProductSearch', function ($resource){
    return $resource('/season-rationing/search.json', {}, {});
});

services.factory('PublicSiteData', function ($resource) {

    return {

        regions : function() {
            var resource = $resource('/public-data/regions.json', {}, {});
            return resource;
        },

        districts : function() {
            var resource = $resource('/public-data/districts.json', {}, {});
            return resource;
        },

        facilities : function() {
            var resource = $resource('/public-data/facilities.json', {}, {});
            return resource;
         },

        ARVProducts : function() {
            var resource = $resource('/public-data/arv-products.json', {}, {});
            return resource;
        },

        ILSProducts : function() {
            var resource = $resource('/public-data/ils-products.json', {}, {});
            return resource;
        },

        TBProducts : function() {
            var resource = $resource('/public-data/tb-products.json', {}, {});
            return resource;
        },

        NationalLabProducts : function() {
            var resource = $resource('/public-data/labnational-products.json', {}, {});
            return resource;
        },

        RegionalLabProducts : function() {
            var resource = $resource('/public-data/labregional-products.json', {}, {});
            return resource;
        },

        DistrictLabProducts : function() {
            var resource = $resource('/public-data/labdist-products.json', {}, {});
            return resource;
        },

        ZoneLabProducts : function() {
            var resource = $resource('/public-data/labzone-products.json', {}, {});
            return resource;
        }


    };
});




services.factory('getTimelinessReport',function($resource){
    return $resource('/reports/reportdata/timeliness.json',{},{});
});

services.factory("getTimelinessStatusData",function($resource){
    return $resource('/reports/timelinessStatusData/timelinessData.json',{},{});
});

services.factory("getFacilityRnRTimelinessReportData", function($resource){
    return $resource('/reports/timelinessStatusData/getFacilityRnRStatusData.json',{},{});
});

services.factory("getTimelinessReportingDates", function($resource){
    return $resource('/reports/reportingDates/getTimelinessReportingDates.json',{},{});
});

/* RMNCH report POC*/

services.factory('RmnchStockedOutFacilityList', function($resource){
    return $resource('/rmnch/stocked-out-facilities.json',{}, {});
});

services.factory('RmnchOverStockedFacilityList', function($resource){
    return $resource('/rmnch/over-stocked-facilities.json',{}, {});
});

services.factory('RmnchUnderStockedFacilityList', function($resource){
    return $resource('/rmnch/under-stocked-facilities.json',{}, {});
});

services.factory('RmnchAdequatelyStockedFacilityList', function($resource){
    return $resource('/rmnch/adequately-stocked-facilities.json',{}, {});
});

services.factory('RmnchStockStatusProductList', function($resource){
    return $resource('/rmnch/stock-status-products.json',{}, {});
});

services.factory('RmnchStockedOutFacilityByProductList', function($resource){
    return $resource('/rmnch/stocked-out-products.json',{}, {});
});

services.factory('RmnchOverStockedFacilityByProductList', function($resource){
    return $resource('/rmnch/over-stocked-products.json',{}, {});
});

services.factory('RmnchUnderStockedFacilityByProductList', function($resource){
    return $resource('/rmnch/under-stocked-products.json',{}, {});
});

services.factory('RmnchAdequatelyStockedFacilityByProductList', function($resource){
    return $resource('/rmnch/adequately-stocked-products.json',{}, {});
});
services.factory('RmnchStockStatusProductConsumptionGraph', function($resource){
    return $resource('/rmnch/stock-status-product-consumption.json',{}, {});
});

services.factory('RmnchProducts', function($resource){
    return $resource('/reports/rmnch-products.json',{},{});
});

services.factory('CustomReportList', function ($resource) {
    return $resource('/report-api/list.json', {}, {});
});

services.factory('CustomReportFullList', function ($resource) {
    return $resource('/report-api/full-list.json', {}, {});
});

services.factory('SaveCustomReport', function ($resource) {
    return $resource('/report-api/save.json', {}, {method: 'POST'});
});

services.factory('CustomReportValue', function ($resource) {
    return $resource('/report-api/report.json', {}, {});
});

services.factory('PriceScheduleCategories', function ($resource) {
    return $resource('/priceScheduleCategories.json', {}, {});
});

services.factory('CCERepairManagement', function ($resource) {
    return $resource('/reports/reportdata/cceRepairManagement.json', {}, {});
});

services.factory('CCERepairManagementEquipmentList', function ($resource) {
    return $resource('/reports/reportdata/cceRepairManagementEquipmentList.json', {}, {});
});

services.factory('ReplacementPlanSummaryReport', function($resource){
    return $resource('/reports/reportdata/replacementPlanSummary.json',{},{});
});

services.factory('EquipmentsInNeedForReplacement', function($resource){
    return $resource('/reports/reportdata/equipmentsInNeedForReplacement.json',{},{});
});

services.factory('VaccineMonthlyReport', function ($resource){
    return $resource('/vaccine/report/vaccine-monthly-report.json', {}, {});
});


services.factory('VaccineUsageTrend', function ($resource){
    return $resource('/vaccine/report/vaccine-usage-trend.json', {}, {});
});

services.factory('VaccineReportLegendContent', function ($resource) {
    return $resource('/report_legend.json', {}, {});
});

services.factory("FacilityGeoTree",function($resource)  {
    return   $resource('/geoFacilityTree.json', {}, {});
});

services.factory('GetLastPeriods', function($resource) {
   return $resource('/reports/last-periods.json', {}, {});
});

services.factory('GetProgramPeriodTracerProductsTrend', function($resource) {
    return $resource('/dashboard/program/:programId/period/:periodId/tracer-products-trend.json', {}, {});
});

services.factory('DashboardReportingPerformance', function($resource) {
    return $resource('/dashboard/program/:programId/period/:periodId/reporting-performance.json', {}, {});
});

services.factory('DashboardDistrictStockSummary', function($resource) {
    return $resource('/dashboard/program/:programId/period/:periodId/district-stock-summary.json', {}, {});
});

services.factory('DashboardFacilityStockSummary', function($resource) {
    return $resource('/dashboard/program/:programId/period/:periodId/facility-stock-summary.json', {}, {});
});

services.factory('GetStockOutFacilitiesForProgramPeriodAndProductCode', function($resource) {
    return $resource('/dashboard/program/:programId/period/:periodId/product/:productCode/stocked-out-facilities.json', {}, {});
});


services.factory('GetVaccineReportPeriodTree',function ($resource){
    return $resource('/reports/vaccineYearSchedulePeriod', {}, {});
});

services.factory("ELMISInterface",function($resource)  {
    return   {
        getInterface : function(){
            return $resource('/ELMISInterface/:id.json', {}, {});
        },

        getInterfacesReference : function(){
            return $resource('/ELMISAllActiveInterfaces.json', {}, {});
          },

        getFacilityMapping : function(){
            return $resource('/ELMISInterfacesMapping/{facilityId}.json', {}, {});
        },

        getAllinterfaces : function(){
            return $resource('/ELMISAllInterfaces.json');
        }

    };
});

services.factory('ELMISInterfaceSave', function ($resource) {
    return $resource('/ELMISInterface.json', {}, {save:{method:'POST'}});
});

services.factory('FacilitiesByLevel', function($resource){
    return $resource('/reports/facility-By-level.json',{},{});
});

services.factory('RequisitionReportService', function($resource){
    return $resource('/reports/requisition-report.json',{},{});
});

services.factory('ProductReportService', function ($resource) {
    return {
        loadAllProducts: function () {
            return $resource('/rest-api/lookup/products', {pageSize: 2000}, {});
        },
        loadProductReport: function () {
            return $resource('/reports/single-product-report', {}, {save: {method: 'POST'}});
        },
        loadFacilityReport: function () {
            return $resource('/reports/all-products-report', {}, {save: {method: 'POST'}});
        }
    };
});

services.factory('FacilityService',function($resource){
    return {
        allFacilities: function () {
            return $resource('/rest-api/lookup/facilities', {pageSize: 2000}, {});
        }
    };
});

services.factory('GeographicZoneService',function($resource){
    return {
        loadGeographicZone: function () {
            return $resource('/rest-api/lookup/geographic-zones', {}, {});
        },
        loadGeographicLevel: function () {
            return $resource('/rest-api/lookup/geographic-levels', {}, {});
        }
    };
});
