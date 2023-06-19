using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate16 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Flats_FlatOwners_idFlatOwner",
                table: "Flats");

            migrationBuilder.DropIndex(
                name: "IX_Flats_idFlatOwner",
                table: "Flats");

            migrationBuilder.DropColumn(
                name: "idFlatOwner",
                table: "Flats");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "idFlatOwner",
                table: "Flats",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Flats_idFlatOwner",
                table: "Flats",
                column: "idFlatOwner");

            migrationBuilder.AddForeignKey(
                name: "FK_Flats_FlatOwners_idFlatOwner",
                table: "Flats",
                column: "idFlatOwner",
                principalTable: "FlatOwners",
                principalColumn: "id");
        }
    }
}
