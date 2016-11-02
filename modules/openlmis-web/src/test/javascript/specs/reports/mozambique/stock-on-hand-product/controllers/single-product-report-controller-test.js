describe('Single Product Report Controller',function () {
    var scope, facilityProductData, httpBackend, dateFilter, lotExpiryDateService, window;


    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $filter, $controller, LotExpiryDateService, $window) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        dateFilter = $filter('date');
        window = $window;
        $controller(SingleProductReportController, {$scope: scope});
    }));


    it('should redirect to lot expiry date report', function () {
        var facilityCode = 'HF1';
        scope.reportParams = {
            endTime: '2016-11-01',
            productCode: '01A03'
        };

        expect(scope.generateRedirectToExpiryDateReportURL(facilityCode)).toBe('/public/pages/reports/mozambique/index.html#/lot-expiry-dates?facilityCode=HF1&date=2016-11-01&drugCode=01A03');
    })
});