function ViewRnrViaDetailController($scope, $route, $location, Requisitions, downloadPdfService, downloadSimamService) {
    $scope.pageSize = 20;
    $scope.currentPage = 1;
    $scope.rnrItemsVisible = [];
    $scope.rnr = [];

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadRequisitionDetail();
    });

    function generateEmptyRows(extraRows) {
        for (var i = 0; i < $scope.pageSize - extraRows; i++) {
            $scope.regularRnrItems.push({});
        }
    }

    $(".btn-download-pdf").hide();
    $(".btn-download-simam").hide();

    $scope.loadRequisitionDetail = function () {
        Requisitions.get({id: $route.current.params.rnr, operation:"skipped"}, function (data) {
            $scope.rnr = data.rnr;

            populateKitItems(data.rnr.fullSupplyLineItems);

            var extraRows = $scope.regularRnrItems.length % $scope.pageSize;
            if ($scope.regularRnrItems.length === 0) {
                generateEmptyRows(0);
            } else {
                generateEmptyRows(extraRows);
                refreshItems();
            }

            parseSignature($scope.rnr.rnrSignatures);

            downloadPdfService.init($scope, $scope.rnr.id);
            downloadSimamService.init($scope, $scope.rnr.id);

            $scope.numPages = $scope.regularRnrItems.length / $scope.pageSize;
        });

    };

    function populateKitItems(rnrItems) {
        $scope.regularRnrItems = [];
        for (var i = 0; i < rnrItems.length; i++) {
            var currentItem = rnrItems[i];
            if (currentItem.isKit) {
                if (currentItem.productCode === 'SCOD10') {
                    $scope.usKitItem = currentItem;
                } else if (currentItem.productCode === 'SCOD12') {
                    $scope.apeKitItem = currentItem;
                }
            } else {
                $scope.regularRnrItems.push(currentItem);
            }
        }
    }

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
            $scope.rnrItemsVisible.push($scope.regularRnrItems[i + index]);
        }
    }

    $scope.$watch("currentPage", function () {
        $location.search("page", $scope.currentPage);
    });
}
