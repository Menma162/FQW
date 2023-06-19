using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Newtonsoft.Json;

namespace HouseManagement.Models
{
    public class Indication
    {
        public int id { get; set; }
        public DateTime dateTransfer { get; set; }
        public int value { get; set; }
        [ForeignKey("counter")]
        public int idCounter { get; set; }
        [JsonIgnore]
        [ForeignKey("idCounter")]
        public Counter? counter { get; set; }
    }
}
