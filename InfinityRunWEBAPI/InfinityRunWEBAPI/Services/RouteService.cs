using Microsoft.Extensions.Options;
using MongoDB.Driver;
using InfinityRunWEBAPI.Models;

namespace InfinityRunWEBAPI.Services
{
    public class RouteService
    {
        private readonly IMongoCollection<Models.Route> _routesCollection;
        
        public RouteService(
            IOptions<InfinityRunDatabaseSettings> infinityRunDatabaseSettings)
        {
            var mongoClient = new MongoClient(
               infinityRunDatabaseSettings.Value.ConnectionString);

            var mongoDatabase = mongoClient.GetDatabase(
                infinityRunDatabaseSettings.Value.DatabaseName);

            _routesCollection = mongoDatabase.GetCollection<Models.Route>(
                infinityRunDatabaseSettings.Value.RoutesCollectionName);
        }

        public async Task<List<Models.Route>> GetAsync() =>
            await _routesCollection.Find(_ => true).ToListAsync();

        public async Task<Models.Route?> GetAsync(string id) =>
            await _routesCollection.Find(x => x.Id == id).FirstOrDefaultAsync();
        
        public async Task CreateAsync(Models.Route newRoute) =>
            await _routesCollection.InsertOneAsync(newRoute);

        public async Task RemoveAsync(string id) =>
            await _routesCollection.DeleteOneAsync(x => x.Id == id);
    }
}
