/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Rnr body', function () {
  var elm , scope, compile, timeout;
  var windowHeight = window.innerHeight;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($compile, $rootScope, $timeout) {
    elm = angular.element('<div class="rnr-body"></div>');
    timeout = $timeout;
    scope = $rootScope.$new();
    compile = $compile;
  }));

  it('should set the maximum height of the rnr-body to the windows height', function () {
    compile(elm)(scope);
    timeout.flush();

    expect(parseInt(elm.css('max-height'))).toEqual(windowHeight);
  });

  it('should set the maximum height of the rnr-body to the windows height on resize of window', function () {
    compile(elm)(scope);
    $(window).trigger('resize');

    expect(parseInt(elm.css('max-height'))).toEqual(windowHeight);
  });
});