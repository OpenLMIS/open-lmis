function ListFacilitiesController($scope, FacilityList, $http) {
   // FacilityList.get({"page":30,"max":20}, function(data) {
       // data = data.pages.rows;
        $scope.filterOptions = {
            filterText: "",
            useExternalFilter: true
        };
        $scope.pagingOptions = {
            pageSizes: [ 20, 40, 50, 100],
            pageSize: 20,
            totalServerItems: 0,
            currentPage: 1
        };
   // sortInfo:{
   //     field: 'columnField'
   //     column: ng.Column instance.
   //         direction: 'ASC' || 'DESC'
   // }
        $scope.sortInfo = {field: "code", direction : "ASC", column :  "code",  useExternalSorting: true };
       // $scope.sortInfo = [{ fields:[ 'code'], directions: ['ASC']}];

        $scope.setPagingData = function(data, page, pageSize, total){
            var pagedData = data.slice((page - 1) * pageSize, page * pageSize);     //
            $scope.myData = pagedData;//data;
            $scope.pagingOptions.totalServerItems = data.length;//total;
            if (!$scope.$$phase) {
                $scope.$apply();
            }
        };
        $scope.getPagedDataAsync = function (pageSize, page, searchText) {
            setTimeout(function () {
                var data;
                if (searchText) {
                    var ft = searchText.toLowerCase();
                    $http.get('http://localhost:9091/reports/reportdata/facilities.json').success(function (largeLoad) {
                        largeLoad = largeLoad.pages.rows;
                        data = largeLoad.filter(function(item) {
                            return JSON.stringify(item).toLowerCase().indexOf(ft) != -1;
                        });
                        $scope.setPagingData(data,page,pageSize);
                    });
                } else {
                 //   FacilityList.get({"max" : $scope.pagingOptions.pageSize, "page" : $scope.pagingOptions.currentPage}, function(data) {
                 //       $scope.setPagingData(data.pages.rows,page,pageSize,data.pages.total);
                 //   });
                    $http.get('http://localhost:9091/reports/reportdata/facilities.json').success(function (largeLoad) {
                        largeLoad = largeLoad.pages.rows;
                        $scope.setPagingData(largeLoad,page,pageSize);
                    });
                }
            }, 100);
        };

        $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);

        $scope.$watch('pagingOptions', function () {
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
        }, true);
        $scope.$watch('filterOptions', function () {
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
        }, true);

    ///
  //  $scope.$watch('sortInfo', function () {
  //      alert('Sorted Info: ' + $scope.sortInfo );
   //     $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
   // }, true);
 //   $scope.$watch('sortInfo.field', function () {
  //      alert('Sorted field: ' + $scope.sortInfo.field );
  //      $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
  //  }, true);
  //  $scope.$watch('sortInfo.column', function () {
  //      alert('Sorted column: ' + $scope.sortInfo.column );
   //     $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
   // }, true);
  //  $scope.$watch('sortInfo.direction', function () {
   //     alert('Sorted direction: ' + $scope.sortInfo.direction );
   //     $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
   // }, true);
      ///


        $scope.gridOptions = {
            data: 'myData',
            enablePaging: true,
            enableSorting :true,
            showFooter: true,
            selectWithCheckboxOnly :false,
            pagingOptions: $scope.pagingOptions,
            filterOptions: $scope.filterOptions,
            //useExternalSorting: true,
            //sortInfo: $scope.sortInfo,
            showColumnMenu: true,
            enableRowReordering: true
        };
}
