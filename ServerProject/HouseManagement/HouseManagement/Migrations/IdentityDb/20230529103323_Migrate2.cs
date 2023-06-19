using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations.IdentityDb
{
    public partial class Migrate2 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "idHouse",
                table: "AspNetUsers");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "idHouse",
                table: "AspNetUsers",
                type: "int",
                nullable: true);
        }
    }
}
