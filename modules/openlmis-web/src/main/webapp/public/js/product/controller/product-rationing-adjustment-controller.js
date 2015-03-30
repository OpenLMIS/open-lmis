function ProductRationingAdjustmentController($scope, productDTO,seasonalityRationingTypeList, adjustmentFactorList, facilityTypes,messageService, requisitionGroups, Products, FacilityByTypeAndRequisition, $location) {

  $scope.newProgramProduct = {active: false};
  $scope.product = {};
  $scope.facilityTypes = facilityTypes;
  $scope.requisitionGroups = requisitionGroups;
  $scope.seasonalityRationingTypeList = seasonalityRationingTypeList;
  $scope.adjustmentFactorList = adjustmentFactorList;
  $scope.facilities = [];
  $scope.$parent.message = "";
  $scope.selectAll = false;

  $scope.addBatchesModal = undefined;

  if (!isUndefined(productDTO)) {
    if (!isUndefined(productDTO.product)) {
      $scope.product = productDTO.product;
    }
    else {
      $scope.product = {};
    }
    $scope.productLastUpdated = productDTO.productLastUpdated;
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

  $scope.openModal = function () {
    $scope.addBatchesModal = true;
    //alert('selectedItems '+ JSON.stringify($scope.selectedItems));
   };
  var myHeaderCellTemplate = '<input type="checkbox" ng-model="selectAll" ng-click="openRnr()"/>';
  $scope.gridOptions = { data: 'facilities',
    multiSelect: true,
    selectedItems: $scope.selectedItems,
    afterSelectionChange: function (rowItem, event) {
      $scope.openModal();
    },
    showFooter: false,
    checkboxHeaderTemplate: '<input class="ngSelectionHeader" type="checkbox" ng-model="allSelected" ng-change="toggleSelectAll(allSelected)"/>',

    showSelectionCheckbox: true,
    enableColumnResize: true,
    showColumnMenu: false,
    //sortInfo: { fields: ['submittedDate'], directions: ['asc'] },
    showFilter: false,
    columnDefs: [
      {field: 'name', displayName: messageService.get("header.name") },
      {field: 'code', displayName: messageService.get("header.code")},
      {field: 'geographicZone.name', displayName: messageService.get("label.district")},
      {field: 'emergency', displayName: messageService.get("requisition.type.emergency"),
        cellTemplate: '<div class="ngCellText checked"><a href="/public/pages/dashboard/index.html#dashoard" ng-click="">hi</a></div>',
        width: 110 }

      /*{field: 'emergency', headerCellTemplate : myHeaderCellTemplate, displayName: messageService.get("requisition.type.emergency"),
        cellTemplate: '<div class="ngCellText checked"><a href="/public/pages/dashboard/index.html#dashoard" ng-click="">hi</a></div>',
        width: 110 }*/
    ]
  };

  $scope.save = function () {
    if ($scope.productForm.$error.required) {
      $scope.showError = true;
      $scope.error = "form.error";
      return;
    }

    if ($scope.product.id) {
      Products.update({id: $scope.product.id}, {product: $scope.product, programProducts: $scope.programProducts}, success, error);
    }
    else {
      Products.save({}, {product: $scope.product, programProducts: $scope.programProducts}, success, error);
    }
  };

  $scope.cancel = function () {
    $scope.$parent.productId = undefined;
    $scope.$parent.message = "";
    $location.path('#/search');
  };

  $scope.edit = function (index) {
    $scope.programProducts[index].previousProgramProduct = angular.copy($scope.programProducts[index]);
    $scope.programProducts[index].underEdit = true;
  };

  $scope.cancelEdit = function (index) {
    $scope.programProducts[index] = $scope.programProducts[index].previousProgramProduct;
    $scope.programProducts[index].underEdit = false;
    $scope.programProducts[index].previousProgramProduct = undefined;
  };

  $scope.updateCategory = function (index) {
    $scope.programProducts[index].productCategory = _.find($scope.categories, function (category) {
      return category.id === $scope.programProducts[index].productCategory.id;
    });
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