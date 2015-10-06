
/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
var vaccine = angular.module('VaccineModule', ['openlmis','ui.bootstrap','nsPopover','ngTable','datatables']).config(['$routeProvider', function ($routeProvider) {

    $routeProvider
        // this route is responding to
        .when('/internation_arrival', {
            templateUrl: "/public/pages/var/partials/international_shipment.html",
            controller: "StockModuleController"
        })
        // this route is responding topackage arrival route
        .when('/package_arrivals', {
            templateUrl: "/public/pages/var/partials/package_arrival.html",
            controller: "StockModuleController"
        })
        // this route is responding to items route
        .when('/items', {
            templateUrl: "/public/pages/var/partials/stock_items.html",
            controller: "StockModuleController"
        })
        // this route is responding to prepare  route
        .when('/prepare', {
            templateUrl: "/public/pages/var/partials/prepare.html",
            controller: "StockModuleController"
        })
        // this route is responding to sending package
        .when('/sending_package', {
            templateUrl: "/public/pages/var/partials/sending_package.html",
            controller: "StockModuleController"
        })
        // this route is responding to confirm  route
        .when('/confirm', {
            templateUrl: "/public/pages/var/partials/confirm_reception.html",
            controller: "StockModuleController"
        })
        // this route is responding to receive route
        .when('/receive', {
            templateUrl: "/public/pages/var/partials/receive.html",
            controller: "StockReceiveController"
        })
        // this route is responding to home route
        .when('/home', {
            templateUrl: "/public/pages/var/partials/stockHome.html",
            controller: "StockModuleController"
        })
       // this route is responding to pre advice route
        .when('/preadvice', {
            templateUrl: "/public/pages/var/partials/preadvice.html",
            controller: "StockModuleController"
        })
        // this route is responding to pre advice route
        .when('/package_information', {
            templateUrl: "/public/pages/var/partials/package_information.html",
            controller: "StockModuleController"
        })
        // this route is responding to scan package route
        .when('/', {
            templateUrl: "/public/pages/var/partials/receive.html",
            controller: "StockReceiveController"
        })
        // this route is responding to  any link other than registered
        .otherwise({redirectTo: '/'});

}]).directive('onKeyup', function () {
        return function (scope, elm, attrs) {
            elm.bind("keyup", function () {
                scope.$apply(attrs.onKeyup);
            });
        };
});

vaccine.controller("StockModuleController",function($scope,$http,$location,$routeParams,$resource,$filter){

    $scope.packagesJson = null;
    $scope.data = {};
    $scope.current_user = {};
    $scope.data.vaccine_packages = {};
    $scope.programId = "82";
    $scope.vaccines = [];
    $scope.package = {};
    //get the required program.
    $http.get('/programs').success(function(data) {
        angular.forEach(data.programs,function(value){
            if(value.name === "Vaccine"){
                $scope.programId = value.id;
            }
        });
    }).error(function(data) {
        console.log("Error:" + data);
    });

    //get current logged in user
    $http.get('/user-context').success(function(data) {
        $scope.current_user = data;
    }).error(function(data) {
        console.log("Error:" + data);
    });

    //get a list of vaccines
    $http.get('/vaccine/inventory/programProducts/programId/'+$scope.programId).success(function(data) {
        angular.forEach(data.programProductList,function(value){
            if(value.productCategory.name === "Vaccine"){
                $scope.vaccines.push({'id':value.product.id,'name':value.product.primaryName});
            }
        });
    }).error(function(data) {
        console.log("Error:" + data);
    });

    $scope.getVaccineName = function(id){
        var name= "";
        angular.forEach($scope.vaccines,function(value){
            if(value.id === id){
                name = value.name;
            }
        });
        return name;
    };

    //load all packaging details
    $http.get('/vaccine/gitn_lookup/all').
        success(function(data) {
            $scope.gtin_lookups = data.gitn_lookup;
            $scope.data.vaccine_packages = data.gitn_lookup;
        }).
        error(function(data) {
            console.log("Error:" + data);
        });

    initialize();
    function initialize() {
        var sscc_input =angular.element('#sscc_number_field');
        $(sscc_input).focus();
        $scope.editFlag = false;
        $scope.productSelectOption = {maximumSelectionSize : 4};
        $scope.$parent.currentTab = 'SUMMARY';
        $scope.showProductsFilter = true;

    }

    //getting var reports
    $http.get('/vaccine/var_details/all').
        success(function(data) {
            $scope.data.reports = data.var_details;
        }).
        error(function(data) {
            console.log("Error:" + data);
        });

    // temporary api call to pull packages
    //$http.get('/public/pages/var/dummData/manufacturerpackage.json').
    $scope.fecthPackages = function(){
        $http.get('/vaccine/var_items/all').
            success(function(data) {
                angular.forEach(data.var_items,function(value){
                    angular.forEach($scope.gtin_lookups,function(val){
                        if(val.id === value.gtinlookupid){
                            value.packaging = val;
                        }
                    });
                    angular.forEach($scope.data.reports,function(val){
                        if(val.id === value.vardetailsid){
                            value.details = val;
                        }
                    });
                });
                $scope.packagesJson = data.var_items;
            }).
            error(function(data) {
                console.log("Error:" + data);
            });
    };
    $scope.fecthPackages();

    $scope.vaccinePackages = [];
    $scope.gtinSelect = false;
    $scope.pullPackaging = function(id){

        $scope.vaccinePackages = [];
        angular.forEach($scope.gtin_lookups,function(value){
            if(value.productid == id){
                $scope.vaccinePackages.push(value);
            }
        });
        $scope.gtinSelect = true;
    };
    // HANDLING PRE ADVICE MENU
    $scope.tabToggle = function(menu){
        $scope.container = menu;
        if(menu==="all"){
            $scope.filtering = false;
            $scope.all = true;
            $scope.pending = false;
            $scope.received = false;
            $scope.viewForm = false;
            $scope.viewTable = true;
            $scope.editForm = false;
        }
        if(menu==="received"){
            $scope.filtering =true;
            $scope.status = "received";
            $scope.all = false;
            $scope.pending = false;
            $scope.received = true;
            $scope.viewForm = false;
            $scope.viewTable = true;
            $scope.editForm = false;
        }
        if(menu === "pending"){
            $scope.filtering =true;
            $scope.all = false;
            $scope.status = "pending";
            $scope.pending = true;
            $scope.received = false;
            $scope.viewForm = false;
            $scope.viewTable = true;
            $scope.editForm = false;
        }
    };
    $scope.tabToggle('all');

    $scope.fillFromShipment = function(shipment_id){
        angular.forEach($scope.data.reports,function(val){
            if(val.awbnumber === shipment_id){
                $scope.package.flight_number = val.flightnumber;
                $scope.package.airport = val.destnationairport;
                $scope.package.arrival_date = new Date(val.estimatetimeofarrival);
                $scope.package.order_number = val.purchaseordernumber;
                $scope.package.shipment_id = val.awbnumber;
                $scope.package.awb_number = val.airwaybill;
            }
        });
    };

     $scope.fillFromAWB = function(awb){
         angular.forEach($scope.data.reports,function(val){
             if(val.airwaybill === awb){
                 $scope.package.flight_number = val.flightnumber;
                 $scope.package.airport = val.destnationairport;
                 $scope.package.arrival_date =  new Date(val.estimatetimeofarrival);
                 $scope.package.order_number = val.purchaseordernumber;
                 $scope.package.shipment_id = val.awbnumber;
                 $scope.package.awb_number = awb;
             }
         });
     };
    // table between add form and table
    //$scope.viewTable = true;
    var viewTable   = angular.element("#viewTable");
    var editForm    = angular.element("#editForm");
    var viewForm    = angular.element("#viewForm");
    jQuery(viewTable).show();
    jQuery(editForm).hide();
    jQuery(viewForm).hide();
    $scope.toggleFormAndTable = function(tobeSeen){
        if(tobeSeen==="addNew"){
            //$scope.viewForm = true;
            //$scope.viewTable = false;
            //$scope.editForm = false;
            jQuery(viewTable).hide();
            jQuery(editForm).hide();
            jQuery(viewForm).show();
        }
        if(tobeSeen==="cancelAdd"){
            //$scope.viewForm = false;
            //$scope.viewTable = true;
            //$scope.editForm = false;
            jQuery(viewTable).show();
            jQuery(editForm).hide();
            jQuery(viewForm).hide();
        }
    };

    // adding package
    //$scope.package = null;
    $scope.addPackage = function(data){
        $scope.packageStructure = {
            "awbnumber": data.shipment_id,
            "flightnumber":data.flight_number,
            "destnationairport":data.airport,
            "actualtimeofarrival":null,
            "estimatetimeofarrival": data.arrival_date,
            "numberofitemsinspected":null,
            "coolanttype":null,
            "tempraturemonitor":null ,
            "purchaseordernumber": data.order_number,
            "clearingagent":null,
            "labels":null,
            "comments":null,
            "invoice":null,
            "packinglist":null,
            "releasecerificate":null,
            "deliverystatus":'pending',
            "airwaybill":data.awb_number,
            "createdby":$scope.current_user.id
        };
        $scope.itemStructure = {
            "vardetailsid": null,
            "shipmentnumber": data.awb_number,
            "productid": data.vaccine_id,
            "manufacturedate": data.manufacture_date,
            "expiredate": data.expiry_date,
            "lotnumber": data.lot_number,
            "numberofdoses": data.number_of_doses,
            "derliverystatus": 'pending',
            "numberreceived": null,
            "physicaldamage": null,
            "damagedamount": null,
            "vvmstatus": null,
            "problems": null,
            "modifiedby": null,
            "gtinlookupid": data.vaccine_packaging_id,
            "createdby":$scope.current_user.id
        };

            $http.post('/vaccine/var_details/save',$scope.packageStructure).
                success(function(data) {
                    $scope.itemStructure.vardetailsid = data.var_details.id;
                    $http.post('/vaccine/var_items/save',$scope.itemStructure).
                        success(function(data) {
                            jQuery(viewTable).show();
                            jQuery(viewForm).hide();
                            $scope.fecthPackages();
                        }).
                        error(function(data) {
                            console.log("Error:" + data);
                        });
                }).
                error(function(data) {
                    console.log("Error:" + data);
                });
        };

    $scope.editPackage = function(id,packageObject){
        console.log(id);
        $scope.editPackage = {};
        //$scope.viewForm = false;
        //$scope.editForm = true;
        //$scope.viewTable = false;
        jQuery(viewTable).hide();
        jQuery(editForm).show();
        jQuery(viewForm).hide();
        $scope.package = packageObject;

        $scope.editPackage.delivery_status= 'pending';
        //$scope.editPackage.expire_date= formatDate(packageObject.expire_date);
        $scope.editPackage.lot_number= packageObject.lot_number;
        //$scope.editPackage.manufacture_date= Date(formatDate(packageObject.manufacture_date));
        $scope.editPackage.number_of_doses= packageObject.number_of_doses;
        $scope.editPackage.purchasing_order_number= "order";
        $scope.editPackage.shipment_id= packageObject.shipment_id;
        $scope.editPackage.vaccine_packaging= null;
        $scope.editPackage.vaccine_packaging_id= packageObject.vaccine_packaging_id;

        $scope.fecthPackages();

    };
    $scope.updatePackage = function(id,packageObject){
        packageObject.id=id;
        console.log(packageObject);
        var updateUrl =  encodeURI('/var/manufacture/package/');
        $http.post(updateUrl,packageObject).
            success(function(data){
                $scope.fecthPackages();
                //$scope.viewForm = false;
                //$scope.editForm = false;
                //$scope.viewTable = true;
                jQuery(viewTable).show();
                jQuery(editForm).hide();
                jQuery(viewForm).hide();

            }).
            error(function(data) {
                console.log("Error:" + data);
            });


    };
    $scope.cancelEditpackage = function(editPackage){
        //$scope.editPackage = null;
        //$scope.viewForm = false;
        //$scope.editForm = false;
        //$scope.viewTable = true;
        jQuery(viewTable).show();
        jQuery(editForm).hide();
        jQuery(viewForm).hide();
    };
    $scope.deletePackage = function(id,packageObject){
        var updateUrl =  encodeURI('/var/manufacture/package/'+id);
        //Calling Web API to fetch shopping cart items
        $http.delete(updateUrl).success(function(data){
            //Passing data to deferred's resolve function on successful completion
            $scope.fecthPackages();
        }).error(function(){

            //Sending a friendly error message in case of failure
            deferred.reject("An error occured while editing item");
        });
    };
    // cancell package adding
    $scope.cancelAddPackage = function(data){
        $scope.package = null;
        $scope.viewForm = false;
        $scope.editForm = false;
        $scope.viewTable = true;
        jQuery(viewTable).show();
        jQuery(editForm).hide();
        jQuery(viewForm).hide();
    };
    // action to scan package

    //// RECEIVE PACKAGE
    $scope.scan_afresh = true;
    $scope.scan_lotnumber = false;
     // action to scan package
    var scan_package_button =angular.element('#scan_package_button');
    $scope.scapPackage = function(){
        var shipping_number =angular.element('#shipping_number').val();
        $scope.filtering_shipping_number = shipping_number;
        //window.location.href = link;// normal angular function don work
        $scope.scan_lotnumber = true;
        $scope.scan_afresh = false;
    };
    // action to scan sub package
    $scope.scapLotNumber = function(){
      var lot_number =angular.element('#lot_number_filed').val();
        $scope.lot_number = lot_number;
      var link = encodeURI("/public/pages/var/index.html#/"+"confirm?lotn="+lot_number+"&ssc="+$scope.sscc_number);
      window.location.href = link;// normal angular function don work
    };

    var confirm_package_button = angular.element("#confirm_package_button");
    $scope.condition = {'quantity':null,'physical_damage':null,'vvmstatus':null,'temp_monitors':null,'problems':null};
    $(confirm_package_button).bind("click",function(){
        var packageDelt = null;
        angular.forEach($scope.packagesJson,function(value,index){
            if(value.lot_number === $scope.lot_number){
                packageDelt = value;
            }
        });

         var confirmObject = {
            package_number: packageDelt.shipment_id,
            lot_number: packageDelt.lot_number,
            number_as_expected:$scope.condition.quantity,
            gtin: 'hyr23',
            number_recieved: 3,
            number_expected: 21,
            physical_damage: $scope.condition.physical_damage,
            vvm_status: $scope.condition.vvmstatus,
            problems: $scope.condition.problems,
            receiving_user:1,
            user:1,
            vaccine_packaging_id:2
        };

        $http({
            method: 'POST',
            url: '/var/package/arrival',
            data: JSON.stringify(confirmObject),
            headers: {'Content-Type': 'application/json'},
            success: function(obj) {
               console.log(obj);
            },
            error:function(data) {
                        console.log("Error:" + data);
                    }
        });
    });
    $scope.cancelConfirmation = function(){

        //var link = encodeURI("/public/pages/var/index.html#/receive");
        //window.location.href = link;// normal angular function don work
    };

    $scope.numberOfBoxes = function(doses_per_vials,vials_per_box,number_of_doses){
        $scope.boxes = parseInt(number_of_doses/(doses_per_vials*vials_per_box),10);
        var boxes = $scope.boxes;
        return boxes;
    };
    if($routeParams.ssc){
        $scope.sscc_number = $routeParams.ssc;
    }
    if($routeParams.lotn){
    $scope.lot_number = $routeParams.lotn;
    }



    $scope.checkExpiredate = function(){
        return true;
    };
    function formatDate(date) {
        var d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2){ month = '0' + month;}
        if (day.length < 2) { day = '0' + day; }

        return [year, month, day].join('-');
    }
});


vaccine.controller("StockMenuController",function($scope) {
    var dashboardMenuService = {};

    dashboardMenuService.tabs = [
        {header: 'Pre Advice', content:'/public/pages/var/index.html#/preadvice', name:'preadvice', closable:false, displayOrder: 0},
        {header: 'Receive Package', content:'/public/pages/var/index.html#/receive', name:'receive', closable:false, displayOrder: 1},
        {header: 'VAR', content:'/public/pages/var/index.html#/prepare', name:'prepare', closable:false, displayOrder: 2},
        //{header: 'Stock Items', content:'/public/pages/var/index.html#/items', name:'items', closable: false, displayOrder: 3},
        {header: 'GTIN Lookup', content:'/public/pages/var/index.html#/package_information', name:'items', closable: false, displayOrder: 4}
    ];


    $scope.dashboardTabs = dashboardMenuService.tabs;

    $scope.$on('dashboardTabUpdated', function(){
    $scope.dashboardTabs = dashboardMenuService.tabs;
    });
    $scope.closeTab = function(tabName){
    dashboardMenuService.closeTab(tabName);
    };

});

vaccine.controller("StockPackageController",function($scope,$http) {
    //initialize variables
    $scope.data = {};
    $scope.data.manufactures = {};
    $scope.showTable = true;
    $scope.showEdit = false;

    $scope.gtinStructure = {
        "gtin": null,
        "productid":null,
        "dosespervial":null,
        "vialsperbox": null,
        "boxesperbox":null,
        "createdBy":null
    };

    // load all vaccines packaging information
    $http.get('/vaccine/manufacturers').success(function(data) {
            $scope.data.manufactures = data.manufacturers;
        }).error(function(data) {
            console.log("Error:" + data);
        });

    //display the add table
    $scope.addNew = function(){
        $scope.showTable = false;
        $scope.showEdit = false;
    };

    //display the edit form
    $scope.editPackageInfo = function(id,packages){
        $scope.showTable = false;
        $scope.showEdit = true;
        $scope.data.package = packages;
        package.vaccine_id = packages.vaccine.id;
        package.manufacturer_id = packages.manufacturer.id;
    };

   //display the add table
    $scope.adding = false;
    $scope.addNewPackage = function(packages){
        $scope.adding = true;
        packages.status = '';
        $scope.packageToAdd = {
            "gtin": packages.gtin,
            "productid":packages.vaccine_id,
            "manufacturename":packages.manufacturer_id,
            "dosespervial":packages.doses_per_vial,
            "vialsperbox": packages.vials_per_box,
            "boxesperbox":packages.boxes_per_box,
            "createdBy":$scope.current_user.id
        };
        packages.country_id = 1;
        $http.post('/vaccine/gitn_lookup/save',$scope.packageToAdd). success(function(data) {
            $http.get('/vaccine/gitn_lookup/all').
                success(function(data) {
                    $scope.gtin_lookups = data.gitn_lookup;
                    $scope.data.vaccine_packages = data.gitn_lookup;
                }).
                error(function(data) {
                    console.log("Error:" + data);
                });
            $scope.cancelAdd();
        }). error(function(data) {
            console.log("Error:" + data);
        });
    };

    //hide the add table
    $scope.cancelAdd = function(){
        $scope.data.package = {};
        $scope.showTable = true;
        $scope.showEdit = false;
    };

    //update package information
    $scope.update = function(packages){
        var updateUrl =  '/var/vaccine/packaging/'+id;
        //Calling Web API to fetch shopping cart items
        $http.post(updateUrl,packages).success(function(data){
            //Passing data to deferred's resolve function on successful completion
            $scope.pullPackagingInformation();
            $scope.cancelAdd();
        }).error(function(data){
            console.log("Error:" + data);
            //Sending a friendly error message in case of failure
        });
    };


    //hide the add table
    $scope.deletePackageInfo = function(id,packages){
        var updateUrl =  '/var/vaccine/packaging/'+id;
        //Calling Web API to fetch shopping cart items
        $http.delete(updateUrl).success(function(data){
            //Passing data to deferred's resolve function on successful completion
            $scope.pullPackagingInformation();
        }).error(function(data){
            console.log("Error:" + data);
            //Sending a friendly error message in case of failure
        });
    };
});

vaccine.controller("StockReceiveController",function($scope,$http){
    $scope.scan_afresh = true;
    $scope.data = {};
    $scope.data.packages = [];
    $scope.data.processed_packages = [];
    $scope.loading_packages = false;
    $scope.no_package = false;
    $scope.process_package = false;
    $scope.data.shipping_number = "";
    $scope.programId = "82";
    $scope.date = new Date();
    $("#shipping_number").focus();
    $scope.packagesJson = null;
    $scope.data = {};
    $scope.current_user = {};
    $scope.data.vaccine_packages = {};
    $scope.programId = "82";
    $scope.vaccines = [];
    $scope.package = {};
    $scope.package.packing_list ="Yes" ;$scope.package.release_certificate ="Yes" ;$scope.package.invoice ="Yes" ;
    $scope.package.airway_bill ="Yes" ;$scope.package.labels ="Yes" ;
    $scope.package.collant_type ="Dry Ice" ;$scope.package.temp_monitor ="VVM" ;$scope.package.comments ="" ;
    //get the required program.
    $http.get('/programs').success(function(data) {
        angular.forEach(data.programs,function(value){
            if(value.name === "Vaccine"){
                $scope.programId = value.id;
            }
        });
    }).error(function(data) {
        console.log("Error:" + data);
    });

    //get current logged in user
    $http.get('/user-context').success(function(data) {
        $scope.current_user = data;
    }).error(function(data) {
        console.log("Error:" + data);
    });

    //get a list of vaccines
    $http.get('/vaccine/inventory/programProducts/programId/'+$scope.programId).success(function(data) {
        angular.forEach(data.programProductList,function(value){
            if(value.productCategory.name === "Vaccine"){
                $scope.vaccines.push({'id':value.product.id,'name':value.product.primaryName});
            }
        });
    }).error(function(data) {
        console.log("Error:" + data);
    });

    $scope.getVaccineName = function(id){
        var name= "";
        angular.forEach($scope.vaccines,function(value){
            if(value.id === id){
                name = value.name;
            }
        });
        return name;
    };

    //load all packaging details
    $http.get('/vaccine/gitn_lookup/all').
        success(function(data) {
            $scope.gtin_lookups = data.gitn_lookup;
            $scope.data.vaccine_packages = data.gitn_lookup;
        }).
        error(function(data) {
            console.log("Error:" + data);
        });

    //getting var reports
    $http.get('/vaccine/var_details/all').
        success(function(data) {
            $scope.data.reports = data.var_details;
        }).
        error(function(data) {
            console.log("Error:" + data);
        });

    // temporary api call to pull packages
    //$http.get('/public/pages/var/dummData/manufacturerpackage.json').
    $scope.fecthPackages = function(){
        $http.get('/vaccine/var_items/all').
            success(function(data) {
                angular.forEach(data.var_items,function(value){
                    angular.forEach($scope.gtin_lookups,function(val){
                        if(val.id === value.gtinlookupid){
                            value.packaging = val;
                        }
                    });
                    angular.forEach($scope.data.reports,function(val){
                        if(val.id === value.vardetailsid){
                            value.details = val;
                        }
                    });
                });
                $scope.packagesJson = data.var_items;
                $scope.data.packages = data.var_items;
            }).
            error(function(data) {
                console.log("Error:" + data);
            });
    };
    $scope.fecthPackages();
    $scope.data.processed_packages = [];
    $scope.scanPackage = function(){
        $scope.data.processed_packages = [];
        $scope.loading_packages = true;
        var i = 0;
        angular.forEach($scope.data.packages,function(val){
            if(val.shipmentnumber === $scope.data.shipping_number){
                i++;
                $scope.data.processed_packages.push(val);
            }
        });
        if(i === 0){
            $scope.data.processed_packages = {};
            $scope.no_package = true;
            $scope.loading_packages = false;
        }
        if(i !== 0){
            $scope.no_package = false;
            $scope.process_package = true;
            $scope.scan_afresh = false;
            $scope.loading_packages = false;
        }

    };

    //display package details
    $scope.loading_item = false;
    $scope.error_loading_item = false;
    $scope.show_singleItem = false;
    $scope.data.current_item = [];
    $scope.scanLotNumber = function(lot_number){
        $scope.barcode ={};
        if(lot_number.length > 35){
            var n = lot_number.lastIndexOf("21");
            $scope.barcode.lot_number = lot_number.substring(29,n);
        }else if(lot_number.length < 10){
            $scope.barcode.lot_number = lot_number;
        }else if(lot_number.length >= 20){
            $scope.barcode.lot_number = lot_number.substring(29);
            $scope.barcode.expiry = lot_number.substring(21,27);
            $scope.barcode.gtin = lot_number.substring(5,19);

        }

        $scope.loading_item = true;
        $scope.data.current_item = $scope.getItem($scope.barcode.lot_number);
        if($scope.data.current_item.length === 0){
            $scope.error_loading_item = true;
            $scope.loading_item = false;
        }else{
            $scope.error_loading_item = false;
            $scope.loading_item = false;
            $scope.show_singleItem = true;
            $scope.process_package = false;
        }
    };

    //find an item with specific lot number
    $scope.getItem = function(lot_number){
        var item = [];
        angular.forEach($scope.data.processed_packages,function(value){
            if(lot_number === value.lotnumber){
                item = value;
                $scope.condition = {};
                //$scope.condition.number_recieved = $scope.numberOfBoxes(value.vaccine_packaging.doses_per_vial,value.vaccine_packaging.vials_per_box,value.number_of_doses)
                $scope.condition.partials = "No";
                $scope.condition.vvm_status = "I (Okey)";
                $scope.condition.damaged_amount = 0;
                $scope.condition.physical_damage = "No";
                $scope.condition.number_recieved = 0;
                $scope.condition.number_as_expected = "Yes";
                $scope.condition.partials = "No";
                $scope.condition.number_of_partials = 0;
                $scope.condition.boxes_with_problems = 0;
            }
        });
        return item;
    };

    $scope.createParials = function(){
        $scope.condition.parials_boxes = [];
        for(var i = 0;i<$scope.condition.number_of_partials;i++){
            $scope.condition.parials_boxes.push({'lotnumber':$scope. data.current_item.lotnumber,'box_number':'','available_amount':''});
        }
        //return $scope.condition.parials_boxes;
    };

    $scope.createVVMS = function(){
        $scope.condition.problem_boxes = [];
        for(var i = 0;i<$scope.condition.boxes_with_problems;i++){
            $scope.condition.problem_boxes.push({'lotnumber':$scope. data.current_item.lotnumber,'box_number':'','alarm':'','cold_chain':''});
        }
        //return $scope.condition.problem_boxes;
    };

    $scope.getVVMStatus = function(){
        if($scope.condition.vvm_status === 'III( Bad)' || $scope.condition.vvm_status === 'IV( Bad)'){
            if($scope.condition.boxes_with_problems !== 0){
                return true;
            }else{
                return false;
            }

        }else{
            return false;
        }
    };

    //rollback completely
    $scope.cancelPackageProcessing = function(){
        $scope.data.processed_packages = [];
        $scope.loading_packages = false;
        $scope.no_package = false;
        $scope.process_package = false;
        $scope.scan_afresh = true;
        $scope.data.shipping_number = "";
        $("#shipping_number").focus();
    };

    //go back one step to list of items
    $scope.cancelConfirmation = function(){
        $scope.show_singleItem = false;
        $scope.process_package = true;
    };
    //uncover the number of boxes
    $scope.numberOfBoxes = function(doses_per_vials,vials_per_box,number_of_doses){
        $scope.boxes = parseInt(number_of_doses/(doses_per_vials*vials_per_box),10);
        var boxes = $scope.boxes;
        return boxes;
    };

    //return color of a column
    $scope.getcolor = function (status) {
        if(status === "Pending"){
            return '';
        }if(status === "processed"){
            return "rgba(91,155,67,0.6)";
        }
    };

    //determine if all items have been checked
    $scope.allChecked = function(){
        var count  = 0;
        var item_length = $scope.data.processed_packages.length;
        angular.forEach($scope.data.processed_packages,function(value){
            if(value.derliverystatus === 'processed'){
                count++;
            }
        });
        if(count === item_length){
            return true;
        }else{
            return false;
        }
    };

    //confirming single item
    $scope.itemTosave = {};
    $scope.itemTosave.arrival_package = [];
    $scope.addSingleItem = function(details,item){
        item.derliverystatus = "processed";
        item.conditions = details;
        $scope.show_singleItem = false;
        $scope.process_package = true;
    };

    $scope.showReport = false;
    $scope.displaySummary = function(){
        $scope.showReport = true;
    };
    $scope.hideSummary = function(){
        $scope.showReport = false;
    };
    $scope.doneSaving= false;
    $scope.saveVAR = function(){
        $scope.doneSaving= true;
        var numberofIetms = 0;
        angular.forEach($scope.data.processed_packages,function(data){
            numberofIetms ++;
            $scope.itemStructure = {
                "id":data.id,
                "vardetailsid": data.vardetailsid,
                "shipmentnumber": data.shipmentnumber,
                "productid": data.productid,
                "manufacturedate": data.manufacturedate,
                "expiredate": data.expiredate,
                "lotnumber": data.lotnumber,
                "numberofdoses": data.numberofdoses,
                "derliverystatus": 'received',
                "numberreceived": parseInt(data.conditions.number_recieved,10),
                "physicaldamage": data.conditions.physical_damage,
                "damagedamount": null,
                "vvmstatus": data.conditions.vvm_status,
                "problems": data.conditions.problems,
                "modifiedby": $scope.current_user.userId,
                "gtinlookupid": data.gtinlookupid
            };

            $scope.lotStructure = {
                "productid":data.productid,
                "lotCode": data.lotnumber,
                "manufacturerName": data.packaging.manufacturename,
                "manufactureDate": data.manufacturedate,
                "expirationDate": data.expiredate,
                "createdBy": $scope.current_user.userId,
                "modifiedBy": $scope.current_user.userId
            };

            //send an update for a package
            $http.post('/vaccine/inventory/lot/create',$scope.lotStructure).
                success(function(data) {
                    console.log(data);
                }).
                error(function(data) {
                    console.log("Error:" + data);
                });
            $scope.packageStructure = {
                "id":data.details.id,
                "awbnumber": data.details.awbnumber,
                "flightnumber":data.details.flightnumber,
                "destnationairport":data.details.destnationairport,
                "actualtimeofarrival":new Date(),
                "estimatetimeofarrival": data.details.estimatetimeofarrival,
                "numberofitemsinspected":numberofIetms,
                "coolanttype":$scope.package.collant_type,
                "tempraturemonitor":$scope.package.temp_monitor ,
                "purchaseordernumber": data.details.purchaseordernumber,
                "clearingagent":$scope.package.airway_bill,
                "labels":$scope.package.labels,
                "comments":$scope.package.comments,
                "invoice":$scope.package.invoice,
                "packinglist":$scope.package.packing_list,
                "releasecerificate":$scope.package.release_certificate,
                "deliverystatus":'received',
                "airwaybill":data.details.airwaybill
            };
            console.log($scope.packageStructure);

            //send an update for a package
            $http.post('/vaccine/var_items/save',$scope.itemStructure).
                success(function(data) {
                    console.log(data);
                }).
                error(function(data) {
                    console.log("Error:" + data);
                });
            //if(data.conditions.problem_boxes.length != 0){
            //   angular.forEach(data.conditions.problem_boxes,function(val){
            //      $scope.alarmStructure = {
            //         'vardetailsid':data.vardetailsid,
            //         'productid':data.productid,
            //         'boxnumber':val.box_number,
            //         'lotnumber':data.lotnumber,
            //         'alarmtemprature':val.alarm,
            //         'coldchainmonitor':val.cold_chain,
            //         'timeofinspection':new Date(),
            //         'gtinlookupid':data.gtinlookupid,
            //         'createdby':$scope.current_user.id
            //      }
            //   })
            //}if(data.conditions.parials_boxes.length != 0){
            //   angular.forEach(data.conditions.parials_boxes,function(val){
            //      $scope.partialStructure = {
            //         'vardetailsid':data.vardetailsid,
            //         'productid':data.productid,
            //         'boxnumber':val.box_number,
            //         'lotnumber':data.lotnumber,
            //         'expectednumber':val.available_amount,
            //         'availablenumber':val.available_amount,
            //         'gtinlookupid':data.gtinlookupid,
            //         'createdby':$scope.current_user.id
            //      }
            //   })
            //}
        });
        $http.post('/vaccine/var_details/save',$scope.packageStructure).
            success(function(data) {
                console.log(data);
                $scope.doneSaving= false;
                $scope.cancelPackageProcessing();

            }).
            error(function(data) {
                console.log("Error:" + data);
            });

    };

});