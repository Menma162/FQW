using HouseManagement.Models;
using HouseManagement.OtherClasses;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace HouseManagement.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class ResidenceController : ControllerBase
    {
        private readonly DBContext _context;

        public ResidenceController(DBContext context)
        {
            _context = context;
        }

        [HttpGet("byflat/{id}")]
        public IEnumerable<FlatOwner> GetByFlat(int id)
        {
            var idFlatOwners = _context.Residences.Where(it => it.idFlat == id).Select(it => it.idFlatOwner).ToList();
            List<FlatOwner> flatOwners = new();
            foreach (var idFlatOwner in idFlatOwners)
            {
                flatOwners.Add(_context.FlatOwners.First(it => it.id == idFlatOwner));
            }
            return flatOwners;
        }

        [HttpGet("{idFlat}/{idUser}")]
        public IEnumerable<FlatOwner> GetHaveNoFlat(string idUser, int idFlat)
        {
            var idHouse = _context.Flats.First(it => it.id == idFlat).idHouse;
            List<FlatOwner> flatOwners = new();
                flatOwners.AddRange(_context.FlatOwners.Where(it => it.idHouse == idHouse).ToList());
            return flatOwners;
        }


        [Authorize(Roles = UserRoles.HouseAdmin)]
        [HttpPost("{id}")]
        public ActionResult Post(List<Residence> residences, int id)
        {
            if(residences.Count > _context.Flats.First(it => it.id == id).numberOfOwners) 
                return StatusCode(StatusCodes.Status500InternalServerError,
                   new ResponseModel
                   {
                       Status = "Error",
                       Message = "Количество выбранных владельцев жилья превышает количество владельцев в информации о квартире"
                   });

            var residencesForDelete = _context.Residences.Where(it => it.idFlat == id).ToList();
            foreach(var residenceForDelete in residencesForDelete)
            {
                _context.Residences.Remove(residenceForDelete);
            }
            foreach (var residence in residences)
            {
                _context.Residences.Add(residence);
            }
            try
            {
                _context.SaveChanges();
                return StatusCode(StatusCodes.Status201Created);
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError);
            }
        }
    }
}
