describe("Single product Report Controller", function () {
    var scope, productData, httpBackend;

    productData = [
        {
            "drug.drug_code": "07A03",
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
            "drug.drug_code": "07A03",
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
    beforeEach(inject(function (_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;

        $controller(SingleProductReportController, {$scope: scope});
    }));

    it('should load single product report successfully', function () {
        scope.reportParams = {};
        scope.loadReport();
        expect(scope.invalid).toBe(true);
        scope.reportParams.productCode = '01A01';

        httpBackend.expectGET('/cubesreports/cube/vw_daily_full_soh/facts?cut=occurred:-|drug:01A01').respond(200, productData);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toBe(1);
        expect(scope.reportData[0]['drug.drug_name']).toEqual("Paracetamol500mgComprimidos");
    });
});