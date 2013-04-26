
CREATE EXTENSION "uuid-ossp";

ALTER TABLE "public"."facilities" ALTER COLUMN "satelliteparentcode" TYPE varchar(9);
CREATE INDEX "i_facility_name" ON "public"."facilities" ("name" ASC);
DROP INDEX "public"."uc_facility_code";
CREATE INDEX "i_facility_approved_product_programproductid_facilitytypeid" ON "public"."facility_approved_products" ("programproductid" ASC, "facilitytypeid" ASC);
DROP INDEX "public"."uc_facility_operators";

CREATE INDEX "i_processing_period_startdate_enddate" ON "public"."processing_periods" ("startdate" ASC, "enddate" ASC);

DROP INDEX "public"."uc_period_name";
DROP INDEX "public"."ucschedulecode";
ALTER TABLE "public"."products" ALTER COLUMN "strength" TYPE varchar(50);
DROP INDEX "public"."uc_product_code";
CREATE INDEX "i_program_product_price_history_programproductid" ON "public"."program_product_price_history" ("programproductid" ASC);
ALTER TABLE "public"."program_products" ALTER COLUMN "modifieddate" DROP DEFAULT;
CREATE INDEX "i_program_product_programid_productid" ON "public"."program_products" ("programid" ASC, "productid" ASC);
CREATE INDEX "program_id_index" ON "public"."program_rnr_columns" ("programid" ASC);
ALTER TABLE "public"."programs" ADD COLUMN "templateconfigured" bool;
ALTER TABLE "public"."programs" ALTER COLUMN "lastmodifieddate" TYPE timestamp(6), ALTER COLUMN "lastmodifieddate" SET DEFAULT now();

ALTER TABLE "public"."programs_supported" DROP CONSTRAINT "programs_supported_pkey";

ALTER TABLE "public"."programs_supported" ADD COLUMN "id" SERIAL NOT NULL;
ALTER TABLE "public"."programs_supported" ALTER COLUMN "facilityid" DROP NOT NULL;
ALTER TABLE "public"."programs_supported" ALTER COLUMN "programid" DROP NOT NULL;
CREATE INDEX "i_program_supported_facilityid" ON "public"."programs_supported" ("facilityid" ASC);
CREATE INDEX "i_program_supported_facilityid_programid" ON "public"."programs_supported" ("facilityid" ASC, "programid" ASC);
ALTER TABLE "public"."requisition_group_members" ADD COLUMN "id" SERIAL NOT NULL;
CREATE INDEX "i_requisition_group_member_facilityid" ON "public"."requisition_group_members" ("facilityid" ASC);
ALTER TABLE "public"."requisition_group_program_schedules" ADD COLUMN "id"  SERIAL NOT NULL;
ALTER TABLE "public"."requisition_group_program_schedules" ALTER COLUMN "scheduleid" SET NOT NULL;
CREATE INDEX "i_requisition_group_program_schedules_requisitiongroupid" ON "public"."requisition_group_program_schedules" ("requisitiongroupid" ASC);
CREATE INDEX "i_requisition_group_supervisorynodeid" ON "public"."requisition_groups" ("supervisorynodeid" ASC);
DROP INDEX "public"."ucrequisitiongroupcode";
ALTER TABLE "public"."requisition_line_items" ADD COLUMN "productdisplayorder" int4;
ALTER TABLE "public"."requisition_line_items" ADD COLUMN "productcategorydisplayorder" int4;
CREATE INDEX "i_requisition_line_items_rnrid" ON "public"."requisition_line_items" ("rnrid" ASC);

CREATE INDEX "i_requisitions_programid_supervisorynodeid" ON "public"."requisitions" ("programid" ASC, "supervisorynodeid" ASC);


DROP INDEX "public"."unique_role_name";


CREATE INDEX "i_supervisory_node_parentid" ON "public"."supervisory_nodes" ("parentid" ASC);
DROP INDEX "public"."isupervisorynodeparentid";
DROP INDEX "public"."ucsupervisorynodecode";
ALTER TABLE "public"."users" ALTER COLUMN "email" DROP NOT NULL;
ALTER TABLE "public"."users" ADD COLUMN "vendorid" int4;

CREATE UNIQUE INDEX uc_users_username_vendor ON users USING btree (lower(username::text) COLLATE pg_catalog."default", vendorid);

DROP INDEX "public"."uc_users_username";
CREATE TABLE "public"."vendors" (
"id" SERIAL NOT NULL,
"name" varchar(250) NOT NULL,
"authtoken" uuid NOT NULL DEFAULT uuid_generate_v4(),
"active" bool,
CONSTRAINT "vendors_pkey" PRIMARY KEY ("id")
);
DROP TABLE "public"."user_password_token";




ALTER TABLE "public"."programs_supported" ADD CONSTRAINT "programs_supported_pkey" PRIMARY KEY ("id") ;
ALTER TABLE "public"."requisition_group_members"DROP CONSTRAINT "requisition_group_members_pkey";
ALTER TABLE "public"."requisition_group_members" ADD CONSTRAINT "requisition_group_members_pkey" PRIMARY KEY ("id") ;
ALTER TABLE "public"."requisition_group_program_schedules"DROP CONSTRAINT "requisition_group_program_schedules_pkey";
ALTER TABLE "public"."requisition_group_program_schedules" ADD CONSTRAINT "requisition_group_program_schedules_pkey" PRIMARY KEY ("id") ;


ALTER TABLE "public"."users" ADD CONSTRAINT "users_vendorid_fkey" FOREIGN KEY ("vendorid") REFERENCES "public"."vendors" ("id");


CREATE INDEX i_requisitions_status ON requisitions(LOWER(status));
CREATE INDEX i_users_firstName_lastName_email ON users(LOWER(firstName), LOWER(lastName), LOWER(email));
CREATE UNIQUE INDEX uc_processing_period_name_scheduleid  ON processing_periods  USING btree  (lower(name::text) COLLATE pg_catalog."default", scheduleid);


INSERT into vendors(name,active) values ('openLmis',TRUE);

UPDATE users set vendorid = 1 where userName =  'Admin123';
