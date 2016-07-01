function AdjustmentOccurrencesReportController($scope, $controller, $filter, $http, $q, AdjustmentOccurrencesChartService, CubesGenerateCutParamsService, CubesGenerateUrlService, DateFormatService) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.adjustmentTypes = [
    {value: "negative", name: "Negative Adjustment"},
    {value: "positive", name: "Positive Adjustment"}
  ];

  $scope.$on("$viewContentLoaded", function () {
    $scope.loadProducts();
    $scope.loadHealthFacilities();
  });

  $scope.generateAdjustmentReport = function () {
    if ($scope.checkDateValidRange() && validateProduct()) {
      $scope.locationIdToCode($scope.reportParams);

      var promises = requestAdjustmentDataForEachPeriod();
      $q.all(promises).then(function (adjustmentsInPeriods) {
        var adjustmentsData = _.pluck(_.pluck(adjustmentsInPeriods, "data"), "adjustment");
        var selectedProduct = _.find($scope.products, function (product) {
          return product.code === $scope.reportParams.productCode;
        });
        var selectedAdjustmentType = $scope.reportParams.adjustmentType;

        var label = selectedAdjustmentType.charAt(0).toUpperCase() + selectedAdjustmentType.slice(1) +
            ' adjustments ' + (selectedProduct ? 'for ' + selectedProduct.primaryName : '');

        AdjustmentOccurrencesChartService.renderAdjustmentChart("adjustments-report", adjustmentsData, selectedAdjustmentType, label);
      });
    }
  };


  function requestAdjustmentDataForEachPeriod() {
    var periodsInSelectedRange = $scope.splitPeriods($scope.reportParams.startTime, $scope.reportParams.endTime);
    return _.map(periodsInSelectedRange, function (period) {
      var cutParams = $scope.generateCutParamsByAdjustmentType(period, $scope.reportParams.adjustmentType);

      return $http
          .get(CubesGenerateUrlService.generateAggregateUrl("vw_period_movements", ["reason_code"], cutParams))
          .then(function (adjustmentData) {

            var adjustment = {
              "EXPIRED_RETURN_TO_SUPPLIER": 0,
              "DAMAGED": 0,
              "LOANS_DEPOSIT": 0,
              "INVENTORY_NEGATIVE": 0,
              "PROD_DEFECTIVE": 0,
              "RETURN_TO_DDM":0,

              "CUSTOMER_RETURN": 0,
              "EXPIRED_RETURN_FROM_CUSTOMER": 0,
              "DONATION": 0,
              "LOANS_RECEIVED": 0,
              "INVENTORY_POSITIVE": 0,
              "RETURN_FROM_QUARANTINE": 0
            };

            _.map(adjustmentData.data.cells, function (cell) {
              var reason = cell.reason_code;
              var occurrences = cell.occurrences;
              adjustment[reason] = occurrences;
            });

            adjustment.period = DateFormatService.formatDateWithLocale(period.periodStart) +
                "-" +
                DateFormatService.formatDateWithLocale(period.periodEnd);

            adjustmentData.data.adjustment = adjustment;

            return adjustmentData;
          });
    });
  }

  $scope.generateCutParamsByAdjustmentType = function (period, adjustmentType) {
    var cutParams = CubesGenerateCutParamsService.generateCutsParams("periodstart",
        $filter("date")(period.periodStart, "yyyy,MM,dd"),
        $filter("date")(period.periodStart, "yyyy,MM,dd"),
        $scope.reportParams.selectedFacility,
        [{"drug.drug_code": $scope.reportParams.productCode}],
        $scope.reportParams.selectedProvince,
        $scope.reportParams.selectedDistrict
    );

    var adjustmentReasonCodes = {
      "negative": [
        "EXPIRED_RETURN_TO_SUPPLIER",
        "DAMAGED",
        "LOANS_DEPOSIT",
        "INVENTORY_NEGATIVE",
        "PROD_DEFECTIVE",
        "RETURN_TO_DDM"],
      "positive": [
        "CUSTOMER_RETURN",
        "EXPIRED_RETURN_FROM_CUSTOMER",
        "DONATION",
        "LOANS_RECEIVED",
        "INVENTORY_POSITIVE",
        "RETURN_FROM_QUARANTINE"]
    };

    cutParams.push({
      dimension: "reason_code",
      values: adjustmentReasonCodes[adjustmentType]
    });
    return cutParams;
  };

  function validateProduct() {
    $scope.noProductSelected = !$scope.reportParams.productCode;
    return !$scope.noProductSelected;
  }

}