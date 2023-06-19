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
    public class FlatOwnerController : ControllerBase
    {
        private readonly DBContext _context;
        private readonly UserManager<ApplicationUser> userManager;

        public FlatOwnerController(UserManager<ApplicationUser> userManager, DBContext context)
        {
            this.userManager = userManager;
            _context = context;
        }

        // GET: api/<FlatOwnerController>
        [HttpGet]
        public IEnumerable<FlatOwner> Get()
        {
            return _context.FlatOwners;
        }

        // GET api/<FlatOwnerController>/5
        [HttpGet("{id}")]
        public ActionResult<FlatOwner> Get(int id)
        {
            FlatOwner flatOwner = _context.FlatOwners.Find(id);
            if (flatOwner == null)
            {
                return NotFound();
            }
            return flatOwner;
        }

        [HttpGet("house/{id}")]
        public IEnumerable<FlatOwner> GetByHouse(int id)
        {
            return _context.FlatOwners.Where(it => it.idHouse == id);
        }

        [HttpGet("admin/{id}")]
        public IEnumerable<FlatOwner> GetByAdminHouses(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            List<FlatOwner> flatOwners = new();
            foreach (var idHouse in idHouses)
            {
                flatOwners.AddRange(_context.FlatOwners.Where(it => it.idHouse == idHouse));
            }
            return flatOwners;
        }

        [HttpGet("house/haveNotUser/{id}")]
        public async Task<IEnumerable<FlatOwner>> GetByHouseHaveNotUserAsync(int id)
        {
            var flatOwners = _context.FlatOwners.Where(it => it.idHouse == id && it.email != null).ToList();
            var newFlatOwners = new List<FlatOwner>();
            foreach(var flatOwner in flatOwners)
            {
                var user = await userManager.FindByNameAsync(flatOwner.email);
                if (user == null) newFlatOwners.Add(flatOwner);
            }
            return newFlatOwners;
        }

        // POST api/<FlatOwnerController>
        [Authorize(Roles = UserRoles.HouseAdmin)]
        [HttpPost]
        public async Task<ActionResult> PostAsync(FlatOwner flatOwner)
        {
            if (flatOwner.id == 0)
            {
                int count = 0;
                if (flatOwner.email != null)
                {
                    count += _context.FlatOwners.Where(it => it.email == flatOwner.email).Count();

                    var userWithEmail = await userManager.FindByNameAsync(flatOwner.email);
                    if (userWithEmail != null) count++;
                }
                int countPhone = _context.FlatOwners.Where(it => it.phoneNumber == flatOwner.phoneNumber && it.idHouse != flatOwner.idHouse).Count();
                if (count == 0 && countPhone == 0)
                {
                    _context.FlatOwners.Add(flatOwner);
                    _context.SaveChanges();
                    return CreatedAtAction(nameof(Get), new { id = flatOwner.id }, flatOwner);
                }
                else
                {
                    string message = "";
                    if (count != 0) message = "Email уже занят";
                    else message = "Номер телефона уже занят";
                    return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel
                    {
                        Status = "Error",
                        Message = message
                    });
                }
            }
            else
            {
                return BadRequest();
            }
        }

        // PUT api/<FlatOwnerController>/5
        [HttpPut("{id}")]
        public async Task<ActionResult<FlatOwner>> PutAsync(int id, FlatOwner flatOwner)
        {
            if (id != flatOwner.id)
            {
                return BadRequest();
            }
            if (!_context.FlatOwners.Any(s => s.id == id))
                return NotFound();
            int count = 0;
            if (flatOwner.email != null)
            {
                count += _context.FlatOwners.Where(it => it.email == flatOwner.email && it.id != flatOwner.id).Count();

                var userWithEmail = await userManager.FindByNameAsync(flatOwner.email);
                if (userWithEmail != null && userWithEmail.idFlatOwner != flatOwner.id) count++;
            }
            string? email = _context.FlatOwners.Where(it => it.id == flatOwner.id).Select(it => it.email).FirstOrDefault();
            int countPhone = _context.FlatOwners.Where(it => it.phoneNumber == flatOwner.phoneNumber && it.id != flatOwner.id && it.idHouse != flatOwner.idHouse).Count();
            ApplicationUser? userFromDb = null;
            if(email != null) userFromDb = await userManager.FindByNameAsync(email);
            
            if (countPhone == 0 && count == 0)
            {
                _context.Entry(flatOwner).State = EntityState.Modified;
                try
                {
                    if (userFromDb != null)
                    {
                        if (flatOwner.email == null) return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel
                        {
                            Status = "Error",
                            Message = "Невозможно оставить пустым поле Email, так как у владельца есть аккаунт"
                        });
                        else
                        {
                            userFromDb.UserName = flatOwner.email;
                            var result = await userManager.UpdateAsync(userFromDb);
                        }
                    }
                    _context.SaveChanges();
                    return CreatedAtAction(nameof(Get), new { id = flatOwner.id }, flatOwner);
                }
                catch (DbUpdateConcurrencyException)
                {
                    return StatusCode(StatusCodes.Status500InternalServerError);
                }
            }
            else
            {
                string message = "";
                if (count != 0) message = "Email уже занят";
                else message = "Номер телефона уже занят";
                return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel
                {
                    Status = "Error",
                    Message = message
                });
            }
        }

        // DELETE api/<FlatOwnerController>/5
        [Authorize(Roles = UserRoles.HouseAdmin)]
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(int id)
        {
            FlatOwner flatOwnerFromBase = _context.FlatOwners.Find(id);
            if (flatOwnerFromBase != null)
            {
                int countFlats = _context.Residences.Where(it => it.idFlatOwner == id).Count();
                int countUser = 0;
                if (flatOwnerFromBase.email != null)
                {
                    var userFromDb = await userManager.FindByNameAsync(flatOwnerFromBase.email);
                    if (userFromDb != null) countUser++;
                }
                if (countUser == 0 && countFlats == 0)
                {
                    _context.FlatOwners.Remove(flatOwnerFromBase);
                    _context.SaveChanges();
                    return StatusCode(StatusCodes.Status204NoContent);
                }
                else
                {
                    string message = "";
                    if (countFlats != 0) message = "Удаление невозможно, так как у владельца есть квартиры";
                    else message = "Удаление невозможно, так как у владельца есть аккаунт";
                    return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel
                    {
                        Status = "Error",
                        Message = message
                    });
                }
            }
            else
            {
                return BadRequest();
            }
        }
    }
}
