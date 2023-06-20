using HouseManagement.Models;
using HouseManagement.OtherClasses;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Globalization;
using System.Net;

namespace HouseManagement.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class IndicationController : ControllerBase
    {
        private readonly DBContext _context;

        public IndicationController(DBContext context)
        {
            _context = context;
        }

        [Authorize(Roles = "FlatOwner,HouseAdmin")]
        [HttpGet("{id}")]
        public ActionResult<Indication> Get(int id)
        {
            Indication? item = _context.Indications.Find(id);
            if (item == null)
            {
                return NotFound();
            }
            return item;
        }

        [Authorize(Roles = "FlatOwner,HouseAdmin")]
        [HttpGet("lastfromcounter/{id}")]
        public ActionResult<Indication?> GetLastByCouter(int id)
        {
            var lastIndication = _context.Indications.FromSqlRaw(@"SELECT * FROM INDICATIONS WHERE dateTransfer IN (SELECT max(dateTransfer) FROM INDICATIONS WHERE idCounter = " + id + " )").FirstOrDefault();
            return lastIndication;
        }

        [Authorize(Roles = "FlatOwner,HouseAdmin")]
        [HttpGet("lastfromflat/{id}")]
        public IEnumerable<Indication> GetLastByFlat(int id)
        {
            var dateNow = DateTime.UtcNow;
            var indications = new List<Indication>();
            var counters = _context.Counters.Where(it => it.idFlat == id);
            foreach (var counter in counters)
            {
                indications.AddRange(_context.Indications.Where(it => it.dateTransfer.Year == dateNow.Year && it.dateTransfer.Month == dateNow.Month && it.idCounter == counter.id));
            }
            return indications;
        }

        [Authorize(Roles = "HouseAdmin")]
        [HttpGet("lastfromadmin/{id}")]
        public IEnumerable<Indication> GetLastByAdmin(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            List<Counter> counters = new();
            List<Indication> indications = new();

            var dateNow = DateTime.UtcNow;

            foreach (var idHouse in idHouses)
            {
                counters.AddRange(_context.Counters.Where(it => it.idHouse == idHouse && it.used == true));
            }
            foreach (var idHouse in idHouses)
            {
                var settingsServices = _context.SettingsServices.Where(it => it.haveCounter == true && it.idHouse == idHouse).ToList();
                List<Counter> newCounters = new();
                foreach (var setting in settingsServices)
                    newCounters.AddRange(counters.Where(it => it.idService == setting.idService && it.idHouse == idHouse).ToList());

                foreach (var counter in newCounters)
                    indications.AddRange(_context.Indications.Where(it => it.idCounter == counter.id && it.dateTransfer.Year == dateNow.Year && it.dateTransfer.Month == dateNow.Month).ToList());
            }
            return indications;
        }

        [Authorize(Roles = "FlatOwner,HouseAdmin")]
        [HttpGet("lastfromhouse/{id}")]
        public IEnumerable<Indication> GetLastByHouse(int id)
        {
            var dateNow = DateTime.UtcNow;
            var indications = new List<Indication>();
            var counters = _context.Counters.Where(it => it.idHouse == id && it.IMDOrGHMD == false);
            foreach (var counter in counters)
            {
                indications.AddRange(_context.Indications.Where(it => it.dateTransfer.Year == dateNow.Year && it.dateTransfer.Month == dateNow.Month && it.idCounter == counter.id));
            }
            return indications;
        }

        [Authorize(Roles = "FlatOwner,HouseAdmin")]
        [HttpGet("lastmonthfromcounter/{id}")]
        public ActionResult<Indication?> GetLastMonthByCouter(int id)
        {
            var dateNow = DateTime.UtcNow;
            int month = dateNow.Month - 1;
            int year = dateNow.Year;
            if (dateNow.Month == 1)
            {
                month = 12;
                year = dateNow.Year - 1;
            }
            var lastIndication = _context.Indications.FirstOrDefault(it => it.dateTransfer.Year == year && it.dateTransfer.Month == month && it.idCounter == id);
            return lastIndication;
        }

        [Authorize(Roles = "FlatOwner")]
        [HttpGet("flatowner/{id}")]
        public IEnumerable<Indication> GetIndicationsByIdFlat(int id)
        {
            var idFlats = _context.Residences.Where(it => it.idFlatOwner == id);
            var counters = new List<Counter>();
            foreach (var flat in idFlats)
            {
                counters.AddRange(_context.Counters.Where(it => it.idFlat == flat.idFlat && it.used == true));
            }
            var indications = new List<Indication>();
            foreach (var counter in counters)
            {
                indications.AddRange(_context.Indications.Where(it => it.idCounter == counter.id)); ;
            }
            return indications;
        }

        //[Authorize(Roles = "FlatOwner,HouseAdmin")]
        //[HttpGet("house/{id}")]
        //public IEnumerable<Indication> GetIndicationsByHouse(int id)
        //{
        //    var counters = _context.Counters.Where(it => it.idHouse == id);
        //    List<Indication> indications = new();
        //    foreach (var counter in counters)
        //        indications.AddRange(_context.Indications.Where(it => it.idCounter == counter.id).ToList());
        //    return indications;
        //}

        [Authorize(Roles = "HouseAdmin")]
        [HttpGet("admin/{id}")]
        public IEnumerable<Indication> GetIndicationsByAdmin(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            List<Counter> counters = new();
            foreach (var idHouse in idHouses)
            {
                counters.AddRange(_context.Counters.Where(it => it.idHouse == idHouse));
            }
            List<Indication> indications = new();
            foreach (var counter in counters)
                indications.AddRange(_context.Indications.Where(it => it.idCounter == counter.id).ToList());
            return indications;
        }

        [Authorize(Roles = "HouseAdmin")]
        [HttpGet("summary/{id}")]
        public IEnumerable<SummaryData> GetSummaryIndicationsByAdminSumma(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            List<SummaryData> summaries = new();
            List<Counter> counters = new();
            List<Indication> indications = new();
            List<Counter> resultCounters = new();

            var dateNow = DateTime.UtcNow;

            foreach (var idHouse in idHouses)
            {
                counters.AddRange(_context.Counters.Where(it => it.idHouse == idHouse && it.used == true));
            }
            foreach (var idHouse in idHouses)
            {
                var settingsServices = _context.SettingsServices.Where(it => it.haveCounter == true && it.idHouse == idHouse).ToList();
                List<Counter> newCounters = new();
                foreach (var setting in settingsServices)
                    newCounters.AddRange(counters.Where(it => it.idService == setting.idService && it.idHouse == idHouse).ToList());

                foreach (var counter in newCounters)
                    indications.AddRange(_context.Indications.Where(it => it.idCounter == counter.id && it.dateTransfer.Year == dateNow.Year && it.dateTransfer.Month == dateNow.Month).ToList());
            }

            foreach(var idHouse in idHouses)
            {
                var settings = _context.SettingsServices.Where(it => it.idHouse == idHouse && it.haveCounter == true).ToList();
                foreach(var setting in settings)
                {
                    var service = _context.Services.First(it => it.id == setting.idService);
                    var newCounters = counters.Where(it => it.idHouse == idHouse && it.idService == setting.idService).ToList();
                    var house = _context.Houses.First(it => it.id == idHouse);
                    if (summaries.Where(it => it.nameService == service.nameService && it.nameHouse == house.name).Count() == 0)
                    {
                        int summ = 0;
                        foreach (var counter in newCounters)
                        {
                            summ += indications.Where(it => it.idCounter == counter.id).Sum(it => it.value);
                        }
                        summaries.Add(new SummaryData(service.nameService, house.name, summ, GetUnit(service.nameService)));
                    }
                    else
                    {
                        int index = summaries.IndexOf(summaries.First(it => it.nameService == service.nameService));
                        int summ = summaries[index].summa;
                        foreach (var counter in newCounters)
                        {
                            summ += indications.Where(it => it.idCounter == counter.id).Sum(it => it.value);
                        }
                        summaries[index] = new SummaryData(service.nameService, house.name, summ, GetUnit(service.nameService));
                    }
                }
            }
            return summaries;
        }

        private string GetUnit(string name)
        {
            string unit = "";
            switch (name)
            {
                case "Холодное водоснабжение":
                    unit = "м³";
                    break;
                case "Горячее водоснабжение":
                    unit = "м³";
                    break;
                case "Электроэнергия":
                    unit = "кВт·ч";
                    break;
                case "Газ":
                    unit = "м³";
                    break;
                case "Отопление":
                    unit = "Гкал";
                    break;
            }
            return unit;
        }

        [Authorize(Roles = "HouseAdmin")]
        [HttpGet("month/{month}/year/{year}/flat/{id}")]
        public IEnumerable<Indication> GetIndicationsByDateFlat(int id, int month, int year)
        {
            var counters = _context.Counters.Where(it => it.idFlat == id);
            List<Indication> indications = new();
            foreach (var counter in counters)
                indications.AddRange(_context.Indications.Where(it => it.idCounter == counter.id && it.dateTransfer.Year == year && it.dateTransfer.Month == month).ToList());
            return indications;
        }

        [Authorize(Roles = "HouseAdmin")]
        [HttpGet("month/{month}/year/{year}/house/{id}")]
        public IEnumerable<Indication> GetIndicationsByDateHouse(int id, int month, int year)
        {
            var settingsServices = _context.SettingsServices.Where(it => it.typeIMD == false && it.haveCounter == true && it.idHouse == id).ToList();
            List<Counter> counters = new();
            foreach (var setting in settingsServices)
                counters.AddRange(_context.Counters.Where(it => it.idService == setting.idService && it.idHouse == id).ToList());

            List<Indication> indications = new();
            foreach (var counter in counters)
                indications.AddRange(_context.Indications.Where(it => it.idCounter == counter.id && it.dateTransfer.Year == year && it.dateTransfer.Month == month).ToList());
            return indications;
        }

        [Authorize(Roles = "HouseAdmin")]
        [HttpPost]
        public ActionResult Post(Indication item)
        {
            if (item.id == 0)
            {
                var dateNow = DateTime.UtcNow;
                var nowIndication = _context.Indications.FirstOrDefault(it => it.dateTransfer.Year == dateNow.Year && it.dateTransfer.Month == dateNow.Month && it.idCounter == item.idCounter);
                if (nowIndication != null) return StatusCode(StatusCodes.Status500InternalServerError);

                var counter = _context.Counters.First(it => it.id == item.idCounter);
                var count = _context.Payments.Where(it => it.period == GetPeriod() && it.flat.idHouse == counter.idHouse).Count();
                if (count != 0) return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "За этот период уже произведены начисления"
               });

                _context.Indications.Add(item);
                _context.SaveChanges();
                return CreatedAtAction(nameof(Get), new { id = item.id }, item);
            }
            else
            {
                return BadRequest();
            }
        }

        private static string GetPeriod()
        {
            var dateNow = DateTime.UtcNow;
            int year = dateNow.Year;
            int month = dateNow.Month;
            if (month == 1)
            {
                month = 12;
                year -= 1;
            }
            else month -= 1;
            return CultureInfo.CurrentCulture.DateTimeFormat.GetMonthName(month) + " " + year.ToString();
        }

        [Authorize(Roles = "FlatOwner")]
        [HttpPost("fromOwner")]
        public ActionResult PostFromOwner(Indication item)
        {
            if (item.id == 0)
            {
                var lastIndication = _context.Indications.FromSqlRaw(@"SELECT * FROM INDICATIONS WHERE dateTransfer IN (SELECT max(dateTransfer) FROM INDICATIONS WHERE idCounter = " + item.idCounter + " )").FirstOrDefault();
                if (lastIndication == null || item.value >= lastIndication.value)
                {
                    var counter = _context.Counters.First(it => it.id == item.idCounter);
                    var count = _context.Payments.Where(it => it.period == GetPeriod() && it.flat.idHouse == counter.idHouse).Count();
                    if (count != 0) return StatusCode(StatusCodes.Status500InternalServerError,
                   new ResponseModel
                   {
                       Status = "Error",
                       Message = "За этот период уже произведены начисления"
                   });
                    _context.Indications.Add(item);
                    _context.SaveChanges();
                    return CreatedAtAction(nameof(Get), new { id = item.id }, item);
                }
                else return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "Текущее показание должно быть больше предыдущего"
               });
            }
            else return BadRequest();
        }

        [Authorize(Roles = "FlatOwner")]
        [HttpPut("fromOwner/{id}")]
        public ActionResult<Indication> PutFromOwner(int id, Indication item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.Indications.Any(s => s.id == id))
                return NotFound();

            var lastIndication = _context.Indications.FromSqlRaw(@"SELECT * FROM INDICATIONS WHERE dateTransfer IN (SELECT max(dateTransfer) FROM INDICATIONS WHERE idCounter = " + item.idCounter + " AND id != " + item.id + ")").FirstOrDefault();

            if (lastIndication == null || item.value >= lastIndication.value)
            {
                var counter = _context.Counters.First(it => it.id == item.idCounter);
                var count = _context.Payments.Where(it => it.period == GetPeriod() && it.flat.idHouse == counter.idHouse).Count();
                if (count != 0) return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "За этот период уже произведены начисления"
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
                   Message = "Текущее показание должно быть больше предыдущего"
               });
        }

        [Authorize(Roles = "HouseAdmin")]
        [HttpPut("{id}")]
        public ActionResult<Indication> Put(int id, Indication item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.Indications.Any(s => s.id == id))
                return NotFound();
            var dateNow = DateTime.UtcNow;
            if (item.dateTransfer.Year == dateNow.Year && item.dateTransfer.Month == dateNow.Month)
            {
                var counter = _context.Counters.First(it => it.id == item.idCounter);
                var count = _context.Payments.Where(it => it.period == GetPeriod() && it.flat.idHouse == counter.idHouse).Count();
                if (count != 0) return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "За этот период уже произведены начисления"
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
            else return StatusCode(StatusCodes.Status500InternalServerError);
        }

        [Authorize(Roles = "FlatOwner,HouseAdmin")]
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            Indication itemFromBase = _context.Indications.Find(id);
            var dateNow = DateTime.UtcNow;
            if (itemFromBase != null)
            {
                if (itemFromBase.dateTransfer.Year == dateNow.Year && itemFromBase.dateTransfer.Month == dateNow.Month)
                {
                    var counter = _context.Counters.First(it => it.id == itemFromBase.idCounter);
                    var count = _context.Payments.Where(it => it.period == GetPeriod() && it.flat.idHouse == counter.idHouse).Count();
                    if (count != 0) return StatusCode(StatusCodes.Status500InternalServerError,
                   new ResponseModel
                   {
                       Status = "Error",
                       Message = "За этот период уже произведены начисления"
                   });
                    _context.Indications.Remove(itemFromBase);
                    _context.SaveChanges();
                    return StatusCode(StatusCodes.Status204NoContent);
                }
                else return StatusCode(StatusCodes.Status500InternalServerError);
            }
            else
            {
                return BadRequest();
            }
        }
    }
}
