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
