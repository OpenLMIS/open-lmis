describe("Single facility Report Controller", function () {
    var scope, facilityProductData, httpBackend, dateFilter;

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
    beforeEach(inject(function (_$httpBackend_, $rootScope, $filter, $controller) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        dateFilter = $filter('date');

        $controller(SingleFacilityReportController, {$scope: scope});
    }));

    it('should load all product report successfully', function () {
        scope.reportParams = {facilityId: undefined};

        scope.loadReport();
        expect(scope.invalid).toBe(true);
        scope.reportParams.facilityId = 414;

        httpBackend.expectGET('/cubesreports/cube/vw_daily_full_soh/facts?cut=occurred:-').respond(200, facilityProductData);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toBe(1);
        expect(scope.reportData[0]['drug.drug_name']).toEqual("Hidralazina25mg/5mLInjectável");
        expect(scope.reportData[0].occurred_date).toEqual("2016-01-15");
    });
});