﻿using Microsoft.Extensions.Options;
using MongoDB.Driver;
using InfinityRunWEBAPI.Models;
using System.Text;

namespace InfinityRunWEBAPI.Services
{
    public class UserService
    {
        private readonly IMongoCollection<User> _usersCollection;

        public UserService(
            IOptions<InfinityRunDatabaseSettings> infinityRunDatabaseSettings)
        {
            var mongoClient = new MongoClient(
               infinityRunDatabaseSettings.Value.ConnectionString);

            var mongoDatabase = mongoClient.GetDatabase(
                infinityRunDatabaseSettings.Value.DatabaseName);

            _usersCollection = mongoDatabase.GetCollection<User>(
                infinityRunDatabaseSettings.Value.UsersCollectionName);
        }

        public async Task<List<User>> GetAsync() =>
            await _usersCollection.Find(_ => true).ToListAsync();

        public async Task<User?> GetAsync(string id) =>
            await _usersCollection.Find(x => x.Id == id).FirstOrDefaultAsync();

        public async Task CreateAsync(User newUser)
        {
            PasswordHash hash = new PasswordHash(newUser.password);
            newUser.password = Encoding.UTF8.GetString(hash.Hash);

            await _usersCollection.InsertOneAsync(newUser);
        }
            
        public async Task UpdateAsync(string id, User updatedUser) =>
            await _usersCollection.ReplaceOneAsync(x => x.Id == id, updatedUser);

        public async Task RemoveAsync(string id) =>
            await _usersCollection.DeleteOneAsync(x => x.Id == id);
    }
}
