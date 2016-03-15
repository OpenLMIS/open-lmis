describe("Stock Out Report Controller", function () {
    var scope, geoZoneData, levels, provinceData, httpBackend, dateFilter, stockOutReportData;

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

    stockOutReportData = {cells: [
        {
            "drug.drug_code": "07A06",
            "duration": 21,
            "record_count": 4,
            "average_days": 5.25,
            "drug.drug_name": "Paracetamol120mg/5mLXarope"
        }, {
            "drug.drug_code": "07A06Z",
            "duration": 45,
            "record_count": 6,
            "average_days": 7.5,
            "drug.drug_name": "Paracetamol125mg/5mLXarope"
        }]};

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $http, $controller, $filter) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        dateFilter = $filter('date');

        $controller(StockOutReportController, {$scope: scope});
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

    it('should change time options correctly', function () {
        scope.$on('$viewContentLoaded');
        scope.changeTimeOption("month");

        var equalDate = dateFilter(new Date().setMonth(new Date().getMonth() - 1), "yyyy-MM-dd");
        expect(scope.reportParams.startTime).toEqual(equalDate);
    });

    it('should get corresponding province by id', function () {
        var provinceById1 = scope.getGeographicZoneById(provinceData, 1);
        var provinceById2 = scope.getGeographicZoneById(provinceData, 2);
        expect(provinceById1["name"]).toEqual("Maputo Província");
        expect(provinceById2["name"]).toEqual("Habel");
    });

    it('should load single product report successfully', function () {
        scope.provinces = provinceData;
        scope.districts = [{
            id: 5,
            code: "MARRACUENE",
            name: "Marracuene"
        }];
        scope.multiProducts = [
            {code: "07A06"},
            {code: "07A06Z"}
        ];
        scope.facilities = [{
            code: "HF8",
            id: 1,
            name: "Habel Jafar"
        }, {
            code: "HF3",
            id: 4,
            name: "Machubo"
        }];
        scope.reportParams = {
            provinceId: "1",
            districtId: "5",
            facilityId: "1",
            startTime: "2015-03-15",
            endTime: "2016-03-15"
        };

        httpBackend.expectGET('/cubesreports/cube/vw_stockouts/aggregate?drilldown=drug&cut=date:2015,03,15-2016,03,15|drug:07A06;07A06Z|facility:HF8|location:MAPUTO_PROVINCIA,MARRACUENE').respond(200, stockOutReportData);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toBe(2);
        expect(scope.reportData[0]["drug.drug_code"]).toEqual("07A06");
        expect(scope.reportData[0]["duration"]).toEqual(21);
        expect(scope.reportData[0]["record_count"]).toEqual(4);
        expect(scope.reportData[0]["average_days"]).toEqual(5.25);
        expect(scope.reportData[0]["drug.drug_name"]).toEqual("Paracetamol120mg/5mLXarope");
        expect(scope.reportParams.reportTitle).toEqual("Maputo Província,MarracueneHabel Jafar");
    });
});