function SaveRnrTemplateController($scope, rnrTemplateForm, $http) {
  $scope.rnrColumns = rnrTemplateForm.rnrColumns;
  $scope.sources = rnrTemplateForm.sources;
  $scope.validate = $scope.rnrColumns[0].validated;

  var setRnRTemplateValidateFlag = function() {
    $.each($scope.rnrColumns, function (index, column) {
      column.validated = $scope.validate;
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

  $scope.validateMessageShown = false;

  $scope.setValidateMessageShownFlag = function() {
    $.each($scope.rnrColumns, function (index, column) {
      if(column.sourceConfigurable){
        if(column.source.code == 'U'){
          $scope.validateMessageShown = true;
        }
        else{
          $scope.validateMessageShown = false;
          return (false);
        }
      }
    });
  };
  $scope.setValidateMessageShownFlag();

  var setValidateLabel = function () {
    if($scope.validate){
      $scope.validateLabel = 'ON';
      $scope.buttonLabel = 'OFF';
    }else{
      $scope.validateLabel = 'OFF';
      $scope.buttonLabel = 'ON';
    }
  };
  setValidateLabel();

  $scope.toggleValidateFlag = function() {
    if($scope.validate){
      $scope.validate = false;
    }else{
      $scope.validate = true;
    }
    setValidateLabel();
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
