describe("Single product Report Controller", function () {
    var scope, productData, httpBackend;

    productData = {
        "products": [{
            "productName": "Lamivudina 150mg/Zidovudina 300mg/Nevirapina 200mg Embalagem 10mg ",
            "facilityName": "Health Facility 1",
            "productQuantity": 210,
            "soonestExpiryDate": 1446539380922,
            "lastSyncDate": 1446538464758
        }]
    };

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;

        $controller(SingleProductReportController, {$scope: scope});
    }));

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
});