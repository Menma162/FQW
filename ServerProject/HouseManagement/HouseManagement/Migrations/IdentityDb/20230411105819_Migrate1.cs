using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations.IdentityDb
{
    public partial class Migrate1 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_AspNetUsers_FlatOwner_idFlatOwner",
                table: "AspNetUsers");

            migrationBuilder.DropForeignKey(
                name: "FK_AspNetUsers_House_idHouse",
                table: "AspNetUsers");

            migrationBuilder.DropTable(
                name: "FlatOwner");

            migrationBuilder.DropTable(
                name: "House");

            migrationBuilder.DropIndex(
                name: "IX_AspNetUsers_idFlatOwner",
                table: "AspNetUsers");

            migrationBuilder.DropIndex(
                name: "IX_AspNetUsers_idHouse",
                table: "AspNetUsers");

            migrationBuilder.AlterColumn<int>(
                name: "idHouse",
                table: "AspNetUsers",
                type: "int",
                nullable: true,
                oldClrType: typeof(int),
                oldType: "int");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AlterColumn<int>(
                name: "idHouse",
                table: "AspNetUsers",
                type: "int",
                nullable: false,
                defaultValue: 0,
                oldClrType: typeof(int),
                oldType: "int",
                oldNullable: true);

            migrationBuilder.CreateTable(
                name: "FlatOwner",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    dateOfRegistration = table.Column<DateTime>(type: "datetime2", nullable: false),
                    email = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    fullName = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    phoneNumber = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_FlatOwner", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "House",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    house = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_House", x => x.id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_AspNetUsers_idFlatOwner",
                table: "AspNetUsers",
                column: "idFlatOwner");

            migrationBuilder.CreateIndex(
                name: "IX_AspNetUsers_idHouse",
                table: "AspNetUsers",
                column: "idHouse");

            migrationBuilder.AddForeignKey(
                name: "FK_AspNetUsers_FlatOwner_idFlatOwner",
                table: "AspNetUsers",
                column: "idFlatOwner",
                principalTable: "FlatOwner",
                principalColumn: "id");

            migrationBuilder.AddForeignKey(
                name: "FK_AspNetUsers_House_idHouse",
                table: "AspNetUsers",
                column: "idHouse",
                principalTable: "House",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
