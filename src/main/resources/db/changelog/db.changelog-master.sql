-- liquibase formatted sql

-- changeset itoutsource.cz1731:1752648834870-1
CREATE SEQUENCE  IF NOT EXISTS action_items_seq INCREMENT BY 50;

-- changeset itoutsource.cz1731:1752648834870-2
CREATE SEQUENCE  IF NOT EXISTS departments_seq INCREMENT BY 50;

-- changeset itoutsource.cz1731:1752648834870-3
CREATE SEQUENCE  IF NOT EXISTS employees_seq INCREMENT BY 50;

-- changeset itoutsource.cz1731:1752648834870-4
CREATE SEQUENCE  IF NOT EXISTS event_attendees_seq INCREMENT BY 50;

-- changeset itoutsource.cz1731:1752648834870-5
CREATE SEQUENCE  IF NOT EXISTS meeting_minutes_seq INCREMENT BY 50;

-- changeset itoutsource.cz1731:1752648834870-6
CREATE SEQUENCE  IF NOT EXISTS schedule_events_seq INCREMENT BY 50;

-- changeset itoutsource.cz1731:1752648834870-7
CREATE TABLE action_items (id BIGINT NOT NULL, description VARCHAR(255), due_date TIMESTAMP WITHOUT TIME ZONE, name VARCHAR(255), status VARCHAR(255), meeting_minute_id BIGINT, CONSTRAINT action_items_pkey PRIMARY KEY (id));

-- changeset itoutsource.cz1731:1752648834870-8
CREATE TABLE departments (id BIGINT NOT NULL, name VARCHAR(255), parent_id BIGINT, CONSTRAINT departments_pkey PRIMARY KEY (id));

-- changeset itoutsource.cz1731:1752648834870-9
CREATE TABLE employees (id BIGINT NOT NULL, badge VARCHAR(255), email VARCHAR(255), name VARCHAR(255), union_id VARCHAR(255), department_id BIGINT, CONSTRAINT employees_pkey PRIMARY KEY (id));

-- changeset itoutsource.cz1731:1752648834870-10
CREATE TABLE event_attendees (id BIGINT NOT NULL, employee_id BIGINT, event_id BIGINT, CONSTRAINT event_attendees_pkey PRIMARY KEY (id));

-- changeset itoutsource.cz1731:1752648834870-11
CREATE TABLE meeting_minutes (id BIGINT NOT NULL, raw_data VARCHAR(255), event_id BIGINT, CONSTRAINT meeting_minutes_pkey PRIMARY KEY (id));

-- changeset itoutsource.cz1731:1752648834870-12
CREATE TABLE schedule_events (id BIGINT NOT NULL, description VARCHAR(255), end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL, location VARCHAR(255), start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL, summary VARCHAR(255) NOT NULL, CONSTRAINT schedule_events_pkey PRIMARY KEY (id));

-- changeset itoutsource.cz1731:1752648834870-13
ALTER TABLE event_attendees ADD CONSTRAINT uk90g1alpkw14uj017mtcdtflx2 UNIQUE (employee_id);

-- changeset itoutsource.cz1731:1752648834870-14
ALTER TABLE meeting_minutes ADD CONSTRAINT ukc9iao7vks2k07m138jjjvlb4v UNIQUE (event_id);

-- changeset itoutsource.cz1731:1752648834870-15
ALTER TABLE action_items ADD CONSTRAINT fkr0lmmac1f9r89ma8vxbs55hcl FOREIGN KEY (meeting_minute_id) REFERENCES meeting_minutes (id) ON DELETE NO ACTION;


CREATE SEQUENCE IF NOT EXISTS action_items_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS departments_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS employees_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS event_attendees_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS meeting_minutes_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS schedule_events_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE action_items
(
    id                BIGINT NOT NULL,
    name              VARCHAR(255),
    description       VARCHAR(255),
    status            VARCHAR(255),
    due_date          TIMESTAMP WITHOUT TIME ZONE,
    meeting_minute_id BIGINT,
    CONSTRAINT pk_action_items PRIMARY KEY (id)
);

CREATE TABLE departments
(
    id               BIGINT NOT NULL,
    name             VARCHAR(255),
    dingtalk_dept_id BIGINT,
    parent_id        BIGINT,
    CONSTRAINT pk_departments PRIMARY KEY (id)
);

CREATE TABLE employees
(
    id            BIGINT NOT NULL,
    name          VARCHAR(255),
    email         VARCHAR(255),
    badge         VARCHAR(255),
    union_id      VARCHAR(255),
    department_id BIGINT,
    CONSTRAINT pk_employees PRIMARY KEY (id)
);

CREATE TABLE event_attendees
(
    id          BIGINT NOT NULL,
    employee_id BIGINT,
    event_id    BIGINT,
    CONSTRAINT pk_event_attendees PRIMARY KEY (id)
);

CREATE TABLE meeting_minutes
(
    id       BIGINT NOT NULL,
    raw_data VARCHAR(255),
    event_id BIGINT,
    CONSTRAINT pk_meeting_minutes PRIMARY KEY (id)
);

CREATE TABLE schedule_events
(
    id          BIGINT                      NOT NULL,
    summary     VARCHAR(255)                NOT NULL,
    description VARCHAR(255),
    start_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    location    VARCHAR(255),
    CONSTRAINT pk_schedule_events PRIMARY KEY (id)
);

ALTER TABLE event_attendees
    ADD CONSTRAINT uc_event_attendees_employee UNIQUE (employee_id);

ALTER TABLE meeting_minutes
    ADD CONSTRAINT uc_meeting_minutes_event UNIQUE (event_id);

ALTER TABLE action_items
    ADD CONSTRAINT FK_ACTION_ITEMS_ON_MEETING_MINUTE FOREIGN KEY (meeting_minute_id) REFERENCES meeting_minutes (id);
CREATE SEQUENCE IF NOT EXISTS action_items_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS departments_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS employees_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS event_attendees_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS meeting_minutes_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS schedule_events_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE action_items
(
    id                BIGINT NOT NULL,
    name              VARCHAR(255),
    description       VARCHAR(255),
    status            VARCHAR(255),
    due_date          TIMESTAMP WITHOUT TIME ZONE,
    meeting_minute_id BIGINT,
    CONSTRAINT pk_action_items PRIMARY KEY (id)
);

CREATE TABLE departments
(
    id               BIGINT NOT NULL,
    name             VARCHAR(255),
    dingtalk_dept_id BIGINT,
    parent_id        BIGINT,
    CONSTRAINT pk_departments PRIMARY KEY (id)
);

CREATE TABLE employees
(
    id            BIGINT NOT NULL,
    name          VARCHAR(255),
    email         VARCHAR(255),
    badge         VARCHAR(255),
    union_id      VARCHAR(255),
    department_id BIGINT,
    CONSTRAINT pk_employees PRIMARY KEY (id)
);

CREATE TABLE event_attendees
(
    id          BIGINT NOT NULL,
    employee_id BIGINT,
    event_id    BIGINT,
    CONSTRAINT pk_event_attendees PRIMARY KEY (id)
);

CREATE TABLE meeting_minutes
(
    id       BIGINT NOT NULL,
    raw_data VARCHAR(255),
    event_id BIGINT,
    CONSTRAINT pk_meeting_minutes PRIMARY KEY (id)
);

CREATE TABLE schedule_events
(
    id          BIGINT                      NOT NULL,
    summary     VARCHAR(255)                NOT NULL,
    description VARCHAR(255),
    start_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    location    VARCHAR(255),
    CONSTRAINT pk_schedule_events PRIMARY KEY (id)
);

ALTER TABLE event_attendees
    ADD CONSTRAINT uc_event_attendees_employee UNIQUE (employee_id);

ALTER TABLE meeting_minutes
    ADD CONSTRAINT uc_meeting_minutes_event UNIQUE (event_id);

ALTER TABLE action_items
    ADD CONSTRAINT FK_ACTION_ITEMS_ON_MEETING_MINUTE FOREIGN KEY (meeting_minute_id) REFERENCES meeting_minutes (id);