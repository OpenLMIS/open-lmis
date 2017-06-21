describe('Single Product Report Controller',function () {
    var scope, httpBackend, dateFilter, window, productData, lotExpiryDateService;

    productData = [
        {
            "drug.drug_code": "01A01",
            "location.province_code": "MAPUTO_PROVINCIA",
            "cmm": -1.0,
            "occurred.month": 12.0,
            "drug.drug_name": "Paracetamol500mgComprimidos",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Marracuene",
            "occurred.day": 8.0,
            "location.district_code": "MARRACUENE",
            "facility.facility_code": "HF1",
            "last_sync_date": "2016-06-21T08:55:53.908508+08:00",
            "expiry_date": "2017-04-08",
            "soh": "1000",
            "occurred_date": "2015-12-08",
            "vw_daily_full_soh_facility_name": "Marracuene",
            "location.district_name": "Marracuene",
            "occurred.year": 2015.0
        },
        {
            "drug.drug_code": "01A01",
            "location.province_code": "MAPUTO_PROVINCIA",
            "cmm": -1.0,
            "occurred.month": 12.0,
            "drug.drug_name": "Paracetamol500mgComprimidos",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Marracuene",
            "occurred.day": 17.0,
            "location.district_code": "MARRACUENE",
            "facility.facility_code": "HF1",
            "last_sync_date": "2016-06-21T08:55:53.908508+08:00",
            "expiry_date": "2017-04-08",
            "soh": "1000",
            "occurred_date": "2015-12-17",
            "vw_daily_full_soh_facility_name": "Marracuene",
            "location.district_name": "Marracuene",
            "occurred.year": 2015.0
        }];

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $filter, $controller, LotExpiryDateService, $window) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        dateFilter = $filter('date');
        window = $window;
        lotExpiryDateService = LotExpiryDateService;
        $controller(SingleProductReportController, {$scope: scope});
    }));

    it('should redirect to lot expiry date report', function () {
        var facilityCode = 'HF1';
        scope.reportParams = {
            endTime: '2016-11-01',
            productCode: '01A03'
        };

        expect(scope.generateRedirectToExpiryDateReportURL(facilityCode)).toBe('/public/pages/reports/mozambique/index.html#/lot-expiry-dates?facilityCode=HF1&date=2016-11-01&drugCode=01A03');
    });

    it('should load single product report successfully', function () {
        scope.reportParams = {};
        scope.loadReport();
        expect(scope.invalid).toBe(true);
        scope.reportParams.productCode = '01A01';
        scope.reportParams.endTime = '2017-01-04';
        scope.reportParams.facilityId = 1;
        scope.reportParams.districtId = 1;
        scope.reportParams.provinceId = 1;

        spyOn(lotExpiryDateService, 'populateLotOnHandInformationForSoonestExpiryDate');

        httpBackend.expectGET('/cubesreports/cube/vw_daily_full_soh/facts?cut=occurred:-2017,01,04|drug:01A01').respond(200, productData);
        httpBackend.expectGET('/cubesreports/cube/vw_cmm_entries/facts?cut=product:01A01|periodbegin:2016,12,21|periodend:2017,01,20').respond(200, productData);

        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toBe(1);
        expect(scope.reportData[0]['drug.drug_name']).toEqual("Paracetamol500mgComprimidos");
    });
});