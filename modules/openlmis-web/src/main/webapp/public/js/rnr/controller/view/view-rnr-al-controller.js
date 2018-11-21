function ViewRnrALController($scope, $route, Requisitions, messageService, Requisitions, downloadPdfService, downloadSimamService) {
  $scope.rnrLineItems = [];
  $scope.regimens =[];
  $scope.regimeTotal = 0;

  $scope.patient = [];

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadALDetail();
  });

  $scope.$on('messagesPopulated', function () {
    $scope.initMonth();
  });

  $(".btn-download-pdf").hide();
  $(".btn-download-simam").hide();
  $scope.loadALDetail = function () {

    Requisitions.get({id: $route.current.params.rnr, operation: "skipped"}, function (data) {
        console.log(data);
        $scope.rnr = data.rnr;
        $scope.year = data.rnr.period.stringYear;

        $scope.initMonth();

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

  var formatDate = function (submitiedDate) {
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
    var regimens = _.groupBy($scope.rnr.regimenLineItems, function (item) {
      return item.categoryName;
    });

    if (regimens.Adults === undefined) {
      regimens.Adults = [];
    }

    if (regimens.Paediatrics === undefined) {
      regimens.Paediatrics = [];
    }

    regimens.Adults.push({categoryName: 'Adults'});
    regimens.Adults.push({categoryName: 'Adults'});

    regimens.Paediatrics.push({categoryName: 'Paediatrics'});
    regimens.Paediatrics.push({categoryName: 'Paediatrics'});

    $scope.regimens = $scope.regimens.concat(regimens.Adults, regimens.Paediatrics);
    calculateRegimeTotal($scope.rnr.regimenLineItems);
  };

  var calculateRegimeTotal = function (regimens) {
    for (var i = 0; i < regimens.length; i++) {
      $scope.regimeTotal += regimens[i].patientsOnTreatment;
    }
  };

}
