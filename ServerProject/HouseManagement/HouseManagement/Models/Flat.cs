using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Xml.Linq;
using Newtonsoft.Json;

namespace HouseManagement.Models
{
    public class Flat
    {
        public int id { get; set; }
        public string personalAccount { get; set; }
        public string flatNumber { get; set; }
        public double totalArea { get; set; }
        public double usableArea { get; set; }
        public int entranceNumber { get; set; }
        public int numberOfRooms { get; set; }
        public int numberOfRegisteredResidents { get; set; }
        public int numberOfOwners { get; set; }
        [ForeignKey("house")]
        public int? idHouse { get; set; }
        [JsonIgnore]
        [ForeignKey("idHouse")]
        public House? house { get; set; }
    }
}
