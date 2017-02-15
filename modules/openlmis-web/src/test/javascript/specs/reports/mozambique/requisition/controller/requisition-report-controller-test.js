describe("requisition report controller", function () {
    var scope, httpBackend, messageService;

    var requisitions = {
    "rnr_list":[
        {   "id":148,
            "programName":"VIA",
            "type":"Emergency",
            "emergency":true,
            "facilityName":"Matalane",
            "districtName": "Marracuene",
            "provinceName": "Maputo",
            "submittedUser":"mystique",
            "clientSubmittedTimeString":"2016-10-27 11:11:20",
            "actualPeriodEnd":null,
            "schedulePeriodEnd":1463759999000,
            "webSubmittedTime":1463453167471,
            "clientSubmittedTime":1477537880000,
            "requisitionStatus":"AUTHORIZED"
        },
        {   "id":149,
            "programName":"VIA",
            "type":"Normal",
            "emergency":false,
            "facilityName":"Matalane",
            "districtName": "Marracuene",
            "provinceName": "Maputo",
            "submittedUser":"mystique",
            "clientSubmittedTimeString":"2016-05-20 23:59:59",
            "actualPeriodEnd":1456197080000,
            "schedulePeriodEnd":1463759999000,
            "webSubmittedTime":1463453174780,
            "clientSubmittedTime":1463759999000,
            "requisitionStatus":"AUTHORIZED"
        },
        {   "id":150,
            "programName":"VIA",
            "type":"Normal",
            "emergency":false,
            "facilityName":"Matalane",
            "districtName": "Marracuene",
            "provinceName": "Maputo",
            "submittedUser":"mystique",
            "clientSubmittedTimeString":"",
            "actualPeriodEnd":1456197080000,
            "schedulePeriodEnd":1463759999000,
            "webSubmittedTime":1463453174780,
            "clientSubmittedTime":null,
            "requisitionStatus":"AUTHORIZED"
        }
    ]};

    beforeEach(module('openlmis'));
    beforeEach(module('ui.bootstrap.dialog'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $controller,_messageService_) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;
        messageService = _messageService_;

        $controller(RequisitionReportController, {$scope: scope});
    }));


    it('should load facility and stock movements successfully', function () {
        scope.reportParams.startTime = '2017-01-01';
        scope.reportParams.endTime = '2017-02-01';
        scope.reportParams.facilityId = 1;
        scope.reportParams.districtId = 1;
        scope.reportParams.provinceId = 1;
        scope.reportParams.selectedFacility = {"name": "Matalane"};
        scope.reportParams.selectedDistrict = {"name": "Marracuene"};
        scope.reportParams.selectedProvince = {"name": "Maputo"};
        httpBackend.expectGET('/reports/requisition-report.json?endTime=2017-02-01+11:59:59&startTime=2017-01-01+00:00:00').respond(200, requisitions);

        scope.loadReport();
        httpBackend.flush();

        expect(scope.requisitions.length).toBe(3);

        expect(scope.requisitions[0].actualPeriodEnd).toBe(1463759999000);
        expect(scope.requisitions[0].submittedStatus).toBe(messageService.get("rnr.report.submitted.status.late"));
        expect(scope.requisitions[1].actualPeriodEnd).toBe(1456197080000);
        expect(scope.requisitions[1].submittedStatus).toBe(messageService.get("rnr.report.submitted.status.ontime"));
        expect(scope.requisitions[2].submittedStatus).toBe(undefined);
    });

    it('should get redirect url of rnr detail page', function () {
        scope.selectedItems[0] = {
            "id":150,
            "programName":"VIA"
        };

        expect(scope.getRedirectUrl()).toBe("/public/pages/logistics/rnr/index.html#/view-requisition-via/150?supplyType=fullSupply&page=1");
    });
});