function SingleFacilityReportController($scope, $filter, $controller, ProductReportService, FeatureToggleService, $cacheFactory, $timeout) {
    $controller('BaseProductReportController', {$scope: $scope});

    if ($cacheFactory.get('keepHistoryInStockOnHandPage') === undefined) {
        $scope.cache = $cacheFactory('keepHistoryInStockOnHandPage', {capacity: 10});
    }
    else {
        $scope.cache = $cacheFactory.get('keepHistoryInStockOnHandPage');
        if ($scope.cache.get('saveDataOfStockOnHand') === "yes") {
            $timeout(function waitHistorySelectShow() {
                if ($('.select2-container .select2-choice .select2-chosen').html() !== undefined) {
                    $('.facility-choose .select2-choice .select2-chosen').html($scope.cache.get('facilityName'));
                    $('.district-choose .select2-choice .select2-chosen').html($scope.cache.get('district'));
                    $('.province-choose .select2-choice .select2-chosen').html($scope.cache.get('province'));
                    $scope.reportParams.endTime = $filter('date')($scope.cache.get('endTime'), "yyyy-MM-dd");
                    $scope.reportParams.facilityId = $scope.cache.get('facilityId');
                    loadReportAction();
                } else {
                    $timeout(waitHistorySelectShow, 1000);
                }
            }, 1000);
        }
    }

    $scope.$on('$viewContentLoaded', function () {

        FeatureToggleService.get({key: 'view.stock.movement'}, function (result) {
            $scope.viewStockMovementToggle = result.key;
        });

        $scope.loadHealthFacilities();
    });

    $scope.loadReport = loadReportAction;
    
    function loadReportAction() {
        var params = {endTime: $filter('date')($scope.reportParams.endTime, "yyyy-MM-dd HH:mm:ss")};
        params.facilityId = $scope.reportParams.facilityId;

        if (validateFacility()) {
            ProductReportService.loadFacilityReport().get(params, function (data) {
                $scope.reportData = data.products;
            });
        }
    }
    
    $scope.saveHistory = function () {
        $scope.cache.put('endTime', $scope.reportParams.endTime);
        $scope.cache.put('facilityId', $scope.reportParams.facilityId);
    };
    
    function validateFacility() {
        $scope.invalid = !$scope.reportParams.facilityId;
        return !$scope.invalid;
    }

}