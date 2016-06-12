describe("Stock Out All Products Report Controller", function () {
    var scope, geoZoneData, levels, provinceData, httpBackend, dateFilter, stockOutReportData, facilityData, districtData, messageService;

    levels = [{
        "id": 5,
        "code": "national",
        "name": "National",
        "levelNumber": 1
    }, {
        "id": 6,
        "code": "province",
        "name": "Province",
        "levelNumber": 2
    }, {
        "id": 7,
        "code": "district",
        "name": "District",
        "levelNumber": 3
    }];

    geoZoneData = {
        "geographic-zones": [{
            "id": 74,
            "name": "Maputo Província",
            "parent": null,
            "parentId": 73,
            "code": "MAPUTO_PROVINCIA",
            "catchmentPopulation": null,
            "latitude": null,
            "longitude": null,
            "levelId": 6
        }, {
            "id": 75,
            "name": "Marracuene",
            "parent": null,
            "parentId": 74,
            "code": "MARRACUENE",
            "catchmentPopulation": null,
            "latitude": null,
            "longitude": null,
            "levelId": 7
        }, {
            "id": 76,
            "name": "Matola",
            "parent": null,
            "parentId": 74,
            "code": "MATOLA",
            "catchmentPopulation": null,
            "latitude": null,
            "longitude": null,
            "levelId": 7
        }, {
            "id": 73,
            "name": "Mozambique",
            "parent": null,
            "parentId": null,
            "code": "MOZ",
            "catchmentPopulation": null,
            "latitude": null,
            "longitude": null,
            "levelId": 5
        }]
    };

    provinceData = [
        {
            code: "MAPUTO_PROVINCIA",
            id: 1,
            levelId: 2,
            name: "Maputo Província",
            parentId: 3
        },
        {
            code: "Habel",
            id: 2,
            levelId: 2,
            name: "Habel",
            parentId: 3
        }];

    facilityData = [{
        code: "HF8",
        id: 1,
        name: "Habel Jafar"
    }, {
        code: "HF3",
        id: 4,
        name: "Machubo"
    }];

    districtData = [{
        id: 5,
        code: "MARRACUENE",
        name: "Marracuene"
    }];

    stockOutReportData = [{
        "drug.drug_code" : "Drug a",
        "drug.drug_name" : "Drug a name",

        "facility.facility_code": "FA",

        "stockout.date": "2016-02-05",
        "stockout.resolved_date": "2016-03-02",

        "overlapped_month": "2016-02-01",
        "overlap_duration": 25
    }, {
        "drug.drug_code" : "Drug a",
        "drug.drug_name" : "Drug a name",

        "facility.facility_code": "FA",

        "stockout.date": "2016-04-05",
        "stockout.resolved_date": "2016-04-12",

        "overlapped_month": "2016-04-01",
        "overlap_duration": 2
    }, {
        "drug.drug_code" : "Drug b",
        "drug.drug_name" : "Drug b name",

        "facility.facility_code": "FA2",

        "stockout.date": "2016-02-05",
        "stockout.resolved_date": "2016-02-08",

        "overlapped_month": "2016-02-01",
        "overlap_duration": 4
    }, {
        "drug.drug_code" : "Drug c",
        "drug.drug_name" : "Drug c name",

        "facility.facility_code": "FB",

        "stockout.date": "2016-02-05",
        "stockout.resolved_date": "2016-02-10",

        "overlapped_month": "2016-02-01",
        "overlap_duration": 6
    }];

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $http, $controller, $filter, _messageService_) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        messageService = _messageService_;
        dateFilter = $filter('date');

        $controller(StockOutAllProductsReportController, {$scope: scope});

        spyOn(messageService, 'get').andCallFake(function (value) {
            if (value == 'report.stock.out.occurrences') return "Stockout occurrences";
            if (value == 'report.avg.stock.out.occurrences') return "Average stockout occurrences"
        });
    }));

    it('should get provinces and districts', function () {
        scope.getProvincesAndDistricts(levels, geoZoneData);

        expect(scope.fullGeoZoneList.length).toEqual(3);
        expect(scope.provinces.length).toEqual(1);
        expect(scope.districts.length).toEqual(2);
    });

    it('should get parent by geoZoneId', function () {
        scope.getProvincesAndDistricts(levels, geoZoneData);

        var parentZone = scope.getParent(75);
        expect(parentZone.id).toEqual(74);
        expect(parentZone.name).toEqual("Maputo Província");
    });

    it('should get corresponding province by id', function () {
        var provinceById1 = scope.getGeographicZoneById(provinceData, 1);
        var provinceById2 = scope.getGeographicZoneById(provinceData, 2);
        expect(provinceById1["name"]).toEqual("Maputo Província");
        expect(provinceById2["name"]).toEqual("Habel");
    });

    it('should load stock out all products report successfully', function () {
        scope.provinces = provinceData;
        scope.districts = districtData;
        scope.facilities = facilityData;
        scope.reportParams = {
            provinceId: "1",
            districtId: "5",
            facilityId: "1",
            startTime: "2015-02-15",
            endTime: "2016-03-15"
        };

        httpBackend.expectGET('/cubesreports/cube/vw_stockouts/facts?cut=overlapped_date:2015,02,15-2016,03,15|facility:HF8|location:MAPUTO_PROVINCIA,MARRACUENE').respond(200, stockOutReportData);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toBe(3);
        expect(scope.reportData[0]["code"]).toEqual("Drug a");
        expect(scope.reportData[0]["totalDuration"]).toEqual(27);
        expect(scope.reportData[0]["occurrences"]).toEqual(2);
        expect(scope.reportData[0]["avgDuration"]).toEqual(13.5);
        expect(scope.reportData[0]["name"]).toEqual("Drug a name");
        expect(scope.reportParams.reportTitle).toEqual("Maputo Província,Marracuene,Habel Jafar");
    });

    it('should change report title and format total duration value when select all facility', function () {
        scope.provinces = provinceData;
        scope.districts = districtData;
        scope.facilities = facilityData;
        scope.reportParams = {
            provinceId: "1",
            districtId: "5",
            facilityId: "",
            startTime: "2015-02-15",
            endTime: "2016-03-15"
        };

        httpBackend.expectGET('/cubesreports/cube/vw_stockouts/facts?cut=overlapped_date:2015,02,15-2016,03,15|location:MAPUTO_PROVINCIA,MARRACUENE').respond(200, stockOutReportData);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportParams.reportTitle).toEqual("Maputo Província,Marracuene");
        expect(scope.reportData[0].totalDuration).toEqual("-");
    });
});