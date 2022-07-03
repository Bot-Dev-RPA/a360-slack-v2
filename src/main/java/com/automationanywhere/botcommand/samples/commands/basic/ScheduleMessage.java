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
import com.automationanywhere.botcommand.samples.Utils.SlackMethods;
import com.automationanywhere.botcommand.samples.Utils.SlackServer;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.NumberInteger;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.slack.api.Slack;

import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.*;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

/**
 * @author James Dickson
 */
@BotCommand
@CommandPkg(label = "Schedule Message",
		description = "Schedules a message to a channel in Slack",
		icon = "SLACK.svg",
		name = "scheduleMessage",
		node_label = "Schedule Message to channel {{channel}} in session {{sessionName}}",
		group_label="Messages",
		comment = true,
		return_label = "Assign result to a String variable", return_type = STRING
		)
public class ScheduleMessage {

	@Sessions
	private Map<String, Object> sessionMap;

	@Execute
	public StringValue execute(
			@Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default")
			@NotEmpty String sessionName,
			@Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Channel Name", description = "e.g. #random")
			@NotEmpty String channel,
			@Idx(index = "3", type = TEXTAREA) @Pkg(label = "Text for message", description = "e.g. Welcome to the channel")
			@NotEmpty String text,
			@Idx(index = "4", type = NUMBER) @Pkg(label = "Number of seconds in the future")
			@NumberInteger
			@NotEmpty Double seconds
	) {
		SlackServer slackObject = (SlackServer) this.sessionMap.get(sessionName);
		return new StringValue(SlackMethods.scheduleMessage(slackObject.slack, slackObject.token, channel, text, seconds.intValue()));
	}
	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}
}
