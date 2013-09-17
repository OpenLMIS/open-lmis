function GeographicZonesController($scope, $routeParams, $location, GeographicZoneCompleteList, CreateGeographicZone, GeographicLevels, GetGeographicZone, SetGeographicZone) {
    $scope.geographicZoneNameInvalid = false;
    $scope.geographicZone = {};
    $scope.geographicZoneLevelInvalid = false;
    $scope.selectedParent = {};


    if ($routeParams.geographicZoneId) {
        GetGeographicZone.get({id: $routeParams.geographicZoneId}, function (data) {
            $scope.geographicZone = data.geographicZone;

            GetGeographicZone.get({id:$scope.geographicZone.parent.id},function(data){
                $scope.selectedParent = data.geographicZone;
            },{});

        }, {});
    }

    GeographicLevels.get(function (data) {
        $scope.geographicLevels = data.geographicLevels;
    })

    GeographicZoneCompleteList.get(function (data) {
        $scope.geographicZones = data.geographicZones;
    });

    $scope.saveGeographicZone = function () {

        if($scope.geographicZoneLevelInvalid){
            $scope.showError = true;
            return false;
        }

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

        SetGeographicZone.save($scope.geographicZone,successHandler,errorHandler);

        return true;
    };

    $scope.validateGeographicZoneName = function () {
        $scope.geographicZoneNameInvalid = $scope.geographicZone.name != null;
    };

    $scope.validateGeographicZoneLevelParent = function(){
        angular.forEach($scope.geographicZones, function(geoZoneParent) {
            if(geoZoneParent.id == $scope.geographicZone.parent.id){
                $scope.selectedParent = geoZoneParent;
            }
        });
        $scope.geographicZoneLevelInvalid = $scope.geographicZone.level.id == $scope.selectedParent.level.id;
    };

    $scope.validateGeographicZoneLevel = function(){
        $scope.geographicZoneLevelInvalid = $scope.geographicZone.level.id == $scope.selectedParent.level.id;
    }


}

