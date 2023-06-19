using HouseManagement.Models;
using HouseManagement.OtherClasses;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Data;

namespace HouseManagement.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class AdvertisementController : ControllerBase
    {
        private readonly DBContext _context;

        public AdvertisementController(DBContext context)
        {
            _context = context;
        }

        [HttpGet]
        public IEnumerable<Advertisement> Get()
        {
            return _context.Advertisements;
        }

        [HttpGet("house/{id}")]
        public IEnumerable<Advertisement> GetByHouse(int id)
        {
            //var user = 
            return _context.Advertisements.Where(it => it.idHouse == id);
        }


        [HttpGet("flatowner/{id}")]
        public IEnumerable<Advertisement> GetByIdFlatOwner(int id)
        {
            return _context.Advertisements.Where(it => it.idHouse == (_context.FlatOwners.First(it => it.id == id).idHouse));
        }

        [HttpGet("admin/{id}")]
        public IEnumerable<Advertisement> GetByAdmin(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id);
            List<Advertisement> advertisements = new List<Advertisement>();
            foreach (var house in idHouses)
            {
                advertisements.AddRange(_context.Advertisements.Where(it => it.idHouse == house.idHouse));
            }
            return advertisements;
        }

        // GET api/<AdvertisementController>/5
        [HttpGet("{id}")]
        public ActionResult<Advertisement> Get(int id)
        {
            Advertisement item = _context.Advertisements.Find(id);
            if (item == null)
            {
                return NotFound();
            }
            return item;
        }

        // POST api/<AdvertisementController>
        [HttpPost]
        public ActionResult Post(AdvertisementCreateModel item)
        {
            var idHouses = item.idHouses;
            List<Advertisement> advertisements = new();
            if (item.id == 0)
            {
                foreach(var idHouse in idHouses)
                {
                    Advertisement advertisement = new Advertisement();
                    advertisement.date = item.date;
                    advertisement.description = item.description;
                    advertisement.idHouse = idHouse;
                    _context.Advertisements.Add(advertisement);
                    _context.SaveChanges();
                    advertisements.Add(advertisement);
                }
                return CreatedAtAction(nameof(Get), advertisements);
            }
            else
            {
                return BadRequest();
            }
        }

        // PUT api/<AdvertisementController>/5
        [HttpPut("{id}")]
        public ActionResult<Advertisement> Put(int id, Advertisement item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.Advertisements.Any(s => s.id == id))
                return NotFound();
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

        // DELETE api/<AdvertisementController>/5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            Advertisement itemFromBase = _context.Advertisements.Find(id);
            if (itemFromBase != null)
            {
                _context.Advertisements.Remove(itemFromBase);
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
