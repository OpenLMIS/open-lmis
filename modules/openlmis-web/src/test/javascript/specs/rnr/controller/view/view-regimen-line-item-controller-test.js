/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('ViewRegimenLineItemController', function () {
  var scope;

  beforeEach(inject(function ($rootScope, $controller) {
    scope = $rootScope.$new();
    scope.rnr = {regimenLineItems: [
      {category: {name: "cName"}},
      {category: {name: "cName"}}
    ]};
    scope.regimenColumns = [
      {name: 'name', visible: true},
      {name: 'name1', visible: false}
    ];
    $controller(ViewRegimenLineItemController, {$scope: scope});
  }));

  it('should set visible columns for regimen', function () {
    expect(scope.visibleRegimenColumns).toEqual([
      {name: 'name', visible: true}
    ]);
  });

  it('should return false if category are same', function () {
    expect(scope.showCategory(1)).toBeFalsy();
  });

  it('should return true if category are different', function () {
    scope.rnr.regimenLineItems[0].category.name = 'bName';
    expect(scope.showCategory(1)).toBeTruthy();
  });
});