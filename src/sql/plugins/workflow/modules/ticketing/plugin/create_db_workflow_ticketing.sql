/*==================================================================*/
/* Table structure for table workflow_task_ticketing_information    */
/*==================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_information;
CREATE TABLE workflow_task_ticketing_information
(
    id_history INT DEFAULT 0 NOT NULL,
    id_task INT DEFAULT NULL,
    information_value LONG VARCHAR DEFAULT NULL,
    id_channel INT DEFAULT 0 NOT NULL,
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

