CREATE TABLE requisition_group_program_schedule (
requisition_group_id INTEGER REFERENCES requisition_group(id),
program_id INTEGER REFERENCES program(id),
schedule_id INTEGER REFERENCES schedule(id),
modified_by VARCHAR(50),
modified_date TIMESTAMP  DEFAULT  CURRENT_TIMESTAMP,
PRIMARY KEY (requisition_group_id, program_id)
);

