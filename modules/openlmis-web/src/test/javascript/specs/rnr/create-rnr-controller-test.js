describe('CreateRnrController', function () {

    var scope, ctrl, httpBackend, location, requisitionHeader, controller;

    beforeEach(module('openlmis.services'));
    beforeEach(module('openlmis.localStorage'));

    beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, _localStorageService_) {
        scope = $rootScope.$new();
        location = $location;
        controller = $controller;
        httpBackend = $httpBackend;
        scope.$parent.facility = "10134";
        scope.$parent.program = {code:"programCode"};
        scope.saveRnrForm = {$error:{ rnrError:false }};
        localStorageService = _localStorageService_;

        requisitionHeader = {"requisitionHeader":{"facilityName":"National Warehouse",
            "facilityCode":"10134", "facilityType":{"code":"Warehouse"}, "facilityOperatedBy":"MoH", "maximumStockLevel":3, "emergencyOrderPoint":0.5,
            "zone":{"label":"state", "value":"Arusha"}, "parentZone":{"label":"state", "value":"Arusha"}}};


        httpBackend.when('GET', '/logistics/facility/10134/requisition-header.json').respond(requisitionHeader);
        httpBackend.when('GET', '/logistics/rnr/programCode/columns.json').respond({"rnrColumnList":[
            {"testField":"test"}
        ]});
        httpBackend.when('GET', '/reference-data/currency.json').respond({"responseData":"$"});
        ctrl = controller(CreateRnrController, {$scope:scope, $location:location, localStorageService:localStorageService});
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

    it('should get Currency from service', function () {
        scope.getCurrency();
        httpBackend.flush();
        expect(scope.currency).toEqual("$");
    });

    it("should get undefined when the column name is quantityApproved and status is INITIATED", function(){
        scope.$parent.rnr = {"status":"INITIATED"};
        var isShown = scope.showSelectedColumn("quantityApproved");
        expect(isShown).toEqual(undefined);
    });

    it("should get undefined when the column name is quantityApproved and status is CREATED", function(){
        scope.$parent.rnr = {"status":"CREATED"};
        var isShown = scope.showSelectedColumn("quantityApproved");
        expect(isShown).toEqual(undefined);
    });

    it("should get 'defined' when the column name is not quantityApproved", function(){
        scope.$parent.rnr = {"status":"whatever"};
        var isShown = scope.showSelectedColumn("anyOtherColumn");
        expect(isShown).toEqual("defined");
    });

    it("should get 'defined' when the column name is quantityApproved and status is SUBMITTED", function(){
        scope.$parent.rnr = {"status":"SUBMITTED"};
        var isShown = scope.showSelectedColumn("quantityApproved");
        expect(isShown).toEqual("defined");
    });

    it("should get 'defined' when the column name is quantityApproved and status is SUBMITTED", function(){
        scope.$parent.rnr = {"status":"APPROVED"};
        var isShown = scope.showSelectedColumn("quantityApproved");
        expect(isShown).toEqual("defined");
    });
});
