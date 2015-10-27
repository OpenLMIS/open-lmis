function ViewRnrMmiaController($scope, $route, Requisitions) {

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadMmiaDetail();
    });

    $scope.loadMmiaDetail = function () {
        Requisitions.get({id: $route.current.params.rnr}, function (data) {
            console.log(data);

            $scope.rnr = data.rnr;

            $scope.adult = [];
            $scope.children = [];
            $scope.other = [];

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

            $scope.regimen = data.rnr.regimenLineItems;

            $scope.regimenTotal = 0;
            $scope.regimenAdult = [];
            $scope.regimenChildren = [];
            for (i = 0; i < 8; i++) {
                $scope.regimenTotal += $scope.regimen[i].patientsOnTreatment;
                $scope.regimenAdult.push($scope.regimen[i]);
            }
            for (i = 8; i < 18; i++) {
                $scope.regimenTotal += $scope.regimen[i].patientsOnTreatment;
                $scope.regimenChildren.push($scope.regimen[i]);
            }

            $scope.patient = [];
            for (i = 0; i < 7; i++) {
                $scope.patient.push($scope.rnr.patientQuantifications[i]);
            }
        });

    };
}
