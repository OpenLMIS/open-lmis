/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('Full screen', function () {
  var compile, element, scope, div, printElement, rnrBody;
  var spyElement;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($compile, $rootScope) {

    element = angular.element('<a full-screen href="" class="btn"><i class="icon-resize-full"></i></a>');
    div = angular.element('<div class="toggleFullScreen"></div>');
    printElement = angular.element('<div class="print-button"></div>');
    rnrBody = angular.element('<div></div>')

    compile = $compile;
    scope = $rootScope.$new();

    compile(element)(scope);
    compile(div)(scope);
    compile(printElement)(scope);
    compile(rnrBody);

    spyElement = spyOn(angular, "element").andCallFake(function (selector) {
      if (selector == '.toggleFullScreen') return div;
      if (selector == '.print-button') return printElement;
      return element;
    });
  }));


  it('should toggle class on click', function () {
    expect(element.children().attr('class')).toEqual('icon-resize-full');

    element.trigger('click');

    expect(element.children().attr('class')).toEqual('icon-resize-small');
  });


  it('should scroll to top', function () {
    compile(element)(scope);
    var spyScroll = spyOn(element, 'scrollTop').andReturn();

    element.trigger('click');

    expect(spyElement).toHaveBeenCalledWith(window);
    expect(spyScroll).toHaveBeenCalledWith(0);
  });


  describe('switching to full screen on click', function () {
    it('should slide up if not IE', function () {

      var spyScroll = spyOn(element, 'scrollTop').andCallThrough();
      var spySlideUP = spyOn(div, 'slideUp').andCallThrough();
      var spyAnimate = spyOn(printElement, 'animate').andCallThrough();
      var spyPrintShow = spyOn(printElement, 'show').andCallThrough();
      spyOn(window, 'browserIE').andCallFake(function () {
        return false;
      });

      element.trigger('click');

      expect(spyElement).toHaveBeenCalledWith(window);
      expect(spyElement).toHaveBeenCalledWith('.toggleFullScreen');
      expect(spyElement).toHaveBeenCalledWith('.print-button');
      expect(spyScroll).toHaveBeenCalledWith(0);
      expect(spySlideUP).toHaveBeenCalledWith({duration: 'slow', progress: 'progressFunc', complete: 'completeFunc'});
      expect(spyAnimate).toHaveBeenCalledWith({opacity: 1.0}, 1000);
      expect(spyPrintShow).toHaveBeenCalledWith();
    });

    it('should hide browser is IE', function () {
      var spyScroll = spyOn(element, 'scrollTop').andCallThrough();
      var spyHide = spyOn(div, 'hide').andCallThrough();
      var spyAnimate = spyOn(printElement, 'animate').andCallThrough();
      var spyPrintShow = spyOn(printElement, 'show').andCallThrough();
      spyOn(window, 'browserIE').andCallFake(function () {
        return true;
      });

      element.trigger('click');

      expect(spyElement).toHaveBeenCalledWith(window);
      expect(spyElement).toHaveBeenCalledWith('.toggleFullScreen');
      expect(spyElement).toHaveBeenCalledWith('.print-button');
      expect(spyScroll).toHaveBeenCalledWith(0);
      expect(spyHide).toHaveBeenCalledWith();
      expect(spyAnimate).toHaveBeenCalledWith({opacity: 1.0}, 1000);
      expect(spyPrintShow).toHaveBeenCalledWith();
    });

  });

  describe('switching back to original screen on click', function () {
    beforeEach(function () {
      element.click();
    });

    it('should slide down if not IE', function () {
      var spyScroll = spyOn(element, 'scrollTop').andCallThrough();
      var spySlideDown = spyOn(div, 'slideDown').andCallThrough();
      var spyAnimate = spyOn(printElement, 'animate').andCallThrough();
      spyOn(window, 'browserIE').andCallFake(function () {
        return false;
      });

      element.trigger('click');

      expect(spyElement).toHaveBeenCalledWith(window);
      expect(spyElement).toHaveBeenCalledWith('.toggleFullScreen');
      expect(spyElement).toHaveBeenCalledWith('.print-button');
      expect(spyScroll).toHaveBeenCalledWith(0);
      expect(spySlideDown).toHaveBeenCalledWith({duration: 'slow', progress: 'progressFunc', complete: 'completeFunc'});
      expect(spyAnimate).toHaveBeenCalledWith({opacity: 0}, 1000, jasmine.any(Function));

      var callBack = spyAnimate.calls[0].args[2];
      callBack();

      expect(printElement.css('display')).toEqual('none');

    });

    it('should show if IE', function () {
      var spyScroll = spyOn(element, 'scrollTop').andCallThrough();
      var spyShow = spyOn(div, 'show').andCallThrough();
      var spyAnimate = spyOn(printElement, 'animate').andCallThrough();
      spyOn(window, 'browserIE').andCallFake(function () {
        return true;
      });

      element.trigger('click');

      expect(spyElement).toHaveBeenCalledWith(window);
      expect(spyElement).toHaveBeenCalledWith('.toggleFullScreen');
      expect(spyElement).toHaveBeenCalledWith('.print-button');
      expect(spyScroll).toHaveBeenCalledWith(0);
      expect(spyShow).toHaveBeenCalled();
      expect(spyAnimate).toHaveBeenCalledWith({opacity: 0}, 1000, jasmine.any(Function));

      var callBack = spyAnimate.calls[0].args[2];
      callBack();

      expect(printElement.css('display')).toEqual('none');
    });
  })

});
