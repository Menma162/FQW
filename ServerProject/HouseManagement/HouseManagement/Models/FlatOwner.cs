using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Xml.Linq;

namespace HouseManagement.Models
{
    public class FlatOwner
    {
        public int id { get; set; }
        public string fullName { get; set; }
        public string? email { get; set; }
        public string phoneNumber { get; set; }
        [ForeignKey("house")]
        public int idHouse { get; set; }
        [JsonIgnore]
        [ForeignKey("idHouse")]
        public House? house { get; set; }
    }
}
