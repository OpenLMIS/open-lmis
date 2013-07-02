function GeographicZonesSearchController($scope, $location, GeographicZones, navigateBackService,GeographicZoneList,GetGeographicZone, GeographicZoneCompleteList) {
  $scope.$on('$viewContentLoaded', function() {
    $scope.$apply($scope.query = navigateBackService.query);
    $scope.showGeographicZonesSearchResults('searchGeographicZone');
  });
  $scope.previousQuery = '';

  $scope.showGeographicZonesSearchResults = function (id) {
    var query = document.getElementById(id).value;
    $scope.query = query;

    var len = (query == undefined) ? 0 : query.length;

    if (len >= 3) {
      if ($scope.previousQuery.substr(0, 3) == query.substr(0, 3)) {
        $scope.previousQuery = query;
        filterGeographicZonesByName(query);
        return true;
      }
      $scope.previousQuery = query;
        GeographicZoneList.get({param:$scope.query.substr(0, 3)}, function (data) {
        $scope.geographicZonesList = data.geographicZoneList;
        filterGeographicZonesByName(query);
      }, {});

      return true;
    } else {
        $scope.filteredGeographicZones = GeographicZoneCompleteList.geographicZones;
        return true;
    }
  };

  $scope.editGeographicZones = function (id) {
    var data = {query: $scope.query};
    navigateBackService.setData(data);
    $location.path('edit/' + id);
  };


  $scope.clearSearch = function () {
    $scope.query = "";
    $scope.resultCount = 0;
    angular.element("#searchGeographicZone").focus();
  };

  var filterGeographicZonesByName = function (query) {
    $scope.filteredGeographicZones = [];
    query = query || "";

    angular.forEach($scope.geographicZonesList, function (geographicZone) {

      if (geographicZone.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
        $scope.filteredGeographicZones.push(geographicZone);
      }
    });
    $scope.resultCount = $scope.filteredGeographicZones.length;
  };
}