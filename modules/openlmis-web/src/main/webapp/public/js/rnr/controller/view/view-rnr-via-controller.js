function ViewRnrViaDetailController($scope, $route, $filter, $location, Requisitions, downloadPdfService, downloadSimamService) {
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

            $scope.rnr.submittedDate = $filter('date')(data.rnr.clientSubmittedTime,'dd/MM/yyyy');

            $scope.isEmergency = data.rnr.emergency;
            
            populateKitItems(_.sortBy($scope.rnr.fullSupplyLineItems, 'productCode'));
            
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

            initRequisitionHeaderValue();
        });

    };

    function setPeriodDate() {
        var displayStartDate,displayEndDate;
          if ($scope.rnr.actualPeriodStartDate && $scope.rnr.actualPeriodEndDate) {
              displayStartDate = $scope.rnr.actualPeriodStartDate;
              displayEndDate = $scope.rnr.actualPeriodEndDate;
          } else {
              displayStartDate = $scope.rnr.period.startDate;
              displayEndDate = $scope.rnr.period.endDate;
          }
        $scope.displayStartDate = $filter('date')(displayStartDate,'dd/MM/yyyy');
        $scope.displayEndDate = $filter('date')(displayEndDate,'dd/MM/yyyy');
    }

    function populateKitItems(rnrItems) {
        $scope.regularRnrItems = [];
        for (var i = 0; i < rnrItems.length; i++) {
            var currentItem = rnrItems[i];
            if (currentItem.isKit) {
                if (currentItem.productCode === '26A01') {
                    $scope.usKitItem = currentItem;
                } else if (currentItem.productCode === '26A02') {
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

    function initRequisitionHeaderValue() {
        if ($scope.isEmergency) {
            $scope.displayStartDate = '\\';
            $scope.displayEndDate = '\\';

            $scope.apeKitReceived = '\\';
            $scope.apeKitDispensed = '\\';
            $scope.usKitReceived = '\\';
            $scope.usKitDispensed = '\\';

            $scope.consultationNumber = '\\';
        } else {
            setPeriodDate();

            $scope.apeKitReceived = $scope.apeKitItem === undefined ? '' : $scope.apeKitItem.quantityReceived;
            $scope.apeKitDispensed = $scope.apeKitItem === undefined ? '' : $scope.apeKitItem.quantityDispensed;
            $scope.usKitReceived = $scope.usKitItem === undefined ? '' : $scope.usKitItem.quantityReceived;
            $scope.usKitDispensed = $scope.usKitItem === undefined ? '' : $scope.usKitItem.quantityDispensed;

            $scope.consultationNumber = $scope.rnr.patientQuantifications[0].total;
        }
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
