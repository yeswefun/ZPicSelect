package com.z.zpicselect;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 功能描述：
 * Created by 孙中宛 on 2018/5/28.
 */

public class PicEntity implements Parcelable {

    public String path;
    public String name;
    public long time;

    public PicEntity(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    protected PicEntity(Parcel in) {
        path = in.readString();
        name = in.readString();
        time = in.readLong();
    }

    public static final Creator<PicEntity> CREATOR = new Creator<PicEntity>() {
        @Override
        public PicEntity createFromParcel(Parcel in) {
            return new PicEntity(in);
        }

        @Override
        public PicEntity[] newArray(int size) {
            return new PicEntity[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PicEntity) {
            PicEntity compare = (PicEntity) obj;
            return TextUtils.equals(this.path, compare.path);
        }
        return false;
    }

    @Override
    public String toString() {
        return "PicEntity{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeLong(time);
    }
}
