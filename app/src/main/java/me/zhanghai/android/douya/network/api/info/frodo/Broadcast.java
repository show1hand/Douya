/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.ClipboardCopyable;
import me.zhanghai.android.douya.util.GsonHelper;

public class Broadcast implements ClipboardCopyable, Parcelable {

    @SerializedName("activity")
    public String action;

    @SerializedName("ad_info")
    public BroadcastAdInfo adInfo;

    public SimpleUser author;

    @SerializedName("card")
    public BroadcastAttachment attachment;

    @SerializedName("comments_count")
    public int commentCount;

    @SerializedName("create_time")
    public String createdAt;

    public ArrayList<TextEntity> entities = new ArrayList<>();

    @SerializedName("forbid_reshare_and_comment")
    public boolean isRebroadcastAndCommentForbidden;

    public long id;

    public ArrayList<SizedImage> images = new ArrayList<>();

    @SerializedName("is_status_ad")
    public boolean isStatusAd;

    @SerializedName("is_subscription")
    public boolean isSubscription;

    @SerializedName("like_count")
    public int likeCount;

    @SerializedName("liked")
    public boolean isLiked;

    @SerializedName("parent_id")
    public String parentBroadcastId;

    @SerializedName("parent_status")
    public Broadcast parentBroadcast;

    @SerializedName("reshare_id")
    public String rebroadcastId;

    @SerializedName("reshared_status")
    public Broadcast rebroadcastedBroadcast;

    @SerializedName("reshares_count")
    public int rebroadcastCount;

    @SerializedName("sharing_url")
    public String shareUrl;

    @SerializedName("subscription_text")
    public String subscriptionText;

    public String text;

    public String uri;

    public String getRebroadcastedBy(Context context) {
        return context.getString(
                R.string.broadcast_rebroadcasted_by_format, author.name);
    }

    public boolean isAuthorOneself() {
        return author != null && author.isOneself();
    }

    public CharSequence getTextWithEntities() {
        return TextEntity.applyEntities(text, entities);
    }

    public String getLikeCountString() {
        return likeCount == 0 ? null : String.valueOf(likeCount);
    }

    public void fixLiked(boolean liked) {
        if (isLiked != liked) {
            isLiked = liked;
            if (isLiked) {
                ++likeCount;
            } else {
                --likeCount;
            }
        }
    }

    public String getRebroadcastCountString() {
        return rebroadcastCount == 0 ? null : String.valueOf(rebroadcastCount);
    }

    public void incrementRebroadcastCount() {
        ++rebroadcastCount;
    }

    public String getCommentCountString() {
        return commentCount == 0 ? null : String.valueOf(commentCount);
    }

    public boolean canComment() {
        // TODO: Frodo
        return !isRebroadcastAndCommentForbidden;
    }

    @Override
    public String getClipboardLabel() {
        return author.name;
    }

    @Override
    public String getClipboardText() {
        return getTextWithEntities().toString();
    }

    public static String makeTransitionName(long id) {
        return "broadcast-" + id;
    }

    public String makeTransitionName() {
        return makeTransitionName(id);
    }

    private void fixAction() {
        if (TextUtils.isEmpty(action)) {
            action = "说";
        } else {
            action = action.replaceAll("分享", "推荐");
        }
    }

    private void fix() {
        fixAction();
    }


    public static class Deserializer implements JsonDeserializer<Broadcast> {

        @Override
        public Broadcast deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
            Broadcast broadcast = GsonHelper.GSON.fromJson(json, typeOfT);
            broadcast.fix();
            if (broadcast.parentBroadcast != null) {
                broadcast.parentBroadcast.fix();
            }
            if (broadcast.rebroadcastedBroadcast != null) {
                broadcast.rebroadcastedBroadcast.fix();
            }
            return broadcast;
        }
    }


    public static final Creator<Broadcast> CREATOR = new Creator<Broadcast>() {
        @Override
        public Broadcast createFromParcel(Parcel source) {
            return new Broadcast(source);
        }
        @Override
        public Broadcast[] newArray(int size) {
            return new Broadcast[size];
        }
    };

    public Broadcast() {}

    protected Broadcast(Parcel in) {
        action = in.readString();
        adInfo = in.readParcelable(BroadcastAdInfo.class.getClassLoader());
        author = in.readParcelable(SimpleUser.class.getClassLoader());
        attachment = in.readParcelable(BroadcastAttachment.class.getClassLoader());
        commentCount = in.readInt();
        createdAt = in.readString();
        entities = in.createTypedArrayList(TextEntity.CREATOR);
        isRebroadcastAndCommentForbidden = in.readByte() != 0;
        id = in.readLong();
        images = in.createTypedArrayList(SizedImage.CREATOR);
        isStatusAd = in.readByte() != 0;
        isSubscription = in.readByte() != 0;
        likeCount = in.readInt();
        isLiked = in.readByte() != 0;
        parentBroadcastId = in.readString();
        parentBroadcast = in.readParcelable(Broadcast.class.getClassLoader());
        rebroadcastId = in.readString();
        rebroadcastedBroadcast = in.readParcelable(Broadcast.class.getClassLoader());
        rebroadcastCount = in.readInt();
        shareUrl = in.readString();
        subscriptionText = in.readString();
        text = in.readString();
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeParcelable(adInfo, flags);
        dest.writeParcelable(author, flags);
        dest.writeParcelable(attachment, flags);
        dest.writeInt(commentCount);
        dest.writeString(createdAt);
        dest.writeTypedList(entities);
        dest.writeByte(isRebroadcastAndCommentForbidden ? (byte) 1 : (byte) 0);
        dest.writeLong(id);
        dest.writeTypedList(images);
        dest.writeByte(isStatusAd ? (byte) 1 : (byte) 0);
        dest.writeByte(isSubscription ? (byte) 1 : (byte) 0);
        dest.writeInt(likeCount);
        dest.writeByte(isLiked ? (byte) 1 : (byte) 0);
        dest.writeString(parentBroadcastId);
        dest.writeParcelable(parentBroadcast, flags);
        dest.writeString(rebroadcastId);
        dest.writeParcelable(rebroadcastedBroadcast, flags);
        dest.writeInt(rebroadcastCount);
        dest.writeString(shareUrl);
        dest.writeString(subscriptionText);
        dest.writeString(text);
        dest.writeString(uri);
    }
}
