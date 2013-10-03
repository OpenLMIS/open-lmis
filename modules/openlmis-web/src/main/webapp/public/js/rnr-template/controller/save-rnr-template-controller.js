/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function SaveRnrTemplateController($scope, rnrTemplateForm, program, messageService, $routeParams, RnRColumnList, $location) {
  $scope.rnrColumns = rnrTemplateForm.rnrColumns;
  $scope.sources = rnrTemplateForm.sources;
  $scope.validateFormula = $scope.rnrColumns[0].formulaValidationRequired;
  $scope.program = program;
  $scope.$parent.message = "";
  $scope.selectProgramUrl = "/public/pages/admin/rnr-template/index.html#/select-program";
  $scope.arithmeticValidationLabel = false;

  var setRnRTemplateValidateFlag = function () {
    $.each($scope.rnrColumns, function (index, column) {
      column.formulaValidationRequired = $scope.validateFormula;
    });
  };

  $scope.save = function () {
    updatePosition();
    setRnRTemplateValidateFlag();
    RnRColumnList.post({programId: $routeParams.programId}, $scope.rnrColumns, function () {
      $scope.$parent.message = messageService.get("template.save.success");
      $scope.error = "";
      $scope.errorMap = undefined;
      $location.path('select-program');
    }, function (data) {
      if (data != null) {
        $scope.errorMap = data.data;
      }
      updateErrorMessage("form.error");
    });
  };

  function updatePosition() {
    $scope.rnrColumns.forEach(function (rnrColumn, index) {
      rnrColumn.position = index + 1;
    });
  }

  $scope.sources.forEach(function (source) {
    source.description = messageService.get(source.description);
  });

  function updateErrorMessage(message) {
    $scope.error = message;
    $scope.message = "";
  }


  $scope.setArithmeticValidationMessageShown = function () {
    $.each($scope.rnrColumns, function (index, column) {
      if (column.sourceConfigurable) {
        if (column.source.code == 'U' && column.visible == true) {
          $scope.arithmeticValidationMessageShown = true;
        }
        else {
          $scope.arithmeticValidationMessageShown = false;
          return false;
        }
      }
    });
  };
  $scope.setArithmeticValidationMessageShown();

  var setArithmeticValidationLabel = function () {
    if ($scope.validateFormula) {
      $scope.arithmeticValidationStatusLabel = messageService.get("label.on");
      $scope.arithmeticValidationToggleLabel = messageService.get("label.off");
      $scope.arithmeticValidationMessage = "msg.rnr.arithmeticValidation.turned.on";
    } else {
      $scope.arithmeticValidationStatusLabel =  messageService.get("label.off");
      $scope.arithmeticValidationToggleLabel =  messageService.get("label.on");
      $scope.arithmeticValidationMessage = "";
    }
  };
  setArithmeticValidationLabel();

  $scope.toggleValidateFormulaFlag = function () {
    $scope.validateFormula = !$scope.validateFormula;
    setArithmeticValidationLabel();
  }
}

SaveRnrTemplateController.resolve = {
  rnrTemplateForm:function ($q, RnRColumnList, $location, $route, $timeout) {
    var deferred = $q.defer();
    var id = $route.current.params.programId;

    $timeout(function () {
      RnRColumnList.get({programId:id}, function (data) {
        deferred.resolve(data.rnrTemplateForm);
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
  }
};
