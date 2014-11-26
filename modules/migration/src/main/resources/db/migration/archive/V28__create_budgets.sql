
DROP TABLE IF EXISTS budgets;

CREATE TABLE budgets (
 id SERIAL PRIMARY KEY,
 facilityId INTEGER NOT NULl,
 periodId INTEGER NOT NULL,
 programId  INTEGER NOT NULL,
 netBudgetAmount NUMERIC(20,2) NOT NULl,
 comment TEXT,
 createdBy INTEGER,
 createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 modifiedBy INTEGER,
 modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.budgets OWNER TO postgres;

ALTER TABLE ONLY budgets
    ADD CONSTRAINT budgets_facilityId_fkey FOREIGN KEY (facilityId) REFERENCES facilities(id);
ALTER TABLE ONLY budgets
    ADD CONSTRAINT budgets_periodId_fkey FOREIGN KEY (periodId) REFERENCES processing_periods(id);
ALTER TABLE ONLY budgets
    ADD CONSTRAINT budgets_programId_fkey FOREIGN KEY (programId) REFERENCES programs(id);