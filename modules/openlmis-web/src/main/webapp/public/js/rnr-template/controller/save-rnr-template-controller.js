function SaveRnrTemplateController($scope, rnrTemplateForm, $http) {
  $scope.rnrColumns = rnrTemplateForm.rnrColumns;
  $scope.sources = rnrTemplateForm.sources;
  $scope.validateFormula = $scope.rnrColumns[0].formulaValidated;

  var setRnRTemplateValidateFlag = function() {
    $.each($scope.rnrColumns, function (index, column) {
      column.formulaValidated = $scope.validateFormula;
    });
  };

  $scope.createProgramRnrTemplate = function () {
    setRnRTemplateValidateFlag();
    $http.post('/admin/rnr/' + $scope.program.code + '/columns.json', $scope.rnrColumns).success(function () {
      $scope.message = "Template saved successfully!";
      $scope.error = "";
      $scope.errorMap = undefined;
    }).error(function (data) {
        if (data.errorMap != null) {
          $scope.errorMap = data.errorMap;
        }
        updateErrorMessage("Save Failed!");
      });
  };

  $scope.update = function () {
    $scope.rnrColumns.forEach(function (rnrColumn, index) {
      rnrColumn.position = index + 1;
    });
  };

  function updateErrorMessage(message) {
    $scope.error = message;
    $scope.message = "";
  }

  $scope.arithmeticValidationLabel = false;

  $scope.setArithmeticValidationMessageShown = function() {
    $.each($scope.rnrColumns, function (index, column) {
      if(column.sourceConfigurable){
        if(column.source.code == 'U'){
          $scope.arithmeticValidationMessageShown = true;
        }
        else{
          $scope.arithmeticValidationMessageShown = false;
          return (false);
        }
      }
    });
  };
  $scope.setArithmeticValidationMessageShown();

  var setArithmeticValidationLabel = function () {
    if($scope.validateFormula){
      $scope.arithmeticValidationStatusLabel = 'ON';
      $scope.arithmeticValidationToggleLabel = 'OFF';
    }else{
      $scope.arithmeticValidationStatusLabel = 'OFF';
      $scope.arithmeticValidationToggleLabel = 'ON';
    }
  };
  setArithmeticValidationLabel();

  $scope.toggleValidateFormulaFlag = function() {
    if($scope.validateFormula){
      $scope.validateFormula = false;
    }else{
      $scope.validateFormula = true;
    }
    setArithmeticValidationLabel();
  }
}

SaveRnrTemplateController.resolve = {
  rnrTemplateForm:function ($q, RnRColumnList, $location, $route, $timeout) {
    var deferred = $q.defer();
    var code = $route.current.params.programCode;

    $timeout(function () {
      RnRColumnList.get({programCode:code}, function (data) {
        deferred.resolve(data.rnrTemplateForm);
      }, function () {
        $location.path('select-program');
      });
    }, 100);

    return deferred.promise;
  }
};
