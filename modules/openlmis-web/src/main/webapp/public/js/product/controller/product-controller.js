function ProductController($scope, productGroups, productForms, dosageUnits, programs, categories, productDTO, Products, PriceSchCategories, $location, $filter) {
  $scope.productGroups = productGroups;
  $scope.productForms = productForms;
  $scope.dosageUnits = dosageUnits;
  $scope.categories = categories;
  $scope.newProgramProduct = {active: false};
  $scope.programs = programs;
  $scope.programProducts = [];
  $scope.priceSchedules = [];
  $scope.newPriceSchedule = {};
  $scope.product = {};
  $scope.$parent.message = "";
  $scope.priceScheduleCategories = PriceSchCategories;
  setProgramMessage();

  if (!isUndefined(productDTO)) {
    if (!isUndefined(productDTO.product)) {
      $scope.product = productDTO.product;
      $scope.programProducts = productDTO.programProducts;
      $scope.priceSchedules = productDTO.productPriceSchedules;
      $scope.selectedProductGroupCode = isUndefined($scope.product.productGroup) ? undefined : $scope.product.productGroup.code;
      $scope.selectedProductFormCode = isUndefined($scope.product.form) ? undefined : $scope.product.form.code;
      $scope.selectedProductDosageUnitCode = isUndefined($scope.product.dosageUnit) ? undefined : $scope.product.dosageUnit.code;
      refreshAndSortPrograms();
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

  var findProgramProductsUnderEdit = function () {
    return _.find($scope.programProducts, function (programProduct) {
      return programProduct.underEdit === true;
    });
  };

  var setProductReferenceData = function () {
    $scope.product.productGroup = _.where($scope.productGroups, {code: $scope.selectedProductGroupCode})[0];
    $scope.product.form = _.where($scope.productForms, {code: $scope.selectedProductFormCode})[0];
    $scope.product.dosageUnit = _.where($scope.dosageUnits, {code: $scope.selectedProductDosageUnitCode})[0];
  };

  $scope.save = function () {
    if ($scope.productForm.$error.required) {
      $scope.showError = true;
      $scope.error = "form.error";
      return;
    }
    if (findProgramProductsUnderEdit()) {
      $scope.error = 'error.program.products.not.done';
      return;
    }

    setProductReferenceData();

    if ($scope.product.id) {
      Products.update({id: $scope.product.id}, {product: $scope.product, programProducts: $scope.programProducts, productPriceSchedules : $scope.priceSchedules}, success, error);
    }
    else {
      Products.save({}, {product: $scope.product, programProducts: $scope.programProducts,  productPriceSchedules : $scope.priceSchedules}, success, error);
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

    $scope.addPriceSchedule = function(){

      if(validateDuplicatePriceScheduleCategory($scope.newPriceSchedule)) {
          $scope.error  = "";
          $scope.newPriceSchedule.priceSchedule = $filter('filter')($scope.priceScheduleCategories, {id: $scope.newPriceSchedule.priceSchedule.id})[0];
          $scope.priceSchedules.push($scope.newPriceSchedule);
          $scope.newPriceSchedule = {};
     }
      else
      {

          $scope.error = "Duplicate Price schedule category";
      }
  };

    function validateDuplicatePriceScheduleCategory(priceSchedule){
        for(i=0; i<$scope.priceSchedules.length; i++){
            if($scope.priceSchedules[i].priceSchedule.id == priceSchedule.priceSchedule.id)
                return false;
        }
        return true;
    }

  function setProgramMessage() {
    $scope.programMessage = $scope.programs.length ? "label.select.program" : "label.noProgramLeft";
  }

  function refreshAndSortPrograms() {
    var selectedProgramCodes = _.pluck(_.pluck($scope.programProducts, 'program'), 'code');
    $scope.programs = _.reject($scope.programs, function (program) {
      return _.contains(selectedProgramCodes, program.code);
    });

    setProgramMessage();
  }

  $scope.addNewProgramProduct = function () {
    $scope.programProducts.push($scope.newProgramProduct);
    refreshAndSortPrograms();
    $scope.newProgramProduct = {active: false};
  };

  $scope.mandatoryFieldsNotFilled = function (programProduct) {
    return !(programProduct && programProduct.program && programProduct.productCategory && programProduct.dosesPerMonth);
  };
}

ProductController.resolve = {
  productGroups: function ($q, $timeout, ProductGroups) {
    var deferred = $q.defer();

    $timeout(function () {
      ProductGroups.get({}, function (data) {
        deferred.resolve(data.productGroupList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  productForms: function ($q, $timeout, ProductForms) {
    var deferred = $q.defer();

    $timeout(function () {
      ProductForms.get({}, function (data) {
        deferred.resolve(data.productFormList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  dosageUnits: function ($q, $timeout, DosageUnits) {
    var deferred = $q.defer();

    $timeout(function () {
      DosageUnits.get({}, function (data) {
        deferred.resolve(data.dosageUnitList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  programs: function ($q, $timeout, Program) {
    var deferred = $q.defer();

    $timeout(function () {
      Program.get({}, function (data) {
        deferred.resolve(data.programs);
      }, {});
    }, 100);
    return deferred.promise;
  },

  categories: function ($q, $timeout, ProductCategories) {
    var deferred = $q.defer();

    $timeout(function () {
      ProductCategories.get({}, function (data) {
        deferred.resolve(data.productCategoryList);
      }, {});
    }, 100);
    return deferred.promise;
  },

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

   PriceSchCategories: function ($q, $route, $timeout, PriceScheduleCategories) {
        if ($route.current.params.id === undefined) return undefined;

        var deferred = $q.defer();

        $timeout(function () {
            PriceScheduleCategories.get({}, function (data) {
                deferred.resolve(data.priceScheduleCategories);
            }, {});
        }, 100);
        return deferred.promise;
    }

};
