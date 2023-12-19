db.createUser({
    user: process.env.MONGO_INITDB_LIQUIBASE_USERNAME,
    pwd: process.env.MONGO_INITDB_LIQUIBASE_PASSWORD,
    roles: [
        {
            role: 'dbOwner',
            db: process.env.MONGO_INITDB_DATABASE,
        },
    ],
});

db.createUser({
    user: process.env.MONGO_INITDB_APPLICATION_USERNAME,
    pwd: process.env.MONGO_INITDB_APPLICATION_PASSWORD,
    roles: [
        {
            role: 'readWrite',
            db: process.env.MONGO_INITDB_DATABASE,
        },
    ],
});

db.createCollection('faq');
