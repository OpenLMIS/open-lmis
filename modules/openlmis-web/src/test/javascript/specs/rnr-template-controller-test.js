describe('Rnr Template controllers', function () {

  describe('SaveRnrTemplateController', function () {

    var scope, ctrl, $httpBackend, location, rnrColumnList;

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

      $httpBackend.expectGET('/admin/rnr/programCode/columns.json').respond(
          {
            "rnrTemplateForm":{"rnrColumns":[
              {"id":1, "name":"product_code"},
              {"id":2, "name":"product"}
            ],
              "sources":[
                {"code":"U", "description":"User Input"},
                {"code":"C", "description":"Calculated"}
              ]
            }}
      );
      ctrl = $controller(SaveRnrTemplateController, {$scope:scope, $location:location});
    }));

    it('should get list of rnr columns for configuring', function () {
      $httpBackend.flush();
      expect(scope.rnrColumns).toEqual(rnrColumnList);
      expect(scope.sources).toEqual(sources);
    });

  });
});