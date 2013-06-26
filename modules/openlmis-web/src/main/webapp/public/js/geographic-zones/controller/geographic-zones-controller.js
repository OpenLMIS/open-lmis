function GeographicZonesController($scope, $routeParams, $location, GeographicZones, CreateGeographicZone, GeographicLevels, GetGeographicZone, SetGeographicZone) {
    $scope.geographicZoneNameInvalid = false;
    $scope.geographicZone = {};


    if ($routeParams.geographicZoneId) {
        GetGeographicZone.get({id: $routeParams.geographicZoneId}, function (data) {
            $scope.geographicZone = data.geographicZone;
        }, {});
    }

    GeographicLevels.get(function (data) {
        $scope.geographicLevels = data.geographicLevels;
    })

    GeographicZones.get(function (data) {
        $scope.geographicZones = data.zones;
    });

    $scope.saveGeographicZone = function () {
        var successHandler = function (response) {
            $scope.geographicZone = response.geographicZone;
            $scope.showError = false;
            $scope.error = "";
            $scope.$parent.message = response.success;
            $scope.$parent.geographicZoneId = $scope.geographicZone.id;
            $location.path('');
        };

        var errorHandler = function (response) {
            $scope.showError = true;
            $scope.error = response.data.error;
        };

        if ($scope.geographicZone.id) {
            SetGeographicZone.save($scope.geographicZone,successHandler,errorHandler);
        } else {
            CreateGeographicZone.save($scope.geographicZone,successHandler,errorHandler);
        }
        return true;
    };

    $scope.validateGeographicZoneName = function () {
        $scope.geographicZoneNameInvalid = $scope.geographicZone.name != null && $scope.geographicZone.name.trim().indexOf(' ') >= 0;
    };


}

