package com.automationanywhere.botcommand.samples.Utils;

import com.slack.api.Slack;

public class SlackServer {
    public String token;
    public Slack slack;


    public String getToken() {
        return this.token;
    }
    public Slack getInstance() { return this.slack; }





    public SlackServer(String token, Slack instance){
        this.token = token;
        this.slack = instance;
    }
}