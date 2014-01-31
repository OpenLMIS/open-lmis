/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Rnr Template controllers', function () {

  describe('SaveRnrTemplateController', function () {

    var scope, ctrl, $httpBackend, location, rnrColumnList, sources, rnrTemplateForm, program, routeParams, messageService;

    beforeEach(module('openlmis'));

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, $routeParams, _messageService_) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      location = $location;
      routeParams = $routeParams;
      messageService = _messageService_;

      rnrColumnList = [
        {"id": 2, "name": "productCode", "sourceConfigurable": true, "source": {'code': "U"}, "formulaValidationRequired": false, "visible": true, "position": 2},
        {"id": 1, "name": "skipped", "sourceConfigurable": true, "source": {'code': "U"}, "formulaValidationRequired": true, "visible": true, "position": 1},
        {"id": 3, "name": "product", "sourceConfigurable": true, "source": {'code': "U"}, "formulaValidationRequired": true, "visible": true, "position": 3},
        {"id": 4, "name": "beginningBalance", "sourceConfigurable": true, "source": {'code': "U"}, "formulaValidationRequired": true, "visible": true, "position": 4}
      ];

      sources = [
        {"code": "U", "description": "User Input"},
        {"code": "C", "description": "Calculated"}
      ];

      rnrTemplateForm = { 'rnrColumns': rnrColumnList, 'sources': sources};
      program = {id: 1, name: 'HIV'};
      ctrl = $controller(SaveRnrTemplateController,
        {$scope: scope, $location: location, rnrTemplateForm: rnrTemplateForm, program: program});

    }));

    it('should set program in scope', function () {
      spyOn(messageService, 'get');
      expect(scope.program).toEqual(program);
    });

    it('should filter sortable from non sortable columns and sort non sortable columns according to position', function () {
      var rnrNonSortableColumnList = [
        {"id": 1, "name": "skipped", "sourceConfigurable": true, "source": {'code': "U"}, "formulaValidationRequired": true, "visible": true, "position": 1},
        {"id": 2, "name": "productCode", "sourceConfigurable": true, "source": {'code': "U"}, "formulaValidationRequired": false, "visible": true, "position": 2},
        {"id": 3, "name": "product", "sourceConfigurable": true, "source": {'code': "U"}, "formulaValidationRequired": true, "visible": true, "position": 3}
      ];
      var rnrSortableColumnList = [
        {"id": 4, "name": "beginningBalance", "sourceConfigurable": true, "source": {'code': "U"}, "formulaValidationRequired": true, "visible": true, "position": 4}
      ];
      expect(scope.rnrSortableColumns).toEqual(rnrSortableColumnList);
      expect(scope.rnrNonSortableColumns).toEqual(rnrNonSortableColumnList);
    });


    it('should set arithmetic validation message shown flag to False', function () {
      scope.rnrColumns = rnrColumnList;
      scope.rnrColumns[0].source.code = 'C';
      scope.setArithmeticValidationMessageShown();
      expect(scope.arithmeticValidationMessageShown).toBeFalsy();
    });

    it('should save R&R template', function () {
      routeParams.programId = 1;
      $httpBackend.expect('POST', '/program/1/rnr-template.json').respond(200);
      scope.save();
      $httpBackend.flush();
    });

    it('should save R&R template and redirect to select program page', function () {
      spyOn(location, 'path').andCallThrough();
      spyOn(messageService, 'get');
      routeParams.programId = 1;
      $httpBackend.expect('POST', '/program/1/rnr-template.json').respond(200);
      scope.save();
      $httpBackend.flush();
      expect(location.path).toHaveBeenCalledWith('select-program');
      expect(messageService.get).toHaveBeenCalledWith('template.save.success');
    })

    it('should get list of rnr columns for configuring', function () {
      expect(scope.rnrColumns).toEqual(rnrColumnList);
      expect(scope.sources).toEqual(sources);
    });

    it('should set validateFormula flag on load', function () {
      expect(scope.validateFormula).toBeFalsy();
    });

    it('should toggle arithmetic validation flag', function () {
      spyOn(messageService, 'get').andCallFake(function (arg) {
        if (arg == 'label.on') return 'ON';
        if (arg == 'label.off') return 'OFF';
      })
      scope.validateFormula = true;
      scope.toggleValidateFormulaFlag();
      expect(scope.validateFormula).toBeFalsy();
      expect(scope.arithmeticValidationStatusLabel).toEqual("OFF");
      expect(scope.arithmeticValidationToggleLabel).toEqual("ON");
      scope.toggleValidateFormulaFlag();
      expect(scope.validateFormula).toBeTruthy();
      expect(scope.arithmeticValidationStatusLabel).toEqual("ON");
      expect(scope.arithmeticValidationToggleLabel).toEqual("OFF");
    });

    it('should set arithmetic validation message shown flag to True', function () {
      scope.rnrColumns = rnrColumnList;
      scope.setArithmeticValidationMessageShown();
      expect(scope.arithmeticValidationMessageShown).toBeTruthy();
    });
  });
});
