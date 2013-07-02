/**
 * User: Elias
 * Date: 5/4/13
 * Time: 2:33 PM
 */

//var services = angular.module('openlmis.services', ['ngResource']);

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

services.factory('ProductCategories', function($resource){
    return $resource('/reports/productCategories.json', {}, {});
}) ;

services.factory('SummaryReport', function($resource){
    return $resource('/reports/summary.json', {}, {});
}) ;

services.factory('NonReportingFacilities', function($resource){
    return $resource('/reports/non_reporting.json', {}, {});
});

services.factory('RequisitionGroups', function($resource){
    return $resource('/reports/rgroups.json', {}, {});
});

services.factory('RequisitionGroupsByProgramSchedule', function($resource){
    return $resource('/reports/reporting_groups_by_program_schedule.json', {}, {});
});

services.factory('AdjustmentSummaryReport', function ($resource) {
    return $resource('/reports/reportdata/adjustmentSummary.json', {}, {});
});

services.factory('AdjustmentTypes', function($resource){
    return $resource('/reports/adjustmentTypes.json', {}, {});
});

services.factory('StockedOutFacilities',function($resource){
   return $resource('/reports/stockedOut.json',{},{});
});

services.factory('DistrictConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/districtConsumption.json', {}, {});
});

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

services.factory('FacilityList', function ($resource) {
    return $resource('/reports/facilitylist.json', {}, {});
});

services.factory('GeographicZones', function ($resource) {
    return $resource('/reports/geographicZones.json', {}, {});
});

services.factory('GeographicZone', function ($resource) {
    return $resource('/geographicZone/:id.json', {}, {update:{method:'PUT'}});
});

services.factory('CreateGeographicZone', function ($resource) {
    return $resource('/geographicZone/insert.json', {}, {insert:{method:'POST'}});
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

//Parameters are passed for searching geographic zones.
services.factory('GeographicZoneList', function ($resource) {
    return $resource('/geographicZones.json', {}, {});
});

services.factory('GeographicZoneCompleteList', function ($resource) {
    return $resource('/geographicZone/getList.json', {}, {});
});