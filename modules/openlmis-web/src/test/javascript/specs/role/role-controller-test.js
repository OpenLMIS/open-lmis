describe("Role", function () {
  describe("Role Controller", function () {
    var ctrl, scope, $httpBackend;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('/rights.json').respond({"rights":"test list"})
        ctrl = $controller(SaveRoleController, {$scope:scope, rights:{} });
      }
    ));

    it('should save a role', function () {
      $httpBackend.expectPOST('/role.json').respond(200, {"success":"Saved successfully"});
      scope.roleForm = {invalid:false};
      scope.saveRole();
      $httpBackend.flush();
    });

  });
});

