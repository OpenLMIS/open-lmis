
services.factory('MailingLabels', function ($resource) {
    return $resource('/reports/reportdata/mailingLabels.json', {}, {});
});

services.factory('ConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/consumption.json', {}, {});
});

services.factory('AverageConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/averageConsumption.json', {}, {});
});

services.factory('Products', function($resource){
    return $resource('/reports/products.json', {}, {});
}) ;
services.factory('ReportRegimenCategories', function($resource){
   return $resource('/reports/regimenCategories.json', {}, {}) ;
} );

services.factory('ProductsByCategory', function($resource){
    return $resource('/reports/products_by_category.json', {}, {});
});

services.factory('ProductCategories', function($resource){
    return $resource('/reports/productCategories.json', {}, {});
}) ;

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

services.factory('RequisitionGroups', function($resource){
    return $resource('/reports/rgroups.json', {}, {});
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

services.factory('ReportSchedules', function ($resource) {
    return $resource('/reports/schedules.json', {}, {});
});

services.factory('ReportFacilityTypes', function ($resource) {
    return $resource('/reports/facilityTypes.json', {}, {});
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

services.factory('GeographicZones', function ($resource) {
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

services.factory('Supplylines', function ($resource) {
    return $resource('/supplylines.json', {});
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

services.factory('SaveRequisitionGroupMember',function($resource){
    return $resource('/requisitionGroupMember/insert.json',{},{});
});

services.factory('RemoveRequisitionGroupMember',function($resource){
    return $resource('/requisitionGroupMember/remove/:rgId/:facId.json',{},{});
});

services.factory("ProductForms",function($resource)  {
    return   $resource('/reports/productForms.json', {}, {});
});

services.factory("DosageUnits",function($resource)  {
    return   $resource('/reports/dosageUnits.json', {}, {});
});

services.factory("ProductGroups",function($resource)  {
    return   $resource('/reports/productGroups.json', {}, {});
});

// mahmed 07.13.2013
services.factory('CreateProduct', function ($resource) {
    return $resource('/createProduct.json', {}, {post:{method:'POST'}});
});

// mahmed 07.13.2013
services.factory('UpdateProduct', function ($resource) {
    return $resource('/updateProduct.json', {}, {update:{method:'PUT'}});
});

// mahmed 07.13.2013
services.factory('RemoveProduct', function ($resource) {
    return $resource('/removeProduct/:id.json', {}, {update:{method:'PUT'}});
});

// mahmed 07.13.2013
services.factory('RestoreProduct', function ($resource) {
    return $resource('/restoreProduct/:id.json', {}, {update:{method:'PUT'}});
});

services.factory('ProductCost', function ($resource) {
    return $resource('/programs/:productId/productcost.json', {}, {});
});

services.factory('AllProductCost', function ($resource) {
    return $resource('/allproductcost.json', {}, {});
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
//It populate all programs with regimens
services.factory('ReportRegimenPrograms', function ($resource) {
    return $resource('/reports/regimenPrograms.json', {}, {});
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
/*services.factory('UserDefaultSupervisoryNode', function($resource){
   return $resource('/reports/user/default-supervisory-node.json',{},{});
});*/
services.factory('ProgramListBySupervisoryNodes', function ($resource) {
    return $resource('/reports/supervisory-nodes/programs.json', {}, {});
});
services.factory("FacilitiesByProgramAndRequisitionGroupParams",function($resource)  {
    return   $resource('/reports/facilities/supervisory-node/:supervisoryNodeId/program/:programId/schedule/:scheduleId.json', {}, {});
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

services.factory('RequisitionGroupsBySupervisoryNodeProgramSchedule', function($resource){
    return $resource('/reports/reporting_groups_by_supervisory_node_program_schedule.json', {}, {});
});

services.factory('StockedOutFacilitiesByRequisitionGroup', function($resource){
    return $resource('/dashboard/requisitionGroup/:rgroupId/program/:programId/period/:periodId/product/:productId/stockedOutFacilities.json',{},{});

});
services.factory('Alerts', function($resource){
    return $resource('/dashboard/alerts.json',{},{});
});
services.factory('NotificationAlerts', function($resource) {
    return $resource('/dashboard/notification/alerts.json', {}, {});
});
services.factory('DashboardNotificationsDetail', function($resource){
   return $resource('/dashboard/notifications/:alertId/:detailTable.json',{},{});
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
