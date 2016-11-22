function AdjustmentOccurrencesReportController($scope, $controller, $filter, $http, $q, AdjustmentOccurrencesChartService, CubesGenerateCutParamsService, CubesGenerateUrlService, DateFormatService, messageService, ReportExportExcelService) {
  $controller("BaseProductReportController", {$scope: $scope});

  var adjustmentsData;
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

  $scope.adjustmentTypes = [
    {value: "negative", name: messageService.get("stock.movement.negative.adjustment")},
    {value: "positive", name: messageService.get("stock.movement.positive.adjustment")}
  ];

  $scope.$on("$viewContentLoaded", function () {
    $scope.loadProducts();
    $scope.loadHealthFacilities();
  });

  $scope.generateAdjustmentReport = function () {
    if ($scope.checkDateValidRange() && validateProduct() && validateAdjustmentType()) {
      $scope.locationIdToCode($scope.reportParams);

      var promises = requestAdjustmentDataForEachPeriod();
      $q.all(promises).then(function (adjustmentsInPeriods) {
        adjustmentsData = _.pluck(_.pluck(adjustmentsInPeriods, "data"), "adjustment");
        var selectedProduct = _.find($scope.products, function (product) {
          return product.code === $scope.reportParams.productCode;
        });
        var selectedAdjustmentType = $scope.reportParams.adjustmentType;

        var label = "";

        if (selectedAdjustmentType === 'negative') {
          label = messageService.get("stock.movement.negative.adjustment.title") + " " + selectedProduct.primaryName;
        } else {
          label = messageService.get("stock.movement.positive.adjustment.title") + " " + selectedProduct.primaryName;
        }

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

            adjustment.period = DateFormatService.formatDateWithLocaleNoDay(period.periodStart) +
                "-" +
                DateFormatService.formatDateWithLocaleNoDay(period.periodEnd);

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

    cutParams.push({
      dimension: "reason_code",
      values: adjustmentReasonCodes[adjustmentType]
    });
    return cutParams;
  };

  $scope.exportXLSX = function() {
    var data = {
      reportHeaders: {
        drugCode: messageService.get('report.header.drug.code'),
        drugName: messageService.get('report.header.drug.name'),
        province: messageService.get('report.header.province'),
        district: messageService.get('report.header.district'),
        facility: messageService.get('report.header.facility'),
        adjustmentType: messageService.get('report.header.adjustment.type'),
        adjustmentReason: messageService.get('report.header.adjustment.reason'),
        occurrencesPeriod: messageService.get('report.header.occurrences.period'),
        occurrencesTimes: messageService.get('report.header.occurrences.times')
      },
      reportContent: []
    };

    adjustmentsData.forEach(function(adjustment) {
      var selectedReasonKeys = adjustmentReasonCodes[$scope.reportParams.adjustmentType];

      selectedReasonKeys.forEach(function (selectedReasonKey) {
        var adjustmentReportContent = {};
        adjustmentReportContent.drugCode = $scope.reportParams.productCode;
        adjustmentReportContent.drugName = $scope.getDrugByCode($scope.reportParams.productCode).primaryName;
        adjustmentReportContent.province = $scope.reportParams.selectedProvince ? $scope.reportParams.selectedProvince.name : 'All';
        adjustmentReportContent.district = $scope.reportParams.selectedDistrict ? $scope.reportParams.selectedDistrict.name : 'All';
        adjustmentReportContent.facility = $scope.reportParams.selectedFacility ? $scope.reportParams.selectedFacility.name : 'All';
        adjustmentReportContent.adjustmentType = _.find($scope.adjustmentTypes, {value: $scope.reportParams.adjustmentType}).name;

        var reasonDescriptionKey = 'stock.movement.' + selectedReasonKey;
        adjustmentReportContent.adjustmentReason = messageService.get(reasonDescriptionKey);
        adjustmentReportContent.occurrencesPeriod = adjustment.period;
        adjustmentReportContent.occurrencesTimes = adjustment[selectedReasonKey];

        data.reportContent.push(adjustmentReportContent);
      });
    });

    ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.adjustment.occurrences.report'));
  };

  function validateProduct() {
    $scope.noProductSelected = !$scope.reportParams.productCode;
    return !$scope.noProductSelected;
  }

  function validateAdjustmentType() {
    $scope.noAdjustmentTypeSelected = !$scope.reportParams.adjustmentType;
    return !$scope.noAdjustmentTypeSelected;
  }

}