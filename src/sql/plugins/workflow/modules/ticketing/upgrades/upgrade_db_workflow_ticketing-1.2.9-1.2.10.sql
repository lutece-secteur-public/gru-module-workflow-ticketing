DROP TABLE IF EXISTS workflow_task_ticketing_mark_unread_config;
CREATE TABLE workflow_task_ticketing_mark_unread_config (
	id_task INT(11) NOT NULL,
	id_marking INT(11) NOT NULL,
	PRIMARY KEY (id_task)
);
INSERT INTO workflow_task_ticketing_mark_unread_config (id_task, id_marking) SELECT id_task, 1 FROM workflow_task WHERE task_type_key = 'taskTicketingMarkAsUnread';


