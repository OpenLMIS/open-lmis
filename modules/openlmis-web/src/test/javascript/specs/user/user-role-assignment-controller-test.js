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
        {id:1, name:'p1'},
        {id:2, name:'p2'},
        {id:3, name:'p3'},
        {id:4, name:'p4'}
      ]};

      var existingProgramsMappedForUser = [
        {programId:2},
        {programId:3}
      ];
      scope.user = {roleAssignments:existingProgramsMappedForUser};

      var availablePrograms = scope.availablePrograms();

      expect(availablePrograms).toEqual([
        {id:1, name:'p1'},
        {id:4, name:'p4'}
      ]);
    });
  });
});