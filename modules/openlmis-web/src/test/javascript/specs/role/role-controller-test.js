describe("Role", function () {

  beforeEach(module('openlmis.services'));

  describe("Create Role", function () {

    it('should create a role', function () {
      var ctrl, scope, $httpBackend;
      inject(function ($rootScope, _$httpBackend_, $controller) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('/rights.json').respond({"rights":"test rights"});
        ctrl = $controller(SaveRoleController, {$scope:scope, $routeParams:{} });
      });

      $httpBackend.expectPOST('/roles.json').respond({"success":"Saved successfully"});

      scope.role.name = "roleName";
      scope.role.rights= ["right1"];

      scope.saveRole();
      $httpBackend.flush();
    });
  });

  describe("Edit Role", function () {

    it('should update a role', function () {
      var ctrl, scope, $httpBackend;
      inject(function ($rootScope, _$httpBackend_, $controller) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        $httpBackend.expectGET('/roles/123.json').respond({"role":"test role"});
        $httpBackend.expectGET('/rights.json').respond({"rights":"test Rights"});
        ctrl = $controller(SaveRoleController, {$scope:scope, $routeParams:{id:123} });
      });

      $httpBackend.expectPUT('/roles/123.json').respond(
        {"success":"success"});
      scope.role.name = "name";
      scope.role.rights = ["right1"]
      scope.saveRole();
      $httpBackend.flush();
    });
  });

  describe("updateRights()", function () {
    var rightList, role, ctrl, scope, $httpBackend;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
      role = {"id":1, "name":"Admin", "rights":[
        {"right":"RIGHT1", "rightName":"Right1"},
        {"right":"RIGHT2", "rightName":"Right2"}
      ]};
      rightList = [
        {"right":"RIGHT1", "rightName":"Right1"},
        {"right":"RIGHT2", "rightName":"Right2"},
        {"right":"RIGHT3", "rightName":"Right3"}
      ];
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      $httpBackend.expectGET('/roles/123.json').respond({"role":role});
      $httpBackend.expectGET('/rights.json').respond({"rights":rightList});
      ctrl = $controller(SaveRoleController, {$scope:scope, $routeParams:{id:123} });
      $httpBackend.flush();
      expect(scope.role).toEqual(role);
      expect(scope.rights).toEqual(rightList);
    }));

    it('should add the selected rights to the role', function () {
      scope.updateRights(true, rightList[2]);

      expect(scope.role.rights.length).toEqual(3);
      expect(scope.role.rights[0]).toEqual(rightList[0]);
      expect(scope.role.rights[1]).toEqual(rightList[1]);
      expect(scope.role.rights[2]).toEqual(rightList[2]);
    });

    it('should remove the right from role, if not selected', function () {
      scope.updateRights(false, rightList[0]);
      expect(scope.role.rights.length).toEqual(1);
      expect(scope.role.rights[0]).toEqual(role.rights[1]);
    });


  });
});

