var services = angular.module('openlmis.services', ['ngResource']);

services.factory('User', function ($resource) {
    return $resource('/user.json', {}, {});
});

services.factory('Program', function ($resource) {
    return $resource('/admin/programs.json', {}, {});
});

services.factory('RnRColumnList', function ($resource) {
    return $resource('/admin/rnr/:programCode/columns.json', {}, {});
});

services.factory('ProgramRnRColumnList', function ($resource) {
    return $resource('/logistics/rnr/:programCode/columns.json', {}, {});
});

services.factory('Facility', function ($resource) {
    return $resource('/admin/facility/:id.json', {}, {});
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
    return $resource('/role.json', {}, {});
});

services.factory('UserSupervisedProgramList', function($resource) {
    return $resource('/create/requisition/supervised/programs.json',{}, {})
});

services.factory('UserSupervisedFacilitiesForProgram', function($resource) {
    return $resource('/create/requisition/supervised/:programId/facilities.json',{}, {})
});