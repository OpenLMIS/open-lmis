var services = angular.module('openlmis.services', ['ngResource']);

services.factory('Program', function ($resource) {
    return $resource('/admin/programs.json', {}, {});
});

services.factory('RnRColumnList', function ($resource) {
    return $resource('/program/:programId/rnr-template.json', {}, {});
});

services.factory('ProgramRnRColumnList', function ($resource) {
    return $resource('/logistics/rnr/:programId/columns.json', {}, {});
});

services.factory('Facility', function ($resource) {
    return $resource('/admin/facility/:id.json', {}, {});
});

services.factory('User', function ($resource) {
    return $resource('/user.json', {}, {});
});

services.factory('SearchUser', function ($resource) {
  return $resource('/users.json?param=:param', {}, {});
});

services.factory('Users', function ($resource) {
  return $resource('/users.json', {}, {});
});

services.factory('User', function($resource) {
  return $resource('/users/:id.json', {}, {update: {method:'PUT'}});
});

services.factory('UserById', function ($resource) {
  return $resource('/admin/user/:id.json', {}, {});
});

services.factory('UpdateUserPassword', function ($resource) {
  return $resource('/user/updatePassword.json', {}, {});
});


services.factory('UserFacilityList', function ($resource) {
    return $resource('/logistics/user/facilities.json', {}, {});
});

//todo add right/operation code as param
services.factory('UserSupportedProgramInFacilityForAnOperation', function ($resource) {
    return $resource('/logistics/facility/:facilityId/user/programs.json', {}, {});
});

services.factory('RequisitionHeader', function ($resource) {
    return $resource('/logistics/facility/:facilityId/requisition-header.json', {}, {});
});

services.factory('FacilityReferenceData', function ($resource) {
    return $resource('/admin/facility/reference-data.json', {}, {});
});

services.factory('AllFacilities', function ($resource) {
    return $resource('/admin/facilities.json', {}, {});
});

services.factory('Rights', function ($resource) {
    return $resource('/rights.json', {}, {});
});

services.factory('Role', function ($resource) {
    return $resource('/roles/:id.json', {}, {update: {method:'PUT'}});
});

services.factory('Roles', function ($resource) {
    return $resource('/roles.json', {}, {});
});

services.factory('UserSupervisedProgramList', function ($resource) {
    return $resource('/create/requisition/supervised/programs.json', {}, {})
});

services.factory('UserSupervisedFacilitiesForProgram', function ($resource) {
    return $resource('/create/requisition/supervised/:programId/facilities.json', {}, {})
});

services.factory('ReferenceData', function ($resource) {
    return $resource('/reference-data/currency.json', {}, {});
});

services.factory('Requisitions', function($resource) {
    return $resource('/requisitions/:id/:operation.json', {}, {update : {method:'PUT'}});
});

services.factory('Requisition', function($resource) {
  return $resource('/requisitions.json', {}, {});
});

services.factory('RequisitionById', function ($resource) {
  return $resource('/requisitions/:id.json', {}, {});
});

services.factory('RequisitionForApproval', function($resource) {
  return $resource('/requisitions-for-approval.json', {}, {});
});

services.factory('RequisitionForApprovalById', function($resource) {
  return $resource('/requisitions-for-approval/:id.json', {}, {});
});


services.factory('LossesAndAdjustmentsReferenceData', function($resource) {
  return $resource('/requisitions/lossAndAdjustments/reference-data.json', {}, {})
});

services.factory('Schedules', function ($resource) {
    return $resource('/schedules.json', {});
});

services.factory('Schedule', function ($resource) {
  return $resource('/schedules/:id.json', {}, {update: {method:'PUT'}});
});

services.factory('Periods', function ($resource) {
  return $resource('/schedules/:scheduleId/periods.json', {}, {});
});

services.factory('PeriodsForFacilityAndProgram', function ($resource) {
  return $resource('/logistics/facility/:facilityId/program/:programId/periods.json', {}, {});
});

services.factory('Period', function ($resource) {
  return $resource('/periods/:id.json', {}, {});
});

services.factory('SupportedUploads', function ($resource) {
  return $resource('/supported-uploads.json', {}, {});
});

services.factory('ForgotPassword', function ($resource) {
  return $resource('/forgot-password.json', {}, {});
});

services.factory('FacilityApprovedProducts', function ($resource) {
  return $resource('/facilityApprovedProducts/facility/:facilityId/program/:programId/nonFullSupply.json', {}, {});
});


services.factory('RequisitionLineItem', function ($resource) {
  return $resource('/logistics/requisition/lineItem.json', {}, {});
});

services.factory('SearchFacilitiesByCodeOrName', function ($resource) {
  return $resource('/facilitiesByCodeOrName.json', {}, {});
});



