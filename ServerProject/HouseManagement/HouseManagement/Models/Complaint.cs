using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations.Schema;

namespace HouseManagement.Models
{
    public class Complaint
    {
        public int id { get; set; }
        public string status { get; set; }
        public string description { get; set; }
        public string? photo { get; set; }
        public DateTime date { get; set; }
        [ForeignKey("flat")]
        public int idFlat { get; set; }
        [JsonIgnore]
        [ForeignKey("idFlat")]
        public Flat? flat { get; set; }
    }
}
