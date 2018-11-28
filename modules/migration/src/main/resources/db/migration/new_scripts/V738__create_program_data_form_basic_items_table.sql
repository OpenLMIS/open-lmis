CREATE TABLE program_data_form_basic_items
(
  id                      SERIAL PRIMARY KEY,
  formId                  INTEGER REFERENCES program_data_forms(id),
  productCode             VARCHAR(50) REFERENCES products(code),
  beginningBalance        INTEGER,
  quantityReceived        INTEGER,
  quantityDispensed       INTEGER,
  totalLossesAndAdjustments INTEGER,
  stockInHand             INTEGER,
  createdBy               INTEGER,
  modifiedBy              INTEGER,
  createdDate             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedDate            TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)