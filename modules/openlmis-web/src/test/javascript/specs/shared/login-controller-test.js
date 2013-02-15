describe("LoginController", function () {

  beforeEach(module('openlmis.services'));

  beforeEach(module('openlmis.localStorage'));

  var scope, ctrl,httpBackend,messageService, window, controller, mockWindowLocation;

  beforeEach(inject(function ($rootScope, $controller,_messageService_,_$httpBackend_) {
    httpBackend = _$httpBackend_;
    scope = $rootScope.$new();
    messageService = _messageService_;
    controller = $controller;

    window = {"location":{"href":"someOtherUrl.html"}};
    spyOn(window, 'location');
    ctrl = controller(LoginController, {$scope:scope, messageService:messageService, $window:window});

  }));


//TODO : Find way to test window.location
  it('should not login and show error when login fails', function () {
    scope.username = "john";
    scope.password = "openLmis";
    window = {"location":{"href":"someOtherUrl.html"}};
    ctrl = controller(LoginController, {$scope:scope, messageService:messageService, $window:window});

    spyOn(messageService, 'populate');
    httpBackend.when('POST','/j_spring_security_check').respond({"authenticated":false, "error":"true"});

    scope.doLogin();
    httpBackend.flush();

    expect(scope.loginError).toBe('The username or password you entered is incorrect. Please try again.');
  });

  it('should not login and show error when server returns error', function () {
    scope.username = "john";
    scope.password = "openLmis";


    spyOn(messageService, 'populate');
    httpBackend.when('POST','/j_spring_security_check').respond(500, {});

    scope.doLogin();
    httpBackend.flush();

    expect(scope.loginError).toBe('Server Error!!');
  });
});