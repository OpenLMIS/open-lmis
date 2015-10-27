function SingleProductReportController($scope, SingleProductReportService) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadGeographicZone();
    });

    $scope.loadProducts = function () {
        SingleProductReportService.loadAllProducts().get({}, function (data) {
            $scope.products = data.products;
        }, function () {
        });
    };

    $scope.loadGeographicZone = function () {
        SingleProductReportService.loadGeographicLevel().get({}, function (data) {
            $scope.geographicZoneLevel = data.zones;

            SingleProductReportService.loadGeographicZone().get({}, function (data) {

                if(!$scope.geographicZoneLevel){
                    return;
                }

                var provinceLevel = $scope.geographicZoneLevel.find(function (level) {
                    return level.code === "Province";
                });

                var districtLevel = $scope.geographicZoneLevel.find(function(level) {
                    return level.code === "District";
                });

                $scope.provinces = [];
                $scope.districts = [];
                for(var geoZone in data.zones){
                    if (geoZone.levelId == provinceLevel.id){
                        $scope.provinces.add(geoZone);
                    } else if (geoZone.levelId == districtLevel.id){
                        $scope.districts.add(geoZone);
                    }
                }
            }, function () {});

        }, function () {
        });
    };
}
