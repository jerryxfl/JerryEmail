package com.edu.cdp.net.websocket.bean;

public class ServerRequest {
    //接受 {"command":"MESSAGE","send":"3","target":"1","content":"你好啊"}
    //返回 {"commend":"SUCCESS","content":"","target":3}
    private String command;
    private String send;
    private String target;
    private String content;

    public ServerRequest() {
    }

    public ServerRequest(String command, String send, String target, String content) {
        this.command = command;
        this.send = send;
        this.target = target;
        this.content = content;
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
