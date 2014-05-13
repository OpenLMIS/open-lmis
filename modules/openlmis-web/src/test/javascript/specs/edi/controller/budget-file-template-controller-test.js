/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Budget File Template Controller", function () {
  var scope, controller, httpBackend, messageService, budgetFileTemplate, $q, timeout, deferredObject, dateFormats, location;

  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, $controller, $httpBackend, _messageService_, $location) {
    messageService = _messageService_;
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    location = $location;

    budgetFileTemplate = {
      "configuration": {
        "id": null,
        "createdBy": null,
        "modifiedBy": null,
        "createdDate": 1378965630040,
        "modifiedDate": 1378965630040,
        "headerInFile": false
      },
      "columns": [
        {
          "id": 1,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "facilityCode",
          "dataFieldLabel": "header.facility.coder",
          "position": 1,
          "include": true,
          "mandatory": true
        },
        {
          "id": 2,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "programCode",
          "dataFieldLabel": "header.program.code",
          "position": 2,
          "include": true,
          "mandatory": true
        },
        {
          "id": 3,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "periodStartDate",
          "dataFieldLabel": "header.period.start.date",
          "position": 3,
          "include": true,
          "mandatory": true,
          "datePattern": "dd/MM/yy"

        },
        {
          "id": 4,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "allocatedBudget",
          "dataFieldLabel": "header.allocatedBudget",
          "position": 4,
          "include": true,
          "mandatory": true
        },
        {
          "id": 5,
          "createdDate": 1378965630041,
          "modifiedDate": 1378965630041,
          "name": "notes",
          "dataFieldLabel": "header.notes",
          "position": 5,
          "include": false,
          "mandatory": false
        }
      ]
    }

    dateFormats = [
      {"format": "ddMMyyyy", "orderDate": true},
      {"format": "dd/MM/yyyy", "orderDate": true},
      {"format": "dd-MM-yyyy", "orderDate": true},
      {"format": "MMddyy", "orderDate": true},
      {"format": "yyyy/MM/dd", "orderDate": false}
    ];

    controller(BudgetFileTemplateController, {$scope: scope, budgetFileTemplate: budgetFileTemplate, dateFormats: dateFormats});
  }));

  it('should save budget file template', function () {
    httpBackend.expect('POST', '/budget-file-template.json').respond(200, {"success": "saved successfully"});
    spyOn(location, 'path');
    spyOn(messageService, 'get').andReturn("saved successfully");
    scope.saveBudgetFileTemplate();
    httpBackend.flush();
    expect(scope.message).toEqual("saved successfully");
    expect(scope.error).toEqual("");
    expect(location.path).toHaveBeenCalledWith("configure-system-settings");
  });

  it('should not save budget file template if position is invalid', function () {
    budgetFileTemplate.columns[0].position = "";
    scope.saveBudgetFileTemplate();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("file.invalid.position");
  });

  it('should not save budget file template if position is zero', function () {
    budgetFileTemplate.columns[0].position = 0;
    scope.saveBudgetFileTemplate();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("file.invalid.position");
  });

  it('should not save budget file template if position is duplicate', function () {
    budgetFileTemplate.columns[0].position = budgetFileTemplate.columns[1].position;
    scope.saveBudgetFileTemplate();
    expect(scope.message).toEqual("");
    expect(scope.error).toEqual("file.duplicate.position");
  });

});