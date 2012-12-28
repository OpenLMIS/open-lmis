describe("Role", function () {

  beforeEach(module('openlmis.services'));

  describe("Create", function () {
    var ctrl, scope, $httpBackend;
    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      $httpBackend.expectGET('/rights.json').respond({"rights":"test rights"});
      ctrl = $controller(SaveRoleController, {$scope:scope, $routeParams:{} });
    }));

    it('should save a role', function () {
      $httpBackend.expectPOST('/roles.json').respond({"success":"Saved successfully"});
      scope.roleForm = {invalid:false};
      scope.saveRole();
      $httpBackend.flush();
      expect(scope.role).toEqual({rights:[]});
      expect(scope.rights).toEqual("test rights");
    });

  });

  describe("Edit", function () {
      var ctrl, scope, $httpBackend;

      beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('/roles/123.json').respond({"role":"test role"});
        $httpBackend.expectGET('/rights.json').respond({"rights":"test rights"});
        ctrl = $controller(SaveRoleController, {$scope:scope, $routeParams:{id:123} });
      }));

      it('should update a role', function () {
        $httpBackend.expectPUT('/roles/123.json').respond(
          {"success":"success"});
        scope.roleForm = {invalid:false};
        scope.saveRole();
        $httpBackend.flush();
        expect(scope.role).toEqual("test role");
        expect(scope.rights).toEqual("test rights");
      });

    });
});

