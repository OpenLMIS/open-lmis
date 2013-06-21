function SaveRegimenTemplateController($scope, program, regimens, regimenCategories, messageService, Regimens) {
  $scope.program = program;
  $scope.regimens = regimens;
  $scope.regimenCategories = regimenCategories;
  $scope.selectProgramUrl = "/public/pages/admin/regimen-template/index.html#/select-program";

  $scope.regimensByCategory = {};


  function addRegimenByCategory(regimen) {
    var regimenCategoryId = regimen.category.id;
    var regimenList = $scope.regimensByCategory[regimenCategoryId];
    if (regimenList) {
      regimenList.push(regimen);
    } else {
      regimenList = [regimen];
    }
    $scope.regimensByCategory[regimenCategoryId] = regimenList;
    console.log($scope.regimenByCategory);
  }

  $($scope.regimens).each(function (index, regimen) {
    addRegimenByCategory(regimen);
  });


  $scope.addNewRegimen = function () {
    if ($scope.newRegimenForm.$error.required) {
      $scope.newRegimenError = messageService.get('label.missing.values');
    } else {
      $scope.newRegimen.programId = $scope.program.id;
      $scope.newRegimen.displayOrder = 1;
      addRegimenByCategory($scope.newRegimen);
      $scope.newRegimenError = null;
    }
    $scope.newRegimen = null;
  };

  $scope.save = function () {
    var regimenListToSave = [];
    var regimenLists = _.values($scope.regimensByCategory);
    $(regimenLists).each(function (index, regimenList) {
      regimenListToSave = regimenListToSave.concat(regimenList);
    });
    Regimens.update({}, regimenListToSave, function (data) {

    }, {});
  };
}

SaveRegimenTemplateController.resolve = {

  regimens:function ($q, ProgramRegimens, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      ProgramRegimens.get({programId:id}, function (data) {
        deferred.resolve(data.regimens);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },


  program:function ($q, Program, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      Program.get({id:id}, function (data) {
        deferred.resolve(data.program);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  regimenCategories:function ($q, RegimenCategories, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      RegimenCategories.get({}, function (data) {
        deferred.resolve(data.regimen_categories);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  }

};
