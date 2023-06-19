using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate9 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "typeIMD",
                table: "SettingsServices",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "typeIMD",
                table: "SettingsServices");
        }
    }
}
