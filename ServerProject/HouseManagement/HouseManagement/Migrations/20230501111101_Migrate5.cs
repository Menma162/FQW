using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate5 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "idHouse",
                table: "Advertisements",
                type: "int",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.CreateIndex(
                name: "IX_Advertisements_idHouse",
                table: "Advertisements",
                column: "idHouse");

            migrationBuilder.AddForeignKey(
                name: "FK_Advertisements_Houses_idHouse",
                table: "Advertisements",
                column: "idHouse",
                principalTable: "Houses",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Advertisements_Houses_idHouse",
                table: "Advertisements");

            migrationBuilder.DropIndex(
                name: "IX_Advertisements_idHouse",
                table: "Advertisements");

            migrationBuilder.DropColumn(
                name: "idHouse",
                table: "Advertisements");
        }
    }
}
