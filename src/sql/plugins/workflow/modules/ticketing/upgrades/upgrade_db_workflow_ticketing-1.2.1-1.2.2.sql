/*====================================================================*/
/* Table structure for table workflow_task_ticketing_modify_config    */
/*====================================================================*/
DROP TABLE IF EXISTS workflow_task_ticketing_modify_config;
CREATE TABLE workflow_task_ticketing_modify_config (
id_task INT NOT NULL,
id_entry INT NOT NULL,
PRIMARY KEY (id_task, id_entry)
);

