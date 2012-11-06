var services = angular.module('openlmis.services', ['ngResource']);

services.factory('Program', function($resource){
    return $resource('/openlmis/admin/programs/programs.json', {}, {});
});

services.factory('RnRMasterColumnList', function($resource){
    return $resource('/openlmis/admin/rnr/master/columns.json', {}, {});
});


services.factory('RrRProgramTemplate', function($resource){
    return $resource('/openlmis/admin/rnr/master/:programId/columns', {}, {});
});

