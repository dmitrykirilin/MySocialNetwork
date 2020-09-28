create table user_subscriptions(
    channel_id int4 not null references users,
    subscriber_id int4 not null references users,
    primary key (channel_id, subscriber_id)
)