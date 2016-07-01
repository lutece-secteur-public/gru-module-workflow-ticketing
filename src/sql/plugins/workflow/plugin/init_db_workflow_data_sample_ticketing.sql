DELETE FROM workflow_task WHERE id_action >= 300 AND id_action < 450 AND id_task != 303 ;
DELETE FROM workflow_action WHERE id_workflow >= 300 AND id_workflow < 400;
DELETE FROM workflow_state WHERE id_workflow >= 300 AND id_workflow < 400;
DELETE FROM workflow_workflow WHERE id_workflow >= 300 AND id_workflow < 400; 
INSERT INTO workflow_workflow (id_workflow, name, description, creation_date, is_enabled, workgroup_key) 
	VALUES	(301,'Workflow GRU','Workflow GRU','2016-01-13 08:36:34',1,'all');

INSERT INTO workflow_state (id_state, name, description, id_workflow, is_initial_state, is_required_workgroup_assigned, id_icon, display_order) 
	VALUES	(301,'Nouveau','Nouveau',301,1,0,NULL,1),
			(303,'A traiter','A traiter',301,0,0,NULL,2),
			(304,'En attente de compléments par l''usager','En attente de compléments par l''usager',301,0,0,NULL,3),
			(305,'Escaladé niveau 3','Le ticket est escaladé vers un agent de niveau 3',301,0,0,NULL,4),
			(306,'Traité','Traité',301,0,0,NULL,5);

INSERT INTO workflow_action (id_action, name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action) 
	VALUES 	(301,'Initialisation','Initialisation de la sollicitation',301,301,303,1,1,0,1,0),
			(303,'Requalifier','Requalification de la sollicitation',301,303,303,1,0,0,2,0),
            (304,'Escalader','Escalade vers un agent de niveau 2',301,303,303,1,0,0,6,0),
            (305,'Escalader','Escalade vers un agent de niveau 3',301,303,305,1,0,0,7,0),
			(306,'Assigner à une autre entité','Assignation de la sollicitation à une autre entité',301,303,303,1,0,0,5,0),
            (307,'Assigner à un autre agent','Assignation de la sollicitation à un autre agent',301,303,303,1,0,0,4,0),
			(308,'Me l''assigner','Prise en charge de la sollicitation',301,303,303,1,0,0,3,0),
			(309,'Demander compléments','Demande d''informations complémentaires à l''usager',301,303,304,1,0,0,8,0),
			(310,'Répondre pour l''usager','Réponse à la place de l''usager',301,304,303,1,0,0,9,0),
			(311,'Répondre (usager)','Réponse de l''usager à une demande d''informations',301,304,303,1,0,0,10,0),
            (312,'Répondre à l''usager','Réponse finale à l''usager',301,303,306,1,0,0,11,0),
            (313,'Répondre à l''escalade', 'Réponse à l''escalade', 301, 305, 303, 1, 0, 0, 12, 0),
            (314,'Assigner à un autre agent','Assignation de la sollicitation à un autre agent',301,305,305,1,0,0,4,0), -- assignation a autre agent pour tickets escaladés
			(315,'Me l''assigner','Prise en charge de la sollicitation',301,305,305,1,0,0,3,0) -- auto assignation pour tickets escaladés
;
		
INSERT INTO workflow_task (id_task, task_type_key, id_action, display_order) 
	VALUES 	(301,'taskTicketingGenerateTicketReference',301,1), -- Initialize
			(302,'taskTicketingAssignUnitLinkedToCategory',301,2),
			(303, 'taskAutomaticAssignment',301,3),
			-- 304 is reserved by module ticketing gru 	(304,'taskTicketingCreateCustomer',301,4)
			(305, 'taskTicketingIndexTicket',301,5),
            (307, 'taskTicketingRegisterChannel',301,7),
            (341,'taskTicketingQualifyTicket',303,1), -- Qualify
            (342,'taskTicketingModifyTicketCategory',303,2),
            (343, 'taskTypeComment', 303,3),
            (344, 'taskTicketingIndexTicket',303,4),
            (351,'taskTicketingAssignTicketToUnit',306,1), -- Assign to unit
            (352, 'taskTypeComment', 306,2),
            (361,'taskTicketingAssignTicketToUser',307,1), -- Assign to user
            (362, 'taskTypeComment', 307,2),
            (371,'taskTicketingAssignTicketToMe',308,1), -- Assign to me
            (372, 'taskTypeComment', 308,2),
			(381,'taskTicketingAssignUpTicket',304,1), -- Assign up
			(382, 'taskTypeComment', 304,2),
			(386,'taskTicketingAssignUpTicket',305,1), -- Assign up level 3
			(387, 'taskTypeComment', 305,2),
            (390, 'taskTicketingEditTicket', 309,1), -- Ask for user information
            (391, 'taskTicketingIndexTicket',309,2),
            (400, 'taskTicketingEditTicket', 310,1), -- Reply to info request from agent to agent
            (401, 'taskTicketingSelectChannel', 310,2),
            (403, 'taskTicketingIndexTicket', 310,4),
            (410, 'taskTicketingEditTicket',311,1), -- Reply to info request from user to agent
            (411, 'taskTicketingSelectChannel',311,2),
            (413, 'taskTicketingIndexTicket', 311,4),
			(420, 'taskTicketingReply', 312,1), -- Reply to user
            (423, 'taskTicketingIndexTicket', 312,3),
            (441, 'taskTicketingReplyAssignUpTicket',313,1), -- Reply to assign up
            (442, 'taskTypeComment', 313,2),
            (461,'taskTicketingAssignTicketToUser',314,1), -- Assign to user LEVEL3
            (462, 'taskTypeComment', 314,2),
            (471,'taskTicketingAssignTicketToMe',315,1), -- Assign to me LEVEL3
            (472, 'taskTypeComment', 315,2)
;


DELETE FROM workflow_task_comment_config WHERE id_task >= 300 AND id_task < 500;			
INSERT INTO workflow_task_comment_config (id_task, title, is_mandatory, is_richtext) 
	VALUES	(343, 'Commentaire', 0, 1),
			(352, 'Commentaire', 0, 1),
			(362, 'Commentaire', 0, 1),
			(372, 'Commentaire', 0, 1),
			(382, 'Commentaire', 0, 1),
			(387, 'Commentaire', 0, 1),
            (442, 'Commentaire', 0, 1),
            (462, 'Commentaire', 0, 1),
			(472, 'Commentaire', 0, 1)
;

DELETE FROM workflow_task_ticketing_edit_ticket_config;
INSERT INTO workflow_task_ticketing_edit_ticket_config (id_task, message_direction, id_user_edition_action) 
    VALUES  (390, 1, 311),  -- Ask for user information
            (400, 0, 310), -- Reply to agent from agent
            (410, 0, 311) -- Reply to agent from user
;
			
			
DELETE FROM workflow_task_ticketing_reply_config;
INSERT INTO workflow_task_ticketing_reply_config (id_task, message_direction) 
    VALUES  (420, 1) -- Reply to user
;

DELETE FROM core_datastore WHERE entity_key LIKE 'ticketing.configuration.%';
INSERT INTO `core_datastore` (`entity_key`, `entity_value`) VALUES
('ticketing.configuration.workflow.id', '301'),
('ticketing.configuration.states.selected', '303'),
('ticketing.configuration.states.selected.for.role.gru_level_3', '305'),
('ticketing.configuration.state.id.closed', '306'),
('ticketing.configuration.actions.filtered.when.assigned.to.me', '308;315'),
('ticketing.configuration.adminUser.id.front', '5'),
('ticketing.configuration.channel.id.front', '99')
;
