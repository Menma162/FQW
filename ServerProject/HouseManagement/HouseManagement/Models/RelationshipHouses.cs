using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations.Schema;

namespace HouseManagement.Models
{
    public class RelationshipHouses
    {
        public RelationshipHouses(string idUser, int idHouse)
        {
            this.idUser = idUser;
            this.idHouse = idHouse;
        }
        public int id { get; set; }
        public string idUser { get; set; }
        [ForeignKey("house")]
        public int idHouse { get; set; }
        [JsonIgnore]
        [ForeignKey("idHouse")]
        public House? house { get; set; }
    }
}
