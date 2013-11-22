/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('set focus', function () {
  var element , scope, compile, timeout, inputVisibleEnable, inputInvisible, inputDisable;

  beforeEach(module('rnr'));
  beforeEach(inject(function ($compile, $rootScope, $timeout) {
    element = angular.element('<div id="rnr-body" set-focus></div>');
    inputVisibleEnable = angular.element('<input type="text">');
    inputInvisible = angular.element('<input type="text" style="display: none">');
    inputDisable = angular.element('<input type="text" disabled>');

    scope = $rootScope.$new();
    compile = $compile;
    timeout = $timeout;
    element.append(inputDisable);
    element.append(inputInvisible);
    element.append(inputVisibleEnable);
    compile(element)(scope);
    angular.element(document.body).append(element);
  }));
  afterEach(function () {
    element.remove()
  });

  it('should focus on the first enabled input field', function () {
    timeout.flush();
    expect($(inputVisibleEnable).is(':focus')).toBeTruthy();
  });

  it('should focus on the first enabled input field on click of an element', function () {
    element.trigger('click');
    expect($(inputVisibleEnable).is(':focus')).toBeTruthy();
  });
});