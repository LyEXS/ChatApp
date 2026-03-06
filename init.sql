
create table Conversation
(
    id_conversation int auto_increment
        primary key,
    nom             varchar(100)         null,
    est_groupe      tinyint(1) default 0 null
);

create table Utilisateur
(
    id_utilisateur varchar(36)  not null
        primary key,
    username       varchar(50)  not null,
    password       varchar(255) not null,
    constraint username
        unique (username)
);

create table Message
(
    id_message      varchar(36)                         not null
        primary key,
    id_conversation varchar(36)                         not null,
    id_utilisateur  varchar(36)                         not null,
    content         text                                not null,
    date_time       timestamp default CURRENT_TIMESTAMP null,
    constraint Message_ibfk_1
        foreign key (id_conversation) references Conversation (id_conversation)
            on delete cascade,
    constraint Message_ibfk_2
        foreign key (id_utilisateur) references Utilisateur (id_utilisateur)
            on delete cascade
);

create index id_conversation
    on Message (id_conversation);

create index id_utilisateur
    on Message (id_utilisateur);

create table Utilisateur_2
(
    id_utilisateur varchar(36)  null,
    username       varchar(50)  null,
    password       varchar(255) null
);

create table Utilisateur_Conversation
(
    id_utilisateur  int not null,
    id_conversation int not null,
    primary key (id_utilisateur, id_conversation),
    constraint Utilisateur_Conversation_ibfk_1
        foreign key (id_utilisateur) references Utilisateur (id_utilisateur)
            on delete cascade,
    constraint Utilisateur_Conversation_ibfk_2
        foreign key (id_conversation) references Conversation (id_conversation)
            on delete cascade
);

create index id_conversation
    on Utilisateur_Conversation (id_conversation);

