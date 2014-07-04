/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Services", function () {
  var httpMock, successStub, failureStub;


  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend) {
    httpMock = $httpBackend;
    successStub = jasmine.createSpy();
    failureStub = jasmine.createSpy();

  }));

  afterEach(function () {
    httpMock.verifyNoOutstandingExpectation();
    httpMock.verifyNoOutstandingRequest();
  });

  describe("ApproveRnrService", function () {

    var requisitionForApprovalService;

    beforeEach(inject(function (RequisitionForApproval) {
      requisitionForApprovalService = RequisitionForApproval;
    }));

    it('should GET R&Rs pending for approval', function () {
      var requisitions = {"rnr_list": []};
      httpMock.expect('GET', "/requisitions-for-approval.json").respond(requisitions);
      requisitionForApprovalService.get({}, function (data) {
        expect(data.rnr_list).toEqual(requisitions.rnr_list);
      }, function () {
      });
      httpMock.flush();
    });
  });

  describe("SupplyLineSearchService", function () {

    var supplyLineSearchService;

    beforeEach(inject(function (SupplyLinesSearch) {
      supplyLineSearchService = SupplyLinesSearch;
    }));

    it('should GET searched supplyLines', function () {
      var supplyLinesResponse = {"supplyLines": [], "pagination": {}};
      httpMock.expect('GET', "/supplyLines/search.json").respond(supplyLinesResponse);
      supplyLineSearchService.get({}, function (data) {
        expect(data.supplyLines).toEqual(supplyLinesResponse.supplyLines);
        expect(data.pagination).toEqual(supplyLinesResponse.pagination);
      }, function () {
      });
      httpMock.flush();
    });
  });

  describe("programProductsFilter", function () {

    var programProductsFilter;

    beforeEach(inject(function (ProgramProductsFilter) {
      programProductsFilter = ProgramProductsFilter;
    }));

    it('should filter program products', function () {
      var programProductsFilterResponse = {"programProducts": []};
      var programId = 1, facilityTypeId = 2;
      httpMock.expectGET('/programProducts/filter/programId/' + programId + '/facilityTypeId/' + facilityTypeId + '.json')
        .respond(200, {programProductList: programProductsFilterResponse});

      programProductsFilter.get({'programId': programId, 'facilityTypeId': facilityTypeId},
        function (data) {
          successStub();
          expect(data.programProductList).toEqual(programProductsFilterResponse);
        },
        function () {
          failureStub();
        });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status', function () {
      var programId = 1, facilityTypeId = 2;

      httpMock.expectGET('/programProducts/filter/programId/' + programId + '/facilityTypeId/' + facilityTypeId + '.json')
        .respond(404);

      programProductsFilter.get({'programId': programId, 'facilityTypeId': facilityTypeId},
        function () {
          successStub();
        },
        function () {
          failureStub();
        });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("Facility type approved products", function () {

    var facilityTypeApprovedProducts;

    beforeEach(inject(function (FacilityTypeApprovedProducts) {
      facilityTypeApprovedProducts = FacilityTypeApprovedProducts;
    }));

    it('should GET searched FacilityTypeApprovedProducts', function () {
      var FacilityApprovedProductsResponse = {"facilityApprovedProducts": [], "pagination": {}};
      httpMock.expectGET("/facilityApprovedProducts.json").respond(200, FacilityApprovedProductsResponse);
      facilityTypeApprovedProducts.get({}, function (data) {
        expect(data.facilityApprovedProducts).toEqual(FacilityApprovedProductsResponse.facilityApprovedProducts);
        expect(data.pagination).toEqual(FacilityApprovedProductsResponse.pagination);
        successStub();
      }, function () {
        failureStub();
      });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while get', function () {
      httpMock.expectGET("/facilityApprovedProducts.json").respond(400);

      facilityTypeApprovedProducts.get({}, function () {
        successStub();
      }, function () {
        failureStub();
      });

      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });

    it('should add facility type approved product', function () {
      var successMessage = "Saved successfully";
      httpMock.expectPOST('/facilityApprovedProducts.json').respond(200, {"success": successMessage});

      facilityTypeApprovedProducts.save({}, {},
        function (data) {
          successStub();
          expect(data.success).toEqual(successMessage);
        },
        function () {
          failureStub();
        });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while post', function () {
      httpMock.expectPOST('/facilityApprovedProducts.json').respond(404);

      facilityTypeApprovedProducts.save({}, {},
        function () {
          successStub();
        },
        function () {
          failureStub();
        });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });

    it('should update facility type approved product', function () {
      var successMessage = "Updated successfully";
      httpMock.expectPUT('/facilityApprovedProducts.json').respond(200, {"success": successMessage});

      facilityTypeApprovedProducts.update({}, {},
        function (data) {
          successStub();
          expect(data.success).toEqual(successMessage);
        },
        function () {
          failureStub();
        });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status while updating', function () {
      httpMock.expectPOST('/facilityApprovedProducts.json').respond(404);

      facilityTypeApprovedProducts.save({}, {},
        function () {
          successStub();
        },
        function () {
          failureStub();
        });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });

  describe("Reports", function () {

    var reports;

    beforeEach(inject(function (Reports) {
      reports = Reports;
    }));

    it('should get report parameters', function () {
      var templateResponse = {"template": {}};
      var templateId = 1;
      httpMock.expectGET('/reports/' + templateId + '.json')
          .respond(200, {template: templateResponse});

      reports.get({'id': templateId},
          function (data) {
            successStub();
            expect(data.template).toEqual(templateResponse);
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).toHaveBeenCalled();
      expect(failureStub).not.toHaveBeenCalled();
    });

    it('should raise error if server does not respond with OK status', function () {
      var templateId = 1;

      httpMock.expectGET('/reports/' + templateId + '.json')
          .respond(404);

      reports.get({'id': templateId},
          function () {
            successStub();
          },
          function () {
            failureStub();
          });
      httpMock.flush();
      expect(successStub).not.toHaveBeenCalled();
      expect(failureStub).toHaveBeenCalled();
    });
  });
});