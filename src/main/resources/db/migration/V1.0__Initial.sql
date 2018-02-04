create table location.location (
        location_uuid uuid not null,
        date_time_at timestamp with time zone,
        icao_code varchar(255),
        latitude varchar(6),
        longitude varchar(7),
        name varchar(255),
        post_code varchar(255),
        user_uuid uuid not null,
        primary key (location_uuid)
    )