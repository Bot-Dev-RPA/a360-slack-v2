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
import com.automationanywhere.commandsdk.model.AttributeType;

import javax.swing.*;
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
@CommandPkg(label = "Upload File",
		description = "Upload file to channel(s) in Slack",
		icon = "SLACK.svg",
		name = "fileUpload",
		node_label = "Upload file to channels in session {{sessionName}}",
		group_label="Files",
		comment = true,
		return_label = "Assign result to a string variable", return_type = STRING
		)
public class FileUpload {

	@Sessions
	private Map<String, Object> sessionMap;

	@Execute
	public StringValue execute(
			@Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default")
			@NotEmpty String sessionName,
			@Idx(index = "2", type = TEXT) @Pkg(label="Channels",
					description = "separate multiple channels with a comma e.g. 'C20230303,C4045050'")
			@NotEmpty String channels,
			@Idx(index = "3", type = AttributeType.TEXT) @Pkg(label = "File Name", description = "e.g. random file")
			@NotEmpty String fileName,
			@Idx(index = "4", type = AttributeType.TEXT) @Pkg(label = "Comment", description = "Comment to include with file post")
			@NotEmpty String comment,
			@Idx(index = "5", type = AttributeType.FILE) @Pkg(label = "File")
			@NotEmpty String filePath
	) {
		List<String> channelsList = Arrays.asList(channels.split("\\s*,\\s*"));
		SlackServer slackObject = (SlackServer) this.sessionMap.get(sessionName);
		SlackMethods slack = new SlackMethods()
				.setInstance(slackObject.slack)
				.setToken(slackObject.token);
		return new StringValue(slack.fileUpload(fileName, comment, filePath, channelsList));
	}

	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

}
