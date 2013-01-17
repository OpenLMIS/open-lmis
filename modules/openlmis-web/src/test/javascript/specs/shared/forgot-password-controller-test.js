describe("ForgotPasswordController", function () {


  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    controller = $controller(ForgotPasswordController, {$scope:scope});
  }));

  it("Should give error message if User does not enter both username and email", function () {
    scope.username = "";
    scope.email = "";
    scope.sendForgotPasswordEmail();
    expect(scope.error).toEqual("Please enter either your Email or Username");
  });

  it("Should send the forgot password email when user enters either username or email", function () {
    scope.user = {"userName":"Admin123"};
    scope.email = "";
    var user = {username:"Admin123"};
    $httpBackend.expectPOST('/forgot-password.json').respond(200, {"user":user});
    scope.sendForgotPasswordEmail();
    expect(scope.submitDisabled).toEqual(true);
    expect(scope.submitButtonLabel).toEqual("Sending...");
  });
});
