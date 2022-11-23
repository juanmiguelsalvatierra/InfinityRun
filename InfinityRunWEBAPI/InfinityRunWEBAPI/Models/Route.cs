using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;
using Newtonsoft.Json;

namespace InfinityRunWEBAPI.Models
{
    public class Route
    {
        [BsonElement("_id")]
        [JsonProperty("_id")]
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string? Id { get; set; }
        
        [BsonElement("userId")]
        public string? userId { get; set; }

        [BsonElement("name")]
        public string? name { get; set; }
        
        [BsonElement("routepoints")]
        [BsonRepresentation(BsonType.Array)]
        public double[,]? routepoints { get; set; }
    }
}
