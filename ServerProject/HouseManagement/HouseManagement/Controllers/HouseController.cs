using HouseManagement.Models;
using HouseManagement.OtherClasses;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HouseManagement.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class HouseController : ControllerBase
    {
        // GET: HouseController
        private readonly DBContext _context;
        private readonly UserManager<ApplicationUser> userManager;

        public HouseController(UserManager<ApplicationUser> userManager, DBContext context)
        {
            this.userManager = userManager;
            _context = context;
        }

        // GET: api/<HouseController>
        [HttpGet]
        public IEnumerable<House> Get()
        {
            return _context.Houses;
        }

        [HttpGet("notUses")]
        public IEnumerable<House> GetNotUses()
        {
            var idHouses = _context.RelationshipHouses.Select(it => it.idHouse).ToList();
            return _context.Houses.Where(it => !idHouses.Any(p => it.id == p));
        }

        [HttpGet("byuser/{id}")]
        public IEnumerable<House> GetByUser(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse);
            List<House> houses = new List<House>();
            foreach (var idHouse in idHouses)
            {
                houses.Add(_context.Houses.First(it => it.id == idHouse));
            }
            return houses;
        }

        // GET api/<HouseController>/5
        [HttpGet("{id}")]
        public ActionResult<House> Get(int id)
        {
            House item = _context.Houses.Find(id);
            if (item == null)
            {
                return NotFound();
            }
            return item;
        }

        [HttpGet("names/{id}")]
        public ActionResult<string> GetNamesByUser(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse);
            string names = "";
            foreach (var idHouse in idHouses)
            {
                names += _context.Houses.First(it => it.id == idHouse).name + ",";
            }
            return names;
        }

        // POST api/<HouseController>
        [HttpPost]
        public ActionResult Post(House item)
        {
            if (item.id == 0)
            {
                if (_context.Houses.Where(it => it.name == item.name).Count() != 0)
                    return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel
                    {
                        Status = "Error",
                        Message = "Дом с таким названием уже существует"
                    });

                _context.Houses.Add(item);
                _context.SaveChanges();


                var services = _context.Services;


                foreach (var service in services)
                {
                    SettingsService settingsService = new();
                    settingsService.idHouse = item.id;
                    settingsService.paymentPeriod = ",1,2,3,4,5,6,7,8,9,10,11,12";
                    settingsService.paymentStatus = true;
                    settingsService.startDateTransfer = 1;
                    settingsService.endDateTransfer = 10;
                    settingsService.valueRate = 0;
                    settingsService.valueNormative = 0;
                    settingsService.haveCounter = false;
                    settingsService.typeIMD = false;
                    settingsService.idService = service.id;

                    if (service.nameService == "Кап. ремонт" || service.nameService == "Домофон" || service.nameService == "Вывоз мусора" ||
                        service.nameService == "Лифт")
                    {
                        settingsService.valueNormative = null;
                        settingsService.typeIMD = null;
                    }
                    if (service.nameService == "Водоотведение")
                    {
                        settingsService.typeIMD = null;
                    }
                    _context.SettingsServices.Add(settingsService);
                }
                _context.SaveChanges();
                return CreatedAtAction(nameof(Get), new { id = item.id }, item);
            }
            else
            {
                return BadRequest();
            }
        }

        // PUT api/<HouseController>/5
        [HttpPut("{id}")]
        public ActionResult<House> Put(int id, House item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.Houses.Any(s => s.id == id))
                return NotFound();
            if (_context.Houses.Where(it => it.name == item.name && it.id != item.id).Count() != 0)
                return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel
                {
                    Status = "Error",
                    Message = "Дом с таким названием уже существует"
                });
            _context.Entry(item).State = EntityState.Modified;
            try
            {
                _context.SaveChanges();
                return CreatedAtAction(nameof(Get), new { id = item.id }, item);
            }
            catch (DbUpdateConcurrencyException)
            {
                return StatusCode(StatusCodes.Status500InternalServerError);
            }
        }

        // DELETE api/<HouseController>/5
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(int id)
        {
            House itemFromBase = _context.Houses.Find(id);
            if (itemFromBase != null)
            {
                int count = _context.Flats.Where(it => it.idHouse == id).Count();
                count += _context.FlatOwners.Where(it => it.idHouse == id).Count();
                count += _context.SettingsServices.Where(it => it.idHouse == id).Count();
                count += _context.Counters.Where(it => it.idHouse == id).Count();
                count += _context.Advertisements.Where(it => it.idHouse == id).Count();
                count += _context.RelationshipHouses.Where(it => it.idHouse == id).Count();
                if (count != 0) return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel
                {
                    Status = "Error",
                    Message = "Удаление невозможно, так как у дома есть данные по квартирам или пользователям"
                });
                _context.Houses.Remove(itemFromBase);
                _context.SaveChanges();
                return StatusCode(StatusCodes.Status204NoContent);
            }
            else
            {
                return BadRequest();
            }
        }
    }
}
