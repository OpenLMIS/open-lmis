describe("Stock Out Report Controller", function () {
    var scope, geoZoneData, levels, provinceData, httpBackend, dateFilter;

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
            latitude: null,
            levelId: 2,
            longitude: null,
            name: "Maputo Província",
            parent: null,
            parentId: 3
        },
        {
            code: "Habel",
            id: 2,
            latitude: null,
            levelId: 2,
            longitude: null,
            name: "Habel",
            parent: null,
            parentId: 3
        }];

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_,$rootScope, $http, $controller, $filter) {
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
});