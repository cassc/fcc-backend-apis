drop table if exists `comment`;

CREATE TABLE `comment` (
`id` integer primary key,
`host` text not null,
`uri` text not null,
`email` text not null,
`nickname` text not null,
`content` text not null,
`ip` text not null
);

