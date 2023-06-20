using HouseManagement.Models;
using HouseManagement.OtherClasses;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;

namespace HouseManagement.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
    public class UserController : ControllerBase
    {
        // GET: UserController
        private readonly DBContext _context;
        private readonly UserManager<ApplicationUser> userManager;
        private readonly RoleManager<IdentityRole> roleManager;
        private readonly IConfiguration _configuration;
        public UserController(UserManager<ApplicationUser> userManager, RoleManager<IdentityRole> roleManager, IConfiguration configuration, DBContext context)
        {
            this.userManager = userManager;
            this.roleManager = roleManager;
            _configuration = configuration;
            _context = context;
        }

        [Authorize(Roles = "HouseAdmin")]
        [HttpGet("house/houseAdmin/{id}")]
        public IEnumerable<UserModel> GetByHouseAdmin(string id)
        {
            var idHouses = _context.RelationshipHouses.Where(it => it.idUser == id).Select(it => it.idHouse).ToList();
            var users = new List<UserModel>();
            foreach (var idHouse in idHouses)
            {
                var idUsers = _context.RelationshipHouses.Where(it => it.idHouse == idHouse).Select(it => it.idUser).ToList();
                var flatowners = _context.FlatOwners.Where(it => it.idHouse == idHouse).Select(it => it.id).ToList();
                foreach(var flatowner in flatowners)
                {
                    var userItem = userManager.Users.FirstOrDefault(it => it.idFlatOwner == flatowner);
                    if(userItem != null) idUsers.Add(userItem.Id);
                }
                foreach (var idUser in idUsers)
                {
                    var user= userManager.Users.First(it => it.Id == idUser);
                    var idHousesItem = _context.RelationshipHouses.Where(it => it.idUser == user.Id).Select(it => it.idHouse).ToList();
                    var userRole = userManager.GetRolesAsync(user).Result[0];
                    UserModel userModel = new UserModel(user.Id, user.UserName, "", userRole, user.idFlatOwner, idHousesItem);
                    if (users.Where(it => it.id == userModel.id).Count() == 0) users.Add(userModel);
                }
            }
            return users;
        }

        [Authorize(Roles = "MainAdmin")]
        [HttpGet("house/mainAdmin")]
        public IEnumerable<UserModel> GetByMainAdmin()
        {
            var usersFtomDB = userManager.Users.ToList();
            var users = new List<UserModel>();
            foreach (var user in usersFtomDB)
            {
                var idHousesItem = _context.RelationshipHouses.Where(it => it.idUser == user.Id).Select(it => it.idHouse).ToList();
                var userRole = userManager.GetRolesAsync(user).Result[0];
                if (userRole == "HouseAdmin")
                    users.Add(new UserModel(user.Id, user.UserName, "", userRole, user.idFlatOwner, idHousesItem));
            }
            return users;
        }

        [Authorize(Roles = "FlatOwner,HouseAdmin")]
        [HttpGet("dateNow")]
        public DateTime GetDateNow()
        {
            return DateTime.Now;
        }

        [HttpGet("{id}")]
        public ActionResult<UserModel> Get(string id)
        {
            var userFtomDB = userManager.Users.Where(it => it.Id == id).FirstOrDefault();
            if (userFtomDB != null)
            {
                var userRole = userManager.GetRolesAsync(userFtomDB).Result[0];
                var idHousesItem = _context.RelationshipHouses.Where(it => it.idUser == userFtomDB.Id).Select(it => it.idHouse).ToList();
                return new UserModel(userFtomDB.Id, userFtomDB.UserName, "", userRole, userFtomDB.idFlatOwner, idHousesItem);
            }
            return BadRequest();
        }

        [HttpPut("password/{id}")]
        public ActionResult<UserModel> PutPassword(string id, UserModel item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!userManager.Users.Any(s => s.Id == id))
                return NotFound();
            try
            {
                var userFromDb = userManager.FindByIdAsync(id).Result;
                var token = userManager.GeneratePasswordResetTokenAsync(userFromDb).Result;
                var result = userManager.ResetPasswordAsync(userFromDb, token, item.password).Result;
                if (result.Succeeded)
                    return CreatedAtAction(nameof(Get), new { id = item.id }, item);
            }
            catch (DbUpdateConcurrencyException)
            {
                return StatusCode(StatusCodes.Status500InternalServerError);
            }
            return NoContent();
        }

        [HttpPut("login/{id}")]
        public ActionResult<UserModel> PutLogin(string id, UserModel item)
        {
            if (id != item.id)
            {
                return BadRequest();
            }
            if (!userManager.Users.Any(s => s.Id == id))
                return NotFound();
            try
            {
                var count = 0;
                if (item.role == "FlatOwner")
                {
                    count += _context.FlatOwners.Where(it => it.email == item.email).Where(it => it.id != item.idFlatOwner).Count();
                }
                var userWithEmail = userManager.FindByNameAsync(item.email).Result;
                if (userWithEmail != null && userWithEmail.Id != item.id) count += 1;
                if (count == 0)
                {
                    var userFromDb = userManager.FindByIdAsync(id).Result;
                    userFromDb.UserName = item.email;
                    var result = userManager.UpdateAsync(userFromDb).Result;
                    if (result.Succeeded)
                    {
                        if (item.role == "FlatOwner")
                        {
                            var flatOwner = _context.FlatOwners.Where(it => it.id == item.idFlatOwner).FirstOrDefault();
                            if (flatOwner != null)
                            {
                                flatOwner.email = item.email;
                                _context.Entry(flatOwner).State = EntityState.Modified;
                                _context.SaveChanges();
                            }
                        }
                        return CreatedAtAction(nameof(Get), new { id = item.id }, item);
                    }
                }
                else
                {
                    return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "Email уже занят"
               });
                }

            }
            catch (DbUpdateConcurrencyException)
            {
                return StatusCode(StatusCodes.Status500InternalServerError);
            }
            return NoContent();
        }

        [Authorize(Roles = "MainAdmin,HouseAdmin")]
        [HttpPost]
        public async Task<ActionResult<UserModel>> PostAsync(UserModel item)
        {
            if (item.id != null)
            {
                return BadRequest();
            }

            var userExists = await userManager.FindByNameAsync(item.email);
            var flatOwner = _context.FlatOwners.FirstOrDefault(it => it.email == item.email);
            if (flatOwner != null && flatOwner.id == item.idFlatOwner) flatOwner = null;
            if (userExists != null || flatOwner != null)
                return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel { Status = "Error", Message = "Такая почта уже занята" });

            ApplicationUser user = new ApplicationUser()
            {
                SecurityStamp = Guid.NewGuid().ToString(),
                UserName = item.email,
                idFlatOwner = item.idFlatOwner
            };

            int count = 0;
            item.idHouses.Sort();
            foreach (var idHouse in item.idHouses)
            {
                var idUsers = _context.RelationshipHouses.Where(it => it.idHouse == idHouse).Select(it => it.idUser).ToList();
                foreach (var idUser in idUsers)
                {
                    var userDb = await userManager.FindByIdAsync(idUser);
                    var userRole = userManager.GetRolesAsync(userDb).Result[0];
                    if (userRole == UserRoles.HouseAdmin)
                    {
                        var idHousesItem = _context.RelationshipHouses.Where(it => it.idUser == idUser).Select(it => it.idHouse).ToList();
                        idHousesItem.Sort();
                        if (!idHousesItem.SequenceEqual(idHousesItem)) count++;
                    }
                }
            }

            if (count != 0) return BadRequest();

            var result = await userManager.CreateAsync(user, item.password);
            if (!result.Succeeded)
                return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "Не удалось создать пользователя"
               });

            if (!await roleManager.RoleExistsAsync(UserRoles.MainAdmin))
                await roleManager.CreateAsync(new IdentityRole(UserRoles.MainAdmin));
            if (!await roleManager.RoleExistsAsync(UserRoles.HouseAdmin))
                await roleManager.CreateAsync(new IdentityRole(UserRoles.HouseAdmin));
            if (!await roleManager.RoleExistsAsync(UserRoles.FlatOwner))
                await roleManager.CreateAsync(new IdentityRole(UserRoles.FlatOwner));

            await userManager.AddToRoleAsync(user, item.role);

            var idHouses = item.idHouses;
            foreach (var idHouse in idHouses)
            {
                _context.RelationshipHouses.Add(new RelationshipHouses(user.Id, idHouse));
                _context.SaveChanges();
            }

            return Ok();
        }

        [Authorize(Roles = "MainAdmin,HouseAdmin")]
        [HttpDelete("{id}")]
        public ActionResult Delete(string id)
        {
            var userFromDb = userManager.FindByIdAsync(id).Result;
            if (userFromDb != null)
            {
                userManager.DeleteAsync(userFromDb);

                var relations = _context.RelationshipHouses.Where(it => it.idUser == id).ToList();
                _context.RemoveRange(relations);
                _context.SaveChanges();
                return StatusCode(StatusCodes.Status204NoContent);
            }
            else
            {
                return BadRequest();
            }
        }

        [HttpGet("username/{username}")]
        public ActionResult<UserModel> GetUser(String username)
        {
            var userFromDb = userManager.FindByNameAsync(username).Result;
            if (userFromDb != null)
            {
                var idHouses = new List<int>();
                var userRole = userManager.GetRolesAsync(userFromDb).Result[0];
                if (userRole == UserRoles.HouseAdmin)
                    idHouses = _context.RelationshipHouses.Where(it => it.idUser == userFromDb.Id).Select(it => it.idHouse).ToList();
                if (userRole == UserRoles.FlatOwner)
                    idHouses = _context.FlatOwners.Where(it => it.id == userFromDb.idFlatOwner).Select(it => it.idHouse).ToList();

                return (new UserModel(userFromDb.Id, userFromDb.UserName, "", userRole, userFromDb.idFlatOwner, idHouses));
            }
            else return BadRequest();
        }
    }
}
