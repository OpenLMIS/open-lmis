function ViewRnrPTVController($scope, $route, Requisitions, messageService, DateFormatService, downloadPdfService, downloadSimamService) {

  $scope.rnrLineItems = [];

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadALDetail();
  });

  $scope.$on('messagesPopulated', function () {
    $scope.loadALDetail();
  });

  $(".btn-download-pdf").hide();
  $(".btn-download-simam").hide();
  $scope.loadALDetail = function () {

    Requisitions.get({id: $route.current.params.rnr, operation: "skipped"}, function (data) {
        $scope.rnr = data.rnr;
        $scope.year = $scope.rnr.period.stringEndDate.substr(6, 4);
        $scope.productNumber = $scope.rnr.fullSupplyLineItems.length;
        $scope.serviceNumber = $scope.rnr.fullSupplyLineItems[0].serviceItems.length;

        $scope.initPatient();
        $scope.initPeriod();
        $scope.initMonth();
        $scope.initDate();
        $scope.initContent();

        parseSignature($scope.rnr.rnrSignatures);

        downloadPdfService.init($scope, $scope.rnr.id);
        downloadSimamService.init($scope, $scope.rnr.id);
    });
  };

  $scope.initPatient = function () {
    _.map($scope.rnr.regimenLineItems, function (regimen) {
      if (regimen.categoryName == 'Adults') {
        $scope.adults = regimen.patientsOnTreatment;
      }
      if (regimen.categoryName == 'Paediatrics') {
        $scope.children = regimen.patientsOnTreatment;
      }
    });
  };

  $scope.initPeriod = function () {
    $scope.startDate = $scope.rnr.period.stringStartDate;
    $scope.endDate = $scope.rnr.period.stringEndDate;
  };

  $scope.initContent = function () {
    var content = [];
    var head = [messageService.get('pdf.ptv.initial.stock')];
    content.push(head);

    _.map($scope.rnr.fullSupplyLineItems, function (product) {
      var row = [];
      row.push(product.beginningBalance);
      _.map(product.serviceItems, function (service) {
        arrayAdd(head, service.name);
        row.push(service.patientsOnTreatment);
      });
      // for (var i = 0; i < $scope.serviceNumber; i++) {
      //   row.push("");
      // }
      row.push(product.totalServiceQuantity);
      row.push(product.quantityReceived);
      row.push(product.totalLossesAndAdjustments);
      row.push(product.stockInHand);
      content.push(row);
    });

    // for (var i = 0; i < $scope.serviceNumber; i++) {
    //   head.push("");
    // }
    head.push(messageService.get('pdf.ptv.total.ptv'));
    head.push(messageService.get('pdf.ptv.entradas'));
    head.push(messageService.get('pdf.ptv.losses.adjustments'));
    head.push(messageService.get('pdf.ptv.final.stock'));
    $scope.content = format(content);
  };

  $scope.initMonth = function () {
    var month = "month." + $scope.rnr.period.stringEndDate.substr(3, 2);
    $scope.month = messageService.get(month);
  };

  $scope.initDate = function () {
    $scope.submittedDate = DateFormatService.formatDateWithLocaleNoDay($scope.rnr.submittedDate);
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

  function arrayAdd(array, value) {
    if (array.indexOf(value) === -1) {
      array.push(value);
    }
  }

  function format(content) {
    var result = [];
    for (var j = 0; j < content[0].length; j++) {
      var row = [];
      for (var i = 0; i < content.length; i++) {
        row.push(content[i][j]);
      }
      result.push(row);
    }
    return result;
  }

}
