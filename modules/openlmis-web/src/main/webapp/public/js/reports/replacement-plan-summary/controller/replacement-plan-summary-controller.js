function ReplacementPlanSummary($scope, ngTableParams, messageService, getReplacementPlanSummaryReport,getEquipmentsInNeedForReplacement) {
     $scope.equipmentsForReplacementModal = false;


        $scope.statuses =
            [
            {id:0,name:'O',value:'All Obsolete'},
            {id:1,name:'greater',value:'All >10'},
            {id:2,name:'capacityGap',value:'Capacity Gap'},
            {id:3,name:'greater',value:' >5 Breakdowns'},
            {id:4,name:'pqs',value:'Non-PQS Model'}
            ];


    $scope.exportReport = function (type) {
        $scope.filter.pdformat = 1;
        var params = jQuery.param($scope.filter);
       // var url = '/reports/download/equipment_replacement_list/list/' + type + '?' + params;
        var url = '/reports/download/replacement_plan_summary/' + type +'?' + params;





        window.open(url);
    };

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 10           // count per page
    });


    $scope.data = $scope.datarows = [];
   // $scope.filter.max = 10000;

    getReplacementPlanSummaryReport.get($scope.filter, function (data) {

        if (data.pages !== undefined && data.pages.rows !== undefined) {
            $scope.data = data.pages.rows;
            $scope.paramsChanged($scope.tableParams);
        }
    });

    $scope.OnFilterChanged = function () {
        // clear old data if there was any
        $scope.data = $scope.datarows = [];
       $scope.filter.max = 10000;


        getReplacementPlanSummaryReport.get($scope.filter, function (data) {
            if (data.pages !== undefined && data.pages.rows !== undefined) {
                $scope.data = data.pages.rows;



                $scope.paramsChanged($scope.tableParams);

              /*  var replacementyeartwo = _.pluck($scope.data, 'replacementyeartwo');


                var groupedByRegion = _.chain($scope.data).groupBy('levelId').map(function (value, key) {

                    return {levelId: key,geoLevel: _.first(value).region, purchasePrice: _.first(value).purchasePrice, facility: _.first(value).facilityName, allData: value };
                }).value();
                $scope.mama = {};

                var replacementPlans = [];
                $.each(groupedByRegion, function (i, temp1) {
                    var years_total = [];
                    var getAll = [];

                    var mama = [];
                    var facility = _.pluck(temp1.allData, 'facilityName');


                    var replacementYear = _.pluck(temp1.allData, 'replacementYear');


                    var region = _.pluck(temp1.allData, 'region');

                    var total = _.pluck(temp1.allData, 'total');


                    var al = [{facility:facility},{replacementYear:replacementYear},{total:total}];


                    var currentYear = new Date().getFullYear();

                    var mama2 = {};
                    $.each(temp1.allData, function (i, temp2) {
                        $scope.na = [];
                        if (temp2.replacementYear > 0) {
                            years_total[temp2.referenceYear] = temp2.total;

                            mama2 = {mama: years_total};
                            for (var z = 0; z < 5; z++) {


                                $scope.mama3 = mama.push(currentYear + z);

                            }

                        }
                        else {
                            // debugger
                        }

                    });

                    replacementPlans.push({levelId:temp1.levelId,geoLevel: temp1.geoLevel, purchasePrice: temp1.purchasePrice,
                        replacementYear: replacementYear,
                        years: total,facility:temp1.facility});

                });


                $scope.replacementPlans = replacementPlans;*/


            }
        });

    };


    $scope.getEquipmentList = function (feature, element,years) {
        console.log(element);
        console.log(feature);
        console.log(years);



        getEquipmentsInNeedForReplacement.get({program:$scope.filter.program,
            regionId:feature,
            plannedYear:element},

        function(data){

            $scope.facility = data.equipmentsInNeedOfReplacement;

            $scope.title = 'List of Equipments To be Replaced In  '+years + '  For  '+element.facilityName;

            $scope.equipmentsForReplacementModal = true;

        });


    };



  $scope.years = [];

   function currentYear(){

      for (var i=0; i<5; i++)
      $scope.years.push(i+new Date().getFullYear());
      return  $scope.years;

  }

    currentYear();

}