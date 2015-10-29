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
console.log($scope.rnr);
            $scope.year = data.rnr.period.name.substr(3, 4);

            $scope.initMonth();

            $scope.initProduct();
            $scope.initRegime();
            $scope.initPatient();
        });

    };

    $scope.initMonth = function () {
        var month = "month." + $scope.rnr.period.name.substr(0, 3);
        $scope.month = messageService.get(month);

    };

    $scope.initProduct = function () {
        var i = 0;

        for (i = 0; i < 12; i++) {
            $scope.adult.push($scope.rnr.nonSkippedLineItems[i]);
        }

        for (i = 12; i < 22; i++) {
            $scope.children.push($scope.rnr.nonSkippedLineItems[i]);
        }

        for (i = 22; i < 24; i++) {
            $scope.other.push($scope.rnr.nonSkippedLineItems[i]);
        }
    };

    $scope.initRegime = function () {
        var regime = $scope.rnr.regimenLineItems;
        var i = 0;

        for (i = 0; i < 8; i++) {
            $scope.regimeTotal += regime[i].patientsOnTreatment;
            $scope.regimeAdult.push(regime[i]);
        }

        for (i = 8; i < 18; i++) {
            $scope.regimeTotal += regime[i].patientsOnTreatment;
            $scope.regimeChildren.push(regime[i]);
        }
    };

    $scope.initPatient = function () {
        var i = 0;

        for (i = 0; i < 7; i++) {
            $scope.patient.push($scope.rnr.patientQuantifications[i]);
        }
    };
}
