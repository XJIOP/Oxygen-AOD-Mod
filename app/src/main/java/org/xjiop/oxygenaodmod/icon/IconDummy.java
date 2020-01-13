package org.xjiop.oxygenaodmod.icon;

import android.os.Parcel;
import android.os.Parcelable;

class IconDummy {

    public static class Item implements Parcelable {
        final int id;
        final String icon;

        Item(int id,
             String name) {
            this.id = id;
            this.icon = name;
        }

        protected Item(Parcel in) {
            id = in.readInt();
            icon = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(icon);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Item> CREATOR = new Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
    }
}