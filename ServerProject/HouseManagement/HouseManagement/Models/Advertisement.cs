using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace HouseManagement.Models
{
    public class Advertisement
    {
        public int id { get; set; }
        public DateTime date { get; set; }
        public string description { get; set; }

        [ForeignKey("house")]
        public int idHouse { get; set; }
        [JsonIgnore]
        [ForeignKey("idHouse")]
        public House? house { get; set; }
    }
}
