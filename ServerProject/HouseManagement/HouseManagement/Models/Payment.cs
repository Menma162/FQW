using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Newtonsoft.Json;

namespace HouseManagement.Models
{
    public class Payment
    {
        public int id { get; set; }
        public string period { get; set; }
        public double amount { get; set; }
        public double penalties { get; set; }
        [ForeignKey("service")]
        public int idService { get; set; }
        [JsonIgnore]
        [ForeignKey("idService")]
        public Service? service { get; set; }
        [ForeignKey("flat")]
        public int idFlat { get; set; }
        [JsonIgnore]
        [ForeignKey("idFlat")]
        public Flat? flat { get; set; }
    }
}
