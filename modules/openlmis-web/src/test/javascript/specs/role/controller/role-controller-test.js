describe("Role", function () {

  beforeEach(module('openlmis.services'));

  describe("Create Role", function () {
    var ctrl, scope, $httpBackend, rights;
    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      rights = [
        {"right": "CONFIGURE_RNR", "name": "configure rnr", "adminRight": "t"},
        {"right": "MANAGE_FACILITY", "name": "manage facility", "adminRight": "t"},
        {"right": "CREATE_REQUISITION", "name": "create requisition", "adminRight": "f"},
        {"right": "VIEW_REQUISITION", "name": "view requisition", "adminRight": "f"}
      ];
      $httpBackend.when('GET', '/rights.json').respond(200, {"rights": rights});
      ctrl = $controller(RoleController, {$scope: scope});
    }));

    it('should get all rights and separate them into admin and non-admin rights', function () {
      $httpBackend.flush();
      expect(scope.rights).toEqual(rights);
      expect(scope.adminRights).toEqual([
        {"right": "CONFIGURE_RNR", "name": "configure rnr", "adminRight": "t"},
        {"right": "MANAGE_FACILITY", "name": "manage facility", "adminRight": "t"}
      ]);
      expect(scope.nonAdminRights).toEqual([
        {"right": "CREATE_REQUISITION", "name": "create requisition", "adminRight": "f"},
        {"right": "VIEW_REQUISITION", "name": "view requisition", "adminRight": "f"}
      ]);
    });

    it('should create a role', function () {
      $httpBackend.expectPOST('/roles.json').respond({"success": "Saved successfully"});
      scope.role.name = "roleName";
      scope.role.rights = ["right1"];

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
        $httpBackend.expectGET('/roles/123.json').respond({"role": "test role"});
        $httpBackend.expectGET('/rights.json').respond({"rights": "test Rights"});
        ctrl = $controller(RoleController, {$scope: scope, $routeParams: {id: 123} });
      });

      $httpBackend.expectPUT('/roles/123.json').respond(
        {"success": "success"});
      scope.role.name = "name";
      scope.role.rights = ["right1"]
      scope.saveRole();
      $httpBackend.flush();
    });
  });

  describe("updateRights()", function () {
    var rightList, role, ctrl, scope, $httpBackend;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
      role = {"id": 1, "name": "Admin", "rights": [
        {"right": "CONFIGURE_RNR", "name": "configure rnr"},
        {"right": "MANAGE_FACILITY", "name": "manage facility"}
      ]};
      rightList = [
        {"right": "CONFIGURE_RNR", "name": "configure rnr"},
        {"right": "MANAGE_FACILITY", "name": "manage facility"},
        {"right": "CREATE_REQUISITION", "name": "create requisition"},
        {"right": "VIEW_REQUISITION", "name": "view requisition"}
      ];
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      $httpBackend.expectGET('/roles/123.json').respond({"role": role});
      $httpBackend.expectGET('/rights.json').respond({"rights": rightList});
      ctrl = $controller(RoleController, {$scope: scope, $routeParams: {id: 123} });
      $httpBackend.flush();
      expect(scope.role).toEqual(role);
      expect(scope.rights).toEqual(rightList);
    }));

    it('should add the selected rights to the role', function () {
      scope.updateRights(true, rightList[3]);

      expect(scope.role.rights.length).toEqual(3);
      expect(scope.role.rights[0]).toEqual(rightList[0]);
      expect(scope.role.rights[1]).toEqual(rightList[1]);
      expect(scope.role.rights[2]).toEqual(rightList[3]);
    });

    it('should remove the right from role, if not selected', function () {
      scope.updateRights(false, rightList[1]);
      expect(scope.role.rights.length).toEqual(1);
      expect(scope.role.rights[0]).toEqual(rightList[0]);
    });

    it('should add dependent right to role', function () {
      scope.updateRights(true, rightList[2]);
      expect(scope.role.rights.length).toEqual(4);
      expect(scope.role.rights[2]).toEqual(rightList[2]);
      expect(scope.role.rights[3]).toEqual(rightList[3]);
    });

    it('should not remove dependent right to role on deselection', function () {
      scope.updateRights(true, rightList[2]);
      expect(scope.role.rights.length).toEqual(4);
      scope.updateRights(false, rightList[2]);
      expect(scope.role.rights.length).toEqual(3);
      expect(scope.role.rights[2]).toEqual(rightList[3]);
    });

    it("should return true when rights that this right depends upon is selected", function () {
      scope.role.rights.push(rightList[2]);
      expect(scope.areRelatedFieldsSelected(rightList[3])).toBeTruthy();
    });

    it("should return false when rights that this right depends upon is not selected", function () {
      expect(scope.areRelatedFieldsSelected(rightList[3])).toBeFalsy();
    });

    it('should not add dependent right (VIEW_REQUISITION) to the role rights list if it is already present', function () {
      scope.role.rights.push(rightList[3]);
      expect(scope.role.rights.length).toEqual(3);

      scope.updateRights(true, rightList[2]);

      expect(scope.role.rights.length).toEqual(4);
      expect(scope.role.rights[0]).toEqual(rightList[0]);
      expect(scope.role.rights[1]).toEqual(rightList[1]);
      expect(scope.role.rights[2]).toEqual(rightList[3]);
      expect(scope.role.rights[3]).toEqual(rightList[2]);
    });


  });
});

