using HouseManagement.Models;
using HouseManagement.OtherClasses;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Newtonsoft.Json;
using NuGet.Protocol;
using System.IO;
using System.Xml.Linq;

namespace HouseManagement.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class ComplaintController : ControllerBase
    {
        // GET: ComplaintController
        private readonly DBContext _context;
        private readonly IWebHostEnvironment _appEnvironment;

        public ComplaintController(DBContext context, IWebHostEnvironment appEnvironment)
        {
            _context = context;
            _appEnvironment = appEnvironment;
        }

        // GET: api/<ComplaintController>
        [HttpGet]
        public IEnumerable<Complaint> Get()
        {
            return _context.Complaints;
        }

        [HttpGet("house/{id}")]
        public IEnumerable<Complaint> GetByHouse(int id)
        {
            var flats = _context.Flats.Where(it => it.idHouse == id);
            var complaints = new List<Complaint>();
            foreach (var flat in flats)
            {
                complaints.AddRange(_context.Complaints.Where(it => it.idFlat == flat.id));
            }
            return complaints;
        }

        [HttpGet("admin/{id}")]
        public IEnumerable<Complaint> GetByAdmin(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            List<Flat> flats = new List<Flat>();
            foreach (var house in idHouses)
            {
                flats.AddRange(_context.Flats.Where(it => it.idHouse == house));
            }
            var complaints = new List<Complaint>();
            foreach (var flat in flats)
            {
                complaints.AddRange(_context.Complaints.Where(it => it.idFlat == flat.id));
            }
            return complaints;
        }

        // GET api/<ComplaintController>/5
        [HttpGet("{id}")]
        public ActionResult<Complaint> Get(int id)
        {
            Complaint item = _context.Complaints.Find(id);
            if (item == null)
            {
                return NotFound();
            }
            return item;
        }

        [HttpGet("getPhoto/{id}")]
        public async Task<ActionResult<string?>> GetPhotoAsync(int id)
        {
            Complaint? item = _context.Complaints.Find(id);
            if (item == null) return NotFound();
            var result = (await GetBase64("../HouseManagement/Images/" + item.photo));
            return result;
        }

        [HttpGet("getPhotoMobile/{id}")]
        public async Task<ActionResult<byte[]?>> GetPhotoMobileAsync(int id)
        {
            Complaint? item = _context.Complaints.Find(id);
            if (item == null) return NotFound();
            var result = (GetBase64Byte("../HouseManagement/Images/" + item.photo));
            return result;
        }

        private static async Task<string?> GetBase64(string path)
        {
            if (System.IO.File.Exists(path))
            {
                byte[] fileBytes = await System.IO.File.ReadAllBytesAsync(path);
                string base64String = Convert.ToBase64String(fileBytes);
                return base64String;
            }
            return null;
        }

        private static byte[]? GetBase64Byte(string path)
        {
            if (System.IO.File.Exists(path))
            {
                byte[] fileBytes = System.IO.File.ReadAllBytes(path);
                return fileBytes;
            }
            return null;
        }

        [HttpGet("flatowner/{id}")]
        public IEnumerable<Complaint> GetComplaintsByIdFlat(int id)
        {
            var idFlats = _context.Residences.Where(it => it.idFlatOwner == id);
            var complaints = new List<Complaint>();
            foreach (var flat in idFlats)
            {
                complaints.AddRange(_context.Complaints.Where(it => it.idFlat == flat.idFlat));
            }
            return complaints;
        }

        [HttpPost]
        public async Task<ActionResult> PostAsync(Complaint item)
        {
            if (item.id == 0)
            {
                _context.Complaints.Add(item);
                _context.SaveChanges();

                return CreatedAtAction(nameof(Get), new { id = item.id }, item);
            }
            else
            {
                return BadRequest();
            }
        }

        [HttpPost("mvc")]
        public async Task<ActionResult> PostMvcAsync(Complaint item)
        {
            if (item.id == 0)
            {
                _context.Complaints.Add(item);
                _context.SaveChanges();

                return StatusCode(StatusCodes.Status201Created,
               new ResponseModel
               {
                   Status = "Success",
                   Message = item.id.ToString()
               });
            }
            else
            {
                return BadRequest();
            }
        }

        [HttpPut("photo/{id}")]
        public async Task<ActionResult> PutPhoto(IFormFile photo, int id)
        {
            if (id != 0)
            {

                if (!_context.Complaints.Any(s => s.id == id))
                    return NotFound();

                var name = await Save("../HouseManagement/Images/", photo, id);

                Complaint complaint = _context.Complaints.First(s => s.id == id);
                complaint.photo = name;

                _context.Entry(complaint).State = EntityState.Modified;
                try
                {
                    _context.SaveChanges();
                    return CreatedAtAction(nameof(Get), new { id = complaint.id }, complaint);
                }
                catch (DbUpdateConcurrencyException)
                {
                    return StatusCode(StatusCodes.Status500InternalServerError);
                }
            }
            else
            {
                return BadRequest();
            }
        }


        private static async Task<string> Save(string path, IFormFile file, int id)
        {
            string photoName = id.ToString() + ".png";
            path += photoName;
            using (var stream = System.IO.File.Create(path))
            {
                await file.CopyToAsync(stream);
            }
            return photoName;
        }
        private static async Task<string> Save(string path, string base64, int id)
        {
            string photoName = id.ToString() + ".png";
            path += photoName;
            using (var stream = System.IO.File.Create(path))
            {
                var file = Convert.FromBase64String(base64);
                await System.IO.File.WriteAllBytesAsync(path, file);
                //await file.CopyToAsync(stream);
            }
            return photoName;
        }

        // PUT api/<ComplaintController>/5
        [HttpPut("{id}")]
        public ActionResult<Complaint> Put(int id, Complaint item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!_context.Complaints.Any(s => s.id == id))
                return NotFound();
            _context.Entry(item).State = EntityState.Modified;
            try
            {
                string path = "../HouseManagement/Images/" + id + ".png";
                if (System.IO.File.Exists(path) && item.photo == null) 
                    System.IO.File.Delete(path);
                _context.SaveChanges();
                return CreatedAtAction(nameof(Get), new { id = item.id }, item);
            }
            catch (DbUpdateConcurrencyException)
            {
                return StatusCode(StatusCodes.Status500InternalServerError);
            }
        }

        // DELETE api/<ComplaintController>/5
        [HttpDelete("{id}")]
        public ActionResult Delete(int id)
        {
            Complaint itemFromBase = _context.Complaints.Find(id);
            if (itemFromBase != null)
            {
                if(itemFromBase.photo != null)
                {
                    string path = "../HouseManagement/Images/" + itemFromBase.photo;
                    if (System.IO.File.Exists(path)) System.IO.File.Delete(path);
                }
                _context.Complaints.Remove(itemFromBase);
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
