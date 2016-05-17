package com.ittekikun.plugin.eewalert;

import twitter4j.*;

public class EEWStream implements UserStreamListener
{
    //public MineTweetConfig mtConfig;

    public EEWStream(EEWAlert eewAlert)
    {
        //this.mtConfig = mtConfig;
    }

    @Override
    public void onStatus(Status status)
    {
        if(status.getUser().getId() == 214358709L && !(status.isRetweet()))
        {
            String array[] = status.getText().split(",", 0);

            EEW eew = new EEW(array);

            NoticeBuilder.noticeEewMessage(eew);
        }
    }

    @Override
    public void onDeletionNotice(long l, long l1)
    {

    }

    @Override
    public void onFriendList(long[] longs)
    {

    }

    @Override
    public void onFavorite(User user, User user1, Status status)
    {

    }

    @Override
    public void onUnfavorite(User user, User user1, Status status)
    {

    }

    @Override
    public void onFollow(User user, User user1)
    {

    }

    @Override
    public void onUnfollow(User user, User user1)
    {

    }

    @Override
    public void onDirectMessage(DirectMessage directMessage)
    {

    }

    @Override
    public void onUserListMemberAddition(User user, User user1, UserList userList)
    {

    }

    @Override
    public void onUserListMemberDeletion(User user, User user1, UserList userList)
    {

    }

    @Override
    public void onUserListSubscription(User user, User user1, UserList userList)
    {

    }

    @Override
    public void onUserListUnsubscription(User user, User user1, UserList userList)
    {

    }

    @Override
    public void onUserListCreation(User user, UserList userList)
    {

    }

    @Override
    public void onUserListUpdate(User user, UserList userList)
    {

    }

    @Override
    public void onUserListDeletion(User user, UserList userList)
    {

    }

    @Override
    public void onUserProfileUpdate(User user)
    {

    }

    @Override
    public void onUserSuspension(long l)
    {

    }

    @Override
    public void onUserDeletion(long l)
    {

    }

    @Override
    public void onBlock(User user, User user1)
    {

    }

    @Override
    public void onUnblock(User user, User user1)
    {

    }

    @Override
    public void onRetweetedRetweet(User user, User user1, Status status)
    {

    }

    @Override
    public void onFavoritedRetweet(User user, User user1, Status status)
    {

    }

    @Override
    public void onQuotedTweet(User user, User user1, Status status)
    {

    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice)
    {

    }

    @Override
    public void onTrackLimitationNotice(int i)
    {

    }

    @Override
    public void onScrubGeo(long l, long l1)
    {

    }

    @Override
    public void onStallWarning(StallWarning stallWarning)
    {

    }

    @Override
    public void onException(Exception e)
    {

    }
}