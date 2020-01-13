package org.xjiop.oxygenaodmod.category;

import android.os.Parcel;
import android.os.Parcelable;

class CategoriesDummy {

    public static class Item implements Parcelable {
        final String name;
        boolean checked;

        Item(String name,
             boolean checked) {
            this.name = name;
            this.checked = checked;
        }

        protected Item(Parcel in) {
            name = in.readString();
            checked = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeByte((byte) (checked ? 1 : 0));
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