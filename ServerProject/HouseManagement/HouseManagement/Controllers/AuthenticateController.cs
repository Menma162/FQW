using HouseManagement.Models;
using HouseManagement.OtherClasses;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace HouseManagement.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthenticateController : ControllerBase
    {
        private readonly UserManager<ApplicationUser> userManager;
        private readonly RoleManager<IdentityRole> roleManager;
        private readonly IConfiguration _configuration;
        public AuthenticateController(UserManager<ApplicationUser> userManager, RoleManager<IdentityRole> roleManager, IConfiguration configuration)
        {
            this.userManager = userManager;
            this.roleManager = roleManager;
            _configuration = configuration;
        }

        [HttpPost]
        [Route("login")]
        public async Task<IActionResult> Login(LoginModel model)
        {
            try
            {
                var user = await userManager.FindByNameAsync(model.Username);
                if (user != null && await userManager.CheckPasswordAsync(user, model.Password))
                {
                    var userRoles = await userManager.GetRolesAsync(user);
                    var authClaims = new List<Claim>
                { new Claim(ClaimTypes.Name, user.UserName), new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),};
                    foreach (var userRole in userRoles)
                    {
                        authClaims.Add(new Claim(ClaimTypes.Role, userRole));
                    }
                    var authSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["JWT:Secret"]));
                    var token = new JwtSecurityToken(issuer: _configuration["JWT:ValidIssuer"], audience: _configuration["JWT:ValidAudience"],
                    expires: DateTime.Now.AddDays(30), claims: authClaims, signingCredentials: new SigningCredentials(authSigningKey, SecurityAlgorithms.HmacSha256));
                    return Ok(new
                    {
                        token = new JwtSecurityTokenHandler().WriteToken(token),
                        expiration = token.ValidTo
                    });
                }
            }
            catch (Exception ex)
            {
                var ds = ex.Message;
            }
           
            return Unauthorized();
        }

        [HttpPost]
        [Route("register")]
        public async Task<IActionResult> RegisterAdmin(RegisterModel model)
        {
            var userExists = await
            userManager.FindByNameAsync(model.UserName);
            if (userExists != null)
                return StatusCode(StatusCodes.Status500InternalServerError, new ResponseModel { Status = "Error", Message = "User already exists!" });
            ApplicationUser user = new ApplicationUser()
            {
                SecurityStamp = Guid.NewGuid().ToString(),
                UserName = model.UserName
            };
            var result = await userManager.CreateAsync(user, model.Password);
            if (!result.Succeeded)
                return StatusCode(StatusCodes.Status500InternalServerError,
               new ResponseModel
               {
                   Status = "Error",
                   Message = "User creation failed! Please check user details and try again."
               });
            if (!await roleManager.RoleExistsAsync(UserRoles.MainAdmin))
                await roleManager.CreateAsync(new IdentityRole(UserRoles.MainAdmin));
            if (!await roleManager.RoleExistsAsync(UserRoles.HouseAdmin))
                await roleManager.CreateAsync(new IdentityRole(UserRoles.HouseAdmin));
            if (!await roleManager.RoleExistsAsync(UserRoles.FlatOwner))
                await roleManager.CreateAsync(new IdentityRole(UserRoles.FlatOwner));
            if (await roleManager.RoleExistsAsync(UserRoles.MainAdmin))
            {
                await userManager.AddToRoleAsync(user, UserRoles.MainAdmin);
            }
            return Ok(new ResponseModel
            {
                Status = "Success",
                Message = "User created successfully!"
            });
        }
    }
}