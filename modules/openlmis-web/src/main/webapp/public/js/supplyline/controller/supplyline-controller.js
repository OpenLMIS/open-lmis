/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function SupplylineController($scope,  ReportPrograms, AllFacilities, SupervisoryNodes, Supplylines, Supplyline, Supplylinelist, $location,$dialog,messageService,SupplylineDelete) {

    //$scope.$parent.newSupplylineMode = false;

    //initialize
    $scope.supplylinesBackupMap = [];
    $scope.newSupplyline = {};
    $scope.supplylines = {};
    $scope.editSupplyline = {};
    $scope.creationError = '';


    // drop down lists
    ReportPrograms.get(function (data){
        $scope.programs = data.programs;
        //alert(JSON.stringify($scope.programs, null, 4));
    });

    AllFacilities.get(function(data){
        $scope.facilities = data.facilityList;
    });

    SupervisoryNodes.get(function(data){
        $scope.supervisoryNodes = data.supervisoryNodes;
    });

// all supply lines   for list
    Supplylinelist.get({}, function (data) {
        $scope.supplylineslist = data.supplyLineList;

    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });


    // all supply lines
    Supplylines.get({}, function (data) {
        $scope.initialSupplylines = angular.copy(data.supplylines, $scope.initialSupplylines);
        $scope.supplylines = data.supplylines;
        for(var supplylineIndex in data.supplylines){
            var supplyline = data.supplylines[supplylineIndex];
            $scope.supplylinesBackupMap[supplyline.id] =  $scope.getBackupSupplyline(supplyline);
        }

    }, function (data) {
        $location.path($scope.$parent.sourceUrl);
    });


//  given supply line
    $scope.getBackupSupplyline = function (supplyline) {
        return {
            programid: supplyline.programid,
            supplyingfacilityid: supplyline.supplyingfacilityid,
            supervisorynodeid: supplyline.supervisorynodeid,
            description: supplyline.description
        };
    };

    // create supply line
    // this is controller (js)
    $scope.createSupplyline = function () {
        $scope.error = "";

        if ($scope.createSupplylineForm.$invalid) {
            $scope.showErrorForCreate = true;
            return;
        }
        $scope.showErrorForCreate = false;
        Supplylines.save({}, $scope.newSupplyline, function (data) {
             $scope.supplylines.unshift(data.supplyline);
            $scope.completeAddNewSupplyline(data.supplyline);
            $scope.message = data.success;
            setTimeout(function() {
                $scope.$apply(function() {
                    $scope.supplylineslist = data.supplyLineList;
                    $scope.message = "";
                });
            }, 4000);
            $scope.newSupplyline = {};
        }, function (data) {
            $scope.message = "";
            $scope.creationError = data.data.error;
        });
    };

//  switch to new mode
    $scope.startAddNewSupplyline = function() {
        $scope.$parent.newSupplylineMode = true;
        $scope.$parent.formActive = "supplyline-form-active";
    };

    //  backup record
    $scope.completeAddNewSupplyline = function(supplyline) {
        $scope.supplylinesBackupMap[supplyline.id] = $scope.getBackupSupplyline(supplyline);
        $scope.$parent.newSupplylineMode = false;
        $scope.showErrorForCreate = false;
        //$scope.supplylines.refresh();
    };

// cancel record
    $scope.cancelAddNewSupplyline = function(supplyline) {
        $scope.$parent.newSupplylineMode = false;
        $scope.showErrorForCreate = false;
    };

//    //  scope is undefined,
    $scope.supplylineLoaded = function () {

        //alert(JSON.stringify($scope.supplylines, null, 4));
        return !($scope.supplylines == undefined || $scope.supplylines == null);
    };

    // update

    $scope.updateSupplyline = function (supplyline, form) {
        function updateUiData(sourceSupplyline) {
            var supplylinesLength = $scope.supplylines.length;
            //alert(JSON.stringify(sourceSupplyline, null, 4));
            for (var i = 0; i < supplylinesLength; i++) {
                if ($scope.supplylines[i].id == sourceSupplyline.id) {
                    $scope.supplylines[i].programid = sourceSupplyline.programid;
                    $scope.supplylines[i].supervisorynode = sourceSupplyline.supervisorynode;
                    $scope.supplylines[i].supplyingfacilityid = sourceSupplyline.supplyingfacilityid;
                    $scope.supplylines[i].description = sourceSupplyline.description;
                    $scope.supplylines[i].modifiedBy = sourceSupplyline.modifiedBy;
                    $scope.supplylines[i].modifiedDate = sourceSupplyline.modifiedDate;
                }
            }
        }

        $scope.error = "";
        if (form.$invalid) {
            $scope.showErrorForEdit = true;
            return;
        }

        $scope.supplylinesBackupMap[supplyline.id].error = '';
        $scope.showErrorForEdit = true;

        Supplyline.update({id:supplyline.id}, supplyline, function (data) {
            var returnedSupplyline = data.supplyline;
            $scope.supplylinesBackupMap[returnedSupplyline.id] = $scope.getBackupSupplyline(returnedSupplyline);

            updateUiData(returnedSupplyline);
            $scope.message = data.success;
            setTimeout(function() {
                $scope.$apply(function() {
                    // refresh list
                    $scope.supplylineslist = data.supplyLineList;
                    $scope.message = "";
                });
            }, 4000);
            $scope.error = "";
            $scope.newSupplyline = {};
            $scope.editSupplyline = {};

            $scope.supplylinesBackupMap[returnedSupplyline.id].editFormActive = 'updated-item';
            $scope.supplylinesBackupMap[returnedSupplyline.id].edit = false;
        }, function (data) {
            $scope.message = "";
            $scope.startSupplylineEdit(supplyline);
            $scope.supplylinesBackupMap[supplyline.id].error = data.data.error;
        });
    };

    //
    $scope.startSupplylineEdit = function (supplylineUnderEdit) {
        //alert(JSON.stringify(supplylineUnderEdit, null, 4));
        //alert(JSON.stringify($scope.supplylinesBackupMap, null, 4));
        //alert(JSON.stringify($scope.initialSupplylines, null, 4));


        $scope.supplylinesBackupMap[supplylineUnderEdit.id].editFormActive = "supplyline-form-active";
    };

    $scope.cancelSupplylineEdit = function (supplylineUnderEdit) {
        var backupSupplylineRow = $scope.supplylinesBackupMap[supplylineUnderEdit.id];
        supplylineUnderEdit.programid = backupSupplylineRow.programid;
        supplylineUnderEdit.supervisorynodeid = backupSupplylineRow.supervisorynodeid;
        supplylineUnderEdit.supplyingfacilityid = backupSupplylineRow.supplyingfacilityid;
        supplylineUnderEdit.description = backupSupplylineRow.description;
        $scope.supplylinesBackupMap[supplylineUnderEdit.id].error = '';
        $scope.supplylinesBackupMap[supplylineUnderEdit.id].editFormActive = '';
    };


    $scope.showConfirmSupplylineDeleteWindow = function (supplylineUnderDelete) {
        var dialogOpts = {
            id: "deleteSupplylineDialog",
            header: messageService.get('Delete supplyline'),
            body: messageService.get('delete.facility.confirm', supplylineUnderDelete.description, supplylineUnderDelete.id)
        };
        $scope.supplylineUnderDelete = supplylineUnderDelete;
        OpenLmisDialog.newDialog(dialogOpts, $scope.deleteSupplylineCallBack, $dialog, messageService);
    };

    $scope.deleteSupplylineCallBack = function (result) {
        if (!result)
        {
            $scope.supplylinesBackupMap[$scope.supplylineUnderDelete.id].delete = false;
            return;
        }

          SupplylineDelete.get({id : $scope.supplylineUnderDelete.id }, $scope.supplyline, function (data) {

            $scope.message = data.success;
            setTimeout(function() {
                $scope.$apply(function() {
                    // refresh list
                    $scope.supplylineslist = data.supplyLineList;
                    $scope.message = "";
                });
            }, 4000);
            $scope.error = "";
            $scope.newSupplyline = {};
            $scope.editSupplyline = {};

         });

    };


    //


};
