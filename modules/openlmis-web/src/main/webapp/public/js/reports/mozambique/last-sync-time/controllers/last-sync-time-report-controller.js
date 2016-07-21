function LastSyncTimeReportController($scope, GeographicZoneService, $q) {

  $scope.provinces = [];
  $scope.districts = [];

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
    });
  }

}