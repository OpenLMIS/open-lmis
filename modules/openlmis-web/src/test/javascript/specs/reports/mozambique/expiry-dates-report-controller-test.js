describe("Expiry Dates Report Controller", function () {
    var scope, httpBackend, dateFilter, expiryDatesReportData, messageService;

    expiryDatesReportData = [
        {
            "facility.facility_name": "Health Facility 1",
            "facility.facility_code": "HF1",
            "drug.drug_name": "Drug 1",
            "drug.drug_code": "P1",
            "location.district_name": "District 1",
            "location.district_code": "D1",
            "location.province_name": "Province 1",
            "location.province_code": "PH1",
            "expiry_dates": "10/1/2017, 11/11/2018",
            "occurred.year": 2015,
            "occurred.month": 8,
            "occurred.day": 31
        },
        {
            "facility.facility_name": "Health Facility 1",
            "facility.facility_code": "HF1",
            "drug.drug_name": "Drug 1",
            "drug.drug_code": "P1",
            "location.district_name": "District 1",
            "location.district_code": "D1",
            "location.province_name": "Province 1",
            "location.province_code": "PH1",
            "expiry_dates": "10/10/2017, 11/11/2018",
            "occurred.year": 2015,
            "occurred.month": 10,
            "occurred.day": 31
        },
        {
            "facility.facility_name": "Health Facility 1",
            "facility.facility_code": "HF1",
            "drug.drug_name": "Drug 2",
            "drug.drug_code": "P2",
            "location.district_name": "District 1",
            "location.district_code": "D1",
            "location.province_name": "Province 1",
            "location.province_code": "PH1",
            "expiry_dates": "20/10/2017, 11/12/2019",
            "occurred.year": 2015,
            "occurred.month": 11,
            "occurred.day": 30
        },
        {
            "facility.facility_name": "Health Facility 2",
            "facility.facility_code": "HF2",
            "drug.drug_name": "Drug 1",
            "drug.drug_code": "P1",
            "location.district_name": "District 1",
            "location.district_code": "D1",
            "location.province_name": "Province 1",
            "location.province_code": "PH1",
            "expiry_dates": "10/10/2015, 11/11/2018, 12/3/2017",
            "occurred.year": 2015,
            "occurred.month": 9,
            "occurred.day": 20
        }
    ];

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $http, $controller, $filter, _messageService_) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        messageService = _messageService_;
        dateFilter = $filter('date');

        $controller(ExpiryDatesReportController, {$scope: scope});
    }));

    it('should get the expiry dates on the last movement before occurred date for each drug', function() {
        scope.reportParams = {
            endTime: "2015-12-31"
        };

        httpBackend.expectGET('/cubesreports/cube/vw_expiry_dates/facts?cut=occurred:-2015,12,31').respond(200, expiryDatesReportData);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toEqual(2);
        expect(scope.reportData[0].code).toEqual("P1");
        expect(scope.reportData[0].expiry_dates).toEqual(['2015-10-31', '2017-01-31', '2017-03-31', '2018-11-30']);
    });
});