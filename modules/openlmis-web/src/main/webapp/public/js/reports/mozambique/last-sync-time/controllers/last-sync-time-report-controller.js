function LastSyncTimeReportController($scope, $http, GeographicZoneService, CubesGenerateUrlService, CubesGenerateCutParamsService, $filter) {

  $scope.provinces = [];
  $scope.districts = [];
  $scope.tree_data = [];

  $scope.$on('$viewContentLoaded', function () {
    loadGeographicZonesBasedOnUserProfile();
  });

  function loadGeographicZonesBasedOnUserProfile() {
    GeographicZoneService.loadGeographicZone().get({}, function(zoneData) {
      _.forEach(zoneData['geographic-zones'], function(zone) {
        if (zone.levelCode == 'province') {
          $scope.provinces.push(zone);
        } else if (zone.levelCode == 'district') {
          $scope.districts.push(zone);
        }
      });
      loadSyncTimeData();
    });
  }


  function loadSyncTimeData() {
    var cutsParams;
    if($scope.provinces.length > 1) {
      cutsParams = CubesGenerateCutParamsService.generateCutsParams(undefined, undefined, undefined,
          undefined, undefined, undefined, undefined);
    } else if ($scope.districts.length > 1) {
      cutsParams = CubesGenerateCutParamsService.generateCutsParams(undefined, undefined, undefined,
          undefined, undefined, $scope.provinces[0], undefined);
    } else {
      cutsParams = CubesGenerateCutParamsService.generateCutsParams(undefined, undefined, undefined,
          undefined, undefined, $scope.provinces[0], $scope.districts[0]);
    }
    $http.get(CubesGenerateUrlService.generateFactsUrl('vw_sync_time', cutsParams)).success(function(data){
      console.log(data);
    });
  }

}