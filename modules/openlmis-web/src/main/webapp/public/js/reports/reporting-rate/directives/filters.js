app.directive('filters', function(){
  return{
    restrict: 'E',
    link: function(scope, elm, attrs){

    },
    template: '<div class="filters"></div>'
  }
});

// now comes the program filter
app.directive('programFilter',['ReportPrograms' , function(ReportPrograms){
   return {
     restrict: 'E',
     link: function(scope, elm, attr){
       ReportPrograms.get(function (data) {
         scope.programs = data.programs;
         scope.programs.unshift({'name': '-- Select Programs --'});
       });
     },
     template: '<div>' +
                   '<select class="input-large" ui-select2 ng-model="program" ng-change="OnFilterChanged()">' +
                      '<option ng-repeat="program in programs" value="{{ program.id }}">{{program.name}}</option>' +
                   '</select>' +
                '</div>'
   }
}]);

app.directive('yearFilter',['OperationYears' , function(OperationYears){
  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      OperationYears.get(function (data) {
        scope.years = data.years;
        scope.years.unshift('-- Select Year --');
      });
    },
    template: '<div>' +
        '<select class="input-medium" ui-select2 ng-model="year">' +
          '<option ng-repeat="year in years" value="{{ year }}">{{year}}</option>' +
        '</select>' +
        '</div>'
  }
}]);

app.directive('scheduleFilter',['ReportSchedules' , function(ReportSchedules){
  return {
    restrict: 'E',
    link: function(scope, elm, attr){
      ReportSchedules.get(function (data) {
        scope.schedules = data.schedules;
        scope.schedules.unshift({name:'-- Select Group --'});
      });
    },
    template: '<div>' +
        '<select class="input-medium" ui-select2 ng-model="schedule">' +
          '<option ng-repeat="schedule in schedules" value="{{ schedule.id }}">{{schedule.name}}</option>' +
        '</select>' +
        '</div>'
  }
}]);


app.directive('periodFilter',['ReportPeriods','ReportPeriodsByScheduleAndYear' , function( ReportPeriods, ReportPeriodsByScheduleAndYear){

  var onCascadedVarsChanged = function($scope, newValue){

    if($scope.year !== undefined && $scope.schedule !== undefined){
        ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.schedule, year: $scope.year}, function(data){
          $scope.periods = data.periods;
          if(data.periods !== undefined && data.periods.length > 0)
            $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
        });

      }else{
        ReportPeriods.get({ scheduleId : $scope.schedule },function(data) {
          $scope.periods = data.periods;
          if(data.periods !== undefined && data.periods.length > 0)
            $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
        });
      }

  };

  return {
    restrict: 'E',
    link: function(scope, elm, attr){

      scope.$watch('year',function(value){
          onCascadedVarsChanged(scope, value);
      });
      scope.$watch('schedule',function(value){
        onCascadedVarsChanged(scope, value);
      });

    },
    template: '<div>' +
        '<select class="input-medium" ui-select2 ng-model="period" ng-change="OnFilterChanged();">' +
          '<option ng-repeat="period in periods" value="{{ period.id }}">{{period.name}}</option>' +
        '</select>' +
        '</div>'
  }
}]);