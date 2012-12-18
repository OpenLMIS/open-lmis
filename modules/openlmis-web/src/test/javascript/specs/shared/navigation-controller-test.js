describe("Navigation", function () {

    beforeEach(module('openlmis.services'));

    beforeEach(module('openlmis.localStorage'));

    describe("NavigationController", function () {

        var scope, ctrl;

        beforeEach(inject(function ($rootScope, $controller,_localStorageService_) {
            scope = $rootScope.$new();
            localStorageService = _localStorageService_;
            spyOn(localStorageService, 'get').andReturn('MANAGE_FACILITY,UPLOADS');
            //localStorageService.add(localStorageKeys.RIGHT,'MANAGE_FACILITY,UPLOADS'); [Code runs in browser but is not supported on jasmine headless mode ]
            ctrl = $controller(NavigationController, {$scope:scope , localStorageService:localStorageService});
        }));

        it('should check permission', function () {
            expect(true).toEqual(scope.hasPermission("MANAGE_FACILITY"));
            expect(false).toEqual(scope.hasPermission("CREATE_REQUISITION"));
        });


        it('should set user rights into scope', function () {
            expect(scope.rights).toEqual('MANAGE_FACILITY,UPLOADS');
        });
    })
});