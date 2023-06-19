﻿// <auto-generated />
using System;
using HouseManagement.OtherClasses;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Metadata;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;

#nullable disable

namespace HouseManagement.Migrations
{
    [DbContext(typeof(DBContext))]
    [Migration("20230604172408_Migrate17")]
    partial class Migrate17
    {
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "6.0.0")
                .HasAnnotation("Relational:MaxIdentifierLength", 128);

            SqlServerModelBuilderExtensions.UseIdentityColumns(modelBuilder, 1L, 1);

            modelBuilder.Entity("HouseManagement.Models.Advertisement", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<DateTime>("date")
                        .HasColumnType("datetime2");

                    b.Property<string>("description")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.Property<int>("idHouse")
                        .HasColumnType("int");

                    b.HasKey("id");

                    b.HasIndex("idHouse");

                    b.ToTable("Advertisements");
                });

            modelBuilder.Entity("HouseManagement.Models.Complaint", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<DateTime>("date")
                        .HasColumnType("datetime2");

                    b.Property<string>("description")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.Property<int>("idFlat")
                        .HasColumnType("int");

                    b.Property<string>("photo")
                        .HasColumnType("nvarchar(max)");

                    b.Property<string>("status")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.HasKey("id");

                    b.HasIndex("idFlat");

                    b.ToTable("Complaints");
                });

            modelBuilder.Entity("HouseManagement.Models.Counter", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<bool>("IMDOrGHMD")
                        .HasColumnType("bit");

                    b.Property<DateTime>("dateLastVerification")
                        .HasColumnType("datetime2");

                    b.Property<DateTime>("dateNextVerification")
                        .HasColumnType("datetime2");

                    b.Property<int?>("idFlat")
                        .HasColumnType("int");

                    b.Property<int>("idHouse")
                        .HasColumnType("int");

                    b.Property<int>("idService")
                        .HasColumnType("int");

                    b.Property<string>("number")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.Property<bool>("used")
                        .HasColumnType("bit");

                    b.HasKey("id");

                    b.HasIndex("idFlat");

                    b.HasIndex("idHouse");

                    b.HasIndex("idService");

                    b.ToTable("Counters");
                });

            modelBuilder.Entity("HouseManagement.Models.Flat", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<int>("entranceNumber")
                        .HasColumnType("int");

                    b.Property<string>("flatNumber")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.Property<int?>("idHouse")
                        .HasColumnType("int");

                    b.Property<int>("numberOfOwners")
                        .HasColumnType("int");

                    b.Property<int>("numberOfRegisteredResidents")
                        .HasColumnType("int");

                    b.Property<int>("numberOfRooms")
                        .HasColumnType("int");

                    b.Property<string>("personalAccount")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.Property<double>("totalArea")
                        .HasColumnType("float");

                    b.Property<double>("usableArea")
                        .HasColumnType("float");

                    b.HasKey("id");

                    b.HasIndex("idHouse");

                    b.ToTable("Flats");
                });

            modelBuilder.Entity("HouseManagement.Models.FlatOwner", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<string>("email")
                        .HasColumnType("nvarchar(max)");

                    b.Property<string>("fullName")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.Property<int>("idHouse")
                        .HasColumnType("int");

                    b.Property<string>("phoneNumber")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.HasKey("id");

                    b.HasIndex("idHouse");

                    b.ToTable("FlatOwners");
                });

            modelBuilder.Entity("HouseManagement.Models.House", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<string>("name")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.HasKey("id");

                    b.ToTable("Houses");
                });

            modelBuilder.Entity("HouseManagement.Models.Indication", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<DateTime>("dateTransfer")
                        .HasColumnType("datetime2");

                    b.Property<int>("idCounter")
                        .HasColumnType("int");

                    b.Property<int>("value")
                        .HasColumnType("int");

                    b.HasKey("id");

                    b.HasIndex("idCounter");

                    b.ToTable("Indications");
                });

            modelBuilder.Entity("HouseManagement.Models.Payment", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<double>("amount")
                        .HasColumnType("float");

                    b.Property<int>("idFlat")
                        .HasColumnType("int");

                    b.Property<int>("idService")
                        .HasColumnType("int");

                    b.Property<double>("penalties")
                        .HasColumnType("float");

                    b.Property<string>("period")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.Property<bool>("status")
                        .HasColumnType("bit");

                    b.HasKey("id");

                    b.HasIndex("idFlat");

                    b.HasIndex("idService");

                    b.ToTable("Payments");
                });

            modelBuilder.Entity("HouseManagement.Models.RelationshipHouses", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<int>("idHouse")
                        .HasColumnType("int");

                    b.Property<string>("idUser")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.HasKey("id");

                    b.HasIndex("idHouse");

                    b.ToTable("RelationshipHouses");
                });

            modelBuilder.Entity("HouseManagement.Models.Residence", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<int>("idFlat")
                        .HasColumnType("int");

                    b.Property<int>("idFlatOwner")
                        .HasColumnType("int");

                    b.HasKey("id");

                    b.HasIndex("idFlat");

                    b.HasIndex("idFlatOwner");

                    b.ToTable("Residences");
                });

            modelBuilder.Entity("HouseManagement.Models.Service", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<string>("nameCounter")
                        .HasColumnType("nvarchar(max)");

                    b.Property<string>("nameService")
                        .IsRequired()
                        .HasColumnType("nvarchar(max)");

                    b.HasKey("id");

                    b.ToTable("Services");
                });

            modelBuilder.Entity("HouseManagement.Models.SettingsService", b =>
                {
                    b.Property<int>("id")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("int");

                    SqlServerPropertyBuilderExtensions.UseIdentityColumn(b.Property<int>("id"), 1L, 1);

                    b.Property<int?>("endDateTransfer")
                        .HasColumnType("int");

                    b.Property<bool>("haveCounter")
                        .HasColumnType("bit");

                    b.Property<int>("idHouse")
                        .HasColumnType("int");

                    b.Property<int>("idService")
                        .HasColumnType("int");

                    b.Property<int?>("paymentEndDate")
                        .HasColumnType("int");

                    b.Property<string>("paymentPeriod")
                        .HasColumnType("nvarchar(max)");

                    b.Property<bool>("paymentStatus")
                        .HasColumnType("bit");

                    b.Property<int?>("startDateTransfer")
                        .HasColumnType("int");

                    b.Property<bool?>("typeIMD")
                        .HasColumnType("bit");

                    b.Property<double?>("valueNormative")
                        .HasColumnType("float");

                    b.Property<double>("valueRate")
                        .HasColumnType("float");

                    b.HasKey("id");

                    b.HasIndex("idHouse");

                    b.HasIndex("idService");

                    b.ToTable("SettingsServices");
                });

            modelBuilder.Entity("HouseManagement.Models.Advertisement", b =>
                {
                    b.HasOne("HouseManagement.Models.House", "house")
                        .WithMany()
                        .HasForeignKey("idHouse")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("house");
                });

            modelBuilder.Entity("HouseManagement.Models.Complaint", b =>
                {
                    b.HasOne("HouseManagement.Models.Flat", "flat")
                        .WithMany()
                        .HasForeignKey("idFlat")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("flat");
                });

            modelBuilder.Entity("HouseManagement.Models.Counter", b =>
                {
                    b.HasOne("HouseManagement.Models.Flat", "flat")
                        .WithMany()
                        .HasForeignKey("idFlat");

                    b.HasOne("HouseManagement.Models.House", "house")
                        .WithMany()
                        .HasForeignKey("idHouse")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("HouseManagement.Models.Service", "service")
                        .WithMany()
                        .HasForeignKey("idService")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("flat");

                    b.Navigation("house");

                    b.Navigation("service");
                });

            modelBuilder.Entity("HouseManagement.Models.Flat", b =>
                {
                    b.HasOne("HouseManagement.Models.House", "house")
                        .WithMany()
                        .HasForeignKey("idHouse");

                    b.Navigation("house");
                });

            modelBuilder.Entity("HouseManagement.Models.FlatOwner", b =>
                {
                    b.HasOne("HouseManagement.Models.House", "house")
                        .WithMany()
                        .HasForeignKey("idHouse")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("house");
                });

            modelBuilder.Entity("HouseManagement.Models.Indication", b =>
                {
                    b.HasOne("HouseManagement.Models.Counter", "counter")
                        .WithMany()
                        .HasForeignKey("idCounter")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("counter");
                });

            modelBuilder.Entity("HouseManagement.Models.Payment", b =>
                {
                    b.HasOne("HouseManagement.Models.Flat", "flat")
                        .WithMany()
                        .HasForeignKey("idFlat")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("HouseManagement.Models.Service", "service")
                        .WithMany()
                        .HasForeignKey("idService")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("flat");

                    b.Navigation("service");
                });

            modelBuilder.Entity("HouseManagement.Models.RelationshipHouses", b =>
                {
                    b.HasOne("HouseManagement.Models.House", "house")
                        .WithMany()
                        .HasForeignKey("idHouse")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("house");
                });

            modelBuilder.Entity("HouseManagement.Models.Residence", b =>
                {
                    b.HasOne("HouseManagement.Models.Flat", "flat")
                        .WithMany()
                        .HasForeignKey("idFlat")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("HouseManagement.Models.FlatOwner", "flatOwner")
                        .WithMany()
                        .HasForeignKey("idFlatOwner")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("flat");

                    b.Navigation("flatOwner");
                });

            modelBuilder.Entity("HouseManagement.Models.SettingsService", b =>
                {
                    b.HasOne("HouseManagement.Models.House", "house")
                        .WithMany()
                        .HasForeignKey("idHouse")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("HouseManagement.Models.Service", "service")
                        .WithMany()
                        .HasForeignKey("idService")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("house");

                    b.Navigation("service");
                });
#pragma warning restore 612, 618
        }
    }
}
