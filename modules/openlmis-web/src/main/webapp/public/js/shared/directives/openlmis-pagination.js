/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

//  Description:
//  Pagination

app.directive('openlmisPagination', function () {
  return {
    restrict: 'EA',
    scope: {
      numPages: '=',
      currentPage: '=',
      maxSize: '=',
      onSelectPage: '&',
      nextText: '@',
      previousText: '@',
      checkErrorOnPage: '&',
      errorPages: '='
    },
    templateUrl: '/public/pages/template/pagination/openlmis-pagination.html',
    replace: true,
    link: function (scope) {
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
          scope.onSelectPage({ page: page });
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
        if (scope.errorPages) {
          return scope.errorPages.indexOf(page) != -1;
        }
        return scope.checkErrorOnPage({page: page});
      };

    }
  };
});