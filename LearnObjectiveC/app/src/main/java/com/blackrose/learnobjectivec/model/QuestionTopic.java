package com.blackrose.learnobjectivec.model;

/**
 * Created by mrnoo on 16/03/2016.
 */
public class QuestionTopic {
    private String topic;
    private String topicquestion;
    private String topicanswer;

    public QuestionTopic() {
    }

    public QuestionTopic(String topic, String topicquestion, String topicanswer) {
        this.topic = topic;
        this.topicquestion = topicquestion;
        this.topicanswer = topicanswer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopicquestion() {
        return topicquestion;
    }

    public void setTopicquestion(String topicquestion) {
        this.topicquestion = topicquestion;
    }

    public String getTopicanswer() {
        return topicanswer;
    }

    public void setTopicanswer(String topicanswer) {
        this.topicanswer = topicanswer;
    }

    @Override
    public String toString() {
        return "QuestionTopic{" +
                "topic='" + topic + '\'' +
                ", topicquestion='" + topicquestion + '\'' +
                ", topicanswer='" + topicanswer + '\'' +
                '}';
    }
}
