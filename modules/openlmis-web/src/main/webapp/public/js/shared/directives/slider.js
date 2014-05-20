/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

app.directive('slider', function ($timeout) {
  return {
    restrict: 'A',
    link: function (scope, element, attrs) {
      scope.$parent.showSlider = false;

      var progressFunc = function () {
        var bufferHeight = 200;
        var searchFilterBoxHeight = $(this).height();

        var screenViewPort = $(window).height() - bufferHeight;
        var searchFilterTopOffset = $(this).offset().top - $(window).scrollTop();
        var searchFilterViewPort = (screenViewPort - searchFilterTopOffset);

        if (searchFilterViewPort < searchFilterBoxHeight) {
          var scrollableAmount = searchFilterBoxHeight - searchFilterViewPort;
          $(window).scrollTop($(window).scrollTop() + scrollableAmount);
        }
      };

      var openSearchResultsBox = function () {
        if (scope.resultCount >= 0) {
          angular.element(".searchAndFilter .search-list").slideDown({duration: "slow", progress: progressFunc});
        }
      };

      scope.$parent.$watch('showSlider', function () {
        if (scope.showSlider) {
          angular.element(".searchAndFilter").slideDown({duration: "slow", progress: progressFunc});
        }
        else {
          angular.element(".searchAndFilter").slideUp("slow");
        }
      });

      angular.element('#searchButton').click(function () {
        if (!scope.query) return;
        scope.query = scope.query.trim();
        scope.$parent.getSearchResults(scope.query, function (data) {
          scope.facilityList = data.facilityList;
          scope.resultCount = isUndefined(scope.facilityList) ? 0 : scope.facilityList.length;
          scope.message = data.message;
          openSearchResultsBox();
        });
      });

      angular.element('#clearSearch').click(function () {
        angular.element(".searchAndFilter .search-list").slideUp("slow", function () {
          scope.query = undefined;
          scope.facilityList = undefined;
          scope.resultCount = undefined;
          scope.$apply();
          angular.element('#searchFacility').focus();
        });
      });

      scope.associate = function(listItem){
        scope.$parent.associate(listItem);
        scope.$parent.showSlider = false;
      };
    }
  };
});
