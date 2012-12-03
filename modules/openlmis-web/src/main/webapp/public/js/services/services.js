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

services.factory('FacilitySupportedPrograms', function ($resource) {
    return $resource('/logistics/facility/:facilityCode/programs.json', {}, {});
});

services.factory('RequisitionHeader', function ($resource) {
    return $resource('/logistics/facility/:code/requisition-header.json', {}, {});
});

services.factory('FacilityReferenceData', function ($resource) {
    return $resource('/admin/facility/reference-data.json', {}, {});
});
