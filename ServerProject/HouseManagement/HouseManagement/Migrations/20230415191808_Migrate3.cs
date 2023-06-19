using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate3 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Counters_House_idHouse",
                table: "Counters");

            migrationBuilder.DropForeignKey(
                name: "FK_Flats_House_idHouse",
                table: "Flats");

            migrationBuilder.DropPrimaryKey(
                name: "PK_House",
                table: "House");

            migrationBuilder.RenameTable(
                name: "House",
                newName: "Houses");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Houses",
                table: "Houses",
                column: "id");

            migrationBuilder.CreateTable(
                name: "SettingsServices",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    paymentStatus = table.Column<bool>(type: "bit", nullable: false),
                    paymentEndDate = table.Column<int>(type: "int", nullable: true),
                    valueRate = table.Column<double>(type: "float", nullable: false),
                    valueNormative = table.Column<double>(type: "float", nullable: false),
                    haveCounter = table.Column<bool>(type: "bit", nullable: false),
                    startDateTransfer = table.Column<int>(type: "int", nullable: true),
                    endDateTransfer = table.Column<int>(type: "int", nullable: true),
                    paymentPeriod = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    idService = table.Column<int>(type: "int", nullable: false),
                    idHouse = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SettingsServices", x => x.id);
                    table.ForeignKey(
                        name: "FK_SettingsServices_Houses_idHouse",
                        column: x => x.idHouse,
                        principalTable: "Houses",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_SettingsServices_Services_idService",
                        column: x => x.idService,
                        principalTable: "Services",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_SettingsServices_idHouse",
                table: "SettingsServices",
                column: "idHouse");

            migrationBuilder.CreateIndex(
                name: "IX_SettingsServices_idService",
                table: "SettingsServices",
                column: "idService");

            migrationBuilder.AddForeignKey(
                name: "FK_Counters_Houses_idHouse",
                table: "Counters",
                column: "idHouse",
                principalTable: "Houses",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Flats_Houses_idHouse",
                table: "Flats",
                column: "idHouse",
                principalTable: "Houses",
                principalColumn: "id");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Counters_Houses_idHouse",
                table: "Counters");

            migrationBuilder.DropForeignKey(
                name: "FK_Flats_Houses_idHouse",
                table: "Flats");

            migrationBuilder.DropTable(
                name: "SettingsServices");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Houses",
                table: "Houses");

            migrationBuilder.RenameTable(
                name: "Houses",
                newName: "House");

            migrationBuilder.AddPrimaryKey(
                name: "PK_House",
                table: "House",
                column: "id");

            migrationBuilder.AddForeignKey(
                name: "FK_Counters_House_idHouse",
                table: "Counters",
                column: "idHouse",
                principalTable: "House",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Flats_House_idHouse",
                table: "Flats",
                column: "idHouse",
                principalTable: "House",
                principalColumn: "id");
        }
    }
}
