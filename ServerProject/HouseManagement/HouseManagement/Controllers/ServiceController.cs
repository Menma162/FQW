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
        private readonly DBContext _context;

        public ServiceController(DBContext context)
        {
            _context = context;
        }

        [Authorize(Roles = "HouseAdmin,FlatOwner")]
        [HttpGet]
        public IEnumerable<Service> Get()
        {
            return _context.Services;
        }

        [Authorize(Roles = "HouseAdmin")]
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
    }
}
