using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate15 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Residences",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    idFlatOwner = table.Column<int>(type: "int", nullable: false),
                    idFlat = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Residences", x => x.id);
                    table.ForeignKey(
                        name: "FK_Residences_FlatOwners_idFlatOwner",
                        column: x => x.idFlatOwner,
                        principalTable: "FlatOwners",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Residences_Flats_idFlat",
                        column: x => x.idFlat,
                        principalTable: "Flats",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_RelationshipHouses_idHouse",
                table: "RelationshipHouses",
                column: "idHouse");

            migrationBuilder.CreateIndex(
                name: "IX_Residences_idFlat",
                table: "Residences",
                column: "idFlat");

            migrationBuilder.CreateIndex(
                name: "IX_Residences_idFlatOwner",
                table: "Residences",
                column: "idFlatOwner");

            migrationBuilder.AddForeignKey(
                name: "FK_RelationshipHouses_Houses_idHouse",
                table: "RelationshipHouses",
                column: "idHouse",
                principalTable: "Houses",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_RelationshipHouses_Houses_idHouse",
                table: "RelationshipHouses");

            migrationBuilder.DropTable(
                name: "Residences");

            migrationBuilder.DropIndex(
                name: "IX_RelationshipHouses_idHouse",
                table: "RelationshipHouses");
        }
    }
}
