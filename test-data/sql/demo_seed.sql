BEGIN;
SET CONSTRAINTS ALL DEFERRED;

-- No INSERTs for adult_coverage_opened_vial_line_items necessary
-- No UPDATEs for adult_coverage_opened_vial_line_items necessary
-- No INSERTs for alert_facility_stockedout necessary
-- No UPDATEs for alert_facility_stockedout necessary
-- No INSERTs for alert_requisition_approved necessary
-- No UPDATEs for alert_requisition_approved necessary
-- No INSERTs for alert_requisition_emergency necessary
-- No UPDATEs for alert_requisition_emergency necessary
-- No INSERTs for alert_requisition_pending necessary
-- No UPDATEs for alert_requisition_pending necessary
-- No INSERTs for alert_requisition_rejected necessary
-- No UPDATEs for alert_requisition_rejected necessary
-- No INSERTs for alert_stockedout necessary
-- No UPDATEs for alert_stockedout necessary
-- No INSERTs for alert_summary necessary
-- No UPDATEs for alert_summary necessary
-- No INSERTs for alerts necessary
-- No UPDATEs for alerts necessary
-- No INSERTs for budget_configuration necessary
-- No UPDATEs for budget_configuration necessary
-- No INSERTs for budget_file_columns necessary
-- No UPDATEs for budget_file_columns necessary
-- No INSERTs for budget_file_info necessary
-- No UPDATEs for budget_file_info necessary
-- No INSERTs for budget_line_items necessary
-- No UPDATEs for budget_line_items necessary
-- No INSERTs for child_coverage_opened_vial_line_items necessary
-- No UPDATEs for child_coverage_opened_vial_line_items necessary
-- No INSERTs for comments necessary
-- No UPDATEs for comments necessary
-- No INSERTs for configurable_rnr_options necessary
-- No UPDATEs for configurable_rnr_options necessary
-- No INSERTs for configuration_settings necessary
-- No UPDATEs for configuration_settings necessary
-- -------------------
-- INSERTS for public.coverage_product_vials
-- -------------------
INSERT INTO coverage_product_vials
(
  vial,
  productcode,
  childcoverage
)
VALUES
(
  'BCG',
  'bcg20',
  TRUE
);
INSERT INTO coverage_product_vials
(
  vial,
  productcode,
  childcoverage
)
VALUES
(
  'Polio10',
  'polio10',
  TRUE
);
INSERT INTO coverage_product_vials
(
  vial,
  productcode,
  childcoverage
)
VALUES
(
  'Polio20',
  'polio20',
  TRUE
);
INSERT INTO coverage_product_vials
(
  vial,
  productcode,
  childcoverage
)
VALUES
(
  'Penta1',
  'penta1',
  TRUE
);
INSERT INTO coverage_product_vials
(
  vial,
  productcode,
  childcoverage
)
VALUES
(
  'Penta10',
  'penta10',
  TRUE
);
INSERT INTO coverage_product_vials
(
  vial,
  productcode,
  childcoverage
)
VALUES
(
  'PCV',
  'pcv10',
  TRUE
);
INSERT INTO coverage_product_vials
(
  vial,
  productcode,
  childcoverage
)
VALUES
(
  'Measles',
  'measles10',
  TRUE
);
INSERT INTO coverage_product_vials
(
  vial,
  productcode,
  childcoverage
)
VALUES
(
  'Tetanus',
  'tetanus10',
  FALSE
);

-- No UPDATEs for coverage_product_vials necessary
-- -------------------
-- INSERTS for public.coverage_target_group_products
-- -------------------
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'BCG',
  'bcg20',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'Polio (Newborn)',
  'polio10',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'Polio 1st dose',
  'polio10',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'Polio 2nd dose',
  'polio10',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'Polio 3rd dose',
  'polio10',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'Penta 1st dose',
  'penta1',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'Penta 2nd dose',
  'penta1',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'Penta 3rd dose',
  'penta1',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'PCV10 1st dose',
  'pcv10',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'PCV10 2nd dose',
  'pcv10',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'PCV10 3rd dose',
  'pcv10',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'Measles',
  'measles10',
  TRUE
);
INSERT INTO coverage_target_group_products
(
  targetgroupentity,
  productcode,
  childcoverage
)
VALUES
(
  'Pregnant Women',
  'tetanus10',
  FALSE
);

-- No UPDATEs for coverage_target_group_products necessary
-- No INSERTs for custom_reports necessary
-- No UPDATEs for custom_reports necessary
-- -------------------
-- INSERTS for public.delivery_zone_members
-- -------------------
INSERT INTO delivery_zone_members
(
  deliveryzoneid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  4,
  1,
  '2016-02-05 11:16:44.246',
  1,
  '2016-02-05 11:16:44.236'
);
INSERT INTO delivery_zone_members
(
  deliveryzoneid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  5,
  1,
  '2016-02-05 11:16:44.246',
  1,
  '2016-02-05 11:16:44.236'
);
INSERT INTO delivery_zone_members
(
  deliveryzoneid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  6,
  1,
  '2016-02-05 11:16:44.246',
  1,
  '2016-02-05 11:16:44.236'
);
INSERT INTO delivery_zone_members
(
  deliveryzoneid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  3,
  1,
  '2016-02-05 11:16:44.246',
  1,
  '2016-02-05 11:16:44.236'
);
INSERT INTO delivery_zone_members
(
  deliveryzoneid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  1,
  1,
  '2016-02-05 11:16:44.246',
  1,
  '2016-02-05 11:16:44.236'
);
INSERT INTO delivery_zone_members
(
  deliveryzoneid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  2,
  1,
  '2016-02-05 11:16:44.246',
  1,
  '2016-02-05 11:16:44.236'
);
INSERT INTO delivery_zone_members
(
  deliveryzoneid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  7,
  1,
  '2016-02-05 11:16:44.246',
  1,
  '2016-02-05 11:16:44.236'
);

-- No UPDATEs for delivery_zone_members necessary
-- -------------------
-- INSERTS for public.delivery_zone_program_schedules
-- -------------------
INSERT INTO delivery_zone_program_schedules
(
  deliveryzoneid,
  programid,
  scheduleid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  4,
  1,
  1,
  '2016-02-05 11:16:35.247',
  1,
  '2016-02-05 11:16:35.236'
);
INSERT INTO delivery_zone_program_schedules
(
  deliveryzoneid,
  programid,
  scheduleid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  4,
  1,
  1,
  '2016-02-05 11:16:35.247',
  1,
  '2016-02-05 11:16:35.236'
);

-- No UPDATEs for delivery_zone_program_schedules necessary
-- No INSERTs for delivery_zone_warehouses necessary
-- No UPDATEs for delivery_zone_warehouses necessary
-- -------------------
-- INSERTS for public.delivery_zones
-- -------------------
INSERT INTO delivery_zones
(
  code,
  name,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'dz-east',
  'East',
  'Delivery Zone East for Informed Push',
  1,
  '2016-02-05 11:16:28.508',
  1,
  '2016-02-05 11:16:28.495'
);
INSERT INTO delivery_zones
(
  code,
  name,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'dz-west',
  'West',
  'Delivery Zone West for Informed Push',
  1,
  '2016-02-05 11:16:28.508',
  1,
  '2016-02-05 11:16:28.495'
);

-- No UPDATEs for delivery_zones necessary
-- No INSERTs for demographic_estimate_categories necessary
-- No UPDATEs for demographic_estimate_categories necessary
-- No INSERTs for distributions necessary
-- No UPDATEs for distributions necessary
-- No INSERTs for district_demographic_estimates necessary
-- No UPDATEs for district_demographic_estimates necessary
-- No INSERTs for donors necessary
-- No UPDATEs for donors necessary
-- No INSERTs for dosage_frequencies necessary
-- No UPDATEs for dosage_frequencies necessary
-- -------------------
-- INSERTS for public.dosage_units
-- -------------------
INSERT INTO dosage_units
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'mg',
  1,
  '2016-02-05 11:12:27.314',
  1,
  1,
  '2016-02-05 11:12:27.314'
);
INSERT INTO dosage_units
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'mcg',
  2,
  '2016-02-05 11:12:27.314',
  1,
  1,
  '2016-02-05 11:12:27.314'
);
INSERT INTO dosage_units
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'ml',
  3,
  '2016-02-05 11:12:27.314',
  1,
  1,
  '2016-02-05 11:12:27.314'
);
INSERT INTO dosage_units
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'each',
  4,
  '2016-02-05 11:12:27.314',
  1,
  1,
  '2016-02-05 11:12:27.314'
);

-- No UPDATEs for dosage_units necessary
-- No INSERTs for dw_orders necessary
-- No UPDATEs for dw_orders necessary
-- No INSERTs for elmis_help necessary
-- No UPDATEs for elmis_help necessary
-- No INSERTs for elmis_help_document necessary
-- No UPDATEs for elmis_help_document necessary
-- No INSERTs for elmis_help_topic necessary
-- No UPDATEs for elmis_help_topic necessary
-- No INSERTs for elmis_help_topic_roles necessary
-- No UPDATEs for elmis_help_topic_roles necessary
-- No INSERTs for email_attachments necessary
-- No UPDATEs for email_attachments necessary
-- No INSERTs for email_attachments_relation necessary
-- No UPDATEs for email_attachments_relation necessary
-- No INSERTs for email_notifications necessary
-- No UPDATEs for email_notifications necessary
-- No INSERTs for emergency_requisitions necessary
-- No UPDATEs for emergency_requisitions necessary
-- No INSERTs for epi_inventory_line_items necessary
-- No UPDATEs for epi_inventory_line_items necessary
-- No INSERTs for epi_use_line_items necessary
-- No UPDATEs for epi_use_line_items necessary
-- No INSERTs for equipment_cold_chain_equipment_designations necessary
-- No UPDATEs for equipment_cold_chain_equipment_designations necessary
-- No INSERTs for equipment_cold_chain_equipment_pqs_status necessary
-- No UPDATEs for equipment_cold_chain_equipment_pqs_status necessary
-- No INSERTs for equipment_cold_chain_equipments necessary
-- No UPDATEs for equipment_cold_chain_equipments necessary
-- No INSERTs for equipment_contract_service_types necessary
-- No UPDATEs for equipment_contract_service_types necessary
-- No INSERTs for equipment_energy_types necessary
-- No UPDATEs for equipment_energy_types necessary
-- No INSERTs for equipment_inventories necessary
-- No UPDATEs for equipment_inventories necessary
-- No INSERTs for equipment_inventory_statuses necessary
-- No UPDATEs for equipment_inventory_statuses necessary
-- No INSERTs for equipment_maintenance_logs necessary
-- No UPDATEs for equipment_maintenance_logs necessary
-- No INSERTs for equipment_maintenance_requests necessary
-- No UPDATEs for equipment_maintenance_requests necessary
-- No INSERTs for equipment_operational_status necessary
-- No UPDATEs for equipment_operational_status necessary
-- No INSERTs for equipment_service_contract_equipment_types necessary
-- No UPDATEs for equipment_service_contract_equipment_types necessary
-- No INSERTs for equipment_service_contract_facilities necessary
-- No UPDATEs for equipment_service_contract_facilities necessary
-- No INSERTs for equipment_service_contracts necessary
-- No UPDATEs for equipment_service_contracts necessary
-- No INSERTs for equipment_service_types necessary
-- No UPDATEs for equipment_service_types necessary
-- No INSERTs for equipment_service_vendor_users necessary
-- No UPDATEs for equipment_service_vendor_users necessary
-- No INSERTs for equipment_service_vendors necessary
-- No UPDATEs for equipment_service_vendors necessary
-- No INSERTs for equipment_status_line_items necessary
-- No UPDATEs for equipment_status_line_items necessary
-- No INSERTs for equipment_type_products necessary
-- No UPDATEs for equipment_type_products necessary
-- No INSERTs for equipment_type_programs necessary
-- No UPDATEs for equipment_type_programs necessary
-- No INSERTs for equipment_types necessary
-- No UPDATEs for equipment_types necessary
-- No INSERTs for equipments necessary
-- No UPDATEs for equipments necessary
-- -------------------
-- INSERTS for public.facilities
-- -------------------
INSERT INTO facilities
(
  code,
  name,
  description,
  gln,
  mainphone,
  fax,
  address1,
  address2,
  geographiczoneid,
  typeid,
  catchmentpopulation,
  latitude,
  longitude,
  altitude,
  operatedbyid,
  coldstoragegrosscapacity,
  coldstoragenetcapacity,
  suppliesothers,
  sdp,
  online,
  satellite,
  parentfacilityid,
  haselectricity,
  haselectronicscc,
  haselectronicdar,
  active,
  golivedate,
  godowndate,
  COMMENT,
  enabled,
  virtualfacility,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  pricescheduleid
)
VALUES
(
  'F10010',
  'Stonewall Health Center',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  5,
  1,
  24000,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  FALSE,
  TRUE,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  '2003-01-01',
  NULL,
  NULL,
  TRUE,
  FALSE,
  1,
  '2016-02-05 11:13:41.555',
  1,
  '2016-02-05 11:13:41.535',
  NULL
);
INSERT INTO facilities
(
  code,
  name,
  description,
  gln,
  mainphone,
  fax,
  address1,
  address2,
  geographiczoneid,
  typeid,
  catchmentpopulation,
  latitude,
  longitude,
  altitude,
  operatedbyid,
  coldstoragegrosscapacity,
  coldstoragenetcapacity,
  suppliesothers,
  sdp,
  online,
  satellite,
  parentfacilityid,
  haselectricity,
  haselectronicscc,
  haselectronicdar,
  active,
  golivedate,
  godowndate,
  COMMENT,
  enabled,
  virtualfacility,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  pricescheduleid
)
VALUES
(
  'F10050',
  'Teuton Health Center',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  5,
  1,
  20000,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  FALSE,
  TRUE,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  '2003-01-01',
  NULL,
  NULL,
  TRUE,
  FALSE,
  1,
  '2016-02-05 11:13:41.555',
  1,
  '2016-02-05 11:13:41.535',
  NULL
);
INSERT INTO facilities
(
  code,
  name,
  description,
  gln,
  mainphone,
  fax,
  address1,
  address2,
  geographiczoneid,
  typeid,
  catchmentpopulation,
  latitude,
  longitude,
  altitude,
  operatedbyid,
  coldstoragegrosscapacity,
  coldstoragenetcapacity,
  suppliesothers,
  sdp,
  online,
  satellite,
  parentfacilityid,
  haselectricity,
  haselectronicscc,
  haselectronicdar,
  active,
  golivedate,
  godowndate,
  COMMENT,
  enabled,
  virtualfacility,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  pricescheduleid
)
VALUES
(
  'F3020A',
  'Steinbach Hospital',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  2,
  50000,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  '2003-01-01',
  NULL,
  NULL,
  TRUE,
  FALSE,
  1,
  '2016-02-05 11:13:41.555',
  1,
  '2016-02-05 11:13:41.535',
  NULL
);
INSERT INTO facilities
(
  code,
  name,
  description,
  gln,
  mainphone,
  fax,
  address1,
  address2,
  geographiczoneid,
  typeid,
  catchmentpopulation,
  latitude,
  longitude,
  altitude,
  operatedbyid,
  coldstoragegrosscapacity,
  coldstoragenetcapacity,
  suppliesothers,
  sdp,
  online,
  satellite,
  parentfacilityid,
  haselectricity,
  haselectronicscc,
  haselectronicdar,
  active,
  golivedate,
  godowndate,
  COMMENT,
  enabled,
  virtualfacility,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  pricescheduleid
)
VALUES
(
  'F30774',
  'Carberry Dispensary',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  8,
  3,
  3000,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  FALSE,
  TRUE,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  '2003-01-01',
  NULL,
  NULL,
  TRUE,
  FALSE,
  1,
  '2016-02-05 11:13:41.555',
  1,
  '2016-02-05 11:13:41.535',
  NULL
);
INSERT INTO facilities
(
  code,
  name,
  description,
  gln,
  mainphone,
  fax,
  address1,
  address2,
  geographiczoneid,
  typeid,
  catchmentpopulation,
  latitude,
  longitude,
  altitude,
  operatedbyid,
  coldstoragegrosscapacity,
  coldstoragenetcapacity,
  suppliesothers,
  sdp,
  online,
  satellite,
  parentfacilityid,
  haselectricity,
  haselectronicscc,
  haselectronicdar,
  active,
  golivedate,
  godowndate,
  COMMENT,
  enabled,
  virtualfacility,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  pricescheduleid
)
VALUES
(
  'F3060B',
  'Morris Dispensary',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  8,
  3,
  2000,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  FALSE,
  TRUE,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  '2003-01-01',
  NULL,
  NULL,
  TRUE,
  FALSE,
  1,
  '2016-02-05 11:13:41.555',
  1,
  '2016-02-05 11:13:41.535',
  NULL
);
INSERT INTO facilities
(
  code,
  name,
  description,
  gln,
  mainphone,
  fax,
  address1,
  address2,
  geographiczoneid,
  typeid,
  catchmentpopulation,
  latitude,
  longitude,
  altitude,
  operatedbyid,
  coldstoragegrosscapacity,
  coldstoragenetcapacity,
  suppliesothers,
  sdp,
  online,
  satellite,
  parentfacilityid,
  haselectricity,
  haselectronicscc,
  haselectronicdar,
  active,
  golivedate,
  godowndate,
  COMMENT,
  enabled,
  virtualfacility,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  pricescheduleid
)
VALUES
(
  'F29418',
  'Winkler Primary Care Center',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  8,
  4,
  30000,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  FALSE,
  TRUE,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  '2003-01-01',
  NULL,
  NULL,
  TRUE,
  FALSE,
  1,
  '2016-02-05 11:13:41.555',
  1,
  '2016-02-05 11:13:41.535',
  NULL
);
INSERT INTO facilities
(
  code,
  name,
  description,
  gln,
  mainphone,
  fax,
  address1,
  address2,
  geographiczoneid,
  typeid,
  catchmentpopulation,
  latitude,
  longitude,
  altitude,
  operatedbyid,
  coldstoragegrosscapacity,
  coldstoragenetcapacity,
  suppliesothers,
  sdp,
  online,
  satellite,
  parentfacilityid,
  haselectricity,
  haselectronicscc,
  haselectronicdar,
  active,
  golivedate,
  godowndate,
  COMMENT,
  enabled,
  virtualfacility,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  pricescheduleid
)
VALUES
(
  'F505',
  'Winnipeg Provincial Hospital',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  10,
  5,
  1000000,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  FALSE,
  TRUE,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  '2003-01-01',
  NULL,
  NULL,
  TRUE,
  FALSE,
  1,
  '2016-02-05 11:13:41.555',
  1,
  '2016-02-05 11:13:41.535',
  NULL
);
INSERT INTO facilities
(
  code,
  name,
  description,
  gln,
  mainphone,
  fax,
  address1,
  address2,
  geographiczoneid,
  typeid,
  catchmentpopulation,
  latitude,
  longitude,
  altitude,
  operatedbyid,
  coldstoragegrosscapacity,
  coldstoragenetcapacity,
  suppliesothers,
  sdp,
  online,
  satellite,
  parentfacilityid,
  haselectricity,
  haselectronicscc,
  haselectronicdar,
  active,
  golivedate,
  godowndate,
  COMMENT,
  enabled,
  virtualfacility,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  pricescheduleid
)
VALUES
(
  'F09',
  'Manitoba Warehouse',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  10,
  6,
  1000000,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  FALSE,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  '2003-01-01',
  NULL,
  NULL,
  TRUE,
  FALSE,
  1,
  '2016-02-05 11:13:41.555',
  1,
  '2016-02-05 11:13:41.535',
  NULL
);

-- No UPDATEs for facilities necessary
-- -------------------
-- INSERTS for public.facility_approved_products
-- -------------------
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  5,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  6,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  8,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  9,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  10,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  5,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  6,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  8,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  9,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  10,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  7,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  5,
  5,
  6,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  5,
  6,
  6,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  5,
  8,
  6,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  5,
  9,
  6,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  5,
  10,
  6,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  5,
  7,
  6,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  11,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  12,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  13,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  14,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  15,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  11,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  12,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  13,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  15,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  11,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  12,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  13,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  14,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  15,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  1,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  2,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  3,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  4,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  1,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  2,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  3,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  4,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  5,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  6,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  8,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  9,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);
INSERT INTO facility_approved_products
(
  facilitytypeid,
  programproductid,
  maxmonthsofstock,
  minmonthsofstock,
  eop,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  10,
  3,
  NULL,
  NULL,
  1,
  '2016-02-05 11:14:02.474',
  1,
  '2016-02-05 11:14:02.465'
);

-- No UPDATEs for facility_approved_products necessary
-- No INSERTs for facility_demographic_estimates necessary
-- No UPDATEs for facility_demographic_estimates necessary
-- No INSERTs for facility_ftp_details necessary
-- No UPDATEs for facility_ftp_details necessary
-- No INSERTs for facility_mappings necessary
-- No UPDATEs for facility_mappings necessary
-- -------------------
-- INSERTS for public.facility_operators
-- -------------------
INSERT INTO facility_operators
(
  code,
  TEXT,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'moh',
  'Ministry of Health',
  1,
  '2016-02-05 11:13:36.355',
  1,
  1,
  '2016-02-05 11:13:36.355'
);

-- No UPDATEs for facility_operators necessary
-- No INSERTs for facility_program_products necessary
-- No UPDATEs for facility_program_products necessary
-- -------------------
-- INSERTS for public.facility_types
-- -------------------
INSERT INTO facility_types
(
  code,
  name,
  description,
  levelid,
  nominalmaxmonth,
  nominaleop,
  displayorder,
  active,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'health_center',
  'Health Centre',
  NULL,
  NULL,
  3,
  1.00,
  6,
  TRUE,
  '2016-02-05 11:13:30.945',
  1,
  1,
  '2016-02-05 11:13:30.945'
);
INSERT INTO facility_types
(
  code,
  name,
  description,
  levelid,
  nominalmaxmonth,
  nominaleop,
  displayorder,
  active,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'dist_hosp',
  'District Hospital',
  NULL,
  NULL,
  3,
  1.00,
  5,
  TRUE,
  '2016-02-05 11:13:30.945',
  1,
  1,
  '2016-02-05 11:13:30.945'
);
INSERT INTO facility_types
(
  code,
  name,
  description,
  levelid,
  nominalmaxmonth,
  nominaleop,
  displayorder,
  active,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'disp',
  'Dispensary',
  NULL,
  NULL,
  3,
  1.00,
  4,
  TRUE,
  '2016-02-05 11:13:30.945',
  1,
  1,
  '2016-02-05 11:13:30.945'
);
INSERT INTO facility_types
(
  code,
  name,
  description,
  levelid,
  nominalmaxmonth,
  nominaleop,
  displayorder,
  active,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'comm_clinic',
  'Community Clinic',
  NULL,
  NULL,
  3,
  1.00,
  3,
  TRUE,
  '2016-02-05 11:13:30.945',
  1,
  1,
  '2016-02-05 11:13:30.945'
);
INSERT INTO facility_types
(
  code,
  name,
  description,
  levelid,
  nominalmaxmonth,
  nominaleop,
  displayorder,
  active,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'reg_hosp',
  'Regional Hospital',
  NULL,
  NULL,
  3,
  1.00,
  2,
  TRUE,
  '2016-02-05 11:13:30.945',
  1,
  1,
  '2016-02-05 11:13:30.945'
);
INSERT INTO facility_types
(
  code,
  name,
  description,
  levelid,
  nominalmaxmonth,
  nominaleop,
  displayorder,
  active,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'warehouse',
  'Warehouse',
  NULL,
  NULL,
  3,
  1.00,
  1,
  TRUE,
  '2016-02-05 11:13:30.945',
  1,
  1,
  '2016-02-05 11:13:30.945'
);

-- No UPDATEs for facility_types necessary
-- No INSERTs for facility_visits necessary
-- No UPDATEs for facility_visits necessary
-- -------------------
-- INSERTS for public.fulfillment_role_assignments
-- -------------------
INSERT INTO fulfillment_role_assignments
(
  userid,
  roleid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  6,
  8,
  NULL,
  '2016-02-05 11:22:43.647',
  NULL,
  '2016-02-05 11:22:43.647'
);
INSERT INTO fulfillment_role_assignments
(
  userid,
  roleid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  6,
  8,
  3,
  '2016-03-03 11:34:07.178',
  3,
  '2016-03-03 11:34:07.178'
);

-- No UPDATEs for fulfillment_role_assignments necessary
-- No INSERTs for full_coverages necessary
-- No UPDATEs for full_coverages necessary
-- -------------------
-- INSERTS for public.geographic_levels
-- -------------------
INSERT INTO geographic_levels
(
  code,
  name,
  levelnumber,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'country',
  'Country',
  1,
  '2016-02-05 11:13:19.835',
  1,
  1,
  '2016-02-05 11:13:19.835'
);
INSERT INTO geographic_levels
(
  code,
  name,
  levelnumber,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'province',
  'Province',
  2,
  '2016-02-05 11:13:19.835',
  1,
  1,
  '2016-02-05 11:13:19.835'
);
INSERT INTO geographic_levels
(
  code,
  name,
  levelnumber,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'region',
  'Region',
  3,
  '2016-02-05 11:13:19.835',
  1,
  1,
  '2016-02-05 11:13:19.835'
);

-- No UPDATEs for geographic_levels necessary
-- No INSERTs for geographic_zone_geojson necessary
-- No UPDATEs for geographic_zone_geojson necessary
-- -------------------
-- INSERTS for public.geographic_zones
-- -------------------
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'canada',
  'Canada',
  1,
  NULL,
  34880000,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'manitoba',
  'Manitoba',
  2,
  1,
  1272000,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'centralplains',
  'Central Plains',
  3,
  2,
  48289,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'eastman',
  'Eastman',
  3,
  2,
  104535,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'interlake',
  'Interlake',
  3,
  2,
  88854,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'northern',
  'Northern',
  3,
  2,
  88146,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'parkland',
  'Parkland',
  3,
  2,
  42088,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'pembinavalley',
  'Pembina Valley',
  3,
  2,
  52126,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'westman',
  'Westman',
  3,
  2,
  108868,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);
INSERT INTO geographic_zones
(
  code,
  name,
  levelid,
  parentid,
  catchmentpopulation,
  latitude,
  longitude,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'winnipegcr',
  'Winnipeg Capital Region',
  3,
  2,
  767380,
  NULL,
  NULL,
  1,
  '2016-02-05 11:13:25.161',
  1,
  '2016-02-05 11:13:25.146'
);

-- No UPDATEs for geographic_zones necessary
-- No INSERTs for gtin_lookups necessary
-- No UPDATEs for gtin_lookups necessary
-- No INSERTs for interface_apps necessary
-- No UPDATEs for interface_apps necessary
-- No INSERTs for interface_dataset necessary
-- No UPDATEs for interface_dataset necessary
-- No INSERTs for isa_coefficients necessary
-- No UPDATEs for isa_coefficients necessary
-- No INSERTs for losses_adjustments_types necessary
-- No UPDATEs for losses_adjustments_types necessary
-- No INSERTs for lots necessary
-- No UPDATEs for lots necessary
-- No INSERTs for lots_on_hand necessary
-- No UPDATEs for lots_on_hand necessary
-- No INSERTs for manufacturers necessary
-- No UPDATEs for manufacturers necessary
-- No INSERTs for master_regimen_columns necessary
-- No UPDATEs for master_regimen_columns necessary
-- No INSERTs for master_rnr_column_options necessary
-- No UPDATEs for master_rnr_column_options necessary
-- No INSERTs for master_rnr_columns necessary
-- No UPDATEs for master_rnr_columns necessary
-- No INSERTs for mos_adjustment_basis necessary
-- No UPDATEs for mos_adjustment_basis necessary
-- No INSERTs for mos_adjustment_facilities necessary
-- No UPDATEs for mos_adjustment_facilities necessary
-- No INSERTs for mos_adjustment_products necessary
-- No UPDATEs for mos_adjustment_products necessary
-- No INSERTs for mos_adjustment_types necessary
-- No UPDATEs for mos_adjustment_types necessary
-- No INSERTs for order_configuration necessary
-- No UPDATEs for order_configuration necessary
-- No INSERTs for order_file_columns necessary
-- No UPDATEs for order_file_columns necessary
-- No INSERTs for order_number_configuration necessary
-- No UPDATEs for order_number_configuration necessary
-- No INSERTs for order_quantity_adjustment_factors necessary
-- No UPDATEs for order_quantity_adjustment_factors necessary
-- No INSERTs for order_quantity_adjustment_products necessary
-- No UPDATEs for order_quantity_adjustment_products necessary
-- No INSERTs for order_quantity_adjustment_types necessary
-- No UPDATEs for order_quantity_adjustment_types necessary
-- No INSERTs for orders necessary
-- No UPDATEs for orders necessary
-- No INSERTs for patient_quantification_line_items necessary
-- No UPDATEs for patient_quantification_line_items necessary
-- No INSERTs for period_short_names necessary
-- No UPDATEs for period_short_names necessary
-- No INSERTs for pod necessary
-- No UPDATEs for pod necessary
-- No INSERTs for pod_line_items necessary
-- No UPDATEs for pod_line_items necessary
-- No INSERTs for price_schedules necessary
-- No UPDATEs for price_schedules necessary
-- -------------------
-- INSERTS for public.processing_periods
-- -------------------
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  'August2015-October2015',
  NULL,
  '2015-08-01 00:00:00.000',
  '2015-10-31 00:00:00.000',
  3,
  NULL,
  '2016-02-05 11:17:29.908',
  NULL,
  '2016-02-05 11:17:29.908'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  'November2015-January2016',
  NULL,
  '2015-11-01 00:00:00.000',
  '2016-01-31 00:00:00.000',
  3,
  NULL,
  '2016-02-05 11:17:29.908',
  NULL,
  '2016-02-05 11:17:29.908'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  'February2016-April2016',
  NULL,
  '2016-02-01 00:00:00.000',
  '2016-04-30 00:00:00.000',
  3,
  NULL,
  '2016-02-05 11:17:29.908',
  NULL,
  '2016-02-05 11:17:29.908'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  'May2016-July2016',
  NULL,
  '2016-05-01 00:00:00.000',
  '2016-07-31 00:00:00.000',
  3,
  NULL,
  '2016-02-05 11:17:29.908',
  NULL,
  '2016-02-05 11:17:29.908'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'August2015',
  NULL,
  '2015-08-01 00:00:00.000',
  '2015-08-31 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'September2015',
  NULL,
  '2015-09-01 00:00:00.000',
  '2015-09-30 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'October2015',
  NULL,
  '2015-10-01 00:00:00.000',
  '2015-10-31 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'November2015',
  NULL,
  '2015-11-01 00:00:00.000',
  '2015-11-30 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'December2015',
  NULL,
  '2015-12-01 00:00:00.000',
  '2015-12-31 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'January2016',
  NULL,
  '2016-01-01 00:00:00.000',
  '2016-01-31 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'February2016',
  NULL,
  '2016-02-01 00:00:00.000',
  '2016-02-29 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'March2016',
  NULL,
  '2016-03-01 00:00:00.000',
  '2016-03-31 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'April2016',
  NULL,
  '2016-04-01 00:00:00.000',
  '2016-04-30 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'May2016',
  NULL,
  '2016-05-01 00:00:00.000',
  '2016-05-31 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'June2016',
  NULL,
  '2016-06-01 00:00:00.000',
  '2016-06-30 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);
INSERT INTO processing_periods
(
  scheduleid,
  name,
  description,
  startdate,
  enddate,
  numberofmonths,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  'July2016',
  NULL,
  '2016-07-01 00:00:00.000',
  '2016-07-31 00:00:00.000',
  1,
  NULL,
  '2016-02-05 11:17:29.898',
  NULL,
  '2016-02-05 11:17:29.898'
);

-- No UPDATEs for processing_periods necessary
-- -------------------
-- INSERTS for public.processing_schedules
-- -------------------
INSERT INTO processing_schedules
(
  code,
  name,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'm',
  'Monthly',
  'Monthly Schedule',
  NULL,
  '2016-02-05 11:16:00.123',
  NULL,
  '2016-02-05 11:16:00.123'
);
INSERT INTO processing_schedules
(
  code,
  name,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'q',
  'Quarterly',
  'Quarterly Schedule',
  NULL,
  '2016-02-05 11:16:00.123',
  NULL,
  '2016-02-05 11:16:00.123'
);

-- No UPDATEs for processing_schedules necessary
-- -------------------
-- INSERTS for public.product_categories
-- -------------------
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'antibiotics',
  'Antibiotics',
  1,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'antiretrovials',
  'Anti-retrovirals',
  2,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'malariaprophylaxis',
  'Malaria Prophylaxis',
  3,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'protective',
  'Gloves and Protective Gear',
  4,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'antacids',
  'Antacids',
  5,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'testkits',
  'Test Kits',
  6,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'antipyretics',
  'Anti-pyretics',
  7,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'antimalarials',
  'Anti-malarials',
  8,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'vac',
  'Vaccine',
  9,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);
INSERT INTO product_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'nonvac',
  'Non-Vaccine',
  10,
  1,
  '2016-02-05 11:12:40.286',
  1,
  '2016-02-05 11:12:40.275'
);

-- No UPDATEs for product_categories necessary
-- -------------------
-- INSERTS for public.product_forms
-- -------------------
INSERT INTO product_forms
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'tablet',
  1,
  '2016-02-05 11:12:34.243',
  1,
  1,
  '2016-02-05 11:12:34.243'
);
INSERT INTO product_forms
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'capsule',
  2,
  '2016-02-05 11:12:34.243',
  1,
  1,
  '2016-02-05 11:12:34.243'
);
INSERT INTO product_forms
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'powder',
  3,
  '2016-02-05 11:12:34.243',
  1,
  1,
  '2016-02-05 11:12:34.243'
);
INSERT INTO product_forms
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'injection',
  4,
  '2016-02-05 11:12:34.243',
  1,
  1,
  '2016-02-05 11:12:34.243'
);
INSERT INTO product_forms
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'pair',
  5,
  '2016-02-05 11:12:34.243',
  1,
  1,
  '2016-02-05 11:12:34.243'
);
INSERT INTO product_forms
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'syringe',
  6,
  '2016-02-05 11:12:34.243',
  1,
  1,
  '2016-02-05 11:12:34.243'
);
INSERT INTO product_forms
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'device',
  7,
  '2016-02-05 11:12:34.243',
  1,
  1,
  '2016-02-05 11:12:34.243'
);
INSERT INTO product_forms
(
  code,
  displayorder,
  createddate,
  createdby,
  modifiedby,
  modifieddate
)
VALUES
(
  'card',
  8,
  '2016-02-05 11:12:34.243',
  1,
  1,
  '2016-02-05 11:12:34.243'
);

-- No UPDATEs for product_forms necessary
-- -------------------
-- INSERTS for public.product_groups
-- -------------------
INSERT INTO product_groups
(
  code,
  name,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  '1bcg',
  'BCG',
  1,
  '2016-02-05 11:12:45.939',
  1,
  '2016-02-05 11:12:45.926'
);
INSERT INTO product_groups
(
  code,
  name,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  '2polio',
  'Polio',
  1,
  '2016-02-05 11:12:45.939',
  1,
  '2016-02-05 11:12:45.926'
);
INSERT INTO product_groups
(
  code,
  name,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  '3penta',
  'Pentavalent',
  1,
  '2016-02-05 11:12:45.939',
  1,
  '2016-02-05 11:12:45.926'
);
INSERT INTO product_groups
(
  code,
  name,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  '4measles',
  'Sarampo',
  1,
  '2016-02-05 11:12:45.939',
  1,
  '2016-02-05 11:12:45.926'
);
INSERT INTO product_groups
(
  code,
  name,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  '5pcv10',
  'PCV10',
  1,
  '2016-02-05 11:12:45.939',
  1,
  '2016-02-05 11:12:45.926'
);
INSERT INTO product_groups
(
  code,
  name,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  '6hpv',
  'HPV',
  1,
  '2016-02-05 11:12:45.939',
  1,
  '2016-02-05 11:12:45.926'
);
INSERT INTO product_groups
(
  code,
  name,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  '7tetanus',
  'VAT',
  1,
  '2016-02-05 11:12:45.939',
  1,
  '2016-02-05 11:12:45.926'
);

-- No UPDATEs for product_groups necessary
-- No INSERTs for product_price_schedules necessary
-- No UPDATEs for product_price_schedules necessary
-- No INSERTs for product_short_names necessary
-- No UPDATEs for product_short_names necessary
-- -------------------
-- INSERTS for public.products
-- -------------------
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'P10048',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  '3TC/AZT + NVP',
  NULL,
  NULL,
  NULL,
  NULL,
  '300/300/600',
  1,
  1,
  NULL,
  '60 tab bottle',
  20,
  50,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  FALSE,
  NULL,
  25,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'P12573',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'TDF/FTC/EFV',
  NULL,
  NULL,
  NULL,
  NULL,
  '300/200/600',
  1,
  1,
  NULL,
  '30 tab bottle',
  3,
  20,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  FALSE,
  NULL,
  10,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'P200563',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Omeprazole',
  NULL,
  NULL,
  NULL,
  NULL,
  '20',
  1,
  1,
  NULL,
  'each',
  15,
  5,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  FALSE,
  FALSE,
  FALSE,
  NULL,
  4,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'A41099',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Doxycycline',
  NULL,
  NULL,
  NULL,
  NULL,
  '100',
  1,
  1,
  NULL,
  '12 tab strip',
  10,
  12,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  FALSE,
  NULL,
  3,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'S00056L',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Gloves, surgical latex, powered',
  NULL,
  NULL,
  NULL,
  NULL,
  'size 7',
  5,
  4,
  NULL,
  'each',
  1,
  100,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  FALSE,
  NULL,
  25,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'A53007D',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Co-Trimoxazole DS',
  NULL,
  NULL,
  NULL,
  NULL,
  '800/160',
  2,
  1,
  NULL,
  '10 tab strip',
  10,
  20,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  FALSE,
  FALSE,
  FALSE,
  NULL,
  5,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'KM3375-2',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Malaria Rapid Diagnostic Test',
  NULL,
  NULL,
  NULL,
  NULL,
  '1',
  8,
  4,
  NULL,
  'each',
  1,
  50,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  FALSE,
  NULL,
  20,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'P000833',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Quinine Sulphate',
  NULL,
  NULL,
  NULL,
  NULL,
  '300',
  2,
  1,
  NULL,
  'each',
  1,
  500,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  FALSE,
  NULL,
  200,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'A427826',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Ciproflaxin',
  NULL,
  NULL,
  NULL,
  NULL,
  '500',
  2,
  1,
  NULL,
  '10 tab strip',
  10,
  10,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  FALSE,
  NULL,
  5,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'A581453',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Ampicillin PDR',
  NULL,
  NULL,
  NULL,
  NULL,
  '500mg',
  4,
  1,
  NULL,
  'each',
  10,
  50,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  FALSE,
  NULL,
  20,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'N-378013',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Tramedol',
  NULL,
  NULL,
  NULL,
  NULL,
  '50mg/ml',
  4,
  1,
  NULL,
  'each',
  2,
  10,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  FALSE,
  FALSE,
  FALSE,
  NULL,
  25,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'P-228438',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Diclofenac',
  NULL,
  NULL,
  NULL,
  NULL,
  '50',
  1,
  1,
  NULL,
  'each',
  1,
  100,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  FALSE,
  FALSE,
  FALSE,
  NULL,
  50,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'bcg20',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'BCG',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  4,
  1,
  'each',
  1,
  20,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'polio10',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Polio (10 dose)',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  4,
  2,
  'each',
  1,
  10,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'polio20',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Polio (20 dose)',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  4,
  2,
  'each',
  1,
  20,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'penta1',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Pentavalent (1 dose)',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  4,
  3,
  'each',
  1,
  1,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'penta10',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Pentavalent (10 dose)',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  4,
  3,
  'each',
  1,
  10,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'pcv10',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'PCV10',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  4,
  5,
  'each',
  1,
  2,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'hpv2',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'HPV',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  4,
  6,
  'each',
  1,
  2,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'measles10',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Sarampo',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  4,
  4,
  'each',
  1,
  10,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'tetanus10',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'VAT',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  4,
  4,
  7,
  'each',
  1,
  10,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'syringe5ml',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Seringa 5ml',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  6,
  4,
  NULL,
  'each',
  1,
  1,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'syringe05ml',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Seringa 0,5ml',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  6,
  4,
  NULL,
  'each',
  1,
  1,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'syringe005ml',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Seringa 0,05ml',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  6,
  4,
  NULL,
  'each',
  1,
  1,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'safetybox',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Caixas Inciner.',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  7,
  4,
  NULL,
  'each',
  1,
  1,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);
INSERT INTO products
(
  code,
  alternateitemcode,
  manufacturer,
  manufacturercode,
  manufacturerbarcode,
  mohbarcode,
  gtin,
  TYPE,
  primaryname,
  fullname,
  genericname,
  alternatename,
  description,
  strength,
  formid,
  dosageunitid,
  productgroupid,
  dispensingunit,
  dosesperdispensingunit,
  packsize,
  alternatepacksize,
  storerefrigerated,
  storeroomtemperature,
  hazardous,
  flammable,
  controlledsubstance,
  lightsensitive,
  approvedbywho,
  contraceptivecyp,
  packlength,
  packwidth,
  packheight,
  packweight,
  packspercarton,
  cartonlength,
  cartonwidth,
  cartonheight,
  cartonsperpallet,
  expectedshelflife,
  specialstorageinstructions,
  specialtransportinstructions,
  active,
  fullsupply,
  tracer,
  roundtozero,
  archived,
  packroundingthreshold,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'gas',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  'Gás (Cilindro)',
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  7,
  4,
  NULL,
  'each',
  1,
  1,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  NULL,
  TRUE,
  TRUE,
  FALSE,
  TRUE,
  NULL,
  1,
  1,
  '2016-02-05 11:12:52.997',
  1,
  '2016-02-05 11:12:52.980'
);

-- No UPDATEs for products necessary
-- -------------------
-- INSERTS for public.program_product_price_history
-- -------------------
INSERT INTO program_product_price_history
(
  programproductid,
  price,
  priceperdosage,
  source,
  startdate,
  enddate,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  61.30,
  0.12,
  NULL,
  '2016-02-05 11:13:11.977',
  NULL,
  1,
  '2016-02-05 11:13:11.977',
  1,
  '2016-02-05 11:13:11.972'
);
INSERT INTO program_product_price_history
(
  programproductid,
  price,
  priceperdosage,
  source,
  startdate,
  enddate,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  29.26,
  0.49,
  NULL,
  '2016-02-05 11:13:11.977',
  NULL,
  1,
  '2016-02-05 11:13:11.977',
  1,
  '2016-02-05 11:13:11.972'
);
INSERT INTO program_product_price_history
(
  programproductid,
  price,
  priceperdosage,
  source,
  startdate,
  enddate,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  3.75,
  0.04,
  NULL,
  '2016-02-05 11:13:11.977',
  NULL,
  1,
  '2016-02-05 11:13:11.977',
  1,
  '2016-02-05 11:13:11.972'
);
INSERT INTO program_product_price_history
(
  programproductid,
  price,
  priceperdosage,
  source,
  startdate,
  enddate,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  16.40,
  0.08,
  NULL,
  '2016-02-05 11:13:11.977',
  NULL,
  1,
  '2016-02-05 11:13:11.977',
  1,
  '2016-02-05 11:13:11.972'
);

-- No UPDATEs for program_product_price_history necessary
-- -------------------
-- INSERTS for public.program_products
-- -------------------
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  3,
  4,
  90,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  1,
  3,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  3,
  9,
  60,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  1,
  2,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  3,
  10,
  3,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  1,
  1,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  3,
  5,
  1,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  4,
  5,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  3,
  6,
  60,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  1,
  4,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  3,
  3,
  30,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  5,
  6,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  1,
  4,
  90,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  3,
  5,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  1,
  7,
  1,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  6,
  4,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  1,
  8,
  60,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  8,
  1,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  1,
  11,
  1,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  7,
  2,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  1,
  12,
  60,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  7,
  3,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  13,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  9,
  16,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  14,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  9,
  17,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  15,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  9,
  18,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  16,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  9,
  19,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  17,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  9,
  20,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  18,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  9,
  21,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  19,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  9,
  22,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  20,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  9,
  23,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  21,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  9,
  24,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  22,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  10,
  25,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  23,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  10,
  26,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  24,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  10,
  27,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  25,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  10,
  28,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  4,
  26,
  99,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:05.256',
  10,
  29,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  2,
  1,
  60,
  TRUE,
  61.30,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:11.972',
  2,
  1,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  2,
  2,
  120,
  TRUE,
  29.26,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:11.972',
  2,
  2,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  2,
  5,
  1,
  TRUE,
  3.75,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:11.972',
  4,
  4,
  TRUE,
  NULL
);
INSERT INTO program_products
(
  programid,
  productid,
  dosespermonth,
  active,
  currentprice,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  productcategoryid,
  displayorder,
  fullsupply,
  isacoefficientsid
)
VALUES
(
  2,
  6,
  60,
  TRUE,
  16.40,
  1,
  '2016-02-05 11:13:05.272',
  1,
  '2016-02-05 11:13:11.972',
  1,
  3,
  TRUE,
  NULL
);

-- No UPDATEs for program_products necessary
-- No INSERTs for program_regimen_columns necessary
-- No UPDATEs for program_regimen_columns necessary
-- -------------------
-- INSERTS for public.program_rnr_columns
-- -------------------
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  1,
  1,
  'Skip',
  TRUE,
  1,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  2,
  1,
  'Product Code',
  TRUE,
  2,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  3,
  1,
  'Product',
  TRUE,
  3,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  4,
  1,
  'Unit/Unit of Issue',
  TRUE,
  4,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  6,
  1,
  'Total Received Quantity',
  TRUE,
  5,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  7,
  1,
  'Total',
  TRUE,
  6,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  8,
  1,
  'Total Consumed Quantity',
  TRUE,
  7,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  9,
  1,
  'Total Losses / Adjustments',
  TRUE,
  8,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  10,
  1,
  'Stock on Hand',
  TRUE,
  9,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  11,
  1,
  'Total number of new patients added to service on the program',
  TRUE,
  10,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  1,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  12,
  1,
  'Total Stockout Days',
  TRUE,
  11,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  14,
  1,
  'Average Monthly Consumption(AMC)',
  TRUE,
  12,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  16,
  1,
  'Calculated Order Quantity',
  TRUE,
  13,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  17,
  1,
  'Requested Quantity',
  TRUE,
  14,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  18,
  1,
  'Requested Quantity Explanation',
  TRUE,
  15,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  20,
  1,
  'Packs to Ship',
  TRUE,
  16,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  21,
  1,
  'Price per Pack',
  TRUE,
  17,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  22,
  1,
  'Total Cost',
  TRUE,
  18,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  23,
  1,
  'Expiration Date',
  TRUE,
  19,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  24,
  1,
  'Remarks',
  TRUE,
  20,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  25,
  1,
  'Period Normalized Consumption',
  TRUE,
  21,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  15,
  1,
  'Maximum Stock Quantity',
  TRUE,
  22,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Normalized Consumption x 2","id":"CONSUMPTION_X_2"},{"name":"Dispensed Quantity x 2","id":"DISPENSED_X_2"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  5,
  1,
  'Beginning Balance',
  TRUE,
  23,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Allow users to override beginning balance","id":"ALLOW_USER_OVERRIDE"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  19,
  1,
  'Approved Quantity',
  TRUE,
  24,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Accept in Packs","id":"ACCEPT_PACKS"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  13,
  1,
  'Monthly Normalized Consumption',
  TRUE,
  25,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:04.099',
  4,
  '2016-03-03 11:45:04.099',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Dispensed Quantity + No of New Patients","id":"DISPENSED_PLUS_NEW_PATIENTS"},{"name":"(Dispensed x 90) / (90 - Stockout Days)","id":"DISPENSED_X_90"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  1,
  2,
  'Skip',
  TRUE,
  1,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  2,
  2,
  'Product Code',
  TRUE,
  2,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  3,
  2,
  'Product',
  TRUE,
  3,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  4,
  2,
  'Unit/Unit of Issue',
  TRUE,
  4,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  6,
  2,
  'Total Received Quantity',
  TRUE,
  5,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  7,
  2,
  'Total',
  TRUE,
  6,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  8,
  2,
  'Total Consumed Quantity',
  TRUE,
  7,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  9,
  2,
  'Total Losses / Adjustments',
  TRUE,
  8,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  10,
  2,
  'Stock on Hand',
  TRUE,
  9,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  11,
  2,
  'Total number of new patients added to service on the program',
  TRUE,
  10,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  1,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  12,
  2,
  'Total Stockout Days',
  TRUE,
  11,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  14,
  2,
  'Average Monthly Consumption(AMC)',
  TRUE,
  12,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  16,
  2,
  'Calculated Order Quantity',
  TRUE,
  13,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  17,
  2,
  'Requested Quantity',
  TRUE,
  14,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  18,
  2,
  'Requested Quantity Explanation',
  TRUE,
  15,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  20,
  2,
  'Packs to Ship',
  TRUE,
  16,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  21,
  2,
  'Price per Pack',
  TRUE,
  17,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  22,
  2,
  'Total Cost',
  TRUE,
  18,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  23,
  2,
  'Expiration Date',
  TRUE,
  19,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  24,
  2,
  'Remarks',
  TRUE,
  20,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  25,
  2,
  'Period Normalized Consumption',
  TRUE,
  21,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  15,
  2,
  'Maximum Stock Quantity',
  TRUE,
  22,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Normalized Consumption x 2","id":"CONSUMPTION_X_2"},{"name":"Dispensed Quantity x 2","id":"DISPENSED_X_2"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  5,
  2,
  'Beginning Balance',
  TRUE,
  23,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Allow users to override beginning balance","id":"ALLOW_USER_OVERRIDE"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  19,
  2,
  'Approved Quantity',
  TRUE,
  24,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Accept in Packs","id":"ACCEPT_PACKS"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  13,
  2,
  'Monthly Normalized Consumption',
  TRUE,
  25,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:06.710',
  4,
  '2016-03-03 11:45:06.710',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Dispensed Quantity + No of New Patients","id":"DISPENSED_PLUS_NEW_PATIENTS"},{"name":"(Dispensed x 90) / (90 - Stockout Days)","id":"DISPENSED_X_90"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  1,
  3,
  'Skip',
  TRUE,
  1,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  2,
  3,
  'Product Code',
  TRUE,
  2,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  3,
  3,
  'Product',
  TRUE,
  3,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  4,
  3,
  'Unit/Unit of Issue',
  TRUE,
  4,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  6,
  3,
  'Total Received Quantity',
  TRUE,
  5,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  7,
  3,
  'Total',
  TRUE,
  6,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  8,
  3,
  'Total Consumed Quantity',
  TRUE,
  7,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  9,
  3,
  'Total Losses / Adjustments',
  TRUE,
  8,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  10,
  3,
  'Stock on Hand',
  TRUE,
  9,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  11,
  3,
  'Total number of new patients added to service on the program',
  TRUE,
  10,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  1,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  12,
  3,
  'Total Stockout Days',
  TRUE,
  11,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  14,
  3,
  'Average Monthly Consumption(AMC)',
  TRUE,
  12,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  16,
  3,
  'Calculated Order Quantity',
  TRUE,
  13,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  17,
  3,
  'Requested Quantity',
  TRUE,
  14,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  18,
  3,
  'Requested Quantity Explanation',
  TRUE,
  15,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  20,
  3,
  'Packs to Ship',
  TRUE,
  16,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  21,
  3,
  'Price per Pack',
  TRUE,
  17,
  'R',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  22,
  3,
  'Total Cost',
  TRUE,
  18,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  23,
  3,
  'Expiration Date',
  TRUE,
  19,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  24,
  3,
  'Remarks',
  TRUE,
  20,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  25,
  3,
  'Period Normalized Consumption',
  TRUE,
  21,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  'DEFAULT'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  15,
  3,
  'Maximum Stock Quantity',
  TRUE,
  22,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Normalized Consumption x 2","id":"CONSUMPTION_X_2"},{"name":"Dispensed Quantity x 2","id":"DISPENSED_X_2"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  5,
  3,
  'Beginning Balance',
  TRUE,
  23,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Allow users to override beginning balance","id":"ALLOW_USER_OVERRIDE"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  19,
  3,
  'Approved Quantity',
  TRUE,
  24,
  'U',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Accept in Packs","id":"ACCEPT_PACKS"}]'
);
INSERT INTO program_rnr_columns
(
  mastercolumnid,
  programid,
  label,
  visible,
  POSITION,
  source,
  formulavalidationrequired,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  rnroptionid,
  calculationoption
)
VALUES
(
  13,
  3,
  'Monthly Normalized Consumption',
  TRUE,
  25,
  'C',
  TRUE,
  4,
  '2016-03-03 11:45:09.836',
  4,
  '2016-03-03 11:45:09.836',
  NULL,
  '[{"name":"Default", "id":"DEFAULT"},{"name":"Dispensed Quantity + No of New Patients","id":"DISPENSED_PLUS_NEW_PATIENTS"},{"name":"(Dispensed x 90) / (90 - Stockout Days)","id":"DISPENSED_X_90"}]'
);

-- No UPDATEs for program_rnr_columns necessary
-- -------------------
-- INSERTS for public.programs
-- -------------------
INSERT INTO programs
(
  code,
  name,
  description,
  active,
  templateconfigured,
  regimentemplateconfigured,
  budgetingapplies,
  usesdar,
  push,
  sendfeed,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  isequipmentconfigured,
  hideskippedproducts,
  shownonfullsupplytab,
  enableskipperiod,
  enableivdform,
  usepriceschedule
)
VALUES
(
  'art',
  'ART',
  'Anti-Retroviral Therapy',
  TRUE,
  TRUE,
  NULL,
  TRUE,
  NULL,
  FALSE,
  FALSE,
  NULL,
  '2016-02-05 11:12:59.118',
  NULL,
  '2016-02-05 11:12:59.118',
  FALSE,
  FALSE,
  TRUE,
  FALSE,
  FALSE,
  FALSE
);
INSERT INTO programs
(
  code,
  name,
  description,
  active,
  templateconfigured,
  regimentemplateconfigured,
  budgetingapplies,
  usesdar,
  push,
  sendfeed,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  isequipmentconfigured,
  hideskippedproducts,
  shownonfullsupplytab,
  enableskipperiod,
  enableivdform,
  usepriceschedule
)
VALUES
(
  'em',
  'Essential Medicines',
  'Primary Care – Essential Medications',
  TRUE,
  TRUE,
  NULL,
  FALSE,
  NULL,
  FALSE,
  FALSE,
  NULL,
  '2016-02-05 11:12:59.118',
  NULL,
  '2016-02-05 11:12:59.118',
  FALSE,
  FALSE,
  TRUE,
  FALSE,
  FALSE,
  FALSE
);
INSERT INTO programs
(
  code,
  name,
  description,
  active,
  templateconfigured,
  regimentemplateconfigured,
  budgetingapplies,
  usesdar,
  push,
  sendfeed,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  isequipmentconfigured,
  hideskippedproducts,
  shownonfullsupplytab,
  enableskipperiod,
  enableivdform,
  usepriceschedule
)
VALUES
(
  'malaria',
  'Malaria',
  'Malaria Testing and Treatment',
  TRUE,
  TRUE,
  NULL,
  FALSE,
  NULL,
  FALSE,
  FALSE,
  NULL,
  '2016-02-05 11:12:59.118',
  NULL,
  '2016-02-05 11:12:59.118',
  FALSE,
  FALSE,
  TRUE,
  FALSE,
  FALSE,
  FALSE
);
INSERT INTO programs
(
  code,
  name,
  description,
  active,
  templateconfigured,
  regimentemplateconfigured,
  budgetingapplies,
  usesdar,
  push,
  sendfeed,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  isequipmentconfigured,
  hideskippedproducts,
  shownonfullsupplytab,
  enableskipperiod,
  enableivdform,
  usepriceschedule
)
VALUES
(
  'epi',
  'EPI',
  'Routine Immunizations',
  TRUE,
  NULL,
  NULL,
  FALSE,
  NULL,
  TRUE,
  FALSE,
  NULL,
  '2016-02-05 11:12:59.118',
  NULL,
  '2016-02-05 11:12:59.118',
  FALSE,
  FALSE,
  TRUE,
  FALSE,
  FALSE,
  FALSE
);

-- No UPDATEs for programs necessary
-- -------------------
-- INSERTS for public.programs_supported
-- -------------------
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  3,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  4,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  5,
  1,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  5,
  4,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  1,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  4,
  4,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  7,
  3,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  7,
  2,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  7,
  4,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  1,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  3,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  2,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  4,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  2,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  4,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  6,
  3,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  6,
  1,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);
INSERT INTO programs_supported
(
  facilityid,
  programid,
  startdate,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  6,
  4,
  '2003-01-01 00:00:00.000',
  TRUE,
  1,
  '2016-02-05 11:13:49.751',
  1,
  '2016-02-05 11:13:49.742'
);

-- No UPDATEs for programs_supported necessary
-- No INSERTs for refrigerator_problems necessary
-- No UPDATEs for refrigerator_problems necessary
-- No INSERTs for refrigerator_readings necessary
-- No UPDATEs for refrigerator_readings necessary
-- No INSERTs for refrigerators necessary
-- No UPDATEs for refrigerators necessary
-- -------------------
-- INSERTS for public.regimen_categories
-- -------------------
INSERT INTO regimen_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'AD',
  'Adult',
  1,
  1,
  '2016-02-05 11:13:55.530',
  1,
  '2016-02-05 11:13:55.530'
);
INSERT INTO regimen_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'CH',
  'Child',
  2,
  1,
  '2016-02-05 11:13:55.530',
  1,
  '2016-02-05 11:13:55.530'
);
INSERT INTO regimen_categories
(
  code,
  name,
  displayorder,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'IN',
  'Infant',
  3,
  1,
  '2016-02-05 11:13:55.530',
  1,
  '2016-02-05 11:13:55.530'
);

-- No UPDATEs for regimen_categories necessary
-- No INSERTs for regimen_combination_constituents necessary
-- No UPDATEs for regimen_combination_constituents necessary
-- No INSERTs for regimen_constituents_dosages necessary
-- No UPDATEs for regimen_constituents_dosages necessary
-- No INSERTs for regimen_line_items necessary
-- No UPDATEs for regimen_line_items necessary
-- No INSERTs for regimen_product_combinations necessary
-- No UPDATEs for regimen_product_combinations necessary
-- No INSERTs for regimens necessary
-- No UPDATEs for regimens necessary
-- No INSERTs for report_rights necessary
-- No UPDATEs for report_rights necessary
-- -------------------
-- INSERTS for public.requisition_group_members
-- -------------------
INSERT INTO requisition_group_members
(
  requisitiongroupid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  6,
  1,
  '2016-02-05 11:16:18.082',
  1,
  '2016-02-05 11:16:18.073'
);
INSERT INTO requisition_group_members
(
  requisitiongroupid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  4,
  1,
  '2016-02-05 11:16:18.082',
  1,
  '2016-02-05 11:16:18.073'
);
INSERT INTO requisition_group_members
(
  requisitiongroupid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  5,
  1,
  '2016-02-05 11:16:18.082',
  1,
  '2016-02-05 11:16:18.073'
);
INSERT INTO requisition_group_members
(
  requisitiongroupid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  3,
  1,
  '2016-02-05 11:16:18.082',
  1,
  '2016-02-05 11:16:18.073'
);
INSERT INTO requisition_group_members
(
  requisitiongroupid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  6,
  1,
  '2016-02-05 11:16:18.082',
  1,
  '2016-02-05 11:16:18.073'
);
INSERT INTO requisition_group_members
(
  requisitiongroupid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  1,
  1,
  '2016-02-05 11:16:18.082',
  1,
  '2016-02-05 11:16:18.073'
);
INSERT INTO requisition_group_members
(
  requisitiongroupid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  2,
  1,
  '2016-02-05 11:16:18.082',
  1,
  '2016-02-05 11:16:18.073'
);
INSERT INTO requisition_group_members
(
  requisitiongroupid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  3,
  1,
  '2016-02-05 11:16:18.082',
  1,
  '2016-02-05 11:16:18.073'
);
INSERT INTO requisition_group_members
(
  requisitiongroupid,
  facilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  7,
  1,
  '2016-02-05 11:16:18.082',
  1,
  '2016-02-05 11:16:18.073'
);

-- No UPDATEs for requisition_group_members necessary
-- -------------------
-- INSERTS for public.requisition_group_program_schedules
-- -------------------
INSERT INTO requisition_group_program_schedules
(
  requisitiongroupid,
  programid,
  scheduleid,
  directdelivery,
  dropofffacilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  1,
  1,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:16:09.384',
  1,
  '2016-02-05 11:16:09.380'
);
INSERT INTO requisition_group_program_schedules
(
  requisitiongroupid,
  programid,
  scheduleid,
  directdelivery,
  dropofffacilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  2,
  3,
  1,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:16:09.384',
  1,
  '2016-02-05 11:16:09.380'
);
INSERT INTO requisition_group_program_schedules
(
  requisitiongroupid,
  programid,
  scheduleid,
  directdelivery,
  dropofffacilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  2,
  1,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:16:09.384',
  1,
  '2016-02-05 11:16:09.380'
);
INSERT INTO requisition_group_program_schedules
(
  requisitiongroupid,
  programid,
  scheduleid,
  directdelivery,
  dropofffacilityid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  3,
  3,
  1,
  TRUE,
  NULL,
  1,
  '2016-02-05 11:16:09.384',
  1,
  '2016-02-05 11:16:09.380'
);

-- No UPDATEs for requisition_group_program_schedules necessary
-- -------------------
-- INSERTS for public.requisition_groups
-- -------------------
INSERT INTO requisition_groups
(
  code,
  name,
  description,
  supervisorynodeid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'rg1',
  'Southern regions Malaria support',
  'Supports facilities running the Malaria program',
  3,
  1,
  '2016-02-05 11:14:25.995',
  1,
  '2016-02-05 11:14:25.982'
);
INSERT INTO requisition_groups
(
  code,
  name,
  description,
  supervisorynodeid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'rg2',
  'Provincial Park primary care',
  'Manages primary care center in Winkler',
  2,
  1,
  '2016-02-05 11:14:25.995',
  1,
  '2016-02-05 11:14:25.982'
);
INSERT INTO requisition_groups
(
  code,
  name,
  description,
  supervisorynodeid,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'rg6',
  'Provincial Health Plan support',
  'Supports health centres in the southern half of the province',
  1,
  1,
  '2016-02-05 11:14:25.995',
  1,
  '2016-02-05 11:14:25.982'
);

-- No UPDATEs for requisition_groups necessary
-- No INSERTs for requisition_line_item_losses_adjustments necessary
-- No UPDATEs for requisition_line_item_losses_adjustments necessary
-- No INSERTs for requisition_line_items necessary
-- No UPDATEs for requisition_line_items necessary
-- No INSERTs for requisition_signatures necessary
-- No UPDATEs for requisition_signatures necessary
-- No INSERTs for requisition_status_changes necessary
-- No UPDATEs for requisition_status_changes necessary
-- No INSERTs for requisitions necessary
-- No UPDATEs for requisitions necessary
-- No INSERTs for rights necessary
-- No UPDATEs for rights necessary
-- -------------------
-- INSERTS for public.role_assignments
-- -------------------
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  1,
  1,
  NULL,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  2,
  NULL,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  4,
  4,
  NULL,
  1
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  4,
  4,
  NULL,
  2
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  1,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  2,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  3,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  1,
  3,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  1,
  1,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  1,
  2,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  2,
  3,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  2,
  1,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  2,
  2,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  3,
  3,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  3,
  1,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  2,
  5,
  3,
  2,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  3,
  1,
  NULL,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  3,
  3,
  NULL,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  1,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  2,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  3,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  1,
  3,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  1,
  1,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  1,
  2,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  2,
  3,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  2,
  1,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  2,
  2,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  3,
  3,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  3,
  1,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  5,
  3,
  2,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  4,
  4,
  NULL,
  1
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  4,
  4,
  NULL,
  2
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  1,
  NULL,
  NULL,
  NULL
);
INSERT INTO role_assignments
(
  userid,
  roleid,
  programid,
  supervisorynodeid,
  deliveryzoneid
)
VALUES
(
  4,
  3,
  NULL,
  NULL,
  NULL
);

-- No UPDATEs for role_assignments necessary
-- -------------------
-- INSERTS for public.role_rights
-- -------------------
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  1,
  'MANAGE_GEOGRAPHIC_ZONE',
  NULL,
  '2016-02-05 11:22:43.144'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  1,
  'MANAGE_SUPPLY_LINE',
  NULL,
  '2016-02-05 11:22:43.144'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  1,
  'MANAGE_FACILITY_APPROVED_PRODUCT',
  NULL,
  '2016-02-05 11:22:43.144'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  1,
  'MANAGE_PRODUCT',
  NULL,
  '2016-02-05 11:22:43.144'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  1,
  'MANAGE_REQUISITION_GROUP',
  NULL,
  '2016-02-05 11:22:43.144'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  1,
  'MANAGE_SUPERVISORY_NODE',
  NULL,
  '2016-02-05 11:22:43.144'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  1,
  'MANAGE_SETTING',
  NULL,
  '2016-02-05 11:22:43.144'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  1,
  'MANAGE_EQUIPMENT_SETTINGS',
  NULL,
  '2016-02-05 11:22:43.144'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  3,
  'MANAGE_REPORT',
  NULL,
  '2016-02-05 11:22:43.150'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  3,
  'Facilities Missing Supporting Requisition Group',
  NULL,
  '2016-02-05 11:22:43.150'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  3,
  'Facilities Missing Create Requisition Role',
  NULL,
  '2016-02-05 11:22:43.150'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  3,
  'Facilities Missing Authorize Requisition Role',
  NULL,
  '2016-02-05 11:22:43.150'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  3,
  'Supervisory Nodes Missing Approve Requisition Role',
  NULL,
  '2016-02-05 11:22:43.150'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  3,
  'Requisition Groups Missing Supply Line',
  NULL,
  '2016-02-05 11:22:43.150'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  3,
  'Order Routing Inconsistencies',
  NULL,
  '2016-02-05 11:22:43.150'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  3,
  'Delivery Zones Missing Manage Distribution Role',
  NULL,
  '2016-02-05 11:22:43.150'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  4,
  'MANAGE_DISTRIBUTION',
  NULL,
  '2016-02-05 11:22:43.238'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  5,
  'VIEW_REQUISITION',
  NULL,
  '2016-02-05 11:22:43.240'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  5,
  'CREATE_REQUISITION',
  NULL,
  '2016-02-05 11:22:43.240'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  5,
  'AUTHORIZE_REQUISITION',
  NULL,
  '2016-02-05 11:22:43.240'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  5,
  'APPROVE_REQUISITION',
  NULL,
  '2016-02-05 11:22:43.240'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  5,
  'DELETE_REQUISITION',
  NULL,
  '2016-02-05 11:22:43.240'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  5,
  'MANAGE_EQUIPMENT_INVENTORY',
  NULL,
  '2016-02-05 11:22:43.240'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  5,
  'VIEW_STOCK_ON_HAND',
  NULL,
  '2016-02-05 11:22:43.240'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  5,
  'MANAGE_SUPERVISED_EQUIPMENTS',
  NULL,
  '2016-02-05 11:22:43.240'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  5,
  'MANAGE_STOCK',
  NULL,
  '2016-02-05 11:22:43.240'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  6,
  'CONVERT_TO_ORDER',
  NULL,
  '2016-02-05 11:22:43.243'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  6,
  'VIEW_ORDER',
  NULL,
  '2016-02-05 11:22:43.243'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  6,
  'FACILITY_FILL_SHIPMENT',
  NULL,
  '2016-02-05 11:22:43.243'
);
INSERT INTO role_rights
(
  roleid,
  rightname,
  createdby,
  createddate
)
VALUES
(
  6,
  'MANAGE_POD',
  NULL,
  '2016-02-05 11:22:43.243'
);

-- No UPDATEs for role_rights necessary
-- -------------------
-- INSERTS for public.roles
-- -------------------
INSERT INTO roles
(
  name,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'All Reports',
  NULL,
  NULL,
  '2016-02-05 11:22:43.139',
  NULL,
  '2016-02-05 11:22:43.139'
);
INSERT INTO roles
(
  name,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'All Admin Reports',
  NULL,
  NULL,
  '2016-02-05 11:22:43.139',
  NULL,
  '2016-02-05 11:22:43.139'
);
INSERT INTO roles
(
  name,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'Field Coordinator',
  NULL,
  NULL,
  '2016-02-05 11:22:43.139',
  NULL,
  '2016-02-05 11:22:43.139'
);
INSERT INTO roles
(
  name,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'All Requisition',
  NULL,
  NULL,
  '2016-02-05 11:22:43.139',
  NULL,
  '2016-02-05 11:22:43.139'
);
INSERT INTO roles
(
  name,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  'All Fulfillment',
  NULL,
  NULL,
  '2016-02-05 11:22:43.139',
  NULL,
  '2016-02-05 11:22:43.139'
);

-- No UPDATEs for roles necessary
-- No INSERTs for shipment_configuration necessary
-- No UPDATEs for shipment_configuration necessary
-- No INSERTs for shipment_file_columns necessary
-- No UPDATEs for shipment_file_columns necessary
-- No INSERTs for shipment_file_info necessary
-- No UPDATEs for shipment_file_info necessary
-- No INSERTs for shipment_line_items necessary
-- No UPDATEs for shipment_line_items necessary
-- No INSERTs for signatures necessary
-- No UPDATEs for signatures necessary
-- No INSERTs for sms necessary
-- No UPDATEs for sms necessary
-- No INSERTs for stock_adjustment_reasons_programs necessary
-- No UPDATEs for stock_adjustment_reasons_programs necessary
-- No INSERTs for stock_card_entries necessary
-- No UPDATEs for stock_card_entries necessary
-- No INSERTs for stock_card_entry_key_values necessary
-- No UPDATEs for stock_card_entry_key_values necessary
-- No INSERTs for stock_cards necessary
-- No UPDATEs for stock_cards necessary
-- No INSERTs for stock_movement_line_item_extra_fields necessary
-- No UPDATEs for stock_movement_line_item_extra_fields necessary
-- No INSERTs for stock_movement_line_items necessary
-- No UPDATEs for stock_movement_line_items necessary
-- No INSERTs for stock_movement_lots necessary
-- No UPDATEs for stock_movement_lots necessary
-- No INSERTs for stock_movements necessary
-- No UPDATEs for stock_movements necessary
-- -------------------
-- INSERTS for public.supervisory_nodes
-- -------------------
INSERT INTO supervisory_nodes
(
  parentid,
  facilityid,
  name,
  code,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  NULL,
  7,
  'South Central Medical Services',
  'SN80',
  NULL,
  1,
  '2016-02-05 11:14:09.928',
  1,
  '2016-02-05 11:14:09.914'
);
INSERT INTO supervisory_nodes
(
  parentid,
  facilityid,
  name,
  code,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  1,
  3,
  'Winkler District Office',
  'SN97',
  NULL,
  1,
  '2016-02-05 11:14:09.928',
  1,
  '2016-02-05 11:14:09.914'
);
INSERT INTO supervisory_nodes
(
  parentid,
  facilityid,
  name,
  code,
  description,
  createdby,
  createddate,
  modifiedby,
  modifieddate
)
VALUES
(
  NULL,
  3,
  'Malaria program supervision',
  'SN65',
  NULL,
  1,
  '2016-02-05 11:14:09.928',
  1,
  '2016-02-05 11:14:09.914'
);

-- No UPDATEs for supervisory_nodes necessary
-- -------------------
-- INSERTS for public.supply_lines
-- -------------------
INSERT INTO supply_lines
(
  description,
  supervisorynodeid,
  programid,
  supplyingfacilityid,
  exportorders,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  parentid
)
VALUES
(
  NULL,
  1,
  2,
  8,
  TRUE,
  1,
  '2016-02-05 11:14:18.127',
  1,
  '2016-02-05 11:14:18.119',
  NULL
);
INSERT INTO supply_lines
(
  description,
  supervisorynodeid,
  programid,
  supplyingfacilityid,
  exportorders,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  parentid
)
VALUES
(
  NULL,
  1,
  1,
  8,
  TRUE,
  1,
  '2016-02-05 11:14:18.127',
  1,
  '2016-02-05 11:14:18.119',
  NULL
);
INSERT INTO supply_lines
(
  description,
  supervisorynodeid,
  programid,
  supplyingfacilityid,
  exportorders,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  parentid
)
VALUES
(
  NULL,
  1,
  3,
  8,
  TRUE,
  1,
  '2016-02-05 11:14:18.127',
  1,
  '2016-02-05 11:14:18.119',
  NULL
);
INSERT INTO supply_lines
(
  description,
  supervisorynodeid,
  programid,
  supplyingfacilityid,
  exportorders,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  parentid
)
VALUES
(
  NULL,
  3,
  1,
  8,
  TRUE,
  1,
  '2016-02-05 11:14:18.127',
  1,
  '2016-02-05 11:14:18.119',
  NULL
);
INSERT INTO supply_lines
(
  description,
  supervisorynodeid,
  programid,
  supplyingfacilityid,
  exportorders,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  parentid
)
VALUES
(
  NULL,
  3,
  3,
  8,
  TRUE,
  1,
  '2016-02-05 11:14:18.127',
  1,
  '2016-02-05 11:14:18.119',
  NULL
);
INSERT INTO supply_lines
(
  description,
  supervisorynodeid,
  programid,
  supplyingfacilityid,
  exportorders,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  parentid
)
VALUES
(
  NULL,
  3,
  2,
  8,
  TRUE,
  1,
  '2016-02-05 11:14:18.127',
  1,
  '2016-02-05 11:14:18.119',
  NULL
);

-- No UPDATEs for supply_lines necessary
-- No INSERTs for template_parameters necessary
-- No UPDATEs for template_parameters necessary
-- No INSERTs for templates necessary
-- No UPDATEs for templates necessary
-- No INSERTs for user_password_reset_tokens necessary
-- No UPDATEs for user_password_reset_tokens necessary
-- No INSERTs for user_preference_master necessary
-- No UPDATEs for user_preference_master necessary
-- No INSERTs for user_preference_roles necessary
-- No UPDATEs for user_preference_roles necessary
-- No INSERTs for user_preferences necessary
-- No UPDATEs for user_preferences necessary
-- -------------------
-- INSERTS for public.users
-- -------------------
INSERT INTO users
(
  username,
  password,
  firstname,
  lastname,
  employeeid,
  restrictlogin,
  jobtitle,
  primarynotificationmethod,
  officephone,
  cellphone,
  email,
  supervisorid,
  facilityid,
  verified,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  ismobileuser
)
VALUES
(
  'superuser',
  'Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie',
  'Super',
  'User',
  NULL,
  FALSE,
  NULL,
  NULL,
  NULL,
  NULL,
  'superuser@openlmis.com',
  NULL,
  3,
  TRUE,
  TRUE,
  NULL,
  '2016-02-05 11:22:43.246',
  NULL,
  '2016-02-05 11:22:43.246',
  FALSE
);
INSERT INTO users
(
  username,
  password,
  firstname,
  lastname,
  employeeid,
  restrictlogin,
  jobtitle,
  primarynotificationmethod,
  officephone,
  cellphone,
  email,
  supervisorid,
  facilityid,
  verified,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  ismobileuser
)
VALUES
(
  'superadmin',
  'TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie',
  'Super',
  'Admin',
  NULL,
  FALSE,
  NULL,
  NULL,
  NULL,
  NULL,
  'superadmin@openlmis.com',
  NULL,
  NULL,
  TRUE,
  TRUE,
  NULL,
  '2016-02-05 11:22:43.250',
  NULL,
  '2016-02-05 11:22:43.250',
  FALSE
);
INSERT INTO users
(
  username,
  password,
  firstname,
  lastname,
  employeeid,
  restrictlogin,
  jobtitle,
  primarynotificationmethod,
  officephone,
  cellphone,
  email,
  supervisorid,
  facilityid,
  verified,
  active,
  createdby,
  createddate,
  modifiedby,
  modifieddate,
  ismobileuser
)
VALUES
(
  'devadmin',
  'TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie',
  'Dev',
  'Admin',
  NULL,
  FALSE,
  NULL,
  NULL,
  NULL,
  NULL,
  'devadmin@openlmis.com',
  NULL,
  3,
  TRUE,
  TRUE,
  NULL,
  '2016-02-05 11:22:43.250',
  3,
  '2016-03-03 11:34:07.178',
  FALSE
);

-- No UPDATEs for users necessary
-- No INSERTs for vaccination_adult_coverage_line_items necessary
-- No UPDATEs for vaccination_adult_coverage_line_items necessary
-- No INSERTs for vaccination_child_coverage_line_items necessary
-- No UPDATEs for vaccination_child_coverage_line_items necessary
-- No INSERTs for vaccine_discarding_reasons necessary
-- No UPDATEs for vaccine_discarding_reasons necessary
-- No INSERTs for vaccine_diseases necessary
-- No UPDATEs for vaccine_diseases necessary
-- No INSERTs for vaccine_distribution_line_item_lots necessary
-- No UPDATEs for vaccine_distribution_line_item_lots necessary
-- No INSERTs for vaccine_distribution_line_items necessary
-- No UPDATEs for vaccine_distribution_line_items necessary
-- No INSERTs for vaccine_distributions necessary
-- No UPDATEs for vaccine_distributions necessary
-- No INSERTs for vaccine_doses necessary
-- No UPDATEs for vaccine_doses necessary
-- No INSERTs for vaccine_inventory_product_configurations necessary
-- No UPDATEs for vaccine_inventory_product_configurations necessary
-- No INSERTs for vaccine_ivd_tab_visibilities necessary
-- No UPDATEs for vaccine_ivd_tab_visibilities necessary
-- No INSERTs for vaccine_ivd_tabs necessary
-- No UPDATEs for vaccine_ivd_tabs necessary
-- No INSERTs for vaccine_logistics_master_columns necessary
-- No UPDATEs for vaccine_logistics_master_columns necessary
-- No INSERTs for vaccine_lots_on_hand_adjustments necessary
-- No UPDATEs for vaccine_lots_on_hand_adjustments necessary
-- No INSERTs for vaccine_lots_on_hand_vvm necessary
-- No UPDATEs for vaccine_lots_on_hand_vvm necessary
-- No INSERTs for vaccine_order_requisition_line_items necessary
-- No UPDATEs for vaccine_order_requisition_line_items necessary
-- No INSERTs for vaccine_order_requisition_master_columns necessary
-- No UPDATEs for vaccine_order_requisition_master_columns necessary
-- No INSERTs for vaccine_order_requisition_status_changes necessary
-- No UPDATEs for vaccine_order_requisition_status_changes necessary
-- No INSERTs for vaccine_order_requisitions necessary
-- No UPDATEs for vaccine_order_requisitions necessary
-- No INSERTs for vaccine_product_doses necessary
-- No UPDATEs for vaccine_product_doses necessary
-- No INSERTs for vaccine_program_logistics_columns necessary
-- No UPDATEs for vaccine_program_logistics_columns necessary
-- No INSERTs for vaccine_report_adverse_effect_line_items necessary
-- No UPDATEs for vaccine_report_adverse_effect_line_items necessary
-- No INSERTs for vaccine_report_campaign_line_items necessary
-- No UPDATEs for vaccine_report_campaign_line_items necessary
-- No INSERTs for vaccine_report_cold_chain_line_items necessary
-- No UPDATEs for vaccine_report_cold_chain_line_items necessary
-- No INSERTs for vaccine_report_coverage_line_items necessary
-- No UPDATEs for vaccine_report_coverage_line_items necessary
-- No INSERTs for vaccine_report_disease_line_items necessary
-- No UPDATEs for vaccine_report_disease_line_items necessary
-- No INSERTs for vaccine_report_logistics_line_items necessary
-- No UPDATEs for vaccine_report_logistics_line_items necessary
-- No INSERTs for vaccine_report_status_changes necessary
-- No UPDATEs for vaccine_report_status_changes necessary
-- No INSERTs for vaccine_report_vitamin_supplementation_line_items necessary
-- No UPDATEs for vaccine_report_vitamin_supplementation_line_items necessary
-- No INSERTs for vaccine_reports necessary
-- No UPDATEs for vaccine_reports necessary
-- No INSERTs for vaccine_vitamin_supplementation_age_groups necessary
-- No UPDATEs for vaccine_vitamin_supplementation_age_groups necessary
-- No INSERTs for vaccine_vitamins necessary
-- No UPDATEs for vaccine_vitamins necessary
-- No INSERTs for var_details necessary
-- No UPDATEs for var_details necessary
-- No INSERTs for var_item_alarms necessary
-- No UPDATEs for var_item_alarms necessary
-- No INSERTs for var_item_partials necessary
-- No UPDATEs for var_item_partials necessary
-- No INSERTs for var_items necessary
-- No UPDATEs for var_items necessary
-- No INSERTs for dw_order_fill_rate_vw necessary
-- No UPDATEs for dw_order_fill_rate_vw necessary
-- No INSERTs for dw_product_facility_stock_info_vw necessary
-- No UPDATEs for dw_product_facility_stock_info_vw necessary
-- No INSERTs for dw_product_fill_rate_vw necessary
-- No UPDATEs for dw_product_fill_rate_vw necessary
-- No INSERTs for dw_product_lead_time_vw necessary
-- No UPDATEs for dw_product_lead_time_vw necessary
-- No INSERTs for vw_cce_repair_management necessary
-- No UPDATEs for vw_cce_repair_management necessary
-- No INSERTs for vw_cce_repair_management_not_functional necessary
-- No UPDATEs for vw_cce_repair_management_not_functional necessary
-- No INSERTs for vw_cold_chain_equipment necessary
-- No UPDATEs for vw_cold_chain_equipment necessary
-- No INSERTs for vw_district_financial_summary necessary
-- No UPDATEs for vw_district_financial_summary necessary
-- No INSERTs for vw_districts necessary
-- No UPDATEs for vw_districts necessary
-- No INSERTs for vw_e2e_stock_status necessary
-- No UPDATEs for vw_e2e_stock_status necessary
-- No INSERTs for vw_equipment_list_by_donor necessary
-- No UPDATEs for vw_equipment_list_by_donor necessary
-- No INSERTs for vw_expected_facilities necessary
-- No UPDATEs for vw_expected_facilities necessary
-- No INSERTs for vw_facility_requisitions necessary
-- No UPDATEs for vw_facility_requisitions necessary
-- No INSERTs for vw_lab_equipment_status necessary
-- No UPDATEs for vw_lab_equipment_status necessary
-- No INSERTs for vw_order_fill_rate necessary
-- No UPDATEs for vw_order_fill_rate necessary
-- No INSERTs for vw_program_facility_supplier necessary
-- No UPDATEs for vw_program_facility_supplier necessary
-- No INSERTs for vw_regimen_district_distribution necessary
-- No UPDATEs for vw_regimen_district_distribution necessary
-- No INSERTs for vw_replacement_plan_summary necessary
-- No UPDATEs for vw_replacement_plan_summary necessary
-- No INSERTs for vw_requisition_adjustment necessary
-- No UPDATEs for vw_requisition_adjustment necessary
-- No INSERTs for vw_requisition_detail necessary
-- No UPDATEs for vw_requisition_detail necessary
-- No INSERTs for vw_requisition_detail_dw necessary
-- No UPDATEs for vw_requisition_detail_dw necessary
-- No INSERTs for vw_rnr_feedback necessary
-- No UPDATEs for vw_rnr_feedback necessary
-- No INSERTs for vw_rnr_status necessary
-- No UPDATEs for vw_rnr_status necessary
-- No INSERTs for vw_rnr_status_details necessary
-- No UPDATEs for vw_rnr_status_details necessary
-- No INSERTs for vw_stock_cards necessary
-- No UPDATEs for vw_stock_cards necessary
-- No INSERTs for vw_stock_status necessary
-- No UPDATEs for vw_stock_status necessary
-- No INSERTs for vw_stock_status_2 necessary
-- No UPDATEs for vw_stock_status_2 necessary
-- No INSERTs for vw_supply_status necessary
-- No UPDATEs for vw_supply_status necessary
-- No INSERTs for vw_timeliness_report necessary
-- No UPDATEs for vw_timeliness_report necessary
-- No INSERTs for vw_user_districts necessary
-- No UPDATEs for vw_user_districts necessary
-- No INSERTs for vw_user_facilities necessary
-- No UPDATEs for vw_user_facilities necessary
-- No INSERTs for vw_user_geographic_zones necessary
-- No UPDATEs for vw_user_geographic_zones necessary
-- No INSERTs for vw_user_role_assignments necessary
-- No UPDATEs for vw_user_role_assignments necessary
-- No INSERTs for vw_user_supervisorynodes necessary
-- No UPDATEs for vw_user_supervisorynodes necessary
-- No INSERTs for vw_vaccine_campaign necessary
-- No UPDATEs for vw_vaccine_campaign necessary
-- No INSERTs for vw_vaccine_cold_chain necessary
-- No UPDATEs for vw_vaccine_cold_chain necessary
-- No INSERTs for vw_vaccine_coverage necessary
-- No UPDATEs for vw_vaccine_coverage necessary
-- No INSERTs for vw_vaccine_disease_surveillance necessary
-- No UPDATEs for vw_vaccine_disease_surveillance necessary
-- No INSERTs for vw_vaccine_district_target_population necessary
-- No UPDATEs for vw_vaccine_district_target_population necessary
-- No INSERTs for vw_vaccine_estimates necessary
-- No UPDATEs for vw_vaccine_estimates necessary
-- No INSERTs for vw_vaccine_iefi necessary
-- No UPDATEs for vw_vaccine_iefi necessary
-- No INSERTs for vw_vaccine_stock_status necessary
-- No UPDATEs for vw_vaccine_stock_status necessary
-- No INSERTs for vw_vaccine_target_population necessary
-- No UPDATEs for vw_vaccine_target_population necessary
-- No INSERTs for vw_vaccine_vitamin_supplementation necessary
-- No UPDATEs for vw_vaccine_vitamin_supplementation necessary

COMMIT;
