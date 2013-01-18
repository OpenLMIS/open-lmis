describe("User Search Controller", function () {

  var scope, $httpBackend, ctrl;
  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    scope.query = "joh";

    ctrl = $controller('UserSearchController', {$scope:scope});
  }));

  it('should get all users depending on search criteria when three characters are entered in search', function () {
    var userResponse = {"userList":[{"id":1,"firstName":"john","lastName":"Doe","email":"john_doe@gmail.com"}]};
    $httpBackend.when('GET', '/admin/search-user.json?userSearchParam=' + scope.query).respond(userResponse);

    scope.showUserSearchResults(scope.query);
    $httpBackend.flush();

    expect(scope.userList).toEqual([{"id":1,"firstName":"john","lastName":"Doe","email":"john_doe@gmail.com"}]);
  });

  it('should filter users when more than 3 characters are entered for search', function (){
    scope.query="john";
    scope.userList  = [{"id":1,"firstName":"john","lastName":"Doe","email":"john_doe@gmail.com"}];
    scope.showUserSearchResults(scope.query);
    expect(scope.filteredUsers).toEqual([{"id":1,"firstName":"john","lastName":"Doe","email":"john_doe@gmail.com"}]);
  });
});