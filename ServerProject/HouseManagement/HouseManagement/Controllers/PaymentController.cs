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
    public class PaymentController : ControllerBase
    {
        // GET: HouseController
        private readonly DBContext _context;

        public PaymentController(DBContext context)
        {
            _context = context;
        }

        // GET: api/<PaymentController>
        [HttpGet]
        public IEnumerable<Payment> Get()
        {
            return _context.Payments;
        }

        // GET api/<PaymentController>/5
        [HttpGet("{id}")]
        public ActionResult<Payment> Get(int id)
        {
            Payment item = _context.Payments.Find(id);
            if (item == null)
            {
                return NotFound();
            }
            return item;
        }

        [HttpGet("flatowner/{id}")]
        public IEnumerable<Payment> GetPaymentsByIdFlat(int id)
        {
            var idFlats = _context.Residences.Where(it => it.idFlatOwner == id);
            var payments = new List<Payment>();
            foreach (var flat in idFlats)
            {
                payments.AddRange(_context.Payments.Where(it => it.idFlat == flat.idFlat));
            }
            return payments;
        }

        [HttpGet("admin/{id}")]
        public IEnumerable<Payment> GetPaymentsByAdmin(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            var payments = new List<Payment>();
            foreach (var idhouse  in idHouses)
            {
                var flats = _context.Flats.Where(it => it.idHouse == idhouse);
                foreach (var flat in flats)
                {
                    payments.AddRange(_context.Payments.Where(it => it.idFlat == flat.id));
                }
            }
            
            return payments;
        }

        [HttpGet("house/{id}")]
        public IEnumerable<Payment> GetPaymentsByHouse(int id)
        {
            var flats = _context.Flats.Where(it => it.idHouse == id);
            var payments = new List<Payment>();
            foreach (var flat in flats)
            {
                payments.AddRange(_context.Payments.Where(it => it.idFlat == flat.id));
            }
            return payments;
        }

        // POST api/<PaymentController>
        [HttpPost]
        public ActionResult Post(List<Payment> item)
        {
            if (item.Count == 0) return BadRequest();
            int count = _context.Payments.Where(it => it.period == item[0].period).Count(); 
            if (count == 0)
            {
                item.ForEach(it => it.amount = Math.Round(it.amount, 2));
                foreach (var payment in item)
                {
                    _context.Payments.Add(payment);
                    _context.Payments.Where(it => it.period == item[0].period && it.idFlat == payment.idFlat).Count();
                }
                if (count != 0) return StatusCode(StatusCodes.Status500InternalServerError,
                   new ResponseModel
                   {
                       Status = "Error",
                       Message = "За этот период уже произведены начисления"
                   });
                _context.SaveChanges();
                return StatusCode(StatusCodes.Status200OK);
            }
            else
            {
                return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "Начисления за этот период уже произведены"
               });
            }
        }

        // PUT api/<PaymentController>/5
        [HttpPut("{id}")]
        public ActionResult<Payment> Put(int id, Payment item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.Payments.Any(s => s.id == id))
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

        // DELETE api/<PaymentController>/5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            Payment itemFromBase = _context.Payments.Find(id);
            if (itemFromBase != null)
            {
                _context.Payments.Remove(itemFromBase);
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
