using HouseManagement.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace HouseManagement.OtherClasses
{
    public class DBContext : DbContext
    {
        public DbSet<Advertisement> Advertisements { get; set; }
        public DbSet<Complaint> Complaints { get; set; }
        public DbSet<Counter> Counters { get; set; }
        public DbSet<Flat> Flats { get; set; }
        public DbSet<FlatOwner> FlatOwners { get; set; }
        public DbSet<Indication> Indications { get; set; }
        public DbSet<Payment> Payments { get; set; }
        public DbSet<Service> Services { get; set; }
        public DbSet<House> Houses { get; set; }
        public DbSet<SettingsService> SettingsServices { get; set; }
        public DbSet<RelationshipHouses> RelationshipHouses { get; set; }
        public DbSet<Residence> Residences { get; set; }
        public DBContext(DbContextOptions<DBContext> options) : base(options)
        { }
    }
}