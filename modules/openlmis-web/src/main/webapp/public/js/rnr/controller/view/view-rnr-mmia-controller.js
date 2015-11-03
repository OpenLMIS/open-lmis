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
        Requisitions.get({id: $route.current.params.rnr}, function (data) {
            $scope.rnr = data.rnr;

            $scope.year = data.rnr.period.stringYear;

            $scope.initMonth();
            $scope.initProduct();
            $scope.initRegime();
            $scope.initPatient();
        });

    };

    $scope.initMonth = function () {
        var month = "month." + $scope.rnr.period.stringEndDate.substr(3, 2);
        $scope.month = messageService.get(month);

    };

    $scope.initProduct = function () {
        var nonSkippedLineItems = $scope.rnr.fullSupplyLineItems;
        for (var theOneItem in nonSkippedLineItems) {
            formatExpirationDate(theOneItem);
        }

        $scope.adult = nonSkippedLineItems.slice(0,11);
        $scope.children = nonSkippedLineItems.slice(12,21);
        $scope.other = nonSkippedLineItems.slice(22,23);
    };

    var formatExpirationDate = function(theOneItem) {
        if (theOneItem.expirationDate) {
            var splitDate = theOneItem.expirationDate.split('/');
            theOneItem.expirationDate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
        }
    };

    $scope.initRegime = function () {
        var regimes = $scope.rnr.regimenLineItems;

        $scope.regimeAdult = regimes.slice(0, 7);
        $scope.regimeChildren = regimes.slice(8, 17);
        calculateRegimeTotal(regimes);
    };

    var calculateRegimeTotal = function(regimes){
        for(var theOneItem in regimes){
            $scope.regimeTotal += theOneItem.patientsOnTreatment;
        }
    };

    $scope.initPatient = function () {
        for (var i = 0; i < 7; i++) {
            $scope.patient.push($scope.rnr.patientQuantifications[i]);
        }
    };
}
