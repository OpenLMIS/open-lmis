function AdjustmentOccurrencesReportController($scope, $controller, $filter, $http, $q, CubesGenerateCutParamsService, CubesGenerateUrlService, DateFormatService, messageService) {
  $controller("BaseProductReportController", {$scope: $scope});

  $scope.adjustmentTypes = [
    {value: "negative", name: "Negative Adjustment"},
    {value: "positive", name:"Positive Adjustment"}
  ];

  $scope.$on("$viewContentLoaded", function () {
    $scope.loadProducts();
    $scope.loadHealthFacilities();
  });

  $scope.generateAdjustmentReport = function () {
    if ($scope.checkDateValidRange()) {
      $scope.locationIdToCode($scope.reportParams);

      var promises = requestAdjustmentDataForEachPeriod();
      $q.all(promises).then(function (adjustmentsInPeriods) {
        renderAdjustmentChart(_.pluck(_.pluck(adjustmentsInPeriods, "data"), "adjustment"));
      });
    }
  };

  function renderAdjustmentChart(adjustmentsInPeriods) {
    AmCharts.makeChart("adjustments-report", {
      "type": "serial",
      "theme": "light",
      "allLabels": [{
        "text": "Free label",
        "bold": true,
        "align":"center"
      }],
      "legend": {
        "position": "bottom",
        "valueAlign": "left"
      },
      "dataProvider": adjustmentsInPeriods,
      "valueAxes": [{
        "stackType": "regular"
      }],
      "graphs": [
        {
          "balloonText": "EXPIRED_RETURN_TO_SUPPLIER: [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": "EXPIRED_RETURN_TO_SUPPLIER",
          "valueField": "EXPIRED_RETURN_TO_SUPPLIER"
        },
        {
          "balloonText": "DAMAGED: [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": "DAMAGED",
          "valueField": "DAMAGED"
        },
        {
          "balloonText": "LOANS_DEPOSIT: [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": "LOANS_DEPOSIT",
          "valueField": "LOANS_DEPOSIT"
        },
        {
          "balloonText": "INVENTORY_NEGATIVE: [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": "INVENTORY_NEGATIVE",
          "valueField": "INVENTORY_NEGATIVE"
        },
        {
          "balloonText": "PROD_DEFECTIVE: [[value]]",
          "fillAlphas": 0.6,
          "lineAlpha": 0.4,
          "title": "PROD_DEFECTIVE",
          "valueField": "PROD_DEFECTIVE"
        }
      ],
      "chartScrollbar": {
        "oppositeAxis": false,
        "offset": 30
      },
      "chartCursor": {},
      "categoryField": "period",
      "categoryAxis": {
        "startOnAxis": true
      }
    });
  }

  function requestAdjustmentDataForEachPeriod() {
    var periodsInSelectedRange = $scope.splitPeriods($scope.reportParams.startTime, $scope.reportParams.endTime);
    return _.map(periodsInSelectedRange, function (period) {
      var cutParams = CubesGenerateCutParamsService.generateCutsParams("periodstart",
          $filter("date")(period.periodStart, "yyyy,MM,dd"),
          $filter("date")(period.periodStart, "yyyy,MM,dd"),
          $scope.reportParams.selectedFacility,
          [{"drug.drug_code": $scope.reportParams.productCode}],
          $scope.reportParams.selectedProvince,
          $scope.reportParams.selectedDistrict
      );
      
      if($scope.reportParams.adjustmentType === "negative"){
        cutParams.push({
          dimension: "reason_code",
          values: [
            "EXPIRED_RETURN_TO_SUPPLIER",
            "DAMAGED",
            "LOANS_DEPOSIT",
            "INVENTORY_NEGATIVE",
            "PROD_DEFECTIVE"]
        });
      }


      return $http
          .get(CubesGenerateUrlService.generateAggregateUrl("vw_period_movements", ["reason_code"], cutParams))
          .then(function (adjustmentData) {

            var adjustment = {
              "EXPIRED_RETURN_TO_SUPPLIER": 0,
              "DAMAGED": 0,
              "LOANS_DEPOSIT": 0,
              "INVENTORY_NEGATIVE": 0,
              "PROD_DEFECTIVE": 0
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

}