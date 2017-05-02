--
-- Data for table core_datastore
--
DELETE FROM core_datastore WHERE entity_key='module.workflow.ticketing.site_property.externaluser.entiteattribut';
INSERT INTO core_datastore VALUES ('module.workflow.ticketing.site_property.externaluser.entiteattribut', '');

/*===================================================================================*/
/* Table struture for table workflow_task_ticketing_email_external_user_history */
/*===================================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_email_external_user_history;
CREATE TABLE workflow_task_ticketing_email_external_user_history
(
    id_task INT DEFAULT 0 NOT NULL,
    id_history INT DEFAULT 0 NOT NULL,
    id_message_external_user INT DEFAULT 0 NOT NULL,
    PRIMARY KEY (id_task, id_history)
);

/*=================================================================*/
/* Table struture for table workflow_ticketing_email_external_user */
/*=================================================================*/
DROP TABLE IF EXISTS workflow_ticketing_email_external_user;
CREATE TABLE workflow_ticketing_email_external_user
(
    id_message_external_user INT DEFAULT 0 NOT NULL,
    id_ticket INT DEFAULT 0 NOT NULL,
    email_recipients MEDIUMTEXT DEFAULT NULL,
    email_recipients_cc MEDIUMTEXT DEFAULT NULL,
    message_question LONG VARCHAR DEFAULT NULL,
    message_response LONG VARCHAR DEFAULT NULL,
    is_answered INT DEFAULT 0,
    PRIMARY KEY (id_message_external_user)
);

/*===================================================================================*/
/* Table structure for table workflow_task_ticketing_email_external_user_config */
/*===================================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_email_external_user_config;
CREATE TABLE workflow_task_ticketing_email_external_user_config
(
    id_task INT DEFAULT 0 NOT NULL,
    message_direction INT DEFAULT NULL,
    id_following_action INT DEFAULT NULL,
    PRIMARY KEY (id_task)
);

/*================================================================================*/
/* Table struture for table ticket_email_external_user_recipient */
/*================================================================================*/
DROP TABLE IF EXISTS ticket_email_external_user_recipient;
CREATE TABLE ticket_email_external_user_recipient
(
	id_recipient INT DEFAULT 0 NOT NULL,
	id_task INT DEFAULT 0 NOT NULL,
	id_history INT DEFAULT 0 NOT NULL,
	email VARCHAR(255) NOT NULL,
	field VARCHAR(255) DEFAULT NULL,
	name VARCHAR(255) DEFAULT NULL,
	firstname VARCHAR(255) DEFAULT NULL,
	PRIMARY KEY (id_recipient)
);

/*==============================================================================*/
/* Table struture for table workflow_task_ticketing_email_external_user_cc */
/*==============================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_email_external_user_cc;
CREATE TABLE workflow_task_ticketing_email_external_user_cc
(
	id_cc INT DEFAULT 0 NOT NULL,
	id_task INT DEFAULT 0 NOT NULL,
	id_history INT DEFAULT 0 NOT NULL,
	email VARCHAR(255) NOT NULL,
	PRIMARY KEY (id_cc)
);