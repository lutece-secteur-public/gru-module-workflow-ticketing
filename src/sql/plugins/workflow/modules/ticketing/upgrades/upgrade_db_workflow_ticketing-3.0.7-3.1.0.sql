-- Ticketing add automatic notification task for level 3 tickets
INSERT INTO workflow_action
(id_action, name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action)
VALUES(346, 'Relance niveau 3', 'Relance des sollicitations sans réponses au niveau 3', 301, 305, 305, 1, 0, 0, 43, 0);
INSERT INTO workflow_action
(id_action, name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action)
VALUES(347, 'Retour après relance niveau 3', 'Retour après multiple relances sans réponses des sollicitation escaladées niveau 3', 301, 305, 303, 1, 0, 0, 44, 0);

INSERT INTO workflow_task
(id_task, task_type_key, id_action, display_order)
VALUES(597, 'taskTicketingAssignUpTicket', 346, 1);
INSERT INTO workflow_task
(id_task, task_type_key, id_action, display_order)
VALUES(596, 'taskTicketingMarkAsUnread', 347, 1);
INSERT INTO workflow_task
(id_task, task_type_key, id_action, display_order)
VALUES(594, 'taskNotifyGru', 346, 2);

INSERT INTO workflow_task_ticketing_assign_unit_config
(id_task, level_1, level_2, level_3)
VALUES(597, 0, 0, 1);

INSERT INTO workflow_task_ticketing_mark_unread_config
(id_task, id_marking)
VALUES(596, 9);

INSERT INTO workflow_task_notify_gru_cf
(id_task, id_spring_provider, marker_provider_ids, demand_status, crm_status_id, set_onglet, message_guichet, status_text_guichet, sender_name_guichet, subject_guichet, demand_max_step_guichet, demand_user_current_step_guichet, is_active_onglet_guichet, status_text_agent, message_agent, is_active_onglet_agent, subject_email, message_email, sender_name_email, recipients_cc_email, recipients_cci_email, is_active_onglet_email, message_sms, billing_group_sms, billing_account_sms, is_active_onglet_sms, id_mailing_list_broadcast, email_broadcast, sender_name_broadcast, subject_broadcast, message_broadcast, recipients_cc_broadcast, recipients_cci_broadcast, is_active_onglet_broadcast)
VALUES(594, 'notifygru-ticketing.provider-manager.@.ticket', '', 0, 0, 4, NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, 0, -1, '${unit_email!}', 'Mairie de Paris', '[GRU] Relance sollicitation -  ${reference!}', '<p>Bonjour,</p>
<p>Ceci est une relance pour traiter la sollicitation ${reference!}</p>
<p>&nbsp;</p>
<p>Connectez-vous &agrave; l''outil de GRU&nbsp;(<a href="https://ticketing.apps.paris.fr">https://ticketing.apps.paris.fr</a>) pour traiter cette nouvelle demande.</p>', '', '', 1);
