/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

app.directive('openlmisPagination',function () {
  return {
    restrict:'EA',
    scope:{
      numPages:'=',
      currentPage:'=',
      maxSize:'=',
      onSelectPage:'&',
      nextText:'@',
      previousText:'@',
      checkErrorOnPage:'&'
    },
    templateUrl:'/public/pages/template/pagination/pagination.html',
    replace:true,
    link:function (scope) {
      scope.$watch('numPages + currentPage + maxSize', function () {
        scope.pages = [];
        var maxSize = ( scope.maxSize && scope.maxSize < scope.numPages ) ? scope.maxSize : scope.numPages;
        var startPage = scope.currentPage - Math.floor(maxSize / 2);
        if (startPage < 1) {
          startPage = 1;
        }
        if ((startPage + maxSize - 1) > scope.numPages) {
          startPage = startPage - ((startPage + maxSize - 1) - scope.numPages );
        }
        for (var i = 0; i < maxSize && i < scope.numPages; i++) {
          scope.pages.push(startPage + i);
        }
        if (scope.currentPage > scope.numPages) {
          scope.selectPage(scope.numPages);
        }
      });
      scope.noPrevious = function () {
        return scope.currentPage === 1;
      };
      scope.noNext = function () {
        return scope.currentPage === scope.numPages;
      };
      scope.isActive = function (page) {
        return scope.currentPage === page;
      };

      scope.selectPage = function (page) {
        if (!scope.isActive(page)) {
          scope.currentPage = page;
          scope.onSelectPage({ page:page });
        }
      };

      scope.selectPrevious = function () {
        if (!scope.noPrevious()) {
          scope.selectPage(scope.currentPage - 1);
        }
      };
      scope.selectNext = function () {
        if (!scope.noNext()) {
          scope.selectPage(scope.currentPage + 1);
        }
      };

      scope.hasErrorOnPage = function (page) {
        return scope.checkErrorOnPage({page:page});
      };

    }
  };
});