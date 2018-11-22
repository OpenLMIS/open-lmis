function ViewRnrALController($scope, $route, Requisitions, messageService, DateFormatService, downloadPdfService, downloadSimamService) {

  $scope.rnrLineItems = [];

  $scope.$on('$viewContentLoaded', function () {
    $scope.loadALDetail();
  });

  $scope.$on('messagesPopulated', function () {
    $scope.initMonth();
    $scope.initDate();
    $scope.initContent();
  });

  $(".btn-download-pdf").hide();
  $(".btn-download-simam").hide();
  $scope.loadALDetail = function () {

    Requisitions.get({id: $route.current.params.rnr, operation: "skipped"}, function (data) {
        $scope.rnr = data.rnr;
        $scope.year = data.rnr.period.stringYear;

        $scope.initMonth();
        $scope.initDate();
        $scope.initContent();

        parseSignature($scope.rnr.rnrSignatures);

        downloadPdfService.init($scope, $scope.rnr.id);
        downloadSimamService.init($scope, $scope.rnr.id);
    });
  };

  $scope.initContent = function () {
    var content = {};
    content["Consultas AL US/APE Malaria"] = messageService.get("report.header.al.treatment") + messageService.get("report.header.al.month");
    content["Consultas AL STOCK Malaria"] = messageService.get("report.header.al.existentstock") + messageService.get("report.header.al.period");

    _.map($scope.rnr.regimenLineItems, function (item) {
      var regimenItem = {};
      regimenItem.hf = item.hf;
      regimenItem.chw = item.chw;
      regimenItem.total = item.chw + item.hf;
      content[item.name] = regimenItem;
    });

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
