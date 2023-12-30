package com.example.to_donote;

public class task_card {
    private boolean checked = false;
    private String dateTime;
    private String description;
    private String title;

    public task_card(String title2, String description2, String dateTime2) {
        this.title = title2;
        this.description = description2;
        this.dateTime = dateTime2;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title2) {
        this.title = title2;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String dateTime2) {
        this.dateTime = dateTime2;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description2) {
        this.description = description2;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked2) {
        this.checked = checked2;
    }
}
