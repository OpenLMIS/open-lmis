describe("requisition report controller", function () {
    var scope, httpBackend;

    var requisitions = {
    "rnr_list":[
        {   "id":148,
            "programName":"VIA",
            "type":"Emergency",
            "emergency":true,
            "facilityName":"Matalane",
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
            "submittedUser":"mystique",
            "clientSubmittedTimeString":"2016-05-20 23:59:59",
            "actualPeriodEnd":1456197080000,
            "schedulePeriodEnd":1463759999000,
            "webSubmittedTime":1463453174780,
            "clientSubmittedTime":1463759999000,
            "requisitionStatus":"AUTHORIZED"
        }
    ]};

    beforeEach(module('openlmis'));
    beforeEach(inject(function (_$httpBackend_, $rootScope, $controller) {
        scope = $rootScope.$new();
        httpBackend = _$httpBackend_;

        $controller(RequisitionReportController, {$scope: scope});
    }));


    it('should load facility and stock movements successfully', function () {
        var now =  new Date();
        var endTime =  [[now.getFullYear(), formatString(now.getMonth() + 1), formatString(now.getDate())].join('-'),
                        [formatString(now.getHours()), formatString(now.getMinutes()), formatString(now.getSeconds())].join(':')].join('+') ;

        httpBackend.expectGET('/reports/requisition-report.json?endTime=' + endTime + '&startTime=2015-09-26+00:00:00').respond(200, requisitions);

        scope.loadRequisitions();
        httpBackend.flush();

        expect(scope.requisitions.length).toBe(2);

        expect(scope.requisitions[0].actualPeriodEnd).toBe(1463759999000);
        expect(scope.requisitions[0].submittedStatus).toBe('Late');
        expect(scope.requisitions[1].actualPeriodEnd).toBe(1456197080000);
        expect(scope.requisitions[1].submittedStatus).toBe('On time');

    });


    var formatString = function(s) {
        return ("0" + s).slice(-2);
    }
});