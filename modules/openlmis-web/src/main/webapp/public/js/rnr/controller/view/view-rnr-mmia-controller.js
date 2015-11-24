function ViewRnrMmiaController($scope, $route, Requisitions, messageService) {
    $scope.adult = [];
    $scope.children = [];
    $scope.other = [];

    $scope.regimeTotal = 0;
    $scope.regimeAdult = [];
    $scope.regimeChildren = [];

    $scope.patient = [];

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadMmiaDetail();
    });
    $scope.$on('messagesPopulated', function () {
        $scope.initMonth();
    });

    $scope.loadMmiaDetail = function () {
        Requisitions.get({id: $route.current.params.rnr,operation:"skipped"}, function (data) {
            $scope.rnr = data.rnr;

            $scope.year = data.rnr.period.stringYear;

            $scope.initMonth();
            $scope.initProduct();
            $scope.initRegime();

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

    $scope.initMonth = function () {
        var month = "month." + $scope.rnr.period.stringEndDate.substr(3, 2);
        $scope.month = messageService.get(month);

    };

    $scope.initProduct = function () {
        var fullSupplyLineItems = $scope.rnr.fullSupplyLineItems;

        for (var i = 0; i < fullSupplyLineItems.length; i++) {
            formatExpirationDate(fullSupplyLineItems[i]);
        }

        $scope.adult = fullSupplyLineItems.slice(0,12);
        $scope.children = fullSupplyLineItems.slice(12,22);
        $scope.other = fullSupplyLineItems.slice(22,24);
    };

    var formatExpirationDate = function(theOneItem) {
        if (theOneItem.expirationDate) {
            var splitDate = theOneItem.expirationDate.split('/');
            theOneItem.expirationDate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
            theOneItem.expirationDate = new Date(theOneItem.expirationDate);
        }
    };

    $scope.initRegime = function () {
        var regimes = $scope.rnr.regimenLineItems;

        $scope.regimeAdult = regimes.slice(0, 8);
        $scope.regimeChildren = regimes.slice(8, 18);
        calculateRegimeTotal(regimes);
    };

    var calculateRegimeTotal = function(regimes){
        for(var i = 0; i < regimes.length; i++) {
            $scope.regimeTotal += regimes[i].patientsOnTreatment;
        }
    };

}
