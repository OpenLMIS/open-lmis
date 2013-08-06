/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function SupplylineController($scope,$location,$dialog,messageService,ReportPrograms, AllFacilites, SupervisoryNodes, Supplylines, Supplyline, SupplylineDelete) {

    //initialize
    $scope.supplylinesBackupMap = [];
    $scope.newSupplyline = {};
    $scope.supplylines = {};
    $scope.editSupplyline = {};
    $scope.creationError = '';

    if ($scope.$parent.newSupplylineMode || $scope.$parent.editSupplylineMode) {
        $scope.AddEditMode = true;
        $scope.title = ($scope.$parent.newSupplylineMode) ? $scope.title = 'Add Supply Line' : $scope.title = 'Edit Supply Line';

    } else {
        $scope.AddEditMode = false;
        $scope.title = 'Supply Lines';
    }


    // drop down lists
    ReportPrograms.get(function (data){
        $scope.programs = data.programs;
        //alert(JSON.stringify($scope.programs, null, 4));
    });

    AllFacilites.get(function(data){
        $scope.facilities = data.allFacilities;
    });

    //$scope.facilities  ={};

    SupervisoryNodes.get(function(data){
        $scope.supervisoryNodes = data.supervisoryNodes;
    });

    // all supply lines
    Supplylines.get({}, function (data) {
        $scope.initialSupplylines = angular.copy(data.supplylines, $scope.initialSupplylines);
        $scope.supplylines = data.supplylines;
        //alert(JSON.stringify($scope.supplylines, null, 4));
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

    $scope.processSave = function (supplyline,form)
    {
      if ($scope.$parent.newSupplylineMode)
      {
         $scope.createSupplyline();
      } else {
          $scope.updateSupplyline(supplyline, form);
      }
    }

    //  switch to new mode
    $scope.startAddNewSupplyline = function() {
        $scope.setFlags('add','start');
        $scope.$parent.formActive = "supplyline-form-active";
    };

    // create supply line
    $scope.createSupplyline = function () {
        $scope.error = "";

        if ($scope.supplylineForm.$invalid) {
            $scope.showError = true;
            return;
        }
        $scope.showError = false;
        Supplylines.save({}, $scope.supplyline, function (data) {

            //alert(JSON.stringify(data.supplyline, null, 4));
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

   //  backup record
    $scope.completeAddNewSupplyline = function(supplyline) {
        $scope.supplylinesBackupMap[supplyline.id] = $scope.getBackupSupplyline(supplyline);
        $scope.showError = false;
        $scope.setFlags('add','end');
    };

// cancel record
    $scope.cancelAddNewSupplyline = function(supplyline) {
        $scope.showError = false;
        $scope.setFlags('cancel','cancel');
      };

//    //  scope is undefined,
    $scope.supplylineLoaded = function () {
        //alert(JSON.stringify($scope.supplylines, null, 4));
        return !($scope.supplylines == undefined || $scope.supplylines == null);
    };


    //
    $scope.startSupplylineEdit = function (supplylineUnderEdit) {
        $scope.supplylinesBackupMap[supplylineUnderEdit.id].editFormActive = "supplyline-form-active";
        Supplyline.get({id: supplylineUnderEdit.id}, function (data) {
        $scope.supplyline = data.supplyline;
            //alert(JSON.stringify($scope.supplyline, null, 4));
        }, {});
        $scope.setFlags('edit','start');
    };
    // update
    $scope.updateSupplyline = function (supplyline, form) {

       function updateUiData(sourceSupplyline) {

            var supplylinesLength = $scope.supplylines.length;

            for (var i = 0; i < supplylinesLength; i++) {
                if ($scope.supplylines[i].id == sourceSupplyline.id) {
                    $scope.supplylines[i].program = sourceSupplyline.program;
                    $scope.supplylines[i].supervisoryNode = sourceSupplyline.supervisoryNode;
                    $scope.supplylines[i].supplyingFacility = sourceSupplyline.supplyingFacility;
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
            updateUiData(data.supplylines);
            $scope.completeEditSupplyline(returnedSupplyline);
            $scope.message = data.success;
            setTimeout(function() {
                $scope.$apply(function() {
                    // refresh list
                    $scope.message = "";
                });
            }, 4000);
            $scope.error = "";
            $scope.newSupplyline = {};
            $scope.editSupplyline = {};
            $scope.AddEditMode = false;

            $scope.supplylinesBackupMap[returnedSupplyline.id].editFormActive = 'updated-item';
            $scope.supplylinesBackupMap[returnedSupplyline.id].edit = false;
        }, function (data) {
            $scope.message = "";
            $scope.startSupplylineEdit(supplyline);
            $scope.supplylinesBackupMap[supplyline.id].error = data.data.error;
        });
    };

    //  backup record
    $scope.completeEditSupplyline = function(supplyline) {
        $scope.supplylinesBackupMap[supplyline.id] = $scope.getBackupSupplyline(supplyline);

        var supplylinesLength = $scope.supplylines.length;

        for (var i = 0; i < supplylinesLength; i++) {
            if ($scope.supplylines[i].id == supplyline.id) {
                $scope.supplylines[i].program = supplyline.program;
                $scope.supplylines[i].supervisoryNode = supplyline.supervisoryNode;
                $scope.supplylines[i].supplyingFacility = supplyline.supplyingFacility;
                $scope.supplylines[i].description = supplyline.description;
                $scope.supplylines[i].modifiedBy = supplyline.modifiedBy;
                $scope.supplylines[i].modifiedDate = supplyline.modifiedDate;
            }
        }
        $scope.showError = false;
        $scope.setFlags('edit','end');
    };


    $scope.cancelSupplylineEdit = function (supplylineUnderEdit) {
        var backupSupplylineRow = $scope.supplylinesBackupMap[supplylineUnderEdit.id];
        supplylineUnderEdit.programid = backupSupplylineRow.programid;
        supplylineUnderEdit.supervisorynodeid = backupSupplylineRow.supervisorynodeid;
        supplylineUnderEdit.supplyingfacilityid = backupSupplylineRow.supplyingfacilityid;
        supplylineUnderEdit.description = backupSupplylineRow.description;
        $scope.supplylinesBackupMap[supplylineUnderEdit.id].error = '';
        $scope.supplylinesBackupMap[supplylineUnderEdit.id].editFormActive = '';
        alert('cancel');
        $scope.setFlags('cancel','cancel');
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
            $scope.supplylinesBackupMap[$scope.supplylineUnderDelete.id].deleted = false;
            return;
        }

          SupplylineDelete.get({id : $scope.supplylineUnderDelete.id }, $scope.supplyline, function (data) {
          $scope.message = data.success;
          setTimeout(function() {
                $scope.$apply(function() {
                    $scope.supplylines = data.supplylines;
                    $scope.message = "";
                });
            }, 4000);
            $scope.error = "";
            $scope.newSupplyline = {};
            $scope.editSupplyline = {};
            $scope.setFlags('delete','delete');

         });

    };

 $scope.setFlags = function(mode, state)
 {

         $scope.$parent.editSupplylineMode = false;
         $scope.$parent.newSupplylineMode = false;
         $scope.AddEditMode = false;
         $scope.title = 'Supply Lines';


       if (mode == 'edit' && state == 'start') {
        $scope.$parent.editSupplylineMode = true;
        $scope.$parent.newSupplylineMode = false;
        $scope.AddEditMode = true;
        $scope.title = 'Edit Supply Line';
    }

     if (mode == 'edit' && state == 'end') {
         $scope.$parent.editSupplylineMode = false;
         $scope.$parent.newSupplylineMode = false;
         $scope.AddEditMode = false;
         $scope.title = 'Supply Lines';
     }

     if (mode == 'add' && state == 'start') {
         $scope.$parent.editSupplylineMode = false;
         $scope.$parent.newSupplylineMode = true;
         $scope.AddEditMode = true;
         $scope.title = 'Add Supply Line';
     }

     if (mode == 'add' && state == 'end') {
         $scope.$parent.editSupplylineMode = false;
         $scope.$parent.newSupplylineMode = false;
         $scope.AddEditMode = false;
         $scope.title = 'Supply Lines';
     }

 }


    //


};
