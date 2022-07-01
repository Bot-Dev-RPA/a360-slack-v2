/*
 * Copyright (c) 2020 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */

/**
 * @author James Dickson
 */
package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.samples.Utils.SlackMethods;
import com.automationanywhere.botcommand.samples.Utils.SlackServer;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.slack.api.Slack;
import com.slack.api.model.Message;
import com.slack.api.model.event.MessageChangedEvent;

import java.util.List;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXTAREA;
import static com.automationanywhere.commandsdk.model.DataType.STRING;
import static com.automationanywhere.commandsdk.model.DataType.TABLE;

/**
 * @author James Dickson
 */
@BotCommand
@CommandPkg(label = "Conversation History",
		description = "Get conversation history from a channel in Slack",
		icon = "SLACK.svg",
		name = "conversationHistory",
		node_label = "Get conversation history from channel {{channel}} in session {{sessionName}}",
		group_label="Conversations",
		comment = true,
		return_label = "Assign result to a Table variable", return_type = TABLE
		)
public class ConversationHistory {

	@Sessions
	private Map<String, Object> sessionMap;

	@Execute
	public TableValue execute(
			@Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default") @NotEmpty String sessionName,
			@Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Channel Name", description = "e.g. #random") @NotEmpty String channel
	) {
		SlackServer slackObject = (SlackServer) this.sessionMap.get(sessionName);
		String token = slackObject.getToken();
		Slack instance = slackObject.getInstance();
		List<Message> messages = SlackMethods.conversationHistory(instance, token, channel);
		return SlackMethods.messagesToTable(messages);
	}

	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

}
