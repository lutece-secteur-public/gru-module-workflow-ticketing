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

