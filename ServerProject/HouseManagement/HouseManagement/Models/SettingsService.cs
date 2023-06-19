using System.ComponentModel.DataAnnotations.Schema;
using Newtonsoft.Json;

namespace HouseManagement.Models
{
    public class SettingsService
    {
        public int id { get; set; }
        public bool paymentStatus { get; set; }
        public bool? typeIMD { get; set; }
        public double valueRate { get; set; }
        public double? valueNormative { get; set; }
        public bool haveCounter { get; set; }
        public int? startDateTransfer { get; set; }
        public int? endDateTransfer { get; set; }
        public string? paymentPeriod { get; set; }
        [ForeignKey("service")]
        public int idService { get; set; }
        [JsonIgnore]
        [ForeignKey("idService")]
        public Service? service { get; set; }
        [ForeignKey("house")]
        public int idHouse { get; set; }
        [JsonIgnore]
        [ForeignKey("idHouse")]
        public House? house { get; set; }
    }
}
