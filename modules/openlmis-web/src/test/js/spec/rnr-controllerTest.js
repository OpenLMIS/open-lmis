describe('Requisition header controllers', function () {

  describe('RnrController', function () {

    var scope, ctrl;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function ($rootScope) {
      scope = $rootScope.$new();
    }));

    it('should get facilities', inject(function ($controller, $httpBackend, Facility) {
      expect(scope.facilities).toBeUndefined();
      $httpBackend.expect('GET', '/logistics/facilities.json').respond([
        {name:'Nexus S'},
        {name:'Motorola DROID'}
      ]);
      ctrl = $controller(RnrController, {$scope:scope, Facility:Facility});
      console.log(scope.facilities);
    }));

  });

});