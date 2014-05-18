/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

function FacilitySearchFilterController($scope, Facility) {

  $scope.progressFunc = function () {
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

  $scope.$parent.$parent.$watch("sliderState", function (value) {
    if (value) {
      angular.element(".searchAndFilter").slideDown({duration: "slow", progress: $scope.progressFunc});
    }
    else {
      angular.element(".searchAndFilter").slideUp("slow");
    }
  });

  $scope.facilitySearchResults = function () {
    if (!$scope.query) return;
    $scope.query = $scope.query.trim();
    Facility.get({"searchParam": $scope.query}, function (data) {
      $scope.facilityList = data.facilityList;
      $scope.resultCount = $scope.facilityList == undefined ? 0 : $scope.facilityList.length;
      $scope.message = data.message;
      $scope.openSearchResultsBox();
    }, {});
  };

  $scope.openSearchResultsBox = function () {
    if ($scope.resultCount >= 0) {
      angular.element(".searchAndFilter .search-list").slideDown({duration: "slow", progress: $scope.progressFunc});
    }
  };

  $scope.clearSearch = function () {
    angular.element(".searchAndFilter .search-list").slideUp("slow", function () {
      $scope.query = undefined;
      $scope.facilityList = undefined;
      $scope.resultCount = undefined;
      $scope.$apply();
      angular.element('#searchFacility').focus();
    });
  };

  $scope.associateFacility = function (facility) {
    if (isUndefined($scope.$parent.$parent.supervisoryNode)) {
      $scope.$parent.$parent.supervisoryNode = {facility: facility};
    }
    else {
      $scope.$parent.$parent.supervisoryNode.facility = facility;
    }
    $scope.$parent.$parent.sliderState = !$scope.$parent.$parent.sliderState;
  };
}


