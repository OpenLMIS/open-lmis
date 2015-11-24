function ViewRnrViaDetailController($scope, $route, $location, Requisitions) {
    $scope.pageSize = 20;
    $scope.currentPage = 1;
    $scope.rnrItemsVisible = [];
    $scope.rnr = [];

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadRequisitionDetail();
    });

    function generateEmptyRows(extraRows) {
        for (var i = 0; i < $scope.pageSize - extraRows; i++) {
            $scope.rnrItems.push({});
        }
    }

    $scope.loadRequisitionDetail = function () {
        Requisitions.get({id: $route.current.params.rnr, operation:"skipped"}, function (data) {
            $scope.rnr = data.rnr;
            $scope.rnrItems = data.rnr.fullSupplyLineItems;

            var extraRows = $scope.rnrItems.length % $scope.pageSize;
            if ($scope.rnrItems.length === 0) {
                generateEmptyRows($scope.pageSize);
            }
            if (extraRows !== 0) {
                generateEmptyRows(extraRows);
            }

            $scope.numPages = $scope.rnrItems.length / $scope.pageSize;

            refreshItems();
            parseSignature($scope.rnr.rnrSignatures);
        });

    };

    function parseSignature(signatures){
        _.forEach(signatures,function(signature){
           if(signature.type == "SUBMITTER"){
               $scope.submitterSignature = signature.text;
           } else if (signature.type == "APPROVER"){
               $scope.approverSignature = signature.text;
           }
        });
    }

    $scope.$on('$routeUpdate', refreshItems);
    function refreshItems() {
        var index = ($scope.currentPage - 1) * $scope.pageSize;
        $scope.rnrItemsVisible = [];
        for (var i = 0; i < $scope.pageSize; i++) {
            $scope.rnrItemsVisible.push($scope.rnrItems[i + index]);
        }
    }

    $scope.$watch("currentPage", function () {
        $location.search("page", $scope.currentPage);
    });
}
