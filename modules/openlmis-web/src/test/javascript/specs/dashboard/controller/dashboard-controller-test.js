/**
 * Created with IntelliJ IDEA.
 * User: issa
 * Date: 2/28/14
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */

describe("Dashboard Controller",function (){
    var scope, ctrl, httpBackend, location, facilities, controller;

    beforeEach(module('openlmis'));
    beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, $routeParams) {
        scope = $rootScope.$new();
        location = $location;
        httpBackend = $httpBackend;
        facilities = [
            {"id": "10134", "name": "National Warehouse", "description": null}
        ];
        ctrl = $controller(AdminDashboardController, {$scope: scope, $rootScope: rootScope});

    }));

    it('Should')

});
