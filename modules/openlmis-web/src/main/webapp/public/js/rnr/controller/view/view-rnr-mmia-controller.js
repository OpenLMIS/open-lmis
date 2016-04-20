function ViewRnrMmiaController($scope, $route, Requisitions, messageService, downloadPdfService, downloadSimamService) {
    $scope.rnrLineItems = [];

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

    $(".btn-download-pdf").hide();
    $(".btn-download-simam").hide();
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
            downloadSimamService.init($scope, $scope.rnr.id);
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

  function addEmptyLine(fullSupplyLineItems){

        fullSupplyLineItems = _.groupBy(fullSupplyLineItems, function (item) {
            return item.categoryName;
        });

        fullSupplyLineItems.Adult.push({categoryName: 'Adult'});
        fullSupplyLineItems.Adult.push({categoryName: 'Adult'});
        fullSupplyLineItems.Children.push({categoryName: 'Children'});
        fullSupplyLineItems.Solution.push({categoryName: 'Solution'});

        return $scope.rnrLineItems.concat(fullSupplyLineItems.Adult, fullSupplyLineItems.Children, fullSupplyLineItems.Solution);

    }

    $scope.initProduct = function () {
        var fullSupplyLineItems = _.sortBy($scope.rnr.fullSupplyLineItems, 'productCode');

        for (var i = 0; i < fullSupplyLineItems.length; i++) {
            formatExpirationDate(fullSupplyLineItems[i]);
        }

        $scope.rnrLineItems = addEmptyLine(fullSupplyLineItems);
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
        var regimens = $scope.rnr.regimenLineItems;

        $scope.regimeAdult = _.filter(regimens, function(regimen){ return regimen.categoryName == "Adults"; });

        $scope.regimeChildren = _.filter(regimens, function(regimen){ return regimen.categoryName == "Paediatrics"; });

        calculateRegimeTotal(regimens);
    };

    var calculateRegimeTotal = function (regimens) {
        for (var i = 0; i < regimens.length; i++) {
            $scope.regimeTotal += regimens[i].patientsOnTreatment;
        }
    };

}
