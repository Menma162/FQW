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
    public class CounterController : ControllerBase
    {
        // GET: CounterController
        private readonly DBContext _context;

        public CounterController(DBContext context)
        {
            _context = context;
        }

        // GET: api/<CounterController>
        [HttpGet]
        public IEnumerable<Counter> Get()
        {
            return _context.Counters;
        }

        [HttpGet("admin/{id}")]
        public IEnumerable<Counter> GetByAdminHouses(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            List<Counter> counters = new();
            foreach (var idHouse in idHouses)
            {
                counters.AddRange(_context.Counters.Where(it => it.idHouse == idHouse));
            }
            return counters;
        }

        [HttpGet("untransmittedReadings/{id}")]
        public IEnumerable<Counter> GetByUntransmittedReadings(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            List<Counter> counters = new();
            List<Indication> indications = new();
            List<Counter> resultCounters = new();

            var dateNow = DateTime.UtcNow;

            foreach (var idHouse in idHouses)
            {
                counters.AddRange(_context.Counters.Where(it => it.idHouse == idHouse && it.used == true));
            }
            foreach(var idHouse in idHouses)
            {
                var settingsServices = _context.SettingsServices.Where(it => it.haveCounter == true && it.idHouse == idHouse).ToList();
                List<Counter> newCounters = new();
                foreach (var setting in settingsServices)
                    newCounters.AddRange(_context.Counters.Where(it => it.idService == setting.idService && it.idHouse == idHouse).ToList());

                foreach (var counter in newCounters)
                    indications.AddRange(_context.Indications.Where(it => it.idCounter == counter.id && it.dateTransfer.Year == dateNow.Year && it.dateTransfer.Month == dateNow.Month).ToList());

            }


            foreach (var counter in counters)
            {
                if(indications.FirstOrDefault(it => it.idCounter == counter.id) == null) resultCounters.Add(counter);
            }

            return resultCounters;
        }

        // GET api/<CounterController>/5
        [HttpGet("{id}")]
        public ActionResult<Counter> Get(int id)
        {
            Counter item = _context.Counters.Find(id);
            if (item == null)
            {
                return NotFound();
            }
            return item;
        }

        [HttpGet("house/{id}")]
        public IEnumerable<Counter> GetCountersByIdFlatHouse(int id)
        {
            return _context.Counters.Where(it => it.idHouse == id);
        }

        [HttpGet("house/ghAndUsed/{id}")]
        public IEnumerable<Counter> GetCountersByIdGhHouse(int id)
        {
            return _context.Counters.Where(it => it.idHouse == id && it.IMDOrGHMD == false && it.used == true);
        }

        [HttpGet("flat/{id}")]
        public IEnumerable<Counter> GetCountersByFlat(int id)
        {
            return _context.Counters.Where(it => it.idFlat == id && it.used == true);
        }

        [HttpGet("flatowner/{id}")]
        public IEnumerable<Counter> GetCountersByIdFlatAndUsed(int id)
        {
            var idFlats = _context.Residences.Where(it => it.idFlatOwner == id);
            var counters = new List<Counter>();
            foreach (var flat in idFlats)
            {
                counters.AddRange(_context.Counters.Where(it => it.idFlat == flat.idFlat && it.used == true));
            }
            return counters;
        }

        // POST api/<CounterController>
        [HttpPost]
        public ActionResult Post(Counter item)
        {
            if (item.id == 0)
            {
                int count = _context.Counters.Where(it => it.number == item.number && it.idService == item.idService).Count();
                if(count == 0)
                {
                    _context.Counters.Add(item);
                    _context.SaveChanges();
                    return CreatedAtAction(nameof(Get), new { id = item.id }, item);
                }
                else return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "Счетчик с таким наименованием и номером уже существует"
               });
            }
            else
            {
                return BadRequest();
            }
        }

        // PUT api/<CounterController>/5
        [HttpPut("{id}")]
        public ActionResult<Counter> Put(int id, Counter item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.Counters.Any(s => s.id == id))
                return NotFound();

            int count = _context.Counters.Where(it => it.number == item.number && it.idService == item.idService && it.id != item.id).Count();
            if(count == 0)
            {
                var service = _context.SettingsServices.First(it => it.idHouse == item.idHouse && it.idService == item.idService);
                if(service.paymentStatus == false && item.used == true)
                    return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "Данная услуга не начисляется, счетчик не может использоваться"
               });
                if (service.paymentStatus == true && service.haveCounter == false && item.used == true)
                    return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "По данной услуге не могут использоваться счетчики"
               });
                if (service.paymentStatus == true && service.typeIMD == true && item.IMDOrGHMD == false && item.used == true)
                    return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "По данной услуге могут иметься только индивидуальные приборы учета"
               });
                if (service.paymentStatus == true && service.typeIMD == false && item.IMDOrGHMD == true && item.used == true)
                    return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "По данной услуге могут иметься только общедомовые приборы учета"
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
            else return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "Счетчик с таким наименованием и номером уже существует"
               });
        }

        // DELETE api/<CounterController>/5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            Counter itemFromBase = _context.Counters.Find(id);
            if (itemFromBase != null)
            {
                int count = _context.Indications.Where(it => it.idCounter == id).Count();
                if(count == 0)
                {
                    _context.Counters.Remove(itemFromBase);
                    _context.SaveChanges();
                    return StatusCode(StatusCodes.Status204NoContent);
                }
                else return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "Удаление невозможно, так как счетчик задействован в показаниях"
               });
            }
            else
            {
                return BadRequest();
            }
        }
    }
}
