describe('Configure R&R template controller', function () {
  var location, scope;

  beforeEach(inject(function ($rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    $controller(ConfigureRnRTemplateController, {$scope:scope, $location:location, programs:[]});
  }));

  it('should take to configure page', function () {
    spyOn(location, 'path').andCallThrough();

    scope.configure(1);

    expect(location.path).toHaveBeenCalledWith('/create-rnr-template/1');
  });
});