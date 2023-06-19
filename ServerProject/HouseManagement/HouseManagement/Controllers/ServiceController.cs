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
    public class ServiceController : ControllerBase
    {
        // GET: ServiceController
        private readonly DBContext _context;

        public ServiceController(DBContext context)
        {
            _context = context;
        }

        // GET: api/<ServiceController>
        [HttpGet]
        public IEnumerable<Service> Get()
        {
            return _context.Services;
        }

        // GET api/<ServiceController>/5
        [HttpGet("{id}")]
        public ActionResult<Service> Get(int id)
        {
            Service item = _context.Services.Find(id);
            if (item == null)
            {
                return NotFound();
            }
            return item;
        }

        // POST api/<ServiceController>
        [HttpPost]
        public ActionResult Post(Service item)
        {
            if (item.id == 0)
            {
                _context.Services.Add(item);
                _context.SaveChanges();
                return CreatedAtAction(nameof(Get), new { id = item.id }, item);
            }
            else
            {
                Service itemFromBase = _context.Services.Find(item.id);
                if (itemFromBase == null)
                {
                    using (var transaction = _context.Database.BeginTransaction())
                    {
                        _context.Services.Add(item);
                        _context.Database.ExecuteSqlRaw("SET IDENTITY_INSERT ApiHouseManagement.dbo.Services ON; ");
                        _context.SaveChanges();
                        _context.Database.ExecuteSqlRaw("SET IDENTITY_INSERT ApiHouseManagement.dbo.Services OFF; ");
                        transaction.Commit();
                    }
                    return CreatedAtAction(nameof(Get), new { id = item.id }, item);
                }
                else
                {
                    return BadRequest();
                }
            }
        }

        // PUT api/<ServiceController>/5
        [HttpPut("{id}")]
        public ActionResult<Service> Put(int id, Service item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.Services.Any(s => s.id == id))
                return NotFound();
            _context.Entry(item).State = EntityState.Modified;
            try
            {
                _context.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                return StatusCode(StatusCodes.Status500InternalServerError);
            }
            return NoContent();
        }

        // DELETE api/<ServiceController>/5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            Service itemFromBase = _context.Services.Find(id);
            if (itemFromBase != null)
            {
                _context.Services.Remove(itemFromBase);
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
