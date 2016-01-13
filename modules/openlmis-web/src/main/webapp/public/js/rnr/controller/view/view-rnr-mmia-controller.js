function ViewRnrMmiaController($scope, $route, Requisitions, messageService, downloadPdfService) {
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

    $(".btn-download").hide();
    $scope.loadMmiaDetail = function () {
        Requisitions.get({id: $route.current.params.rnr, operation: "skipped"}, function (data) {
            $scope.rnr = data.rnr;

            $scope.year = data.rnr.period.stringYear;

            $scope.initMonth();
            $scope.initProduct();
            $scope.initRegime();
            $scope.initPatient();

            parseSignature($scope.rnr.rnrSignatures);

            downloadPdfService.init($scope, $scope.rnr.id);
        });
    };

    function parseSignature(signatures) {
        _.forEach(signatures, function (signature) {
            if (signature.type == "SUBMITTER") {
                $scope.submitterSignature = signature.text;
            } else if (signature.type == "APPROVER") {
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

        $scope.adult = fullSupplyLineItems.slice(0, 12);
        $scope.children = fullSupplyLineItems.slice(12, 22);
        $scope.other = fullSupplyLineItems.slice(22, 24);
    };

    $scope.initPatient = function () {
        var patientQuantifications = $scope.rnr.patientQuantifications;
        var openlmisMessageMap = {
            "New": "new",
            "Maintenance": "maintenance",
            "Alteration": "alteration",
            "PTV": "ptv",
            "PPE": "ppe",
            "Total Dispensed": "dispensed",
            "Total Patients": "total",
            "Novos": "new",
            "Manutenção": "maintenance",
            "Alteração": "alteration",
            "Total de Meses dispensados": "dispensed",
            "Total de pacientes em TARV na US": "total"
        };

        for (var i = 0; i < patientQuantifications.length; i++) {
            var item = patientQuantifications[i];
            item.category = "view.rnr.mmia.patient." + openlmisMessageMap[item.category];
        }
    };

    var formatExpirationDate = function (theOneItem) {
        if (theOneItem.expirationDate) {
            var splitDate = theOneItem.expirationDate.split('/');
            var yearNumber = splitDate[2];
            var monthNumber = splitDate[1];

            var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            ];

            theOneItem.expirationDate = monthNames[monthNumber - 1] + " " + yearNumber;
        }
    };


    $scope.initRegime = function () {
        var regimes = $scope.rnr.regimenLineItems;

        $scope.regimeAdult = regimes.slice(0, 8);
        $scope.regimeChildren = regimes.slice(8, 18);
        calculateRegimeTotal(regimes);
    };

    var calculateRegimeTotal = function (regimes) {
        for (var i = 0; i < regimes.length; i++) {
            $scope.regimeTotal += regimes[i].patientsOnTreatment;
        }
    };

}
