using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate17 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_FlatOwners_Houses_idHouse",
                table: "FlatOwners");

            migrationBuilder.DropColumn(
                name: "dateOfRegistration",
                table: "FlatOwners");

            migrationBuilder.AlterColumn<int>(
                name: "idHouse",
                table: "FlatOwners",
                type: "int",
                nullable: false,
                defaultValue: 0,
                oldClrType: typeof(int),
                oldType: "int",
                oldNullable: true);

            migrationBuilder.AddForeignKey(
                name: "FK_FlatOwners_Houses_idHouse",
                table: "FlatOwners",
                column: "idHouse",
                principalTable: "Houses",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_FlatOwners_Houses_idHouse",
                table: "FlatOwners");

            migrationBuilder.AlterColumn<int>(
                name: "idHouse",
                table: "FlatOwners",
                type: "int",
                nullable: true,
                oldClrType: typeof(int),
                oldType: "int");

            migrationBuilder.AddColumn<DateTime>(
                name: "dateOfRegistration",
                table: "FlatOwners",
                type: "datetime2",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddForeignKey(
                name: "FK_FlatOwners_Houses_idHouse",
                table: "FlatOwners",
                column: "idHouse",
                principalTable: "Houses",
                principalColumn: "id");
        }
    }
}
