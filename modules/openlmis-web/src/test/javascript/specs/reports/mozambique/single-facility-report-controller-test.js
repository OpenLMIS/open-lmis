describe("Single facility Report Controller", function () {
    var scope, facilityProductData,httpBackend, dateFilter;

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
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_,$rootScope, $filter, $controller) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        dateFilter = $filter('date');

        $controller(SingleFacilityReportController, {$scope: scope});
    }));

    it('should load all product report successfully', function () {
        scope.reportParams = {facilityId : undefined};

        scope.loadReport();
        expect(scope.invalid).toBe(true);
        scope.reportParams.facilityId = 414;

        httpBackend.expectGET('/reports/all-products-report?facilityId=414').respond(200, facilityProductData);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toBe(2);
        expect(scope.reportData[0].productName).toEqual("Lamivudina 150mg/Zidovudina 300mg/Nevirapina 200mg Embalagem 10mg ");
        expect(scope.reportData[1].productName).toEqual("Tenofovir 300mg/Lamivudina 300mg/Efavirenze 600mg Embalagem 10mg ");
    });
});