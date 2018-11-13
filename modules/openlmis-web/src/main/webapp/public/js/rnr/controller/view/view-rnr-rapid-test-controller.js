function ViewRnrRapidTestController($scope, $route, $http, Requisitions, messageService,
                                    downloadPdfService, downloadSimamService) {
  $scope.rnrLineItems = [];
  $scope.regimens = [];
  $scope.regimeTotal = 0;
  
  $scope.patient = [];
  
  $scope.$on('$viewContentLoaded', function () {
    loadRapidTestData();
  });
  
  $scope.$on('messagesPopulated', function () {
    $scope.initMonth();
  });
  
  $(".btn-download-pdf").hide();
  $(".btn-download-simam").hide();
  
  function loadRapidTestData() {
    downloadPdfService.init($scope, 65253);
    downloadSimamService.init($scope, 65253);
  }
  
  
  $scope.initMonth = function () {
    var month = "month." + $scope.rnr.period.stringEndDate.substr(3, 2);
    $scope.month = messageService.get(month);
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
