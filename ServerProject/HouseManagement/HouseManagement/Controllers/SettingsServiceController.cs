using HouseManagement.Models;
using HouseManagement.OtherClasses;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HouseManagement.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class SettingsServiceController : ControllerBase
    {
        // GET: SettingsServiceController
        private readonly DBContext _context;

        public SettingsServiceController(DBContext context)
        {
            _context = context;
        }

        // GET: api/<SettingsServiceController>
        [HttpGet]
        public IEnumerable<SettingsService> Get()
        {
            return _context.SettingsServices;
        }

        [HttpGet("house/{id}")]
        public IEnumerable<SettingsService> GetByHouse(int id)
        {
            return _context.SettingsServices.Where(it => it.idHouse == id);
        }


        [HttpGet("flatowner/{id}")]
        public IEnumerable<SettingsService> GetByIdFlatOwner(int id)
        {
            return _context.SettingsServices.Where(it => it.idHouse == (_context.FlatOwners.First(it => it.id == id).idHouse));
        }

        [HttpGet("admin/{id}")]
        public IEnumerable<SettingsService> GetByAdmin(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            List<SettingsService> list = new List<SettingsService>();
            foreach (var item in idHouses)
            {
                list.AddRange(_context.SettingsServices.Where(it => it.idHouse == item));
            }
            return list;
        }

        // GET api/<SettingsServiceController>/5
        [HttpGet("{id}")]
        public ActionResult<SettingsService> Get(int id)
        {
            SettingsService item = _context.SettingsServices.Find(id);
            if (item == null)
            {
                return NotFound();
            }
            return item;
        }

        // POST api/<SettingsServiceController>
        [HttpPost]
        public ActionResult Post(List<SettingsService> item)
        {
            var count = item.Where(it => it.id != 0).Count();
            if (count == 0)
            {
                _context.SettingsServices.AddRange(item);
                _context.SaveChanges();
                return StatusCode(StatusCodes.Status200OK);
            }
            else
            {
                return BadRequest();
            }
        }

        // PUT api/<SettingsServiceController>/5
        [HttpPut("{id}")]
        public ActionResult<SettingsService> Put(int id, SettingsService item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.SettingsServices.Any(s => s.id == id))
                return NotFound();
            if (item.haveCounter == false)
            {
                var count = _context.Counters.Where(it => it.idHouse == item.idHouse && it.idService == item.idService && it.used == true).Count();
                if (count != 0)
                    return StatusCode(StatusCodes.Status500InternalServerError,
                        new ResponseModel
                        {
                            Status = "Error",
                            Message = "Изменение невозможно, так как в доме имеются счетчики этого типа, которые еще используются"
                        });
            }
            if (item.typeIMD == false)
            {
                var count = _context.Counters.Where(it => it.idHouse == item.idHouse && it.idService == item.idService && it.IMDOrGHMD == true && it.used == true).Count();
                if (count != 0)
                    return StatusCode(StatusCodes.Status500InternalServerError,
                        new ResponseModel
                        {
                            Status = "Error",
                            Message = "Изменение невозможно, так как у квартир имеются счетчики этого типа, которые еще используются"
                        });
            }
            if (item.typeIMD == true)
            {
                var count = _context.Counters.Where(it => it.idHouse == item.idHouse && it.idService == item.idService && it.IMDOrGHMD == false && it.used == true).Count();
                if (count != 0)
                    return StatusCode(StatusCodes.Status500InternalServerError,
                        new ResponseModel
                        {
                            Status = "Error",
                            Message = "Изменение невозможно, так как имеются общедомовые счетчики этого типа, которые еще используются"
                        });
            }
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


        // PUT api/<SettingsServiceController>/5
        [HttpPut("{id}/{idAdmin}")]
        public ActionResult<SettingsService> PutByAdmin(int id, string idAdmin, SettingsService item)
        {
            List<int> idHouses = _context.RelationshipHouses.Where(it => it.idUser == idAdmin).Select(it => it.idHouse).ToList();
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.SettingsServices.Any(s => s.id == id))
                return NotFound();
            if (item.haveCounter == false)
            {
                var count = 0;
                foreach (var idHouse in idHouses)
                {
                    count += _context.Counters.Where(it => it.idHouse == idHouse && it.idService == item.idService && it.used == true).Count();
                }
                if (count != 0)
                    return StatusCode(StatusCodes.Status500InternalServerError,
                        new ResponseModel
                        {
                            Status = "Error",
                            Message = "Изменение невозможно, так как в доме имеются счетчики этого типа, которые еще используются"
                        });
            }
            if (item.typeIMD == false)
            {
                var count = 0;
                foreach (var idHouse in idHouses)
                {
                    count += _context.Counters.Where(it => it.idHouse == idHouse && it.idService == item.idService && it.IMDOrGHMD == true && it.used == true).Count();
                }
                if (count != 0)
                    return StatusCode(StatusCodes.Status500InternalServerError,
                        new ResponseModel
                        {
                            Status = "Error",
                            Message = "Изменение невозможно, так как у квартир имеются счетчики этого типа, которые еще используются"
                        });
            }
            if (item.typeIMD == true)
            {
                var count = 0;
                foreach (var idHouse in idHouses)
                {
                    count += _context.Counters.Where(it => it.idHouse == idHouse && it.idService == item.idService && it.IMDOrGHMD == false && it.used == true).Count();
                }
                if (count != 0)
                    return StatusCode(StatusCodes.Status500InternalServerError,
                        new ResponseModel
                        {
                            Status = "Error",
                            Message = "Изменение невозможно, так как имеются общедомовые счетчики этого типа, которые еще используются"
                        });
            }
            foreach (var idHouse in idHouses)
            {
                var setting = _context.SettingsServices.First(it => it.idHouse == idHouse && it.idService == item.idService);
                setting.paymentPeriod = item.paymentPeriod;
                setting.paymentStatus = item.paymentStatus;
                setting.startDateTransfer = item.startDateTransfer;
                setting.endDateTransfer = item.endDateTransfer;
                setting.valueRate = item.valueRate;
                setting.valueNormative = item.valueNormative;
                setting.haveCounter = item.haveCounter;
                setting.typeIMD = item.typeIMD;
                setting.idService = item.idService;
                _context.Entry(setting).State = EntityState.Modified;
            }
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

        [HttpPut("admin/{id}")]
        public ActionResult<SettingsService> PutAll(List<SettingsService> item, string id)
        {
            foreach (var setting in item)
                if (!_context.SettingsServices.Any(s => s.id == setting.id))
                    return NotFound();
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();

            foreach (var idHouse in idHouses)
            {
                List<SettingsService> settingsServices = _context.SettingsServices.Where(it => it.idHouse == idHouse).ToList();
                foreach (var setting in item)
                {
                    var settingItem = settingsServices.First(it => it.idService == setting.idService);
                    settingItem.paymentStatus = setting.paymentStatus;
                    if (setting.paymentStatus == false)
                    {
                        var count = _context.Counters.Where(it => it.idHouse == idHouse && it.idService == setting.idService && it.used == true).Count();
                        if (count != 0)
                            return StatusCode(StatusCodes.Status500InternalServerError,
                   new ResponseModel
                   {
                       Status = "Error",
                       Message = "По услуге " + _context.Services.First(it => it.id == setting.idService).nameService + " имеются счетчики, которые еще используются"
                   });
                    }

                    _context.Entry(settingItem).State = EntityState.Modified;
                }
            }
            try
            {
                _context.SaveChanges();
                return StatusCode(StatusCodes.Status200OK);
            }
            catch (DbUpdateConcurrencyException)
            {
                return StatusCode(StatusCodes.Status500InternalServerError);
            }
        }

        // DELETE api/<SettingsServiceController>/5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            SettingsService itemFromBase = _context.SettingsServices.Find(id);
            if (itemFromBase != null)
            {
                _context.SettingsServices.Remove(itemFromBase);
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
