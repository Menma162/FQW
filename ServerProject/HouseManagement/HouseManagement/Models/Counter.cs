using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Newtonsoft.Json;

namespace HouseManagement.Models
{
    public class Counter
    {
        public int id { get; set; }
        public string number { get; set; }
        public bool used { get; set; }
        public bool IMDOrGHMD { get; set; }
        public DateTime dateLastVerification { get; set; }
        public DateTime dateNextVerification { get; set; }
        public int idService { get; set; }
        [ForeignKey("idService")]
        public Service? service { get; set; }
        [ForeignKey("flat")]
        public int? idFlat { get; set; }
        [JsonIgnore]
        [ForeignKey("idFlat")]
        public Flat? flat { get; set; }

        [ForeignKey("house")]
        public int idHouse { get; set; }
        [JsonIgnore]
        [ForeignKey("idHouse")]
        public House? house { get; set; }
    }
}
