/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/28/14
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */

describe("Dashboard Controller",function (){
    var scope,rootScope, ctrl, httpBackend, location, facilities;

    beforeEach(module('openlmis'));
    beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller) {
        scope = $rootScope.$new();
        rootScope = $rootScope;
        rootScope.hasPermission = function () {
            return true;
        };
        location = $location;
        httpBackend = $httpBackend;
        facilities = [
            {"id": "10134", "name": "National Warehouse", "description": null, "geographicZone" :{"id": 505}}
        ];
        ctrl = $controller(AdminDashboardController, {$scope: scope, $rootScope: rootScope});

    }));

  /*  it('should load item fill rate data for selected facility ', function (){
        scope.filterObject = {};
        scope.filterObject.facilityId =  10135;
        scope.filterObject.geographicZoneId = facilities[0].geographicZone.id;
        scope.filterObject.productIdList = [211,234];
        scope.filterObject.programId = 1;

        var itemFillRates = [
            {"product": "product 1", "fillRate": 45},
            {"product": "product 2", "fillRate": -55}
        ];

        httpBackend.expectGET('/dashboard/itemFillRate.json?geographicZoneId=505&facilityId=10135&programId=1&periodId=4&productIdList='+scope.filterObject.productIdList).respond({"itemFillRate": itemFillRates});
        scope.loadFillRates();

        httpBackend.flush();

        expect(scope.itemFills).toEqual(itemFillRates);
    });*/

});
