  describe('CreateRnrController',function() {

    var scope, ctrl, $httpBackend, location,requisitionHeader;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function ($rootScope,_$httpBackend_,$controller,$location, $http) {
      scope = $rootScope.$new();
      $httpBackend=_$httpBackend_;
      location=$location;
      requisitionHeader = {"requisitionHeader":{"facilityName":"National Warehouse",
        "facilityCode":"10134","facilityType":{"code":"Warehouse"},"facilityOperatedBy":"MoH","maximumStockLevel":3,"emergencyOrderPoint":0.5,
        "zone":{"label":"state","value":"Arusha"},"parentZone":{"label":"state","value":"Arusha"}}};
      scope.$parent.facility = "10134";
      scope.$parent.program={code:"programCode"};

      $httpBackend.expectGET('/logistics/facility/10134/requisition-header.json').respond(requisitionHeader);
      $httpBackend.expectGET('/logistics/rnr/programCode/columns.json').respond({"rnrColumnList":[{"testField":"test"}]});
      ctrl = $controller(CreateRnrController, {$scope:scope, $location:location});
      scope.saveRnrForm = {$error : { rnrError: false }};
    }));

    it('should get header data', function () {
      $httpBackend.flush();
      expect(scope.header).toEqual({"facilityName":"National Warehouse",
        "facilityCode":"10134","facilityType":{"code":"Warehouse"},"facilityOperatedBy":"MoH","maximumStockLevel":3,"emergencyOrderPoint":0.5,
        "zone":{"label":"state","value":"Arusha"},"parentZone":{"label":"state","value":"Arusha"}});
    });

    it('should get list of Rnr Columns for program', function () {
      $httpBackend.flush();
      expect(scope.programRnRColumnList).toEqual([{"testField":"test"}]);
    });

    it('should save work in progress for rnr', function(){
            scope.$parent.rnr = {"id":"rnrId"};
            $httpBackend.expectPOST('/logistics/rnr/rnrId/save.json').respond(200);
            scope.saveRnr();
            $httpBackend.flush();
            expect(scope.message).toEqual("R&R saved successfully!");
    });

    it('should not save work in progress when for invalid form', function(){
         scope.saveRnrForm.$error.rnrError = [{}];
         scope.saveRnr();
         expect(scope.error).toEqual("Please correct errors before saving.");
    });

    it('should validate  integer field', function(){
         var valid=   scope.positiveInteger(100);
         expect(true).toEqual(valid);
         var valid=   scope.positiveInteger(0);
         expect(true).toEqual(valid);
         valid=   scope.positiveInteger(-1);
         expect(false).toEqual(valid);
         valid=   scope.positiveInteger('a');
         expect(false).toEqual(valid);
         valid=   scope.positiveInteger(5.5);
         expect(false).toEqual(valid);
    });

    it('should validate  float field', function(){
         var valid=   scope.positiveFloat(100);
         expect(true).toEqual(valid);

         valid= scope.positiveFloat(1.000);
         expect(true).toEqual(valid);

         valid=   scope.positiveFloat(0.0);
         expect(true).toEqual(valid);

         valid=   scope.positiveFloat(0.01);
         expect(true).toEqual(valid);

         valid= scope.positiveFloat(1.000001);
         expect(false).toEqual(valid);

         valid=   scope.positiveFloat(100.001);
         expect(false).toEqual(valid);

         valid=   scope.positiveFloat(-1.0);
         expect(false).toEqual(valid);

         valid=   scope.positiveFloat('a');
         expect(false).toEqual(valid);

    });

    it('should calculate consumption', function() {

    });
  });
