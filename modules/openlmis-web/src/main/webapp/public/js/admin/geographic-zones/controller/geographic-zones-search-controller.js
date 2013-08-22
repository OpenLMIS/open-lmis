function GeographicZonesSearchController($scope, $location, navigateBackService, GeographicZoneCompleteList) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.$apply($scope.query = navigateBackService.query);
        $scope.showGeographicZonesSearchResults('searchGeographicZone');
    });
    $scope.previousQuery = '';

    $scope.showGeographicZonesSearchResults = function (id) {

        GeographicZoneCompleteList.get(function (data) {
            $scope.filteredGeographicZones = data.geographicZones;
            $scope.geographicZonesList = $scope.filteredGeographicZones;
        });

        var query = document.getElementById(id).value;
        $scope.query = query;

        var len = (query == undefined) ? 0 : query.length;

        filterGeographicZonesByName(query);
        return true;
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
        query = query || "";

        if (query.length == 0) {
            $scope.filteredGeographicZones = $scope.geographicZonesList;
        }
        else {
            $scope.filteredGeographicZones = [];
            angular.forEach($scope.geographicZonesList, function (geographicZone) {

                if (geographicZone.name.toLowerCase().indexOf(query.trim().toLowerCase()) >= 0) {
                    $scope.filteredGeographicZones.push(geographicZone);
                }
            });
            $scope.resultCount = $scope.filteredGeographicZones.length;
        }
    };

    $scope.filterGeographicZones = function (id) {
        var query = document.getElementById(id).value;
        $scope.query = query;
        filterGeographicZonesByName(query);
    };
}