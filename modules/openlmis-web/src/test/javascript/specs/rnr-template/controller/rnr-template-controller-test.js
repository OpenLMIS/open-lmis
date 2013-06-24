/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('Rnr Template controllers', function () {

  describe('SaveRnrTemplateController', function () {

    var scope, ctrl, $httpBackend, location, rnrColumnList, sources, rnrTemplateForm, program, routeParams, messageService;

    beforeEach(module('openlmis.services'));
    beforeEach(module('openlmis.localStorage'));

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, $routeParams,_messageService_) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      location = $location;
      routeParams = $routeParams;
      messageService = _messageService_;

      rnrColumnList = [
        {"id":1, "name":"product_code", "sourceConfigurable":true, "source":{'code':"U"}, "formulaValidationRequired":true, "visible":true},
        {"id":2, "name":"product", "sourceConfigurable":true, "source":{'code':"U"}, "formulaValidationRequired":true, "visible":true}
      ];

      sources = [
        {"code":"U", "description":"User Input"},
        {"code":"C", "description":"Calculated"}
      ];

      rnrTemplateForm = { 'rnrColumns':rnrColumnList, 'sources':sources};
      program = {id:1, name:'HIV'};
      ctrl = $controller(SaveRnrTemplateController, {$scope:scope, $location: location, rnrTemplateForm:rnrTemplateForm, program:program});

    }));

    it('should set program in scope', function () {
      expect(program).toEqual(scope.program);
    });

    it('should save R&R template', function() {
      routeParams.programId = 1;
      $httpBackend.expect('POST', '/program/1/rnr-template.json').respond(200);
      scope.save();
      $httpBackend.flush();
    });

    it('should save R&R template and redirect to select program page', function() {
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
      expect(scope.validateFormula).toBeTruthy();
    });

    it('should toggle arithmetic validation flag', function () {
      spyOn(messageService, 'get').andCallFake(function(arg) {
        if(arg == 'label.on') return 'ON';
        if(arg == 'label.off') return 'OFF';
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

    it('should set arithmetic validation message shown flag to False', function () {
      scope.rnrColumns = rnrColumnList;
      scope.rnrColumns[0].source.code = 'C';
      scope.setArithmeticValidationMessageShown();
      expect(scope.arithmeticValidationMessageShown).toBeFalsy();
    });

  });
});