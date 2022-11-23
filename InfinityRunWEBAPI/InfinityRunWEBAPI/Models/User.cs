using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;
using Newtonsoft.Json;

namespace InfinityRunWEBAPI.Models
{
    public class User
    {
        [BsonElement("_id")]
        [JsonProperty("_id")]
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string? Id { get; set; }

        [BsonElement("Userame")]
        public string? username { get; set; }

        [BsonElement("Mail")]
        public string? mail { get; set; }

        [BsonElement("Password")]
        public string? password { get; set; }
    }   
}   
