
function ViewRnrDetailController($scope, $route, Requisitions) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.loadRequisitionDetail();
    });

    $scope.loadRequisitionDetail = function () {
        Requisitions.get({id: $route.current.params.rnr}, function (data) {
            $scope.rnr = data.rnr;
            console.log(data);
        });

    };

}
