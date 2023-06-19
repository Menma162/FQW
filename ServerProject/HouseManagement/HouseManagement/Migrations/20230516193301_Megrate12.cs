using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Megrate12 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Payments_Indications_idIndication",
                table: "Payments");

            migrationBuilder.DropIndex(
                name: "IX_Payments_idIndication",
                table: "Payments");

            migrationBuilder.DropColumn(
                name: "idIndication",
                table: "Payments");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "idIndication",
                table: "Payments",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Payments_idIndication",
                table: "Payments",
                column: "idIndication");

            migrationBuilder.AddForeignKey(
                name: "FK_Payments_Indications_idIndication",
                table: "Payments",
                column: "idIndication",
                principalTable: "Indications",
                principalColumn: "id");
        }
    }
}
