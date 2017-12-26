package com.simpletour.library.caocao;

import android.support.annotation.NonNull;

import com.simpletour.library.caocao.base.IAGCallback;
import com.simpletour.library.caocao.base.IAGConversation;
import com.simpletour.library.caocao.base.IAGMessage;
import com.simpletour.library.caocao.base.IAGMember;

import java.util.List;

/**
 * 包名：com.againstalone.lib.imkit
 * 描述：会话实体类
 * 创建者：yankebin
 * 日期：2017/5/18
 */
public class AGConversation implements IAGConversation {
    private int mType;
    private String mCid;
    private IAGConversation.ConversationStatus mStatus;
    private long mCreatedAt;
    IAGMessage mLatestMessage;
    int mUnReadCount;
    long mLastModify;
    String mName;
    String mDesc;
    String  mIcon;
    String draftMessage;
    int totalMembers;
    boolean mHasUnReadAtMessage;
    int mMemberLimit;
    int mGroubLevel;
    boolean mOnlyOwnerModifiable;
    boolean mEnableNotify;
    boolean mEnableNotifySound;
    boolean mHasAt;
    long mOwner;
    AGConversation mConversation;
    private transient boolean mIsSynced;
    private transient boolean isLoaded;

    public AGConversation(){
        mStatus=ConversationStatus.NORMAL;
        mEnableNotify=true;
        mEnableNotifySound=true;
        mIsSynced=true;
        mHasAt=false;
        mCid="aafafafafaafafafafa";
    }

    public final synchronized void sync(){
        if (!mIsSynced){
            if (null==mConversation){
                if (!isLoaded){
                    if (null==mConversation){
                        isLoaded=true;

                    }
                }
            }
        }
    }

    @Override
    public String conversationId() {
        return mCid;
    }

    @Override
    public int type() {
        return mType;
    }

    @Override
    public boolean enableNotify() {
        return mEnableNotify;
    }

    @Override
    public boolean enableNotifySound() {
        return mEnableNotifySound;
    }

    @Override
    public void setEnableNotify(boolean enableNotify, IAGCallback<Void> callback) {

    }

    @Override
    public void setEnableNotifySound(boolean enableNotifySound, IAGCallback<Void> callback) {

    }

    @Override
    public long createdAt() {
        return mCreatedAt;
    }

    @Override
    public long getLastModify() {
        return mLastModify;
    }

    @Override
    public int unReadMessageCount() {
        return mUnReadCount;
    }

    @Override
    public void getMembers(IAGCallback<List<IAGMember>> callback) {

    }

    @Override
    public String conversationName() {
        return mName;
    }

    @Override
    public String conversationDesc() {
        return mDesc;
    }

    @Override
    public int totalMembers() {
        return totalMembers;
    }

    @Override
    public long owner() {
        return mOwner;
    }

    @Override
    public void messageList() {

    }

    @Override
    public List<IAGMessage> localMessageList() {
        return null;
    }

    @Override
    public List<IAGMember> managers() {
        return null;
    }

    @Override
    public IAGMessage lastMessage() {
        return mLatestMessage;
    }

    @Override
    public String icon() {
        return null;
    }

    @Override
    public int getGroupLevel() {
        return 0;
    }

    @Override
    public void updateGrouLevel(int level, IAGCallback<Void> callback) {

    }

    @Override
    public int getMemberLimit() {
        return 0;
    }

    @Override
    public void updateMemberLimit(int limit, IAGCallback<Void> callback) {

    }

    @Override
    public void updateName(String name, IAGMessage message, IAGCallback<Void> callback) {

    }

    @Override
    public void updateWoner(long ownerId, IAGMessage message, IAGCallback<Void> callback) {

    }

    @Override
    public boolean getOnlyOwnerModifiable() {
        return false;
    }

    @Override
    public void setOnlyOwnerModifiable(boolean OnlyOwnerModifiable, IAGMessage message, IAGCallback<Void> callback) {

    }

    @Override
    public void updateIcon(String icon, IAGMessage message, IAGCallback<Void> callback) {

    }

    @Override
    public void quitSlient(IAGMessage message, IAGCallback<Void> callback) {

    }

    @Override
    public void addUnreadCount(int count) {

    }

    @Override
    public void updateDesc(String desc, IAGMessage message, IAGCallback<Void> callback) {

    }

    @Override
    public void resetUnreadCount() {

    }

    @Override
    public void getMessage(long messageId, IAGCallback<IAGMessage> callback) {

    }

    @Override
    public void clear(IAGCallback<Void> callback) {

    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public ConversationStatus status() {
        return mStatus;
    }

    @Override
    public int compareTo(@NonNull IAGConversation o) {
        return 0;
    }
}
