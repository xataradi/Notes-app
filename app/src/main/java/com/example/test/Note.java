package com.example.test;

import android.os.Parcel;
import android.os.Parcelable;

public class Note  implements Parcelable {
    private int Id;
    private String Title;
    private String Contents;
    private int Images;
    private String Timereminder;
    private String Timecreate;
    private int Color;


    protected Note(Parcel in) {
        Id = in.readInt();
        Title = in.readString();
        Contents = in.readString();
        Images = in.readInt();
        Timereminder = in.readString();
        Timecreate = in.readString();
        Color = in.readInt();
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public int getImages() {
        return Images;
    }

    public void setImages(int images) {
        Images = images;
    }

    public String getTimereminder() {
        return Timereminder;
    }

    public void setTimereminder(String timereminder) {
        Timereminder = timereminder;
    }

    public String getTimecreate() {
        return Timecreate;
    }

    public void setTimecreate(String timecreate) {
        Timecreate = timecreate;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public static Creator<Note> getCREATOR() {
        return CREATOR;
    }

    public Note(int id, String title, String contents, int images, String timereminder, String timecreate, int color) {
        Id = id;
        Title = title;
        Contents = contents;
        Images = images;
        Timereminder = timereminder;
        Timecreate = timecreate;
        Color = color;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Title);
        dest.writeString(Contents);
        dest.writeInt(Images);
        dest.writeString(Timereminder);
        dest.writeString(Timecreate);
        dest.writeInt(Color);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
