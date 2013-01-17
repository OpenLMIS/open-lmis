describe("User", function () {

  beforeEach(module('openlmis.services'));

  describe("User Controller", function () {

    var scope, $httpBackend, ctrl, routeParams, user;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      $httpBackend = _$httpBackend_;
      ctrl = $controller(UserController, {$scope:scope, user: {"userName":"shibha"}});
      scope.userForm = {$error:{ pattern:"" }};
    }));

    it('should give success message if save successful', function () {
      scope.user={"userName":"User420"};
      $httpBackend.expectPOST('/admin/users.json').respond(200, {"success":"Saved successfully"});
      scope.saveUser();
      $httpBackend.flush();
      expect("Saved successfully").toEqual(scope.message);
      expect(scope.showError).toBeFalsy();
    });

    it('should give error message if save not successful', function () {
      scope.user={"userName":"User420"};
      $httpBackend.expectPOST('/admin/users.json').respond(400, {"error":"errorMsg"});
      scope.saveUser();
      $httpBackend.flush();
      expect("errorMsg").toEqual(scope.error);
      expect(scope.showError).toBeTruthy();
    });

  });

});