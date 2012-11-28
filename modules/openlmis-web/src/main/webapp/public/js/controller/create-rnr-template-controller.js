function CreateRnrTemplateController($scope, Program) {
  Program.get({}, function (data) {   //success
    $scope.programs = data.programList;
  }, {});
};

angular.module('ui.directives').directive('uiSortable', [
  'ui.config', function(uiConfig) {
    var options;
    options = {};
    if (uiConfig.sortable != null) {
      angular.extend(options, uiConfig.sortable);
    }
    return {
      require: '?ngModel',
      link: function(scope, element, attrs, ngModel) {
        var onStart, onUpdate, opts, _start, _update;
        opts = angular.extend({}, options, scope.$eval(attrs.uiSortable));
        if (ngModel != null) {
          onStart = function(e, ui) {
            return ui.item.data('ui-sortable-start', ui.item.index());
          };
          onUpdate = function(e, ui) {
            var end, start;
            start = ui.item.data('ui-sortable-start');
            end = ui.item.index();
            ngModel.$modelValue.splice(end, 0, ngModel.$modelValue.splice(start, 1)[0]);
            return scope.$apply();
          };
          if (opts.start != null) {
            _start = opts.start;
            opts.start = function(e, ui) {
              onStart(e, ui);
              _start(e, ui);
              return scope.$apply();
            };
          } else {
            opts.start = onStart;
          }
          if (opts.update != null) {
            _update = opts.update;
            opts.update = function(e, ui) {
              onUpdate(e, ui);
              _update(e, ui);
              return scope.$apply();
            };
          } else {
            opts.update = onUpdate;
          }
        }
        return element.sortable(opts);
      }
    };
  }
]);

function SaveRnrTemplateController($scope, RnRColumnList, $http, $location) {
  var CALCULATED = 'Calculated';
  var code = ($scope.program ? $scope.program.code : "");
  RnRColumnList.get({programCode:code}, function (data) {   //success
    $scope.rnrColumnList = data.rnrColumnList;
  }, function () {
    $location.path('select-program');
  });

  $scope.createProgramRnrTemplate = function () {
    var isValid= validateCycleDependency($scope.rnrColumnList);

    if(!isValid) {
      return;
    }

    $http.post('/admin/rnr/' + $scope.program.code + '/columns.json', $scope.rnrColumnList).success(function () {
      $scope.message = "Template saved successfully!";
      $scope.error = "";
    }).error(function () {
        updateErrorMessage("Save Failed!");
      });
  };

  $scope.update = function() {
    x = $scope.rnrColumnList;
    $("#sortable li").each(function(index) {
      $(this).find(".rnr-column-position").val(index + 1);
    });
  };

  function updateErrorMessage(message){
    $scope.error = message;
    $scope.message = "";
  };

  var validateCycleDependency = function(rnrColumnList){
    for(var column in rnrColumnList){
      var rnrColumn = rnrColumnList[column];
      if(rnrColumn.selectedColumnType==CALCULATED){
        var dependencies = rnrColumn.cyclicDependencies;
        for(var dependent in dependencies){
          var dependentColumnName = dependencies[dependent].name
          var dependentColumn = getRnrColumnByName(rnrColumnList, dependentColumnName);
          if(dependentColumn.selectedColumnType==CALCULATED){
            updateErrorMessage("Interdependent fields ("+rnrColumn.label+", "+dependentColumn.label+
              ") cannot be of type 'calculated' at the same time");
            return false;
          }
        }

      }
    }
    return true;
  };

  var getRnrColumnByName = function(rnrColumnList, columnName){
    for(var column in rnrColumnList){
      if(rnrColumnList[column].name==columnName){
        return rnrColumnList[column];
      }
    }
    return;
  }
}
