using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Migrate1 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Counters_Flats_idFlat",
                table: "Counters");

            migrationBuilder.DropColumn(
                name: "endDateTransfer",
                table: "Services");

            migrationBuilder.DropColumn(
                name: "haveCounter",
                table: "Services");

            migrationBuilder.DropColumn(
                name: "paymentEndDate",
                table: "Services");

            migrationBuilder.DropColumn(
                name: "paymentPeriod",
                table: "Services");

            migrationBuilder.DropColumn(
                name: "paymentStatus",
                table: "Services");

            migrationBuilder.DropColumn(
                name: "startDateTransfer",
                table: "Services");

            migrationBuilder.DropColumn(
                name: "valueNormative",
                table: "Services");

            migrationBuilder.DropColumn(
                name: "valueRate",
                table: "Services");

            migrationBuilder.AddColumn<int>(
                name: "idHouse",
                table: "Flats",
                type: "int",
                nullable: true);

            migrationBuilder.AlterColumn<int>(
                name: "idFlat",
                table: "Counters",
                type: "int",
                nullable: true,
                oldClrType: typeof(int),
                oldType: "int");

            migrationBuilder.AddColumn<int>(
                name: "idHouse",
                table: "Counters",
                type: "int",
                nullable: false,
                defaultValue: 0);

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
                name: "IX_Flats_idHouse",
                table: "Flats",
                column: "idHouse");

            migrationBuilder.CreateIndex(
                name: "IX_Counters_idHouse",
                table: "Counters",
                column: "idHouse");

            migrationBuilder.AddForeignKey(
                name: "FK_Counters_Flats_idFlat",
                table: "Counters",
                column: "idFlat",
                principalTable: "Flats",
                principalColumn: "id");

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

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Counters_Flats_idFlat",
                table: "Counters");

            migrationBuilder.DropForeignKey(
                name: "FK_Counters_House_idHouse",
                table: "Counters");

            migrationBuilder.DropForeignKey(
                name: "FK_Flats_House_idHouse",
                table: "Flats");

            migrationBuilder.DropTable(
                name: "House");

            migrationBuilder.DropIndex(
                name: "IX_Flats_idHouse",
                table: "Flats");

            migrationBuilder.DropIndex(
                name: "IX_Counters_idHouse",
                table: "Counters");

            migrationBuilder.DropColumn(
                name: "idHouse",
                table: "Flats");

            migrationBuilder.DropColumn(
                name: "idHouse",
                table: "Counters");

            migrationBuilder.AddColumn<int>(
                name: "endDateTransfer",
                table: "Services",
                type: "int",
                nullable: true);

            migrationBuilder.AddColumn<bool>(
                name: "haveCounter",
                table: "Services",
                type: "bit",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<int>(
                name: "paymentEndDate",
                table: "Services",
                type: "int",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "paymentPeriod",
                table: "Services",
                type: "nvarchar(max)",
                nullable: true);

            migrationBuilder.AddColumn<bool>(
                name: "paymentStatus",
                table: "Services",
                type: "bit",
                nullable: false,
                defaultValue: false);

            migrationBuilder.AddColumn<int>(
                name: "startDateTransfer",
                table: "Services",
                type: "int",
                nullable: true);

            migrationBuilder.AddColumn<int>(
                name: "valueNormative",
                table: "Services",
                type: "int",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "valueRate",
                table: "Services",
                type: "int",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AlterColumn<int>(
                name: "idFlat",
                table: "Counters",
                type: "int",
                nullable: false,
                defaultValue: 0,
                oldClrType: typeof(int),
                oldType: "int",
                oldNullable: true);

            migrationBuilder.AddForeignKey(
                name: "FK_Counters_Flats_idFlat",
                table: "Counters",
                column: "idFlat",
                principalTable: "Flats",
                principalColumn: "id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
