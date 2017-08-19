package com.xdja.contact.http.response.department;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hkb.
 * 2015/7/10/0010.
 */
public class UpdateMemberResponse implements Parcelable {

    private int hasMore;//是否有更多；1-有；2-无

    private String personLastUpdateId;//人员最后一次更新标识

    private ServerMember[] persons;//更新到的部门信息列表

    public boolean getHasMore() {
        return hasMore == 1;
    }

    public void setHasMore(int hasMore) {
        this.hasMore = hasMore;
    }

    public String getPersonLastUpdateId() {
        return personLastUpdateId;
    }

    public void setPersonLastUpdateId(String personLastUpdateId) {
        this.personLastUpdateId = personLastUpdateId;
    }

    public ServerMember[] getPersons() {
        return persons;
    }

    public void setPersons(ServerMember[] persons) {
        this.persons = persons;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.hasMore);
        dest.writeString(this.personLastUpdateId);
        dest.writeParcelableArray(this.persons, 0);
    }

    public UpdateMemberResponse() {
    }

    protected UpdateMemberResponse(Parcel in) {
        this.hasMore = in.readInt();
        this.personLastUpdateId = in.readString();
        this.persons = (ServerMember[]) in.readParcelableArray(ServerMember.class.getClassLoader());
    }

    public static final Parcelable.Creator<UpdateMemberResponse> CREATOR = new Parcelable.Creator<UpdateMemberResponse>() {
        public UpdateMemberResponse createFromParcel(Parcel source) {
            return new UpdateMemberResponse(source);
        }

        public UpdateMemberResponse[] newArray(int size) {
            return new UpdateMemberResponse[size];
        }
    };
}
