describe("Expiry Dates Report Controller", function () {
    var scope, httpBackend, dateFilter, expiryDatesReportData, messageService;

    expiryDatesReportData =
    {
        "cells": [
            {
                "drug.drug_code": "01A01",
                "drug.drug_name": "Digoxina 0,25mg Comp",
                "facility.facility_name": "Matalane",
                "facility.facility_code": "HF2",
                "occurred_sum": 5856192000000.0,
                "expiry_dates": "20/7/2017, 31/5/2016",
                "last_occurred": 1443657600000.0, //10/1/2015
                "last_createddate": 1443657600000.0
            },
            {
                "drug.drug_code": "01A02",
                "drug.drug_name": "Digoxina; 2,5mg/50mL; Gotas Orais",
                "facility.facility_name": "Matalane",
                "facility.facility_code": "HF2",
                "occurred_sum": 2928096000000.0,
                "expiry_dates": "31/10/2016,31/5/2021",
                "last_occurred": 1440979200000.0, //8/31/2015
                "last_createddate": 1440979200000.0

            },
            {
                "drug.drug_code": "01A01",
                "drug.drug_name": "Digoxina 0,25mg Comp",
                "facility.facility_name": "Nhongonhane (Ed.Mondl.)",
                "facility.facility_code": "HF5",
                "occurred_sum": 2928096000000.0,
                "expiry_dates": "31/1/2018,31/7/2017",
                "last_occurred": 1438300800000.0, //7/31/2015
                "last_createddate": 1438300800000.0
            },
            {
                "drug.drug_code": "01A02",
                "drug.drug_name": "Digoxina; 2,5mg/50mL; Gotas Orais",
                "facility.facility_name": "Nhongonhane (Ed.Mondl.)",
                "facility.facility_code": "HF5",
                "occurred_sum": 2928096000000.0,
                "expiry_dates": "31/8/2016,30/4/2017,31/5/2018",
                "last_occurred": 1433030400000.0, //5/31/2015
                "last_createddate": 1433030400000.0
            },
            {
                "drug.drug_code": "01A02",
                "drug.drug_name": "Digoxina; 2,5mg/50mL; Gotas Orais",
                "facility.facility_name": "Nhongonhane (Ed.Mondl.)",
                "facility.facility_code": "HF5",
                "occurred_sum": 2928096000000.0,
                "expiry_dates": "",
                "last_occurred": 1467244800000.0, //6/30/2015
                "last_createddate": 1467244800000.0
            }
        ]
    };

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
            endTime: 1451520000000
        };

        httpBackend.expectGET('/cubesreports/cube/vw_expiry_dates/aggregate?drilldown=facility.facility_code|drug.drug_code|expiry_dates&cut=occurred:-1451577599999').respond(200, expiryDatesReportData);
        scope.loadReport();
        httpBackend.flush();

        expect(scope.reportData.length).toEqual(2);
        expect(scope.reportData[0].code).toEqual("01A01");
        expect(scope.reportData[0].expiry_dates).toEqual([ '2016-05-31', '2017-07-31', '2018-01-31' ]);
        expect(scope.reportData[1].code).toEqual("01A02");
        expect(scope.reportData[1].expiry_dates).toEqual([ '2016-10-31', '2021-05-31' ]);
    });
});