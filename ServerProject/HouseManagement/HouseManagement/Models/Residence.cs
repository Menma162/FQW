using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations.Schema;

namespace HouseManagement.Models
{
    public class Residence
    {
        public Residence(int idFlatOwner, int idFlat)
        {
            this.idFlatOwner = idFlatOwner;
            this.idFlat = idFlat;
        }
        public int id { get; set; }

        [ForeignKey("flatOwner")]
        public int idFlatOwner { get; set; }
        [JsonIgnore]
        [ForeignKey("idFlatOwner")]
        public FlatOwner? flatOwner { get; set; }
        [ForeignKey("flat")]
        public int idFlat { get; set; }
        [JsonIgnore]
        [ForeignKey("idFlat")]
        public Flat? flat { get; set; }
    }
}
