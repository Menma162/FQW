using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace HouseManagement.Migrations
{
    public partial class Initial : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Advertisements",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    status = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    description = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Advertisements", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "FlatOwners",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    fullName = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    email = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    phoneNumber = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    dateOfRegistration = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_FlatOwners", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "Services",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    nameService = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    paymentStatus = table.Column<bool>(type: "bit", nullable: false),
                    paymentEndDate = table.Column<int>(type: "int", nullable: true),
                    valueRate = table.Column<int>(type: "int", nullable: false),
                    valueNormative = table.Column<int>(type: "int", nullable: false),
                    haveCounter = table.Column<bool>(type: "bit", nullable: false),
                    nameCounter = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    startDateTransfer = table.Column<int>(type: "int", nullable: true),
                    endDateTransfer = table.Column<int>(type: "int", nullable: true),
                    paymentPeriod = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Services", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "Flats",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    personalAccount = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    flatNumber = table.Column<int>(type: "int", nullable: false),
                    totalArea = table.Column<double>(type: "float", nullable: false),
                    usableArea = table.Column<double>(type: "float", nullable: false),
                    entranceNumber = table.Column<int>(type: "int", nullable: false),
                    numberOfRooms = table.Column<int>(type: "int", nullable: false),
                    numberOfRegisteredResidents = table.Column<int>(type: "int", nullable: false),
                    number_of_owners = table.Column<int>(type: "int", nullable: false),
                    idFlatOwner = table.Column<int>(type: "int", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Flats", x => x.id);
                    table.ForeignKey(
                        name: "FK_Flats_FlatOwners_idFlatOwner",
                        column: x => x.idFlatOwner,
                        principalTable: "FlatOwners",
                        principalColumn: "id");
                });

            migrationBuilder.CreateTable(
                name: "Complaints",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    status = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    description = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    photo = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    date = table.Column<DateTime>(type: "datetime2", nullable: false),
                    idFlat = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Complaints", x => x.id);
                    table.ForeignKey(
                        name: "FK_Complaints_Flats_idFlat",
                        column: x => x.idFlat,
                        principalTable: "Flats",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Counters",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    number = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    used = table.Column<bool>(type: "bit", nullable: false),
                    IMDOrGHMD = table.Column<bool>(type: "bit", nullable: false),
                    dateLastVerification = table.Column<DateTime>(type: "datetime2", nullable: false),
                    dateNextVerification = table.Column<DateTime>(type: "datetime2", nullable: false),
                    idService = table.Column<int>(type: "int", nullable: false),
                    idFlat = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Counters", x => x.id);
                    table.ForeignKey(
                        name: "FK_Counters_Flats_idFlat",
                        column: x => x.idFlat,
                        principalTable: "Flats",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Counters_Services_idService",
                        column: x => x.idService,
                        principalTable: "Services",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Indications",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    dateTransfer = table.Column<DateTime>(type: "datetime2", nullable: false),
                    value = table.Column<int>(type: "int", nullable: false),
                    idCounter = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Indications", x => x.id);
                    table.ForeignKey(
                        name: "FK_Indications_Counters_idCounter",
                        column: x => x.idCounter,
                        principalTable: "Counters",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Payments",
                columns: table => new
                {
                    id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    period = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    amount = table.Column<double>(type: "float", nullable: false),
                    penalties = table.Column<double>(type: "float", nullable: false),
                    status = table.Column<bool>(type: "bit", nullable: false),
                    idService = table.Column<int>(type: "int", nullable: false),
                    idFlat = table.Column<int>(type: "int", nullable: false),
                    idIndication = table.Column<int>(type: "int", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Payments", x => x.id);
                    table.ForeignKey(
                        name: "FK_Payments_Flats_idFlat",
                        column: x => x.idFlat,
                        principalTable: "Flats",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Payments_Indications_idIndication",
                        column: x => x.idIndication,
                        principalTable: "Indications",
                        principalColumn: "id");
                    table.ForeignKey(
                        name: "FK_Payments_Services_idService",
                        column: x => x.idService,
                        principalTable: "Services",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Complaints_idFlat",
                table: "Complaints",
                column: "idFlat");

            migrationBuilder.CreateIndex(
                name: "IX_Counters_idFlat",
                table: "Counters",
                column: "idFlat");

            migrationBuilder.CreateIndex(
                name: "IX_Counters_idService",
                table: "Counters",
                column: "idService");

            migrationBuilder.CreateIndex(
                name: "IX_Flats_idFlatOwner",
                table: "Flats",
                column: "idFlatOwner");

            migrationBuilder.CreateIndex(
                name: "IX_Indications_idCounter",
                table: "Indications",
                column: "idCounter");

            migrationBuilder.CreateIndex(
                name: "IX_Payments_idFlat",
                table: "Payments",
                column: "idFlat");

            migrationBuilder.CreateIndex(
                name: "IX_Payments_idIndication",
                table: "Payments",
                column: "idIndication");

            migrationBuilder.CreateIndex(
                name: "IX_Payments_idService",
                table: "Payments",
                column: "idService");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Advertisements");

            migrationBuilder.DropTable(
                name: "Complaints");

            migrationBuilder.DropTable(
                name: "Payments");

            migrationBuilder.DropTable(
                name: "Indications");

            migrationBuilder.DropTable(
                name: "Counters");

            migrationBuilder.DropTable(
                name: "Flats");

            migrationBuilder.DropTable(
                name: "Services");

            migrationBuilder.DropTable(
                name: "FlatOwners");
        }
    }
}
