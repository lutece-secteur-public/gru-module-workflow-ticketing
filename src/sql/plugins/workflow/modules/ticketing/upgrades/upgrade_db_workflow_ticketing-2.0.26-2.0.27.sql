-- attributs pour l'évolution "relance automatique"
ALTER TABLE ticketing_ticket ADD nb_relance int(2) default 0;
ALTER TABLE ticketing_ticket ADD date_derniere_relance timestamp;

-- configuration pour l'évolution "relance automatique"
INSERT INTO `core_datastore` (`entity_key`, `entity_value`) VALUES
('ticketing.configuration.relance_auto.nb_relance_max', '3'),
('ticketing.configuration.relance_auto.frequence_relance', '10')
;


-- paramétrage de la tâche de relance automatique
create table workflow_task_ticketing_notify_waiting_ticket_config
(
    id_task int default 0 not null primary key ,
    sender_name varchar(255) null,
    subject varchar(255) null,
    message text null
);


