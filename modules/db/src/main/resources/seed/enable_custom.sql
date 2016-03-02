--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

-- This SQL script re-adds all country-specific data that was removed in disable_custom.sql.

INSERT INTO rights(name, righttype, description, displayorder, displaynamekey)
VALUES ('ACCESS_ILS_GATEWAY','ADMIN','Permission to access the ILS Gateway.',38,'right.ils')
  ,('ACCESS_NEW_DASHBOARD','REPORT','Permission to access new dashboard',13,'right.dashboard')
  ,('APPROVE_IVD','REQUISITION','Permission to Approve ivd form',51,'right.vaccine.approve.ivd')
  ,('CONFIGURE_HELP_CONTENT','ADMIN','Permission to Configure Help Content',33,'right.admin.help.content')
  ,('CREATE_IVD','REQUISITION','Permission to create ivd form',50,'right.vaccine.create.ivd')
  ,('CREATE_ORDER_REQUISITION','REQUISITION','Permission to Create Requisition',NULL,'right.create.order.requisition')
  ,('MANAGE_CUSTOM_REPORTS','REPORT','Permission to manage custom reports',NULL,'right.report.manage.custom.report')
  ,('MANAGE_DONOR','ADMIN','Permission to manage donors.',42,'right.manage.donor')
  ,('MANAGE_ELMIS_INTERFACE','ADMIN','Permission to manage ELMIS interface apps setting',32,'right.admin.elmis.interface')
  ,('MANAGE_SEASONALITY_RATIONING','ADMIN','Permission to manage seasonality rationing ',50,'right.admin.seasonality.rationing')
  ,('MANAGE_SUPPLYLINE','ADMIN','Permission to create and edit Supply Line',31,'right.manage.supplyline')
  ,('MANAGE_VACCINE_DISEASE_LIST','ADMIN','Permission to manage vaccine disease list',200,'right.admin.vaccine.disease')
  ,('MANAGE_VACCINE_PRODUCTS_CONFIGURATION','ADMIN','Permission to manage vaccine product configuration',NULL,'right.manage.vaccine.product.configuration')
  ,('MASS_DISTRIBUTION','REQUISITION','Permission to do mass distribution',NULL,'right.mass.distribution')
  ,('Print Issue report','REPORTING','',NULL,'')
  ,('Print Order Requisition','REPORTING','',NULL,'')
  ,('SERVICE_VENDOR_RIGHT','ADMIN','Permission to use system as service Vendor',41,'right.manage.service.vendor')
  ,('VIEW_ADJUSTMENT_SUMMARY_REPORT','REPORT','Permission to view adjustment summary Report',NULL,'right.report.adjustment')
  ,('VIEW_AVERAGE_CONSUMPTION_REPORT','REPORT','Permission to view avergae consumption Report',NULL,'right.report.average.consumption')
  ,('VIEW_CCE_STORAGE_CAPACITY_REPORT','REPORT','Permission to view CCE Storage Capacity Report',NULL,'right.report.cce.storage.capacity')
  ,('VIEW_COLD_CHAIN_EQUIPMENT_LIST_REPORT','REPORT','Permission to view cold chain equipment list Report',NULL,'right.report.coldchain.equipment')
  ,('VIEW_CONSUMPTION_REPORT','REPORT','Permission to view Consumption Report',NULL,'right.report.consumption')
  ,('VIEW_DASHBOARD_POC','REPORT','Permission to view dashboard poc',NULL,'right.report.dashboard')
  ,('VIEW_DISTRICT_CONSUMPTION_REPORT','REPORT','Permission to view district consumption comparison report',NULL,'right.report.district.consumption')
  ,('VIEW_DISTRICT_FINANCIAL_SUMMARY_REPORT','REPORT','Permission to view District Financial Summary Report',NULL,'right.report.district.financial')
  ,('VIEW_FACILITY_REPORT','REPORT','Permission to view Facility List Report',NULL,'right.report.facility')
  ,('VIEW_IVD','REQUISITION','Permission to view ivd reports',52,'right.vaccine.view.ivd')
  ,('VIEW_LAB_EQUIPMENTS_BY_FUNDING_SOURCE','REPORT','Permission to view lab equipment list by funding source Report',NULL,'right.report.equipment.funding')
  ,('VIEW_LAB_EQUIPMENTS_BY_LOCATION_REPORT','REPORT','Permission to view lab equipments by location Report',NULL,'right.report.lab.equipment.by.location')
  ,('VIEW_LAB_EQUIPMENT_LIST_REPORT','REPORT','Permission to view lab equipment list Report',NULL,'right.report.lab.equipment')
  ,('VIEW_MAILING_LABEL_REPORT','REPORT','Permission to view Mailing labels for Facilities',NULL,'right.report.mailing.label')
  ,('VIEW_ORDER_FILL_RATE_REPORT','REPORT','Permission to view Order Fill Rate Report',NULL,'right.report.order.fillrate')
  ,('VIEW_ORDER_FILL_RATE_SUMMARY_REPORT','REPORT','Permission to view order fill rate summary Report.',NULL,'right.report.fill.rate')
  ,('VIEW_ORDER_REPORT','REPORT','Permission to view Order Report',NULL,'right.report.order')
  ,('VIEW_ORDER_REQUISITION','REQUISITION','Permission to view Order Requisition',NULL,'right.view.order.requisition')
  ,('VIEW_PENDING_REQUEST','REQUISITION','Permission to View Pending Request',NULL,'right.view.pending.request')
  ,('VIEW_PIPELINE_EXPORT','REPORT','Permission to view Pipeline export Report',NULL,'')
  ,('VIEW_REGIMEN_SUMMARY_REPORT','REPORT','Permission to view Regimen Summary Report.',NULL,'right.report.regimen')
  ,('VIEW_REPAIR_MANAGEMENT_REPORT','REPORT','Permission to view Repair Management Report',NULL,'right.report.repair.management')
  ,('VIEW_REPORTING_RATE_REPORT','REPORT','Permission to view Reporting Rate Report',NULL,'right.report.reporting.rate')
  ,('VIEW_REQUISITION_REPORT','REPORT','Permission to View Requisitions Report',NULL,'right.report.requisition')
  ,('VIEW_RNR_FEEDBACK_REPORT','REPORT','Permission to view Report and Requisition Feedback Report.',NULL,'right.report.rnr.feedback')
  ,('VIEW_SEASONALITY_RATIONING_REPORT','REPORT','Permission to view seasonality rationing Report',NULL,'right.report.seasonality.rationing')
  ,('VIEW_STOCKED_OUT_REPORT','REPORT','Permission to view stocked out commodity Report',NULL,'right.report.stocked.out')
  ,('VIEW_STOCK_IMBALANCE_REPORT','ADMIN','Permission to view Stock Imbalance Report.',NULL,'right.report.stock.imbalance')
  ,('VIEW_STOCK_ON_HAND_REPORT','REPORT','Permission to View Stock On Hand Report',NULL,'right.report.stockonhand')
  ,('VIEW_SUMMARY_REPORT','REPORT','Permission to view Summary Report',NULL,'right.report.summary')
  ,('VIEW_SUPPLY_STATUS_REPORT','REPORT','Permission to view supply status by facility report',NULL,'right.report.supply.status')
  ,('VIEW_TIMELINESS_REPORT','REPORT','Permission to view Timeliness Report',NULL,'right.report.timeliness')
  ,('VIEW_VACCINE_REPLACEMENT_PLAN_SUMMARY','REPORT','Permission to View Replacement Plan Summary Report',NULL,'right.report.vaccine.replacement.plan.summary')
  ,('print_vaccine_Order_Requisition','REPORTING','',NULL,'')
  ,('vims_distribution','REPORTING','',NULL,'')
  ,('VIEW_CUSTOM_REPORTS','REPORT','Permission to view Custom Reports in Reports menu',NULL,'right.reports.custom')
  ,('VIEW_FACILITY_INTEGRATION','ADMIN','Permission to view facility integration section',NULL,'right.view.facility.integration')
  ,('VIEW_PRODUCT_RATIONING','ADMIN','Permission to view product rationing column',NULL,'right.view.product.rationing')
  ,('MANAGE_EQUIPMENT_SERVICING', 'ADMIN', 'Permission to manage service vendors and contracts', NULL, 'right.manage.equipment.servicing')
  ,('VIEW_HELP', 'ADMIN', 'Permission to view help link', NULL, 'right.view.help')
  ,('MANAGE_IVD_TEMPLATES', 'REQUISITION', 'Permission to manage IVD tabs in R&R template', NULL, 'right.manage.ivd.templates')
  ,('VIEW_USER_SUMMARY_REPORT', 'REPORT', 'Permission to view user summary Report', NULL, 'right.report.user.summary')
  ,('MANAGE_DEMOGRAPHIC_PARAMETERS', 'ADMIN', 'Permission to manage demographic parameters', 60, 'right.manage.demographic.parameters')
  ,('MANAGE_DEMOGRAPHIC_ESTIMATES', 'REQUISITION', 'Permission to manage demographic estimates', 30, 'right.manage.demographic.estimates')
  ,('FINALIZE_DEMOGRAPHIC_ESTIMATES', 'REQUISITION', 'Permission to finalize demographic estimates', 31, 'right.demographic.estimate.finalize')
  ,('UNLOCK_FINALIZED_DEMOGRAPHIC_ESTIMATES', 'REQUISITION', 'Permission to unlock finalized demographic estimates', 32, 'right.demographic.estimate.unlock.finalized')
;

INSERT INTO report_rights(templateid, rightname)
VALUES (3, 'Print Order Requisition')
  ,(4, 'Print Issue report')
  ,(5, 'vims_distribution')
  ,(6, 'print_vaccine_Order_Requisition')
;

UPDATE configuration_settings
SET value = '/public/pages/dashboard/index.html#/dashboard'
WHERE key = 'LOGIN_SUCCESS_DEFAULT_LANDING_PAGE';

UPDATE configuration_settings
SET isconfigurable = TRUE
WHERE groupname IN ('Dashboard','GENERAL','Order Export','R & R','VACCINE');
