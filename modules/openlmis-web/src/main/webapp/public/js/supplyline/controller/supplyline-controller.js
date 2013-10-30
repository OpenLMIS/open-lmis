/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function SupplylineController($scope,$location,$dialog,messageService,ReportPrograms, SupplyingFacilities, SupervisoryNodes, Supplylines, Supplyline, SupplylineDelete) {

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

    SupplyingFacilities.get(function(data){
        $scope.facilities = data.facilities;
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
    };

    //  switch to new mode
    $scope.startAddNewSupplyline = function() {
        $scope.setFlags('add','start');
        $scope.$parent.formActive = "supplyline-form-active";
    };

    // create supply line
    $scope.createSupplyline = function () {
        $scope.error = "";

        var form = document.getElementById('createSupplylineForm');

        if (form.$invalid) {
            $scope.showError = true;
            return;
        }
        $scope.showError = false;
        Supplylines.save({}, $scope.newSupplyline, function (data) {

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
        return !($scope.supplylines === undefined || $scope.supplylines === null);
    };

    $scope.clearErrorsOnPage = function(){
        $scope.showDelError = false;
        $scope.delError = "";
        $scope.showError = false;
        $scope.message = "";
        $scope.creationError = "";
        $scope.error = "";
    };


    $scope.startSupplylineEdit = function (supplylineUnderEdit) {
        $scope.supplylinesBackupMap[supplylineUnderEdit.id].editFormActive = "supplyline-form-active";
        $scope.supplylinesBackupMap[supplylineUnderEdit.id].edit = true;
        $scope.clearErrorsOnPage();
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
        $scope.setFlags('cancel','cancel');
    };

    $scope.showConfirmSupplylineDeleteWindow = function (supplylineUnderDelete) {
        var dialogOpts = {
            id: "deleteSupplylineDialog",
            header: "Delete supply line",
            body: "Please confirm that you want to delete the supply line with the ID: " + supplylineUnderDelete.id + " and Description: " + supplylineUnderDelete.description
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

        var errorHandler = function (response) {
            $scope.showDelError = true;
            $scope.delError = response.data.error;
            $scope.supplylinesBackupMap[$scope.supplylineUnderDelete.id].deleted = false;
        };

        SupplylineDelete.get({id : $scope.supplylineUnderDelete.id }, function (data) {
          $scope.showDelError = false;
          $scope.delError = "";

          $scope.message = data.success;
          setTimeout(function() {
                $scope.$apply(function() {
                    $scope.supplylines = data.supplylines;
                    $scope.message = "";
                });
            }, 4000);
            $scope.newSupplyline = {};
            $scope.editSupplyline = {};
            $scope.setFlags('delete','delete');
            if(data.success !== null ){
                $scope.showDelError = false;
                $scope.delError = "";
            }
         },errorHandler);

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
 };

}
