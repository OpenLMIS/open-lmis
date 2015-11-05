describe("Product Report Controller", function () {
    var scope, scope2, geoZoneData, levels, productData, facilityProductData, httpBackend;

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

    productData = {
        "products": [{
            "productName": "Lamivudina 150mg/Zidovudina 300mg/Nevirapina 200mg Embalagem 10mg ",
            "facilityName": "Health Facility 1",
            "productQuantity": 210,
            "soonestExpiryDate": 1446539380922,
            "lastSyncDate": 1446538464758
        }]
    };

    facilityProductData = {
        "products": [{
            "productName": "Lamivudina 150mg/Zidovudina 300mg/Nevirapina 200mg Embalagem 10mg ",
            "facilityName": null,
            "productQuantity": 210,
            "soonestExpiryDate": 1446538560900,
            "lastSyncDate": 1446538464758
        }, {
            "productName": "Tenofovir 300mg/Lamivudina 300mg/Efavirenze 600mg Embalagem 10mg ",
            "facilityName": null,
            "productQuantity": 110,
            "soonestExpiryDate": 1446538560900,
            "lastSyncDate": 1446538464758
        }]
    };

    beforeEach(module('openlmis'));
    beforeEach(inject(function (_$httpBackend_,$rootScope,$filter, ProductReportService) {
        scope = $rootScope.$new();
        scope2 = $rootScope.$new();
        httpBackend = _$httpBackend_;

        ProductReportController("singleProduct")(scope, $filter, ProductReportService);
        ProductReportController()(scope2, $filter, ProductReportService);
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

    it('should load single product report successfully', function () {
        scope.reportParams = {productId : undefined};

        scope.loadReport();
        expect(scope.invalid).toBe(true);
        scope.reportParams.productId = 199;

        httpBackend.expectGET('/reports/single-product-report?productId=199').respond(200, productData);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toBe(1);
        expect(scope.reportData[0].productName).toEqual("Lamivudina 150mg/Zidovudina 300mg/Nevirapina 200mg Embalagem 10mg ");
    });

    it('should load all product report successfully', function () {
        scope2.reportParams = {facilityId : undefined};

        scope2.loadReport();
        expect(scope2.invalid).toBe(true);
        scope2.reportParams.facilityId = 414;

        httpBackend.expectGET('/reports/all-products-report?facilityId=414').respond(200, facilityProductData);
        scope2.loadReport();
        httpBackend.flush();

        expect(scope2.reportData.length).toBe(2);
        expect(scope2.reportData[0].productName).toEqual("Lamivudina 150mg/Zidovudina 300mg/Nevirapina 200mg Embalagem 10mg ");
        expect(scope2.reportData[1].productName).toEqual("Tenofovir 300mg/Lamivudina 300mg/Efavirenze 600mg Embalagem 10mg ");
    });

});