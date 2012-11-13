var services = angular.module('openlmis.services', ['ngResource']);

services.factory('Program', function ($resource) {
    return $resource('/admin/programs/all.json', {}, {});
});

services.factory('RnRColumnList', function ($resource) {
    return $resource('/admin/rnr/:programId/columns.json', {}, {});
});

services.factory('Facility', function ($resource) {
    return $resource('/logistics/facilities.json', {}, {});
});

services.factory('FacilitySupportedPrograms', function ($resource) {
    return $resource('/logistics/programs/programsForFacility.json', {}, {});
});

services.factory('RequisitionHeader', function ($resource) {
    return $resource('/logistics/facility/:code/requisition-header.json', {}, {});
});

