DROP TABLE IF EXISTS workflow_resource_history_ticketing;
CREATE TABLE workflow_resource_history_ticketing (
id_history INT NOT NULL,
id_channel INT NOT NULL,
PRIMARY KEY (id_history)
);

INSERT INTO workflow_resource_history_ticketing
SELECT id_history, id_channel
FROM workflow_task_ticketing_information
WHERE id_channel > 0;

ALTER TABLE workflow_task_ticketing_information DROP COLUMN id_channel;


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

