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
    public class FlatController : ControllerBase
    {
        // GET: FlatController
        private readonly DBContext _context;

        public FlatController(DBContext context)
        {
            _context = context;
        }

        // GET: api/<FlatController>
        [HttpGet]
        public IEnumerable<Flat> Get()
        {
            return _context.Flats;
        }

        [HttpGet("house/{id}")]
        public IEnumerable<Flat> GetByHouse(int id)
        {
            return _context.Flats.Where(it => it.idHouse == id);
        }

        [HttpGet("admin/{id}")]
        public IEnumerable<Flat> GetByUserAdmin(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            List<Flat> flats = new List<Flat>();
            foreach(var house in idHouses)
            {
                flats.AddRange(_context.Flats.Where(it => it.idHouse == house));
            }
            return flats;
        }

        // GET api/<FlatController>/5
        [HttpGet("{id}")]
        public ActionResult<Flat> Get(int id)
        {
            Flat item = _context.Flats.Find(id);
            if (item == null)
            {
                return NotFound();
            }
            return item;
        }

        [HttpGet("house/usablearea/{id}")]
        public double GetUsableAreaHouse(int id)
        {
            var flats = _context.Flats.Where(it => it.idHouse == id).ToList();
            double amount = 0;
            foreach (var flat in flats)
                amount += flat.usableArea;
            return amount;
        }


        [HttpGet("house/people/{id}")]
        public int GetCountPeople(int id)
        {
            var flats = _context.Flats.Where(it => it.idHouse == id).ToList();
            int count = 0;
            foreach (var flat in flats)
            {
                if (flat.numberOfRegisteredResidents != 0) count += flat.numberOfRegisteredResidents;
                else count += flat.numberOfOwners;
            }
            return count;
        }

        [HttpGet("house/totalarea/{id}")]
        public double GetTotalAreaHouse(int id)
        {
            var flats = _context.Flats.Where(it => it.idHouse == id).ToList();
            double amount = 0;
            foreach (var flat in flats)
                amount += flat.totalArea;
            return amount;
        }

        [HttpGet("flatowner/{id}")]
        public IEnumerable<Flat> GetByIdFlatOwner(int id)
        {
            var idFlats = _context.Residences.Where(it => it.idFlatOwner == id).ToList();
            List<Flat> flats = new List<Flat>();
            foreach (var idFlat in idFlats)
                flats.Add(_context.Flats.First(it => it.id == idFlat.idFlat));
            return flats;
        }

        //POST api/<FlatController>
        [HttpPost]
        public ActionResult Post(Flat item)
        {
            if (item.id == 0)
            {
                var count = _context.Flats.Where(it => (it.flatNumber == item.flatNumber || it.personalAccount == item.personalAccount) && it.idHouse == item.idHouse).Count();
                if (count == 0)
                {
                    _context.Flats.Add(item);
                    _context.SaveChanges();
                    return CreatedAtAction(nameof(Get), new { id = item.id }, item);
                }
                else return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "Квартира с таким номером или лицевым счетом уже существует"
               });
            }
            return BadRequest();
        }

        // PUT api/<FlatController>/5
        [HttpPut("{id}")]
        public ActionResult<Flat> Put(int id, Flat item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.Flats.Any(s => s.id == id))
                return NotFound();
            var count = _context.Flats.Where(it => (it.flatNumber == item.flatNumber || it.personalAccount == item.personalAccount) && it.idHouse == item.idHouse && it.id != item.id).Count();
            if (count == 0)
            {
                if (item.numberOfOwners < _context.Residences.Where(it => it.idFlat == id).Count())
                    return StatusCode(StatusCodes.Status500InternalServerError,
                       new ResponseModel
                       {
                           Status = "Error",
                           Message = "Количество введенных владельцев жилья меньше, чем количество выбранных"
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
               Message = "Квартира с таким номером или лицевым счетом уже существует"
           });
        }

        // DELETE api/<FlatController>/5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            Flat itemFromBase = _context.Flats.Find(id);
            if (itemFromBase != null)
            {

                int count = _context.Counters.Where(it => it.idFlat == id).Count();
                count += _context.Residences.Where(it => it.idFlat == id).Count();
                count += _context.Payments.Where(it => it.idFlat == id).Count();
                count += _context.Complaints.Where(it => it.idFlat == id).Count();
                if (count == 0)
                {

                    _context.Flats.Remove(itemFromBase);
                    _context.SaveChanges();
                    return StatusCode(StatusCodes.Status204NoContent);
                }
                else
                {
                    return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel
                    {
                        Status = "Error",
                        Message = "Удаление невозможно, так как у квартиры есть либо владельцы, либо счетчики, либо заявки, либо начисления"
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
