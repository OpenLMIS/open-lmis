describe("HeaderController", function () {

  beforeEach(module('openlmis.services'));

  beforeEach(module('openlmis.localStorage'));

  var scope, ctrl,httpBackend,messageService;

  beforeEach(inject(function ($rootScope, $controller,_messageService_,_$httpBackend_) {
    httpBackend = _$httpBackend_;
    scope = $rootScope.$new();
    messageService = _messageService_;
    spyOn(messageService, 'populate');
    httpBackend.when('GET','/user-context.json').respond({"id":123, "userName":"User420"});
    ctrl = $controller(HeaderController, {$scope:scope, messageService:messageService});
  }));

  it('should populate all messages', function () {
    expect(messageService.populate).toHaveBeenCalled();
  });

});