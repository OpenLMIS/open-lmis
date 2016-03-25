function StockOutSingleProductReportController($scope, $filter, $controller, $http, CubesGenerateUrlService, messageService, $dialog) {
    $controller('BaseProductReportController', {$scope: $scope});

    $scope.getTimeRange = function (dateRange) {
        $scope.reportParams.startTime = dateRange.startTime;
        $scope.reportParams.endTime = dateRange.endTime;
    };

    function showDateRangeInvalidWarningDialog() {
        var options = {
            id: "chooseDateAlertDialog",
            header: "title.alert",
            body: "dialog.date.range.invalid.warning"
        };
        MozambiqueDialog.newDialog(options, function () {
        }, $dialog);
    }

    $scope.$on('$viewContentLoaded', function () {
        $scope.loadProducts();
    });

    $scope.loadReport = function () {
        if (isInvalidDateRange()) {
            showDateRangeInvalidWarningDialog();
            return;
        }

        getStockOutDataFromCubes();
    };

    function isInvalidDateRange() {
        return $scope.reportParams.startTime > $scope.reportParams.endTime;
    }

    function getStockReportRequestParam() {
        var params = {};
        params.startTime = $filter('date')($scope.reportParams.startTime, "yyyy,MM,dd");
        params.endTime = $filter('date')($scope.reportParams.endTime, "yyyy,MM,dd");
        params.drugCode = $scope.reportParams.productCode;
        return params;
    }

    function generateCutParams(stockReportParams) {
        var cutsParams = [{
            dimension: "overlapped_date",
            values: [stockReportParams.startTime + "-" + stockReportParams.endTime]
        }];

        cutsParams.push({dimension: "drug", values: [stockReportParams.drugCode]});
        return cutsParams;
    }

    function getStockOutDataFromCubes() {
        if (!validateProduct()) {
            return;
        }

        $http.get(CubesGenerateUrlService.generateFactsUrl('vw_stockouts', generateCutParams(getStockReportRequestParam())))
            .success(function (data) {
                $scope.reportData = data;
            });
    }

    function validateProduct() {
        $scope.invalid = !$scope.reportParams.productCode;
        return !$scope.invalid;
    }


    var testRawTreeData = [
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 12.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Marracuene",
            "overlap_duration": 0,
            "location.district_code": "MAPUTO_DIS1",
            "is_resolved": true,
            "stockout.date": "2015-12-08",
            "facility.facility_code": "HF1",
            "overlapped_date.year": 2015.0,
            "stockout.overlap_duration": 0,
            "stockout.resolved_date": "2015-12-08",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2015-12-01",
            "vw_stockouts_facility_name": "Marracuene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS1"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 1.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Marracuene",
            "overlap_duration": 0,
            "location.district_code": "MAPUTO_DIS1",
            "is_resolved": true,
            "stockout.date": "2016-01-22",
            "facility.facility_code": "HF1",
            "overlapped_date.year": 2016.0,
            "stockout.overlap_duration": 0,
            "stockout.resolved_date": "2016-01-22",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2016-01-01",
            "vw_stockouts_facility_name": "Marracuene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS1"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 1.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Marracuene2",
            "overlap_duration": 0,
            "location.district_code": "MAPUTO_DIS2",
            "is_resolved": true,
            "stockout.date": "2016-01-22",
            "facility.facility_code": "Marracuene2",
            "overlapped_date.year": 2016.0,
            "stockout.overlap_duration": 0,
            "stockout.resolved_date": "2016-01-22",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2016-01-01",
            "vw_stockouts_facility_name": "Marracuene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS2"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 1.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Marracuene3",
            "overlap_duration": 0,
            "location.district_code": "MAPUTO_DIS3",
            "is_resolved": true,
            "stockout.date": "2016-01-22",
            "facility.facility_code": "Marracuene3",
            "overlapped_date.year": 2016.0,
            "stockout.overlap_duration": 0,
            "stockout.resolved_date": "2016-01-22",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2016-01-01",
            "vw_stockouts_facility_name": "Marracuene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS3"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 1.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Marracuene3",
            "overlap_duration": 0,
            "location.district_code": "MAPUTO_DIS3",
            "is_resolved": true,
            "stockout.date": "2016-01-22",
            "facility.facility_code": "Marracuene3",
            "overlapped_date.year": 2016.0,
            "stockout.overlap_duration": 0,
            "stockout.resolved_date": "2016-01-22",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2016-01-01",
            "vw_stockouts_facility_name": "Marracuene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS3"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 1.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Marracuene3",
            "overlap_duration": 10,
            "location.district_code": "MAPUTO_DIS3",
            "is_resolved": false,
            "stockout.date": "2016-01-22",
            "facility.facility_code": "Marracuene3",
            "overlapped_date.year": 2016.0,
            "stockout.overlap_duration": 10,
            "stockout.resolved_date": "2016-03-24",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2016-01-01",
            "vw_stockouts_facility_name": "Marracuene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS3"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 2.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Hello world",
            "overlap_duration": 29,
            "location.district_code": "MAPUTO_DIS4",
            "is_resolved": false,
            "stockout.date": "2016-01-22",
            "facility.facility_code": "Marracuene4",
            "overlapped_date.year": 2016.0,
            "stockout.overlap_duration": 29,
            "stockout.resolved_date": "2016-03-24",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2016-02-01",
            "vw_stockouts_facility_name": "Marracuene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS4"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 3.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Hello world2",
            "overlap_duration": 24,
            "location.district_code": "MAPUTO_DIS4",
            "is_resolved": false,
            "stockout.date": "2016-01-22",
            "facility.facility_code": "Marracuene5",
            "overlapped_date.year": 2016.0,
            "stockout.overlap_duration": 24,
            "stockout.resolved_date": "2016-03-24",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2016-03-01",
            "vw_stockouts_facility_name": "Marracuene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS4"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 10.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Hello world3",
            "overlap_duration": 9,
            "location.district_code": "MAPUTO_DIS4",
            "is_resolved": true,
            "stockout.date": "2015-10-23",
            "facility.facility_code": "HF8",
            "overlapped_date.year": 2015.0,
            "stockout.overlap_duration": 9,
            "stockout.resolved_date": "2015-11-11",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2015-10-01",
            "vw_stockouts_facility_name": "Michafutene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS4"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 11.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Michafutene",
            "overlap_duration": 11,
            "location.district_code": "MAPUTO_DIS4",
            "is_resolved": true,
            "stockout.date": "2015-10-23",
            "facility.facility_code": "HF4",
            "overlapped_date.year": 2015.0,
            "stockout.overlap_duration": 11,
            "stockout.resolved_date": "2015-11-11",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2015-11-01",
            "vw_stockouts_facility_name": "Michafutene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS4"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "MAPUTO_PROVINCIA",
            "overlapped_date.month": 11.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Michafutene",
            "overlap_duration": 8,
            "location.district_code": "MAPUTO_DIS4",
            "is_resolved": true,
            "stockout.date": "2015-11-23",
            "facility.facility_code": "HF4",
            "overlapped_date.year": 2015.0,
            "stockout.overlap_duration": 8,
            "stockout.resolved_date": "2015-12-07",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2015-11-01",
            "vw_stockouts_facility_name": "Michafutene",
            "overlapped_date.day": 1.0,
            "location.district_name": "MAPUTO_DIS4"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "testProv",
            "overlapped_date.month": 9.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "tesProv",
            "facility.facility_name": "Ricatla",
            "overlap_duration": 1,
            "location.district_code": "MATOLA",
            "is_resolved": true,
            "stockout.date": "2015-09-30",
            "facility.facility_code": "HF6",
            "overlapped_date.year": 2015.0,
            "stockout.overlap_duration": 1,
            "stockout.resolved_date": "2015-10-08",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2015-09-01",
            "vw_stockouts_facility_name": "Ricatla",
            "overlapped_date.day": 1.0,
            "location.district_name": "Matola"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "testProv",
            "overlapped_date.month": 10.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "tesProv",
            "facility.facility_name": "Ricatla",
            "overlap_duration": 8,
            "location.district_code": "MATOLA",
            "is_resolved": true,
            "stockout.date": "2015-09-30",
            "facility.facility_code": "HF6",
            "overlapped_date.year": 2015.0,
            "stockout.overlap_duration": 8,
            "stockout.resolved_date": "2015-10-08",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2015-10-01",
            "vw_stockouts_facility_name": "Ricatla",
            "overlapped_date.day": 1.0,
            "location.district_name": "Matola"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "testProv",
            "overlapped_date.month": 10.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "tesProv",
            "facility.facility_name": "Ricatla",
            "overlap_duration": 2,
            "location.district_code": "MATOLA",
            "is_resolved": true,
            "stockout.date": "2015-10-08",
            "facility.facility_code": "HF6",
            "overlapped_date.year": 2015.0,
            "stockout.overlap_duration": 2,
            "stockout.resolved_date": "2015-10-09",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2015-10-01",
            "vw_stockouts_facility_name": "Ricatla",
            "overlapped_date.day": 1.0,
            "location.district_name": "Matola"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "testProv",
            "overlapped_date.month": 11.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "tesProv",
            "facility.facility_name": "Ricatla",
            "overlap_duration": 13,
            "location.district_code": "MATOLA",
            "is_resolved": true,
            "stockout.date": "2015-11-04",
            "facility.facility_code": "HF6",
            "overlapped_date.year": 2015.0,
            "stockout.overlap_duration": 13,
            "stockout.resolved_date": "2015-11-16",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2015-11-01",
            "vw_stockouts_facility_name": "Ricatla",
            "overlapped_date.day": 1.0,
            "location.district_name": "Matola"
        },
        {
            "drug.drug_code": "07A06",
            "location.province_code": "testProv",
            "overlapped_date.month": 12.0,
            "drug.drug_name": "Paracetamol120mg/5mLXarope",
            "location.province_name": "tesProv",
            "facility.facility_name": "Ricatla",
            "overlap_duration": 0,
            "location.district_code": "MATOLA",
            "is_resolved": true,
            "stockout.date": "2015-12-15",
            "facility.facility_code": "HF6",
            "overlapped_date.year": 2015.0,
            "stockout.overlap_duration": 0,
            "stockout.resolved_date": "2015-12-15",
            "program": "VIA ESSENTIAL",
            "overlapped_month": "2015-12-01",
            "vw_stockouts_facility_name": "Ricatla",
            "overlapped_date.day": 1.0,
            "location.district_name": "Matola"
        }];

    $scope.tree_data = generateProvinceStockOut(testRawTreeData);

    $scope.expanding_property = {
        field: "name",
        displayName: " "
    };
    $scope.col_defs = [
        {
            field: "monthlyAvg",
            displayName: messageService.get('report.stock.out.avg.duration')
        },
        {
            field: "monthlyOccurrences",
            displayName: messageService.get('report.stock.out.occurrences')
        },
        {
            field: "totalDuration",
            displayName: messageService.get('report.stock.out.total')
        },{
            field: "incidents",
            displayName :messageService.get('report.stock.out.incidents')
        }
    ];


    function generateProvinceStockOut(testRawTreeData) {
        var groupByProvince = _.groupBy(testRawTreeData, 'location.province_code');

        var provinceData = [];
        for (var property in groupByProvince) {
            if (groupByProvince.hasOwnProperty(property)) {
                var groupByProvinceValue = groupByProvince[property];
                var provinceResult = calculateProvinceStockOut(groupByProvinceValue);

                var provinceChildrenArray = [];
                var districts = _.groupBy(groupByProvinceValue, 'location.district_code');
                _.forEach(districts, function (district) {
                    var facilities = _.groupBy(district, 'facility.facility_code');
                    var districtChildren = [];
                    _.forEach(facilities, function (facility) {
                        var facilityResult = calculateFacilityStockOut(facility);
                        districtChildren.push(facilityResult);
                    });

                    var districtResult = calculateDistrictStockOut(district);

                    districtResult.children = districtChildren;
                    provinceChildrenArray.push(districtResult);
                });

                provinceResult.children = provinceChildrenArray;
                provinceData.push(provinceResult);
            }
        }
        return provinceData;
    }

    function calculateFacilityStockOut(facilityData) {
        var districtName = facilityData[0]['facility.facility_name'];
        return {
            'name': districtName,
            'monthlyAvg': 999.123,
            'monthlyOccurrences': 123.5,
            'totalDuration': 2012,
            'incidents':"1-3Mar  ,  4Mar-4Jun"
        };
    }


    function calculateDistrictStockOut(districtData) {
        var districtName = districtData[0]['location.district_name'];

        return {
            'name': districtName,
            'monthlyAvg': 2,
            'monthlyOccurrences': 1.5,
            'totalDuration': 20,
            'incidents':""
        };
    }

    function calculateProvinceStockOut(groupByProvinceValue) {
        var provinceName = groupByProvinceValue[0]['location.province_name'];

        return {
            'name': provinceName,
            'monthlyAvg': 12.03,
            'monthlyOccurrences': 2.5,
            'totalDuration': 30,
            'incidents':""
        }
    }


}