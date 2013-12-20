/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */


describe('custom horizontal scroll', function () {

  var element, scrollElement, scope, timeout;
  beforeEach(module('rnr'));

  beforeEach(inject(function ($compile, $rootScope, $timeout) {
    element = angular.element('<div id="scrollContainer" custom-horizontal-scroll style="width: 100%; overflow: auto"></div>');
    scrollElement = angular.element('<div id="itemToBeScrolled" class="scrollable"></div>');
    element.append(scrollElement);
    scope = $rootScope.$new();
    timeout = $timeout;
    $compile(element)(scope);
  }));

  afterEach(function () {
    element.remove();
  });

  function initScrollElementAndAppendToBody(widthPassed, heightPassed) {
    scrollElement.css({
      width: widthPassed + 'px',
      minHeight: heightPassed + 'px',
      background: '#999'
    });

    $('body').append(element);
    $(window).trigger('resize');
  }

  //TODO- Fix failing test
  xit('should add a custom scroll to a div', function () {

    var mockWindowWidth = $(window).width() + 1000;

    initScrollElementAndAppendToBody(mockWindowWidth, 450);
    var displayScrollBar = $('.scrollbar').is(':visible');

    expect(displayScrollBar).toBeTruthy();

  });

  it('should not add a custom scroll to a div if the div width is smaller than the parent div', function () {

    var mockWindowWidth = $(window).width() / 2;

    initScrollElementAndAppendToBody(mockWindowWidth, 450);
    var displayScrollBar = $('.scrollbar').is(':visible');

    expect(displayScrollBar).toBeFalsy();

  });

});