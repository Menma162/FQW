using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate18 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "paymentEndDate",
                table: "SettingsServices");

            migrationBuilder.DropColumn(
                name: "status",
                table: "Payments");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "paymentEndDate",
                table: "SettingsServices",
                type: "int",
                nullable: true);

            migrationBuilder.AddColumn<bool>(
                name: "status",
                table: "Payments",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }
    }
}
