function ProductController($scope, productGroups, productForms, dosageUnits, AddEditProgramProducts, $location) {
  $scope.productGroups = productGroups;
  $scope.productForms = productForms;
  $scope.dosageUnits = dosageUnits;
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
  }
};