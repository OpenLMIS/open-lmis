function ProductController($scope, productGroups, productForms, AddEditProgramProducts, $location) {
  $scope.productGroups = productGroups;
  $scope.productForms = productForms;
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
  }
};