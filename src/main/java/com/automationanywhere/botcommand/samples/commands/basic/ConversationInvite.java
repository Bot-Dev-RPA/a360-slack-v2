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
import com.automationanywhere.botcommand.samples.Utils.SlackMethods;
import com.automationanywhere.botcommand.samples.Utils.SlackServer;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.slack.api.model.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;
import static com.automationanywhere.commandsdk.model.DataType.TABLE;

/**
 * @author James Dickson
 */
@BotCommand
@CommandPkg(label = "Conversation Invite",
		description = "Invite users to channel in Slack",
		icon = "SLACK.svg",
		name = "conversationInvite",
		node_label = "Invite users to channel {{channel}} in session {{sessionName}}",
		group_label="Conversations",
		comment = true,
		return_label = "Assign result to a Table variable", return_type = TABLE
		)
public class ConversationInvite {

	@Sessions
	private Map<String, Object> sessionMap;

	@Execute
	public StringValue execute(
			@Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default")
			@NotEmpty String sessionName,
			@Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Channel ID", description = "e.g. #C03N2KMB9K7")
			@NotEmpty String channel,
			@Idx(index = "3", type = TEXT) @Pkg(label="Users",
					description = "separate multiple users with a comma e.g. 'C20230303,C4045050'")
			@NotEmpty String users
	) {
		List<String> userList = Arrays.asList(users.split("\\s*,\\s*"));
		SlackServer slackObject = (SlackServer) this.sessionMap.get(sessionName);
		SlackMethods slack = new SlackMethods()
				.setInstance(slackObject.slack)
				.setToken(slackObject.token);
		return new StringValue(slack.inviteUsers(channel, userList));
	}

	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

}
