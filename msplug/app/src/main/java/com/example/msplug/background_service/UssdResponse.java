package com.example.msplug.background_service;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * Represents the Ussd response, including
 * the message and the return code.
 * @hide
 */
public final class UssdResponse implements Parcelable {
    private CharSequence mReturnMessage;
    private String mUssdRequest;


    /**
     * Implement the Parcelable interface
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUssdRequest);
        TextUtils.writeToParcel(mReturnMessage, dest, 0);
    }

    public String getUssdRequest() {
        return mUssdRequest;
    }

    public CharSequence getReturnMessage() {
        return mReturnMessage;
    }

    /**
     * Implement the Parcelable interface
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * * Initialize the object from the request and return message.
     */
    public UssdResponse(String ussdRequest, CharSequence returnMessage) {
        mUssdRequest = ussdRequest;
        mReturnMessage = returnMessage;
    }

    public static final @NonNull Parcelable.Creator<UssdResponse> CREATOR = new Creator<UssdResponse>() {

        @Override
        public UssdResponse createFromParcel(Parcel in) {
            String request = in.readString();
            CharSequence message = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            return new UssdResponse(request, message);
        }

        @Override
        public UssdResponse[] newArray(int size) {
            return new UssdResponse[size];
        }
    };
}
