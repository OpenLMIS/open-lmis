function CreateRnrTemplateController($scope, Program) {
    Program.get({}, function (data) {   //success
        $scope.programs = data.programList;
    }, {});
}

function SaveRnrTemplateController($scope, RnRColumnList, $http, $location) {
    var DERIVED = 'Derived';
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

    function updateErrorMessage(message){
        $scope.error = message;
        $scope.message = "";
    };

    var validateCycleDependency = function(rnrColumnList){
        for(var column in rnrColumnList){
            var rnrColumn = rnrColumnList[column];
            if(rnrColumn.selectedColumnType==DERIVED){
               var dependencies = rnrColumn.cyclicDependencies;
               for(var dependent in dependencies){
                   var dependentColumnName = dependencies[dependent].name
                   var dependentColumn = getRnrColumnByName(rnrColumnList, dependentColumnName);
                   if(dependentColumn.selectedColumnType==DERIVED){
                      updateErrorMessage("Interdependent fields( "+rnrColumn.name+", "+dependentColumnName+
                                         ") can not be of type 'calculated' at the same time");
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
