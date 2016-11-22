function ConsumptionReportController($scope, $controller, $filter, $http, $q, CubesGenerateCutParamsService, CubesGenerateUrlService, DateFormatService, messageService, ReportExportExcelService) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
        $scope.loadHealthFacilities();
    });

    var consumptionInPeriods;

    $scope.generateConsumptionReport = function () {
        if ($scope.checkDateValidRange() && validateProduct()) {
            $scope.locationIdToCode($scope.reportParams);
            var promises = requestConsumptionDataForEachPeriod();
            $q.all(promises).then(function (consumptionsInPeriods) {
                consumptionInPeriods = _.pluck(_.pluck(consumptionsInPeriods, 'data'), 'summary');
                renderConsumptionChart(consumptionInPeriods);
            });
        }
    };

    $scope.exportXLSX = function() {
        var data = {
            reportHeaders: {
                drugCode: messageService.get('report.header.drug.code'),
                drugName: messageService.get('report.header.drug.name'),
                province: messageService.get('report.header.province'),
                district: messageService.get('report.header.district'),
                facility: messageService.get('report.header.facility'),
                period: messageService.get('report.header.period'),
                cmm: messageService.get('report.header.cmm'),
                consumption: messageService.get('report.header.consumption.during.period'),
                soh: messageService.get('report.header.soh.at.period.end')
            },
            reportContent: []
        };

        consumptionInPeriods.forEach(function (consumptionInPeriod) {
            var consumptionReportContent = {};
            consumptionReportContent.drugCode = $scope.reportParams.productCode;
            consumptionReportContent.drugName = $scope.getDrugByCode($scope.reportParams.productCode).primaryName;
            consumptionReportContent.province = $scope.reportParams.selectedProvince ? $scope.reportParams.selectedProvince.name : 'All';
            consumptionReportContent.district = $scope.reportParams.selectedDistrict ? $scope.reportParams.selectedDistrict.name : 'All';
            consumptionReportContent.facility = $scope.reportParams.selectedFacility ? $scope.reportParams.selectedFacility.name : 'All';
            consumptionReportContent.period = consumptionInPeriod.period;
            consumptionReportContent.cmm = consumptionInPeriod.cmm;
            consumptionReportContent.consumption = consumptionInPeriod.total_quantity;
            consumptionReportContent.soh = consumptionInPeriod.soh;

            data.reportContent.push(consumptionReportContent);
        });

        ReportExportExcelService.exportAsXlsx(data, messageService.get('report.file.consumption.report'));
    };

    function renderConsumptionChart(consumptionInPeriods) {
        AmCharts.makeChart("consumption-report", {
            "type": "serial",
            "theme": "light",
            "dataProvider": consumptionInPeriods,
            "graphs": [{
                "bullet": "round",
                "valueField": "soh",
                "balloonText": messageService.get("stock.movement.soh") + ": [[value]]"
            }, {
                "bullet": "round",
                "valueField": "cmm",
                "balloonText": "CMM: [[value]]"
            }, {
                "bullet": "round",
                "valueField": "total_quantity",
                "balloonText": messageService.get("consumption.chart.balloon.text") + ": [[value]]"
            }],
            "chartScrollbar": {
                "oppositeAxis": false,
                "offset": 100
            },
            "chartCursor": {},
            "categoryField": "period",
            "categoryAxis": {
                "autoRotateCount": 5,
                "autoRotateAngle": 45
            }
        });
    }

    function requestConsumptionDataForEachPeriod() {
        var periodsInSelectedRange = $scope.splitPeriods($scope.reportParams.startTime, $scope.reportParams.endTime);
        return _.map(periodsInSelectedRange, function (period) {
            var cutParams = CubesGenerateCutParamsService.generateCutsParams("periodstart",
                $filter('date')(period.periodStart, "yyyy,MM,dd"),
                $filter('date')(period.periodStart, "yyyy,MM,dd"),
                $scope.reportParams.selectedFacility,
                [{"drug.drug_code": $scope.reportParams.productCode}],
                $scope.reportParams.selectedProvince,
                $scope.reportParams.selectedDistrict
            );
            cutParams.push({
                dimension: 'reason_code', values: [
                    'UNPACK_KIT',
                    'DEFAULT_ISSUE',
                    'PUB_PHARMACY',
                    'MATERNITY',
                    'GENERAL_WARD',
                    'ACC_EMERGENCY',
                    'MOBILE_UNIT',
                    'LABORATORY',
                    'UATS',
                    'PNCTL',
                    'PAV',
                    'DENTAL_WARD',
                    'ISSUE',
                    'NO_MOVEMENT_IN_PERIOD'
                ]
            });
            return $http
                .get(CubesGenerateUrlService.generateAggregateUrl("vw_period_movements", [], cutParams))
                .then(function (consumptionData) {
                    consumptionData.data.summary.period =
                        DateFormatService.formatDateWithLocaleNoDay(period.periodStart) +
                        "-" +
                        DateFormatService.formatDateWithLocaleNoDay(period.periodEnd);
                    return consumptionData;
                });
        });
    }

    function validateProduct() {
        $scope.noProductSelected = !$scope.reportParams.productCode;
        return !$scope.noProductSelected;
    }

}