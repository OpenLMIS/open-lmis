/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/28/14
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */

describe("Dashboard Controller",function (){
    var scope,rootScope, ctrl, $httpBackend, location, facilities;

    beforeEach(module('openlmis'));
    beforeEach(module('dashboard'));
/*

    beforeEach(inject(function (_$httpBackend_, $rootScope, $location, $controller) {
        scope = $rootScope.$new();
        rootScope = $rootScope;

        rootScope.hasPermission = function () {
            return true;
        };
        location = $location;
        $httpBackend = _$httpBackend_;

        facilities = [
            {"id": 10134, "name": "National Warehouse", "description": null, "geographicZone" :{"id": 505}}
        ];
        var userGeographicZoneList = _.map(facilities, function(facility){return facility.geographicZone;});// {"facilityList": facilities};
        var formInputValue =  {};
        var operationYears = {"years" : [2010,2011,2012,2013,2014]};

        var programs = {"programs":[
            {"id":1,"name":"ARV","code":"ARV","description":"ARV"},
            {"id":2,"name":"ILS","code":"ILS","description":"ILS"}
        ]};

        var schedules = {"schedules" : [
            {"id":1,"name":"Oct-Nov","code":"schedule1","description":"schedule1"},
            {"id":2,"name":"Sep-Oct","code":"schedule2","description":"schedule2"}
        ]};

        var periods = {"periods":[
            {"id": 3, "scheduleId": 2, "name": "Dec2012", "description": "Dec2012", "startDate": 1354300200000, "endDate": 1356892200000, "numberOfMonths": 1}
        ]};

        var facilityResponse = {"facilities":[
            {"id":1,"code":"F11","name":"lokesh"}
        ]};

        ctrl = $controller(AdminDashboardController, {$scope: scope, $rootScope: rootScope, userGeographicZoneList : userGeographicZoneList,formInputValue:formInputValue});

        scope.filterObject = {};
        scope.filterObject.facilityId =  10135;
        scope.filterObject.geographicZoneId = facilities[0].geographicZone.id;
        scope.filterObject.rgroupId = 1;
        scope.filterObject.periodId = 1;
        scope.filterObject.productIdList = [211,234];
        scope.filterObject.programId = 1;
        scope.filterObject.scheduleId = 1;

        $httpBackend.when('GET','/reports/operationYears.json').respond(operationYears);
        $httpBackend.when('GET','/reports/programs.json').respond(200,programs);
        $httpBackend.when('GET','/reports/schedules.json').respond(200,schedules);

    }));

    it('should load item fill rate data for selected facility ', function (){

        var itemFillRates = [
            {"product": "product 1", "fillRate": 45},
            {"product": "product 2", "fillRate": -55}
        ];
        var orderFill = {"fillRate" : 45.6 } ;

        $httpBackend.when('GET','/dashboard/itemFillRate.json?facilityId=10135&geographicZoneId=505&periodId=1&productListId=211&productListId=234&programId=1').respond({"itemFillRate": itemFillRates});
        $httpBackend.when('GET','/dashboard/orderFillRate.json?facilityId=10135&geographicZoneId=505&periodId=1&programId=1').respond({"orderFillRate": orderFill});
        scope.loadFillRates();
        $httpBackend.flush();

        expect(scope.itemFills).toEqual(itemFillRates);
        expect(scope.orderFill).toEqual(orderFill);
    });
*/

});
