/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function ProductRationingAdjustmentController($scope, $timeout,productDTO,seasonalityRationingTypeList, AdjustmentProductSearch, adjustmentFactorList, facilityTypes,messageService, requisitionGroups, AdjustmentProducts, FacilityByTypeAndRequisition, $location) {

  $scope.newProgramProduct = {active: false};
  $scope.product = {};
  $scope.facilityTypes = facilityTypes;
  $scope.requisitionGroups = requisitionGroups;
  $scope.seasonalityRationingTypeList = seasonalityRationingTypeList;
  $scope.adjustmentFactorList = adjustmentFactorList;
  $scope.facilities = [];
  $scope.$parent.message = "";
  $scope.selectAll = false;
  $scope.seasonalityAdjustment = {product: productDTO.product};

  if (!isUndefined(productDTO)) {
    if (!isUndefined(productDTO.product)) {
      $scope.product = productDTO.product;
    }
    else {
      $scope.product = {};
    }
  }

  var success = function (data) {
    $scope.error = "";
    $scope.$parent.message = data.success;
    $scope.$parent.productId = data.productId;
    $scope.showError = false;
    $location.path('');
  };

  var error = function (data) {
    $scope.$parent.message = "";
    $scope.error = data.data.error;
    $scope.showError = true;
  };


  $scope.search = function () {
      $scope.facilityTypeId = isUndefined($scope.facilityType) ? 0 : $scope.facilityType.id ;
    $scope.requisitionGroupId = isUndefined($scope.requisitionGroup) ? 0 : $scope.requisitionGroup.id;

    FacilityByTypeAndRequisition.get({facilityTypeId:  $scope.facilityTypeId, requisitionGroupId: $scope.requisitionGroupId}, function (data){
      $scope.facilities = data.facilities;
    }, {});
  };

  $scope.search();

  $scope.selectedItems = [];

  $scope.convertStringToCorrectDateFormat = function(stringDate) {
    if (stringDate) {
      return stringDate.split("-").reverse().join("-");
    }
    return null;
  };

  $scope.showAdjustmentForm = function () {
    if(!isUndefined($scope.selectedItems)){
      $scope.seasonalityAdjustment.facility = $scope.selectedItems[0];
      AdjustmentProductSearch.get({productId: $scope.product.id, facilityId: $scope.seasonalityAdjustment.facility.id}, function (data){
        var adjustmentData = data.adjustmentProducts;
        if(!isUndefined(adjustmentData)){
          $scope.seasonalityAdjustment = adjustmentData;
          $scope.seasonalityAdjustment.startDate = $scope.convertStringToCorrectDateFormat($scope.seasonalityAdjustment.stringStartDate);
          $scope.seasonalityAdjustment.endDate = $scope.convertStringToCorrectDateFormat($scope.seasonalityAdjustment.stringEndDate);

        }else{
          $scope.seasonalityAdjustment = {};
          $scope.seasonalityAdjustment.facility = $scope.selectedItems[0];
          $scope.seasonalityAdjustment.product = productDTO.product;
        }
      });
    }
    $timeout(function () {
      $('html, body').animate({
        scrollTop: $("#seasonality-adjustment").offset().top
      }, 0);
    }, 0);

   };
  var myHeaderCellTemplate = '<input type="checkbox" ng-model="selectAll" ng-click="cc()"/>';
  $scope.gridOptions = { data: 'facilities',
    multiSelect: false,
    selectedItems: $scope.selectedItems,
    afterSelectionChange: function (rowItem, event) {
      $scope.showAdjustmentForm();
    },
    showFooter: false,
    checkboxHeaderTemplate: '<input class="ngSelectionHeader" type="checkbox" ng-model="allSelected" ng-change="toggleSelectAll(allSelected)"/>',

    showSelectionCheckbox: false,
    enableColumnResize: true,
    showColumnMenu: false,
    //sortInfo: { fields: ['submittedDate'], directions: ['asc'] },
    showFilter: false,
    columnDefs: [
      {field: 'name', displayName: messageService.get("header.name") },
      {field: 'code', displayName: messageService.get("header.code")},
      {field: 'geographicZone.name', displayName: messageService.get("label.district")},
      {field: 'emergency', displayName: messageService.get("label.graph"),
        cellTemplate: '<div class="ngCellText checked"><a href="/public/pages/dashboard/index.html#dashoard" ng-click=""></a></div>',
        width: 110 }

      /*{field: 'emergency', headerCellTemplate : myHeaderCellTemplate, displayName: messageService.get("requisition.type.emergency"),
        cellTemplate: '<div class="ngCellText checked"><a href="/public/pages/dashboard/index.html#dashoard" ng-click="">hi</a></div>',
        width: 110 }*/
    ]
  };

  $scope.save = function () {
    //alert(JSON.stringify($scope.seasonalityAdjustment));
   /* if ($scope.productForm.$error.required) {
      $scope.showError = true;
      $scope.error = "form.error";
      return;
    }*/
    AdjustmentProducts.save({}, $scope.seasonalityAdjustment, success, error );
  };

  $scope.cancel = function () {
    $scope.$parent.productId = undefined;
    $scope.$parent.message = "";
    $location.path('#/search');
  };

}

ProductRationingAdjustmentController.resolve = {
  productDTO: function ($q, $route, $timeout, Products) {
    if ($route.current.params.id === undefined) return undefined;

    var deferred = $q.defer();
    var productId = $route.current.params.id;

    $timeout(function () {
      Products.get({id: productId}, function (data) {
        deferred.resolve(data.productDTO);
      }, {});
    }, 100);
    return deferred.promise;
  },
  facilityTypes: function ($q, $timeout, FacilityTypes){
    var deferred = $q.defer();
    $timeout(function () {
      FacilityTypes.get({}, function (data){
        deferred.resolve(data.facilityTypeList);
      },{});
    }, 100);
    return deferred.promise;
  },
  requisitionGroups: function ($q, $timeout, RequisitionGroups){
    var deferred = $q.defer();
    $timeout(function () {
      RequisitionGroups.get({"searchParam": '', "columnName": 'name', "page": 2}, function (data) {
        deferred.resolve(data.requisitionGroupList);
      }, {});
    }, 100);
    return deferred.promise;
  },
  seasonalityRationingTypeList: function ($q, $timeout, SeasonalityRationingTypeList){
    var deferred = $q.defer();
    $timeout(function () {
      SeasonalityRationingTypeList.get({}, function (data) {
        deferred.resolve(data.seasonalityRationingsList);
      }, {});
    }, 100);
    return deferred.promise;
  },
  adjustmentFactorList: function ($q, $timeout, AdjustmentFactorList) {
    var deferred = $q.defer();
    $timeout(function () {
      AdjustmentFactorList.get({}, function (data) {
        deferred.resolve(data.adjustmentFactorList);
      }, {});
    }, 100);
    return deferred.promise;
  }
};
