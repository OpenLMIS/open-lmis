function ViewRnrRapidTestController($scope, $route, Requisitions, messageService, DateFormatService, downloadPdfService, downloadSimamService) {

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
      $scope.year = data.rnr.period.stringEndDate.substr(6, 4);

      $scope.initMonth();
      $scope.initDate();
      $scope.initContent();

      parseSignature($scope.rnr.rnrSignatures);

      downloadPdfService.init($scope, $scope.rnr.id);
      downloadSimamService.init($scope, $scope.rnr.id);
    });
  };

  $scope.initContent = function () {
    var content = [];
    var total = {name: 'total'};

    _.map($scope.rnr.services, function (service) {
      var row = {};

      row.name = service.name;
      _.map(service.programDataColumns, function (dataType) {
        row[dataType.code] = dataType.serviceLineItem.value;
        if (service.name != 'APES') {
          total[dataType.code] = isUndefined(total[dataType.code]) ?
            dataType.serviceLineItem.value : total[dataType.code] + dataType.serviceLineItem.value;
        }
      });

      content.push(row);
    });

    content.push(total);

    $scope.content = content;
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

}
