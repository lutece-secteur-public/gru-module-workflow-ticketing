/*==================================================================*/
/* Table structure for table workflow_task_ticketing_information    */
/*==================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_information;
CREATE TABLE workflow_task_ticketing_information
(
    id_history INT DEFAULT 0 NOT NULL,
    id_task INT DEFAULT NULL,
    information_value LONG VARCHAR DEFAULT NULL,
    PRIMARY KEY (id_history, id_task)
);

/*==================================================================*/
/* Table structure for table workflow_task_ticketing_reply_config    */
/*==================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_reply_config;
CREATE TABLE workflow_task_ticketing_reply_config
(
    id_task INT DEFAULT 0 NOT NULL,
    message_direction INT DEFAULT NULL,
    PRIMARY KEY (id_task)
);

/*==================================================================*/
/* Table structure for table workflow_task_ticketing_editable_ticket    */
/*==================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_editable_ticket;
CREATE TABLE workflow_task_ticketing_editable_ticket
(
    id_history INT DEFAULT 0 NOT NULL,
    id_task INT DEFAULT 0 NOT NULL,
    id_ticket INT DEFAULT 0 NOT NULL,
    message LONG VARCHAR,
    is_edited SMALLINT DEFAULT 0 NOT NULL,
    PRIMARY KEY (id_history, id_task)
);

/*==================================================================*/
/* Table structure for table workflow_task_ticketing_editable_ticket_field    */
/*==================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_editable_ticket_field;
CREATE TABLE workflow_task_ticketing_editable_ticket_field
(
    id_history INT DEFAULT 0 NOT NULL,
    id_entry INT DEFAULT 0 NOT NULL,
    PRIMARY KEY (id_history, id_entry)
);

/*==================================================================*/
/* Table structure for table workflow_task_ticketing_edit_ticket_config    */
/*==================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_edit_ticket_config;
CREATE TABLE workflow_task_ticketing_edit_ticket_config
(
    id_task INT DEFAULT 0 NOT NULL,
    message_direction INT DEFAULT NULL,
    id_user_edition_action INT DEFAULT 0 NOT NULL,
    PRIMARY KEY (id_task)
);

/*==================================================================*/
/* Table structure for table workflow_resource_history_ticketing    */
/*==================================================================*/
DROP TABLE IF EXISTS workflow_resource_history_ticketing;
CREATE TABLE workflow_resource_history_ticketing (
id_history INT NOT NULL,
id_channel INT NOT NULL,
PRIMARY KEY (id_history)
);

/*==================================================================================*/
/* Table structure for table workflow_task_ticketing_automatic_assignment_config    */
/*==================================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_automatic_assignment_config;
CREATE TABLE workflow_task_ticketing_automatic_assignment_config
(
    id_task INT DEFAULT 0 NOT NULL,
    assignment_suffix VARCHAR(5) NOT NULL,
    user_access_code varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    PRIMARY KEY (id_task,assignment_suffix)
);

/*====================================================================*/
/* Table structure for table workflow_task_ticketing_modify_config    */
/*====================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_modify_config;
CREATE TABLE workflow_task_ticketing_modify_config (
id_task INT NOT NULL,
id_entry INT NOT NULL,
PRIMARY KEY (id_task, id_entry)
);

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
    PRIMARY KEY (id_message_agent)
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
/* Table struvture for table ticket_email_external_user_recipient */
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