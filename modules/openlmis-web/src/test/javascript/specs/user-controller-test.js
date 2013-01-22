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

    it('should give success message if save successful', function () {
      scope.user = {"userName":"User420"};
      $httpBackend.expectPOST('/admin/users.json').respond(200, {"success":"Saved successfully"});
      scope.saveUser();
      $httpBackend.flush();
      expect("Saved successfully").toEqual(scope.message);
      expect(scope.showError).toBeFalsy();
    });

    it('should give error message if save not successful', function () {
      scope.user = {"userName":"User420"};
      $httpBackend.expectPOST('/admin/users.json').respond(400, {"error":"errorMsg"});
      scope.saveUser();
      $httpBackend.flush();
      expect("errorMsg").toEqual(scope.error);
      expect(scope.showError).toBeTruthy();
    });

    it("should throw error when username contains space", function() {
      scope.user = {"userName":"User 420"};
      expect(scope.validateUserName()).toBeTruthy();
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
      $httpBackend.when('GET', '/admin/user/1.json').respond({"userName":"User420"});
      ctrl = $controller(UserController, {$scope:scope, $routeParams:routeParams, user:user});
    }));


    it('should get user', function () {
      expect(scope.user).toEqual(user);
    });

  });

});