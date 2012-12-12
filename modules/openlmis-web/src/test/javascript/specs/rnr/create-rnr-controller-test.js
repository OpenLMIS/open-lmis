describe('CreateRnrController', function () {

    var scope, ctrl, httpBackend, location, requisitionHeader, controller;

    beforeEach(module('openlmis.services'));

    beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller) {
        scope = $rootScope.$new();
        location = $location;
        controller = $controller;
        httpBackend = $httpBackend;
        scope.$parent.facility = "10134";
        scope.$parent.program = {code:"programCode"};
        scope.saveRnrForm = {$error:{ rnrError:false }};

        requisitionHeader = {"requisitionHeader":{"facilityName":"National Warehouse",
            "facilityCode":"10134", "facilityType":{"code":"Warehouse"}, "facilityOperatedBy":"MoH", "maximumStockLevel":3, "emergencyOrderPoint":0.5,
            "zone":{"label":"state", "value":"Arusha"}, "parentZone":{"label":"state", "value":"Arusha"}}};

        httpBackend.when('GET', '/logistics/facility/10134/requisition-header.json').respond(requisitionHeader);
        httpBackend.when('GET', '/logistics/rnr/programCode/columns.json').respond({"rnrColumnList":[
            {"testField":"test"}
        ]});

        ctrl = controller(CreateRnrController, {$scope:scope, $location:location});
    }));

    it('should get header data', function () {
        httpBackend.flush();
        expect(scope.header).toEqual({"facilityName":"National Warehouse",
            "facilityCode":"10134", "facilityType":{"code":"Warehouse"}, "facilityOperatedBy":"MoH", "maximumStockLevel":3, "emergencyOrderPoint":0.5,
            "zone":{"label":"state", "value":"Arusha"}, "parentZone":{"label":"state", "value":"Arusha"}});
    });

    it('should get list of Rnr Columns for program', function () {
        httpBackend.flush();
        expect([
            {"testField":"test"}
        ]).toEqual(scope.programRnRColumnList);
    });

    it('should save work in progress for rnr', function () {
        scope.$parent.rnr = {"id":"rnrId"};
        httpBackend.expect('POST', '/logistics/rnr/rnrId/save.json').respond(200);
        scope.saveRnr();
        httpBackend.flush();
        expect(scope.message).toEqual("R&R saved successfully!");
    });

    it('should not save work in progress when for invalid form', function () {
        scope.saveRnrForm.$error.rnrError = [
            {}
        ];
        scope.saveRnr();
        expect(scope.error).toEqual("Please correct errors before saving.");
    });

    it('should validate  integer field', function () {
        expect(scope.positiveInteger(100)).toEqual(true);
        expect(scope.positiveInteger(0)).toEqual(true);
        expect(scope.positiveInteger(-1)).toEqual(false);
        expect(scope.positiveInteger('a')).toEqual(false);
        expect(scope.positiveInteger(5.5)).toEqual(false);
    });

    it('should validate float field', function () {
        expect(scope.positiveFloat(100)).toEqual(true);
        expect(scope.positiveFloat(1.000)).toEqual(true);
        expect(scope.positiveFloat(0.0)).toEqual(true);
        expect(scope.positiveFloat(0.01)).toEqual(true);
        expect(scope.positiveFloat(1.000001)).toEqual(false);
        expect(scope.positiveFloat(100.001)).toEqual(false);
        expect(scope.positiveFloat(-1.0)).toEqual(false);
        expect(scope.positiveFloat('a')).toEqual(false);
    });

    describe('Fill consumption when it is marked as a computed column', function () {

        beforeEach(function () {
            httpBackend.expect('GET', '/logistics/rnr/programCode/columns.json').respond({"rnrColumnList":[
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
            ]});
            ctrl = controller(CreateRnrController, {$scope:scope, $location:location});
        });

        it('should not calculate consumption when one of the dependant columns is not set', function () {
            scope.$parent.rnr = {"id":"rnrId", "lineItems":[
                {"id":1, "beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "lossesAndAdjustments":3, "stockInHand":null}
            ]};

            httpBackend.flush();

            scope.fillCalculatedRnrColumns(0);
            expect(null).toEqual(scope.$parent.rnr.lineItems[0].quantityDispensed);
        });


        it('should calculate consumption', function () {
            scope.$parent.rnr = {"id":"rnrId", "lineItems":[
                {"id":1, "beginningBalance":5, "quantityReceived":20, "quantityDispensed":null, "lossesAndAdjustments":5, "stockInHand":10}
            ]};

            httpBackend.flush();

            scope.fillCalculatedRnrColumns(0);
            expect(10).toEqual(scope.$parent.rnr.lineItems[0].quantityDispensed);
        });

    });

    describe('Fill stock in hand when it is marked as a computed column', function () {

        beforeEach(function () {
            httpBackend.expect('GET', '/logistics/rnr/programCode/columns.json').respond({"rnrColumnList":[
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}}
            ]});
            ctrl = controller(CreateRnrController, {$scope:scope, $location:location});
        });

        it('should not calculate stock in hand when one of the dependant columns is not set', function () {
            scope.$parent.rnr = {"id":"rnrId", "lineItems":[
                {"id":1, "beginningBalance":1, "quantityReceived":2, "quantityDispensed":1, "lossesAndAdjustments":null, "stockInHand":null}
            ]};

            httpBackend.flush();

            scope.fillCalculatedRnrColumns(0);
            expect(null).toEqual(scope.$parent.rnr.lineItems[0].stockInHand);
        });

        it('should calculate stock in hand', function () {
            scope.$parent.rnr = {"id":"rnrId", "lineItems":[
                {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":2, "lossesAndAdjustments":4, "stockInHand":null}
            ]};

            httpBackend.flush();

            scope.fillCalculatedRnrColumns(0);
            expect(5).toEqual(scope.$parent.rnr.lineItems[0].stockInHand);
        });
    });

    describe('Fill normalized consumption', function () {

        beforeEach(function () {
            httpBackend.expect('GET', '/logistics/rnr/programCode/columns.json').respond({"rnrColumnList":[
                {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
                {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
                {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
                {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
                {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
                {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
                // TODO : another test without this param
                {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
            ]});
            ctrl = controller(CreateRnrController, {$scope:scope, $location:location});
        });

        it('should calculate normalized consumption when all dependant columns are set', function () {
            scope.$parent.rnr = {"id":"rnrId", "lineItems":[
                {"id":1, "beginningBalance":1, "quantityReceived":10, "quantityDispensed":null, "lossesAndAdjustments":4, "stockInHand":2, "stockOutDays":5, "newPatientCount":0}
            ]};

            httpBackend.flush();

            scope.fillCalculatedRnrColumns(0);
            expect("5.29").toEqual(scope.$parent.rnr.lineItems[0].normalizedConsumption.toFixed(2));
        })
    });
});
