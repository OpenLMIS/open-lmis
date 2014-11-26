DROP TABLE IF EXISTS odk_stock_status_submission;
CREATE TABLE odk_stock_status_submission
(
  id                               serial       NOT NULL,
  odk_submission_id                integer    NOT NULL,
  msd_code                         varchar(50)  NOT NULL,
  commodity_name                   varchar(400)  NOT NULL,
  managed                          boolean  NOT NULL,
  physical_inventory               numeric(10,2) NOT NULL,
  qty_expired_today                integer NOT NULL,
  stock_card_available               boolean NOT NULL,
  stock_data_three_months            boolean NOT NULL,
  so_seven_days                      boolean NOT NULL,
  total_days_stockedout_three_months integer NOT NULL,
  issued_three_months                numeric(10,2) NOT NULL,
  days_data_available                integer NOT NULL,
  active                             boolean NOT NULL,
  comment TEXT,
  createdby     integer,
  createddate   timestamp    DEFAULT now(),
  modifiedby    integer,
  modifieddate  timestamp    DEFAULT now()
);

ALTER TABLE odk_stock_status_submission
ADD CONSTRAINT odk_stock_status_submission_pkey
PRIMARY KEY (id);


ALTER TABLE odk_stock_status_submission
ADD CONSTRAINT odk_stock_status_submission_submission_id_fkey FOREIGN KEY (odk_submission_id)
REFERENCES odk_submission (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

