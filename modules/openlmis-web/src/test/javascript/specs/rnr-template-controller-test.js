describe('Rnr Template controllers', function () {

  describe('SaveRnrTemplateController', function () {

    var scope, ctrl, $httpBackend, location, rnrColumnList, sources, rnrTemplateForm;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      location = $location;
      scope.program = {code:"programCode"};

      rnrColumnList = [
        {"id":1, "name":"product_code"},
        {"id":2, "name":"product"}
      ];

      sources = [
        {"code":"U", "description":"User Input"},
        {"code":"C", "description":"Calculated"}
      ];

      rnrTemplateForm = { 'rnrColumns': rnrColumnList, 'sources': sources};


      ctrl = $controller(SaveRnrTemplateController, {$scope:scope,rnrTemplateForm: rnrTemplateForm});
    }));

    it('should get list of rnr columns for configuring', function () {
      expect(scope.rnrColumns).toEqual(rnrColumnList);
      expect(scope.sources).toEqual(sources);
    });

    it('should toggle arithmetic validation flag', function() {
      scope.validate = true;
      scope.toggleValidateFormulaFlag();
      expect(scope.formulaValidated).toBeFalsy();
    });



  });
});