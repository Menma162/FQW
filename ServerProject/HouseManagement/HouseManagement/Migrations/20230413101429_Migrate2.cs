using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate2 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "status",
                table: "Advertisements");

            migrationBuilder.AddColumn<DateTime>(
                name: "date",
                table: "Advertisements",
                type: "datetime2",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "date",
                table: "Advertisements");

            migrationBuilder.AddColumn<string>(
                name: "status",
                table: "Advertisements",
                type: "nvarchar(max)",
                nullable: false,
                defaultValue: "");
        }
    }
}
