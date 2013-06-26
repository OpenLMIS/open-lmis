/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function SaveRegimenTemplateController($scope, program, regimens, regimenCategories, messageService, Regimens, $location) {

  $scope.program = program;
  $scope.regimens = regimens;
  $scope.regimenCategories = regimenCategories;
  $scope.selectProgramUrl = "/public/pages/admin/regimen-template/index.html#/select-program";
  $scope.regimensByCategory = {};
  $scope.$parent.message = "";

  function addRegimenByCategory(regimen) {
    regimen.show = true;
    var regimenCategoryId = regimen.category.id;
    var regimenList = $scope.regimensByCategory[regimenCategoryId];
    if (regimenList) {
      regimenList.push(regimen);
    } else {
      regimenList = [regimen];
    }
    $scope.regimensByCategory[regimenCategoryId] = regimenList;
  }

  function filterRegimensByCategory(regimens) {
    $(regimens).each(function (index, regimen) {
      addRegimenByCategory(regimen);
    });
  }

  filterRegimensByCategory($scope.regimens);

  $scope.addNewRegimen = function () {
    if ($scope.newRegimenForm.$error.required) {
      $scope.inputClass = true;
      $scope.newRegimenError = messageService.get('label.missing.values');
    } else {
      $scope.newRegimen.programId = $scope.program.id;
      $scope.newRegimen.displayOrder = 1;
      $scope.newRegimen.disable = true;
      addRegimenByCategory($scope.newRegimen);
      $scope.newRegimenError = null;
      $scope.newRegimen = null;
      $scope.inputClass = false;
    }
  };

  $scope.highlightRequired = function (value) {
    if ($scope.inputClass && isUndefined(value)) {
      return "required-error";
    }
    return null;
  };

  $scope.editRow = function (regimen) {
    regimen.show = false;
  };

  $scope.saveRow = function (regimen) {
    if (!$scope.regimenEditForm.$error.required) {
      regimen.show = true;
    }
  };

  $scope.save = function () {
    var regimenListToSave = [];
    var regimenLists = _.values($scope.regimensByCategory);
    var duplicateRegimen;

    var codes = [];
    $(regimenLists).each(function (index, regimenList) {
      $(regimenList).each(function (index, regimen) {
        if (codes.length > 0 && _.contains(codes, regimen.code)) {
          $scope.error = messageService.get('error.duplicate.regimen.code');
          duplicateRegimen = true;
          return;
        }
        codes.push(regimen.code);
        regimen.show = undefined;
        regimen.displayOrder = index + 1;
      });
      regimenListToSave = regimenListToSave.concat(regimenList);
    });
    if(duplicateRegimen) return;
    Regimens.post({programId: $scope.program.id}, regimenListToSave, function () {
      $scope.$parent.message = messageService.get('regimens.saved.successfully');
      $scope.program.regimenTemplateConfigured = true;
      $location.path('select-program');
    }, function (data) {
    });
  };

}

SaveRegimenTemplateController.resolve = {

  regimens: function ($q, ProgramRegimens, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      ProgramRegimens.get({programId: id}, function (data) {
        deferred.resolve(data.regimens);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  program: function ($q, Program, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      Program.get({id: id}, function (data) {
        deferred.resolve(data.program);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  },

  regimenCategories: function ($q, RegimenCategories, $location, $route, $timeout) {
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