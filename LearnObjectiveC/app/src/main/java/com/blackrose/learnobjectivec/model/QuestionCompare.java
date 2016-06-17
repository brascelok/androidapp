package com.blackrose.learnobjectivec.model;

/**
 * Created by mrnoo on 22/03/2016.
 */
public class QuestionCompare {
    private String comparequestion;
    private String compareanswer1;
    private String compareanswer2;
    private String compareanswer3;

    public QuestionCompare() {
    }

    public QuestionCompare(String comparequestion, String compareanswer1, String compareanswer2, String compareanswer3) {
        this.comparequestion = comparequestion;
        this.compareanswer1 = compareanswer1;
        this.compareanswer2 = compareanswer2;
        this.compareanswer3 = compareanswer3;
    }

    public String getComparequestion() {
        return comparequestion;
    }

    public void setComparequestion(String comparequestion) {
        this.comparequestion = comparequestion;
    }

    public String getCompareanswer1() {
        return compareanswer1;
    }

    public void setCompareanswer1(String compareanswer1) {
        this.compareanswer1 = compareanswer1;
    }

    public String getCompareanswer2() {
        return compareanswer2;
    }

    public void setCompareanswer2(String compareanswer2) {
        this.compareanswer2 = compareanswer2;
    }

    public String getCompareanswer3() {
        return compareanswer3;
    }

    public void setCompareanswer3(String compareanswer3) {
        this.compareanswer3 = compareanswer3;
    }

    @Override
    public String toString() {
        return "QuestionCompare{" +
                "comparequestion='" + comparequestion + '\'' +
                ", compareanswer1='" + compareanswer1 + '\'' +
                ", compareanswer2='" + compareanswer2 + '\'' +
                ", compareanswer3='" + compareanswer3 + '\'' +
                '}';
    }
}
