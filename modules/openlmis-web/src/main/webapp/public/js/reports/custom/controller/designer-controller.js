function CustomReportDesignerController($scope, reports, SaveCustomReport, CustomReportFullList){

  $scope.r = reports;
  $scope.reports = _.groupBy( $scope.r, 'category');

  $scope.init = function(){
    if($scope.sqleditor === undefined){
      $scope.sqleditor = ace.edit("sqleditor");
      $scope.sqleditor.setTheme("ace/theme/chrome");
      $scope.sqleditor.getSession().setMode("ace/mode/pgsql");

      $scope.filter = ace.edit("filtereditor");
      $scope.filter.setTheme("ace/theme/chrome");
      $scope.filter.getSession().setMode("ace/mode/json");

      $scope.column = ace.edit("columneditor");
      $scope.column.setTheme("ace/theme/chrome");
      $scope.column.getSession().setMode("ace/mode/json");
    }
    $scope.sqleditor.setValue($scope.current.query);
    $scope.filter.setValue($scope.current.filters);
    $scope.column.setValue($scope.current.columnoptions);
  };

  $scope.select = function(report){
    // clear previous values and message on screen
    $scope.columns = $scope.data = [];
    $scope.message = undefined;
    $scope.current = report;
    $scope.init();
  };

  $scope.New = function(){
    $scope.current = {quer:'', filters:'[]',columnoptions:'[]'};
    $scope.init();
  };

  $scope.Save = function(){
    $scope.current.query = $scope.sqleditor.getValue();
    $scope.current.filters = $scope.filter.getValue();
    $scope.current.columnoptions = $scope.column.getValue();

    var save = SaveCustomReport.save($scope.current);
    save.$promise.then(function(data){
      $scope.message =  $scope.current.name + ' saved successfully!';
      $scope.current = undefined;
      $scope.r = CustomReportFullList.get();
      $scope.r.$promise.then(function(){
        $scope.reports = _.groupBy( $scope.r.reports, 'category');
      });
    });
  };
}

CustomReportDesignerController.resolve = {
  reports: function ($q, $timeout, CustomReportFullList) {
    var deferred = $q.defer();
    $timeout(function () {
      CustomReportFullList.get(function (data) {
        deferred.resolve(data.reports);
      });
    }, 100);
    return deferred.promise;
  }
};
