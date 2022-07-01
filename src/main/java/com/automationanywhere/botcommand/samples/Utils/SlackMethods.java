package com.automationanywhere.botcommand.samples.Utils;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatDeleteResponse;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.chat.ChatScheduleMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsCreateResponse;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.model.Message;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SlackMethods {

    public static String postMessage(Slack instance, String token, String channelName, String message){
        String postedMessage = null;
        try {
            ChatPostMessageResponse response = instance.methods(token).chatPostMessage(req -> req
                    .channel(channelName)
                    .text(message));
            if (response.isOk()) {
                postedMessage = response.getMessage().toString();
            } else { throw new BotCommandException(response.getError()); }
        } catch (SlackApiException requestFailure) {
            throw new BotCommandException("Slack API responded with unsuccessful status code. " + requestFailure);
        } catch (IOException connectivityIssue) {
            throw new BotCommandException("Connection issue. Please check your token. " + connectivityIssue);
        }
        return postedMessage;
    }

    public static String deleteMessage(Slack instance, String token, String channelName, String timestamp){
        String postedMessage = null;
        try {
            ChatDeleteResponse response = instance.methods(token).chatDelete(req -> req
                    .channel(channelName)
                    .ts(timestamp));
            if (response.isOk()) {
                postedMessage = "Message Deleted.";
            } else { throw new BotCommandException(response.getError()); }
        } catch (SlackApiException requestFailure) {
            throw new BotCommandException("Slack API responded with unsuccessful status code. " + requestFailure);
        } catch (IOException connectivityIssue) {
            throw new BotCommandException("Connection issue. Please check your token. " + connectivityIssue);
        }
        return postedMessage;
    }

    public static String createChannel(Slack instance, String token, String channelName){
        String createdChannel = null;
        try {
            ConversationsCreateResponse channel = instance.methods(token).conversationsCreate(req -> req
                    .name(channelName));
            if (channel.isOk()) {
                createdChannel = channel.getChannel().getId();
            } else { throw new BotCommandException(channel.getError()); }
        } catch (SlackApiException requestFailure) {
            throw new BotCommandException("Slack API responded with unsuccessful status code. " + requestFailure);
        } catch (IOException connectivityIssue) {
            throw new BotCommandException("Connection issue. Please check your token. " + connectivityIssue);
        }
        return createdChannel;
    }

    public static String scheduleMessage(Slack instance, String token, String channelName, String text, Integer seconds){
        String scheduledMessage;
        Instant now = Instant.now();
        long epochValue = now.getEpochSecond() + seconds;
        int epochInt = (int) epochValue;
        try {
            ChatScheduleMessageResponse response = instance.methods(token).chatScheduleMessage(req -> req
                    .channel(channelName)
                    .postAt(epochInt)
                    .text(text));
            if (response.isOk()) {
                scheduledMessage = response.getMessage().getText();
            } else { throw new BotCommandException(response.getError()); }
        } catch (SlackApiException requestFailure) {
            throw new BotCommandException("Slack API responded with unsuccessful status code. " + requestFailure);
        } catch (IOException connectivityIssue) {
            throw new BotCommandException("Connection issue. Please check your token. " + connectivityIssue);
        }
        return scheduledMessage;
    }

    public static List<Message> conversationHistory(Slack instance, String token, String channelName){
        List<Message> messageList = null;
        try {
            ConversationsHistoryResponse channel = instance.methods(token).conversationsHistory(req -> req
                    .channel(channelName));
            if (channel.isOk()) {
                messageList = channel.getMessages();
            } else { throw new BotCommandException(channel.getError()); }
        } catch (SlackApiException requestFailure) {
            throw new BotCommandException("Slack API responded with unsuccessful status code. " + requestFailure);
        } catch (IOException connectivityIssue) {
            throw new BotCommandException("Connection issue. Please check your token. " + connectivityIssue);
        }
        return messageList;
    }

    public static TableValue messagesToTable (List<Message> messages) {
        Table outputTable = new Table();
        TableValue tableVariable = new TableValue();
        //set Schema for table
        List<Schema> schemas = new LinkedList<>();
        schemas.add(new Schema("Text"));
        schemas.add(new Schema("Channel"));
        schemas.add(new Schema("Timestamp"));
        outputTable.setSchema(schemas);
        //Schema set
        List<List<Value>> messageData = new ArrayList<>();
        List<Row> listRows = new ArrayList<>();

        for (int i=0; i< messages.size(); i++) {
            List<Value> rowValues = new ArrayList<>();
            Message currentMessage = messages.get(i);
            //*****Values for Table********Text - Channel - TS************
            rowValues.add(new StringValue(currentMessage.getText()));
            rowValues.add(new StringValue(currentMessage.getChannel()));
            rowValues.add(new StringValue(currentMessage.getTs()));
            //************************************************************
            Row currentMessageRow = new Row();
            currentMessageRow.setValues(rowValues);
            listRows.add(currentMessageRow);
            //outputTable.setRows(listRows);
        }
        outputTable.setRows(listRows);
        tableVariable.set(outputTable);
        return tableVariable;
    }

    public static TableValue listChannels (Slack instance, String token) {
        Table outputTable = new Table();
        TableValue tableVariable = new TableValue();
        //set Schema for table
        List<Schema> schemas = new LinkedList<>();
        schemas.add(new Schema("Channel Name"));
        schemas.add(new Schema("ID"));
        outputTable.setSchema(schemas);
        //Schema set
        List<List<Value>> channelData = new ArrayList<>();
        List<Row> listRows = new ArrayList<>();
        try {
            ConversationsListResponse response = instance.methods(token).conversationsList(req -> req);
            if (response.isOk()) {
                //prepare table
                    for (int i = 0; i < response.getChannels().size(); i++) {
                        List<Value> channels = new ArrayList<>();
                        channels.add(new StringValue(response.getChannels().get(i).getName()));
                        channels.add(new StringValue(response.getChannels().get(i).getId()));
                        Row currentRow = new Row();
                        currentRow.setValues(channels);
                        listRows.add(currentRow);
                    }
                    outputTable.setRows(listRows);
                }
                else { throw new BotCommandException(response.getError()); }
        } catch (SlackApiException requestFailure) {
            throw new BotCommandException("Slack API responded with unsuccessful status code. " + requestFailure);
        } catch (IOException connectivityIssue) {
            throw new BotCommandException("Connection issue. Please check your token. " + connectivityIssue);
        }
        tableVariable.set(outputTable);
        return tableVariable;
    }
}
