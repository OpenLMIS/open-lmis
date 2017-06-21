describe("Single facility Report Controller", function () {
    var scope, facilityProductData, httpBackend, dateFilter, lotExpiryDateService, window;

    facilityProductData = [
        {
            "drug.drug_code": "01C01",
            "location.province_code": "MAPUTO_PROVINCIA",
            "cmm": -1.0,
            "occurred.month": 1.0,
            "drug.drug_name": "Hidralazina25mg/5mLInject\u00e1vel",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Habel Jafar",
            "occurred.day": 12.0,
            "location.district_code": "MARRACUENE",
            "facility.facility_code": "HF8",
            "last_sync_date": "2016-06-21T12:05:46.990453+08:00",
            "expiry_date": "2016-01-30",
            "soh": "5",
            "occurred_date": "2016-01-12",
            "vw_daily_full_soh_facility_name": "Habel Jafar",
            "location.district_name": "Marracuene",
            "occurred.year": 2016.0
        },
        {
            "drug.drug_code": "01C01",
            "location.province_code": "MAPUTO_PROVINCIA",
            "cmm": -1.0,
            "occurred.month": 1.0,
            "drug.drug_name": "Hidralazina25mg/5mLInject\u00e1vel",
            "location.province_name": "Maputo Prov\u00edncia",
            "facility.facility_name": "Habel Jafar",
            "occurred.day": 15.0,
            "location.district_code": "MARRACUENE",
            "facility.facility_code": "HF8",
            "last_sync_date": "2016-06-21T12:05:46.990453+08:00",
            "expiry_date": "2016-01-30",
            "soh": "25",
            "occurred_date": "2016-01-15",
            "vw_daily_full_soh_facility_name": "Habel Jafar",
            "location.district_name": "Marracuene",
            "occurred.year": 2016.0
        }];

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $filter, $controller, LotExpiryDateService, $window) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        dateFilter = $filter('date');
        lotExpiryDateService = LotExpiryDateService;
        window = $window;
        $controller(SingleFacilityReportController, {$scope: scope});
    }));

    it('should load all product report successfully', function () {
        scope.reportParams.facilityId = undefined;
        scope.reportParams.districtId = 1;
        scope.reportParams.provinceId = 1;

        scope.loadReport();
        expect(scope.invalidFacility).toBe(true);
        scope.reportParams.facilityId = 414;
        scope.reportParams.endTime = '2017-02-04';

        spyOn(lotExpiryDateService, 'populateLotOnHandInformationForSoonestExpiryDate');

        httpBackend.expectGET('/cubesreports/cube/vw_daily_full_soh/facts?cut=occurred:-2017,02,04').respond(200, facilityProductData);
        httpBackend.expectGET('/cubesreports/cube/vw_cmm_entries/facts?cut=facility:|periodbegin:2017,01,21|periodend:2017,02,20').respond(200, []);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toBe(1);
        expect(scope.reportData[0]['drug.drug_name']).toEqual("Hidralazina25mg/5mLInjectável");
        expect(scope.reportData[0].occurred_date).toEqual("2016-01-15");
    });

    it('should redirect to lot expiry date report', function () {
        var drugCode = 'test';
        scope.reportParams = {
            endTime: '2016-11-01',
            selectedFacility: {
                id:'1',
                code:'HF1'
            }
        };

        expect(scope.generateRedirectToExpiryDateReportURL(drugCode)).toBe('/public/pages/reports/mozambique/index.html#/lot-expiry-dates?facilityCode=HF1&date=2016-11-01&drugCode=test');
    })
});