using Microsoft.AspNetCore.Identity;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace HouseManagement.Models
{
    public class ApplicationUser : IdentityUser
    {
        public int? idFlatOwner { get; set; }
    }
}
