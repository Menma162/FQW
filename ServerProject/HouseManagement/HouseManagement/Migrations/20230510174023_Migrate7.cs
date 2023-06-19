using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate7 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "idHouse",
                table: "FlatOwners",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_FlatOwners_idHouse",
                table: "FlatOwners",
                column: "idHouse");

            migrationBuilder.AddForeignKey(
                name: "FK_FlatOwners_Houses_idHouse",
                table: "FlatOwners",
                column: "idHouse",
                principalTable: "Houses",
                principalColumn: "id");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_FlatOwners_Houses_idHouse",
                table: "FlatOwners");

            migrationBuilder.DropIndex(
                name: "IX_FlatOwners_idHouse",
                table: "FlatOwners");

            migrationBuilder.DropColumn(
                name: "idHouse",
                table: "FlatOwners");
        }
    }
}
