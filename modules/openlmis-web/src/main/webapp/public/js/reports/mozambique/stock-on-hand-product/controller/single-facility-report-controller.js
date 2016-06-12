function SingleFacilityReportController($scope, $filter, $controller, ProductReportService, FeatureToggleService,$cacheFactory,$timeout) {
    $controller('BaseProductReportController', {$scope: $scope});

    if($cacheFactory.get('keepHistoryInViewRequisitionList') === undefined){
        $scope.cache = $cacheFactory('keepHistoryInViewRequisitionList',{capacity: 10});
    }
    else{
        $scope.cache=$cacheFactory.get('keepHistoryInViewRequisitionList');

        $scope.reportParams.facilityId=$scope.cache.get('facilityId');
        $timeout(function(){
            $('.facility-choose .select2-choice .select2-chosen').html($scope.cache.get('facilityName'));
            $('.district-choose .select2-choice .select2-chosen').html($scope.cache.get('district'));
            $('.province-choose .select2-choice .select2-chosen').html($scope.cache.get('province'));
            $scope.reportParams.endTime=$filter('date')($scope.cache.get('endTime'), "yyyy-MM-dd");
            console.log($scope.cache.get('facilityName')+"**"+$scope.cache.get('district')+"**"+$scope.cache.get('province'))
        }, 1000);
        loadReportAction();
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

        $scope.cache.put('endTime', $scope.reportParams.endTime);
        $scope.cache.put('facilityId',$scope.reportParams.facilityId);
        if (validateFacility()) {
            ProductReportService.loadFacilityReport().get(params, function (data) {
                $scope.reportData = data.products;
            });
        }
    };

    function validateFacility() {
        $scope.invalid = !$scope.reportParams.facilityId;
        return !$scope.invalid;
    }

}