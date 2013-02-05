describe("User", function () {

  beforeEach(module('openlmis.services'));

  describe("User Controller", function () {

    var scope, $httpBackend, ctrl, routeParams, user;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      ctrl = $controller(UserController, {$scope:scope});
      scope.userForm = {$error:{ pattern:"" }};
    }));

    it('should update user successful', function () {
      scope.user = {"id":123, "userName":"User420"};
      $httpBackend.expectPUT('/users/123.json', scope.user).respond(200, {"success":"Saved successfully", "user":{id:123}});

      scope.saveUser();
      $httpBackend.flush();

      expect(scope.message).toEqual("Saved successfully");
      expect(scope.user).toEqual({id:123});
      expect(scope.showError).toBeFalsy();
      expect(scope.error).toEqual("");
    });

    it('should give error message if save not successful', function () {
      scope.user = {"userName":"User420"};
      $httpBackend.expectPOST('/users.json').respond(400, {"error":"errorMsg"});
      scope.saveUser();
      $httpBackend.flush();
      expect("errorMsg").toEqual(scope.error);
      expect(scope.showError).toBeTruthy();
      expect(scope.message).toEqual("");
    });

    it("should throw error when username contains space", function () {
      scope.user = {"userName":"User 420"};
      scope.validateUserName();
      expect(scope.userNameInvalid).toBeTruthy();
    });

    it("should get facilities when user enters 3 characters in search", function () {
      var facilityResponse = {"facilityList":[
        {"code":"F101"}
      ]};
      $httpBackend.expectGET('/facilitiesByCodeOrName.json?searchParam=F10').respond(facilityResponse);

      scope.query = "F10";
      scope.showFacilitySearchResults();

      $httpBackend.flush();
      expect(scope.filteredFacilities).toEqual([
        {"code":"F101"}
      ]);
    });

    it("should filter facilities by facility code when more than 3 characters are entered for search", function () {
      scope.facilityList = [
        {"name":"Village1", "code":"F10111"},
        {"name":"Village2", "code":"F10200"}
      ];

      scope.query = "F101";
      scope.showFacilitySearchResults();

      expect(scope.filteredFacilities).toEqual([
        {"name":"Village1", "code":"F10111"}
      ]);
    });

    it("should filter facilities by facility name when more than 3 characters are entered for search", function () {
      scope.facilityList = [
        {"name":"Village Dispensary", "code":"F10111"},
        {"name":"Facility2", "code":"F10200"}
      ];

      scope.query = "Vill";
      scope.showFacilitySearchResults();

      expect(scope.filteredFacilities).toEqual([
        {"name":"Village Dispensary", "code":"F10111"}
      ]);
    });

    it("should not create a user with empty role", function () {
      var userWithoutRole = {userName:"User 123", roleAssignments:[
        {programId:111, roleIds:[1]},
        {programId:222}
      ]};
      scope.userForm = {$error:{ required:false}};
      scope.user = userWithoutRole;

      expect(scope.saveUser()).toEqual(false);
    });

    it("should create a user with role assignments, if all required fields are present", function () {
      var userWithRoleAssignments = {userName:"User 123", roleAssignments:[
        {programId:111, roleIds:[1, 2, 3]},
        {programId:222, roleIds:[1]}
      ]};
      scope.userForm = {$error:{ required:false}};
      scope.user = userWithRoleAssignments;
      $httpBackend.expectPOST('/users.json', userWithRoleAssignments).respond(200, {"success":"Saved successfully", user:{id:500}});

      expect(scope.saveUser()).toEqual(true);
      $httpBackend.flush();
      expect(scope.message).toEqual("Saved successfully");
      expect(scope.user).toEqual({id:500});
      expect(scope.showError).toBeFalsy();
      expect(scope.error).toEqual("");
    });

    it("should create a user without role assignment, if all required fields are present", function () {
      var userWithoutRoleAssignment = {userName:"User 123"};
      scope.userForm = {$error:{ required:false}};
      scope.user = userWithoutRoleAssignment;
      $httpBackend.expectPOST('/users.json', userWithoutRoleAssignment).respond(200, {"success":"Saved successfully", user:{id:500}});

      expect(scope.saveUser()).toEqual(true);
      $httpBackend.flush();
      expect(scope.message).toEqual("Saved successfully");
      expect(scope.user).toEqual({id:500});
      expect(scope.showError).toBeFalsy();
      expect(scope.error).toEqual("");
    });

    it('should set facilitySelected in scope, whenever user selects a facility as "My Facility" when supported programs are not populated', function () {
      var facility = {id : 74, code:'F10', name : 'facilityName'};
      scope.allSupportedPrograms = undefined;

      var data = {};
      data.facility = facility;
      $httpBackend.expectGET('/admin/facility/'+facility.id+'.json').respond(data);
      $httpBackend.expectGET('/roles.json').respond(200);

      scope.setSelectedFacility(facility);

      $httpBackend.flush();

      expect(scope.facilitySelected).toEqual(facility);
    });

    it('should set facilitySelected in scope, whenever user selects a facility as "My Facility" when supported programs are populated', function () {
      var facility = {id : 74, code:'F10', name : 'facilityName'};
      scope.allSupportedPrograms = [{id : 1, code : 'HIV'}, {id : 2, code : 'ARV'}];

      var data = {};
      data.facility = facility;
      $httpBackend.expectGET('/roles.json').respond(200);

      scope.setSelectedFacility(facility);

      $httpBackend.flush();

      expect(scope.facilitySelected).toEqual(facility);
    });

  });

  describe("User Edit Controller", function () {

    var scope, $httpBackend, ctrl, user;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      routeParams.userId = 1;
      var user = {"id":1};
      $httpBackend = _$httpBackend_;
      $httpBackend.when('GET', '/users/1.json').respond({"userName":"User420"});
      ctrl = $controller(UserController, {$scope:scope, $routeParams:routeParams, user:user});
    }));


    it('should get user', function () {
      expect(scope.user).toEqual(user);
    });

  });

});