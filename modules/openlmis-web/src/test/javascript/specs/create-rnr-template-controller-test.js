describe('Rnr Template controllers', function () {

  describe('SaveRnrTemplateController', function () {

    var scope, ctrl, $httpBackend, location,rnrColumnList;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function ($rootScope,_$httpBackend_,$controller,$location,$http) {
      scope = $rootScope.$new();
      $httpBackend=_$httpBackend_;
      location=$location;
      scope.program={code:"programCode"};

      rnrColumnList = [{"id":1,"name":"product_code","description":"Unique identifier for each commodity","position":1,"label":"Product Code","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"O","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]},{"id":2,"name":"product","description":"Primary name of the product","position":2,"label":"Product","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"R","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]}];
      $httpBackend.expectGET('/admin/rnr/programCode/columns.json').respond
        ({"rnrColumnList":[{"id":1,"name":"product_code","description":"Unique identifier for each commodity","position":1,"label":"Product Code","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"O","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]},{"id":2,"name":"product","description":"Primary name of the product","position":2,"label":"Product","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"R","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]}]});
      ctrl = $controller(SaveRnrTemplateController, {$scope:scope, $location:location});
    }));

    it('should get list of rnr columns for configuring', function() {
      $httpBackend.flush();
      expect(scope.rnrColumnList).toEqual(rnrColumnList);
    });

    it('should validate to error before saving', function() {
        scope.rnrColumnList = [{"id":1,"name":"product_code","description":"Unique identifier for each commodity","position":1,"label":"Product Code","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"O","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]},
                               {"id":2,"name":"product","description":"Primary name of the product","position":2,"label":"Product","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"R","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[{"id":1,"name":"product_code","description":"Unique identifier for each commodity","position":1,"label":"Product Code","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"O","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]}]}];
        scope.createProgramRnrTemplate();

        expect(scope.error).toEqual("Interdependent fields( product, product_code) can not be of type 'calculated' at the same time");
    });

    it('should validate to success before saving', function() {
        scope.rnrColumnList = [{"id":1,"name":"product_code","description":"Unique identifier for each commodity","position":1,"label":"Product Code","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["UserSelected"],"selectedColumnType":"UserSelected","formula":"","indicator":"O","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]},
                               {"id":2,"name":"product","description":"Primary name of the product","position":2,"label":"Product","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"R","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[{"id":1,"name":"product_code","description":"Unique identifier for each commodity","position":1,"label":"Product Code","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"O","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]}]}];
        $httpBackend.expectPOST('/admin/rnr/programCode/columns.json').respond(200);
        scope.createProgramRnrTemplate();
        $httpBackend.flush();
        expect(scope.error).toEqual("");
        expect(scope.message).toEqual("Template saved successfully!");
    });

  });
});