var services = angular.module('openlmis.services', ['ngResource']);

services.factory('Program', function ($resource) {
    return $resource('/admin/programs/programs.json', {}, {});
});

services.factory('RnRColumnList', function ($resource) {
    return $resource('/admin/rnr/:programId/columns.json', {}, {});
});

services.factory('Facility', function ($resource) {
    return $resource('/facilities/all.json', {}, {});
});

services.factory('FacilitySupportedPrograms', function ($resource) {
    return $resource('/admin/programs/programsForFacility.json', {}, {});
});

