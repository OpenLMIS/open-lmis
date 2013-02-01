function RequisitionController($scope, Requisition, $location, $routeParams, $rootScope) {

  $scope.rnrLineItems = [];
  $scope.nonFullSupplyLineItems = [];

  if (!$scope.$parent.rnr) {
    Requisition.get({facilityId:$routeParams.facility, programId:$routeParams.program, periodId:$routeParams.period},
      function (data) {
        if (data.rnr) {
          $scope.rnr = data.rnr;
          $scope.formDisabled = isFormDisabled();
          resetCostsIfNull($scope.rnr);
          populateRnrLineItems($scope.rnr);
        } else {
          $scope.$parent.error = "Requisition does not exist. Please initiate.";
          $location.path($scope.$parent.sourceUrl);
        }
      }, function () {
      });
  } else {
    $scope.formDisabled = isFormDisabled();
    resetCostsIfNull($scope.rnr);
    populateRnrLineItems($scope.$parent.rnr);
  }

  function populateRnrLineItems(rnr) {
    $(rnr.lineItems).each(function (i, lineItem) {
      $scope.rnrLineItems.push(calculateCostForLineItem(lineItem));
    });
    $(rnr.nonFullSupplyLineItems).each(function (i, lineItem) {
      $scope.nonFullSupplyLineItems.push(calculateCostForLineItem(lineItem));
    });
  }

  function calculateCostForLineItem(lineItem) {
    lineItem.cost = parseFloat((lineItem.packsToShip * lineItem.price).toFixed(2)) || 0;
    if (lineItem.lossesAndAdjustments == undefined) lineItem.lossesAndAdjustments = [];
    var rnrLineItem = new RnrLineItem(lineItem);
    jQuery.extend(true, lineItem, rnrLineItem);
    return lineItem;
  }

  function isFormDisabled() {
    if ($scope.rnr || $scope.$parent.rnr) {
      if ($scope.rnr.status == 'AUTHORIZED') return true;
      if (($scope.rnr.status == 'SUBMITTED' && !$rootScope.hasPermission('AUTHORIZE_REQUISITION')) || ($scope.rnr.status == 'INITIATED' && !$rootScope.hasPermission('CREATE_REQUISITION'))) return true;
    }
    return false;
  }

  function resetCostsIfNull(rnr) {
    if (rnr == null) return;
    if (rnr.fullSupplyItemsSubmittedCost == null)
      rnr.fullSupplyItemsSubmittedCost = 0;
    if (rnr.nonFullSupplyItemsSubmittedCost == null)
      rnr.nonFullSupplyItemsSubmittedCost = 0;
  }

  $scope.periodDisplayName = function () {
    if(!$scope.rnr) return;

    var startDate = new Date($scope.rnr.period.startDate);
    var endDate = new Date($scope.rnr.period.endDate);

    return utils.getFormattedDate(startDate) + ' - ' + utils.getFormattedDate(endDate);
  };
}