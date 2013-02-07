describe("User", function () {

  beforeEach(module('openlmis.services'));

  describe("User  Role Assignment Controller", function () {

    var scope, $httpBackend, ctrl;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      ctrl = $controller(UserRoleAssignmentController, {$scope:scope});
    }));

    it('should display only available programs in add dropdown', function () {
      scope.$parent = {allSupportedPrograms:[
        {"program":{id:1, name:'p1'}},
        {"program":{id:2, name:'p2'}},
        {"program":{id:3, name:'p3'}},
        {"program":{id:4, name:'p4'}}
      ]};

      var existingProgramsMappedForUser = [
        {programId:2},
        {programId:3}
      ];
      scope.user = {roleAssignments:existingProgramsMappedForUser};

      var availablePrograms = scope.availablePrograms();

      expect(availablePrograms).toEqual([
        {"program":{id:1, name:'p1'}},
        {"program":{id:4, name:'p4'}}
      ]);
    });
  });
});