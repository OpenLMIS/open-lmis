CREATE TABLE requisition_group_program_schedule (
requisition_group_id INTEGER REFERENCES requisition_group(id),
program_id INTEGER REFERENCES program(id),
schedule_id INTEGER REFERENCES schedule(id),
UNIQUE (requisition_group_id, program_id)
);

