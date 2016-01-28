DROP TABLE IF EXISTS workflow_task_ticketing_information;


/*==================================================================*/
/* Table structure for table workflow_task_ticketing_information    */
/*==================================================================*/
CREATE TABLE workflow_task_ticketing_information
(
    id_history INT DEFAULT 0 NOT NULL,
    id_task INT DEFAULT NULL,
    information_value LONG VARCHAR DEFAULT NULL,
    PRIMARY KEY (id_history, id_task)
);

